package dials.dropwizard.server;

import dials.DialsSystemInitializer;
import dials.dropwizard.server.resources.DialsFeatureStateResource;
import dials.execution.ExecutionContextRecorder;
import dials.execution.LoggingBasedExecutionContextRecorder;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;

public class DialsApplication extends Application<DialsApplicationConfiguration> {

    public static void main(String[] args) throws Exception {
        new DialsApplication().run(args);
    }

    @Override
    public void initialize(Bootstrap bootstrap) {

    }

    @Override
    public void run(DialsApplicationConfiguration configuration, Environment environment) throws Exception {
        initializeDials(getEntityManager(configuration));

        environment.jersey().register(new DialsFeatureStateResource());
    }

    private void initializeDials(EntityManagerFactory entityManagerFactory) throws ClassNotFoundException {
        ExecutionContextRecorder contextRecorder =
                new LoggingBasedExecutionContextRecorder(LoggingBasedExecutionContextRecorder.INFO);
        DialsSystemInitializer.getInstance().withEntityManagerFactory(entityManagerFactory)
                .withExecutionContextRecorder(contextRecorder).withFailFastEnabled(true).initializeSystem();
    }

    private EntityManagerFactory getEntityManager(DialsApplicationConfiguration configuration) {
        Map<String, String> emfProperties = new HashMap<>();
        emfProperties.put("javax.persistence.jdbc.driver", configuration.getDataSourceFactory().getDriverClass());
        emfProperties.put("javax.persistence.jdbc.url", configuration.getDataSourceFactory().getUrl());
        emfProperties.put("javax.persistence.jdbc.user", configuration.getDataSourceFactory().getUser());
        emfProperties.put("javax.persistence.jdbc.password", configuration.getDataSourceFactory().getPassword());

        return Persistence.createEntityManagerFactory("dialsManager", emfProperties);
    }

}
