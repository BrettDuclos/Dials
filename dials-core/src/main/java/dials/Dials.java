package dials;

import akka.actor.*;
import akka.routing.RoundRobinPool;
import dials.execution.ExecutionContext;
import dials.execution.ExecutionRegistry;
import dials.filter.FilterData;
import dials.messages.ContextualMessage;
import dials.messages.FeatureStateRequestMessage;
import dials.messages.FeatureStateResultMessage;
import dials.messages.RegisterErrorMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;

public class Dials {

    private static Logger logger = LoggerFactory.getLogger(Dials.class);

    private static final int TIMEOUT = 500;
    private static final int ACTOR_COUNT = 5;

    private static ActorSystem system;
    private static Inbox inbox;

    private static DialsSystemConfiguration systemConfiguration;

    private static boolean initialized = false;

    protected static void init(DialsSystemConfiguration configuration) {
        systemConfiguration = configuration;
        system = ActorSystem.create("Dials");

        system.actorOf(Props.create(ExecutionRegistry.class, configuration.getExecutionContextRecorder())
                .withRouter(new RoundRobinPool(ACTOR_COUNT)), "ExecutionRegistry");
        inbox = Inbox.create(system);

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

        if (!systemConfiguration.getDataStore().doesFeatureExist(featureName)) {
            logger.warn("Requested feature does not yet exist.");
            return false;
        }

        ActorRef dispatcher = getNewDialsDispatcher();
        sendFeatureStateRequest(featureName, dynamicData, dispatcher);

        boolean state = getFeatureStateResult();

        inbox.send(dispatcher, PoisonPill.getInstance());

        return state;
    }

    private static void sendFeatureStateRequest(String featureName, FilterData dynamicData, ActorRef dispatcher) {
        FeatureStateRequestMessage message = new FeatureStateRequestMessage(featureName, dynamicData, new ContextualMessage(
                new ExecutionContext(featureName).addExecutionStep("Feature State Request Started"), systemConfiguration));
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

    public static void sendError(String featureName) {
        getNewDialsDispatcher().tell(new RegisterErrorMessage(featureName, systemConfiguration), ActorRef.noSender());
    }

    private static ActorRef getNewDialsDispatcher() {
        return system.actorOf(Props.create(DialsFacilitator.class));
    }

    public static void shutdown() {
        system.shutdown();
    }

}
