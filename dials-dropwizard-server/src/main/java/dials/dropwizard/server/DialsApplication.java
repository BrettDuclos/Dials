package dials.dropwizard.server;

import dials.DialsSystemConfiguration;
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
        DataStore dataStore = new DataStoreSelecter().selectDataStore(configuration, environment);
        initializeDials(dataStore, configuration, environment);

        environment.jersey().register(new DialsFeatureStateResource());
        environment.healthChecks().register("Datastore", new DataStoreHealthCheck(dataStore));
    }

    private void initializeDials(DataStore dataStore, DialsApplicationConfiguration configuration,
                                 Environment environment) throws ClassNotFoundException {
        ExecutionContextRecorder contextRecorder = new LoggingBasedExecutionContextRecorder(LoggingBasedExecutionContextRecorder.INFO);
        new DialsSystemConfiguration().withDataStore(dataStore)
                .withExecutionContextRecorder(contextRecorder).withFailFastEnabled(true).initializeSystem();
    }

}
