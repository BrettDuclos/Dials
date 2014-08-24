package dials;

import akka.actor.*;
import akka.routing.RoundRobinPool;
import com.codahale.metrics.Timer;
import dials.execution.ExecutionContext;
import dials.execution.ExecutionRegistry;
import dials.filter.FilterData;
import dials.messages.ContextualMessage;
import dials.messages.FeatureStateRequestMessage;
import dials.messages.FeatureStateResultMessage;
import dials.model.FeatureModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.concurrent.duration.Duration;

import javax.persistence.PersistenceException;
import java.util.concurrent.TimeUnit;

import static com.codahale.metrics.MetricRegistry.name;

public class Dials {

    private static Logger logger = LoggerFactory.getLogger(Dials.class);

    private static final int TIMEOUT = 100;
    private static final int ACTOR_COUNT = 5;

    private static ActorSystem system;

    private static DialsSystemConfiguration systemConfiguration;

    private static boolean initialized = false;

    protected static void init(DialsSystemConfiguration configuration) {
        systemConfiguration = configuration;
        system = ActorSystem.create("Dials");

        system.actorOf(Props.create(ExecutionRegistry.class, configuration.getExecutionContextRecorder())
                .withRouter(new RoundRobinPool(ACTOR_COUNT)), "ExecutionRegistry");

        initialized = true;
    }

    public static boolean getState(String featureName) {
        return getState(featureName, new FilterData());
    }

    public static boolean getState(final String featureName, FilterData dynamicData) {
        if (!initialized) {
            logger.error("Dials system has not been initialized.");
            return false;
        }

        systemConfiguration.getMetricRegistry().meter(name("Dials", featureName, "RequestAttemptMeter")).mark();

        Timer.Context timer = systemConfiguration.getMetricRegistry()
                .timer(name("Dials", featureName, "RequestTimer")).time();

        FeatureModel feature = getFeature(featureName);

        if (feature == null) {
            timer.stop();
            return false;
        }

        if (!feature.getIsEnabled()) {
            logger.warn("Requested feature is disabled.");
            timer.stop();
            return false;
        }

        Inbox inbox = Inbox.create(system);
        ActorRef dispatcher = getNewDialsDispatcher();
        sendFeatureStateRequest(inbox, featureName, dynamicData, dispatcher);

        boolean state = getFeatureStateResult(inbox);

        inbox.send(dispatcher, PoisonPill.getInstance());

        timer.stop();

        if (state) {
            systemConfiguration.getMetricRegistry().meter(name("Dials", featureName, "RequestExecutionMeter")).mark();
        }
        return state;
    }

    private static FeatureModel getFeature(String featureName) {
        FeatureModel feature = null;

        try {
            feature = systemConfiguration.getRepository().getFeature(featureName);
        } catch (PersistenceException e) {
            logger.warn("Requested feature does not yet exist.");
        }

        return feature;
    }

    private static void sendFeatureStateRequest(Inbox inbox, String featureName, FilterData dynamicData, ActorRef dispatcher) {
        FeatureStateRequestMessage message = new FeatureStateRequestMessage(dynamicData, new ContextualMessage(
                new ExecutionContext(featureName).addExecutionStep("Feature State Request Started"), systemConfiguration));
        inbox.send(dispatcher, message);
    }

    private static boolean getFeatureStateResult(Inbox inbox) {
        boolean state = false;

        try {
            state = ((FeatureStateResultMessage) inbox.receive(Duration.create(TIMEOUT, TimeUnit.MILLISECONDS))).getState();
        } catch (Exception e) {
            logger.warn("System timed out calculating feature state, will return false.");
        }
        return state;
    }

    public static void sendError(String featureName) {
        systemConfiguration.getMetricRegistry().meter(name("Dials", featureName, "RequestErrorMeter")).mark();
        systemConfiguration.getRepository().registerFeatureError(featureName);
    }

    private static ActorRef getNewDialsDispatcher() {
        return system.actorOf(Props.create(DialsFacilitator.class));
    }

    public static void shutdown() {
        system.shutdown();
    }

}
