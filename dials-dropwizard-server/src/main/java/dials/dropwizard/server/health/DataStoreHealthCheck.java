package dials.dropwizard.server.health;

import com.codahale.metrics.health.HealthCheck;
import dials.Dials;

public class DataStoreHealthCheck extends HealthCheck {

    @Override
    protected Result check() throws Exception {
        if (Dials.getRegisteredDataStore().isDataStoreAccessible()) {
            return Result.healthy();
        }

        return Result.unhealthy("Unable to access Dials datastore");
    }
}
