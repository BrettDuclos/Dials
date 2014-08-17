package dials;

import akka.actor.*;
import akka.routing.RoundRobinPool;
import dials.datastore.DataStore;
import dials.execution.ExecutionContext;
import dials.execution.ExecutionRegistry;
import dials.filter.FeatureFilter;
import dials.filter.FilterData;
import dials.messages.ContextualMessage;
import dials.messages.FeatureStateRequestMessage;
import dials.messages.FeatureStateResultMessage;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.concurrent.duration.Duration;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class Dials {

    private static Logger logger = LoggerFactory.getLogger(Dials.class);

    private static final int TIMEOUT = 500;
    private static final int ACTOR_COUNT = 5;

    private static DataStore dataStore;
    private static boolean failFastEnabled;

    private static ActorSystem system;
    private static Inbox inbox;

    private static Map<String, Class<? extends FeatureFilter>> availableFeatureFilters;

    private static boolean initialized = false;

    protected static void init(DialsSystemInitializer initializer) {
        failFastEnabled = initializer.isFailFastEnabled();

        dataStore = initializer.getDataStore();

        system = ActorSystem.create("Dials");

        system.actorOf(Props.create(ExecutionRegistry.class, initializer.getExecutionContextRecorder())
                .withRouter(new RoundRobinPool(ACTOR_COUNT)), "ExecutionRegistry");
        inbox = Inbox.create(system);


        availableFeatureFilters = new HashMap<>();
        Reflections reflections = new Reflections("dials");
        Set<Class<? extends FeatureFilter>> filters = reflections.getSubTypesOf(FeatureFilter.class);

        for (Class<? extends FeatureFilter> filter : filters) {
            String key = filter.getSimpleName().replace("FeatureFilter", "");
            availableFeatureFilters.put(key.toLowerCase(), filter);
            logger.info("Detected Feature Filter - " + key);
        }

        initialized = true;
    }

    public static boolean getState(String featureName) {
        return getState(featureName, new FilterData());
    }

    public static boolean getState(String featureName, FilterData dynamicData) {
        if (!initialized) {
            logger.error("Dials system has not been initialized.");
            return false;
        }

        if (!dataStore.doesFeatureExist(featureName)) {
            logger.warn("Requested feature does not yet exist.");
            return false;
        }

        ActorRef dispatcher = getNewDialsDispatcher();
        sendFeatureStateRequest(featureName, dynamicData, dispatcher);

        boolean state = getFeatureStateResult();

        inbox.send(dispatcher, PoisonPill.getInstance());

        return state;
    }

    public static void sendError(String featureName) {
        getRegisteredDataStore().registerError(featureName);
    }


    private static ActorRef getNewDialsDispatcher() {
        return system.actorOf(Props.create(DialsFacilitator.class));
    }

    private static void sendFeatureStateRequest(String featureName, FilterData dynamicData, ActorRef dispatcher) {
        FeatureStateRequestMessage message = new FeatureStateRequestMessage(featureName, dynamicData,
                new ContextualMessage(new ExecutionContext(featureName).addExecutionStep("Feature State Request Started")));
        inbox.send(dispatcher, message);
    }


    private static boolean getFeatureStateResult() {
        boolean state = false;

        try {
            state = ((FeatureStateResultMessage) inbox.receive(Duration.create(TIMEOUT, TimeUnit.MILLISECONDS))).getState();
        } catch (Exception e) {
            logger.warn("System timed out calculating feature state, will return false.");
        }
        return state;
    }

    public static DataStore getRegisteredDataStore() {
        return dataStore;
    }

    public static boolean isFailFastEnabled() {
        return failFastEnabled;
    }

    public static Map<String, Class<? extends FeatureFilter>> getAvailableFeatureFilters() {
        return availableFeatureFilters;
    }

    public static void shutdown() {
        system.shutdown();
    }

}
