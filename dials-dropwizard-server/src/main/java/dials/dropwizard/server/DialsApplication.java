package dials.dropwizard.server;

import dials.DialsSystemInitializer;
import dials.datastore.DataStore;
import dials.dropwizard.server.datastore.DataStoreSelecter;
import dials.dropwizard.server.health.DataStoreHealthCheck;
import dials.dropwizard.server.resources.DialsFeatureStateResource;
import dials.execution.ExecutionContextRecorder;
import dials.execution.LoggingBasedExecutionContextRecorder;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class DialsApplication extends Application<DialsApplicationConfiguration> {

    public static void main(String[] args) throws Exception {
        new DialsApplication().run(args);
    }

    @Override
    public void initialize(Bootstrap bootstrap) {

    }

    @Override
    public void run(DialsApplicationConfiguration configuration, Environment environment) throws Exception {
        initializeDials(configuration, environment);

        environment.jersey().register(new DialsFeatureStateResource());
        environment.healthChecks().register("Datastore", new DataStoreHealthCheck());
    }

    private void initializeDials(DialsApplicationConfiguration configuration, Environment environment) throws ClassNotFoundException {
        DataStore dataStore = new DataStoreSelecter().selectDataStore(configuration, environment);
        ExecutionContextRecorder contextRecorder = new LoggingBasedExecutionContextRecorder(LoggingBasedExecutionContextRecorder.INFO);
        new DialsSystemInitializer().withDataStore(dataStore)
                .withExecutionContextRecorder(contextRecorder).withFailFastEnabled(true).initialize();
    }

}
