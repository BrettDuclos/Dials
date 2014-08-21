package dials.dropwizard.server.health;

import com.codahale.metrics.health.HealthCheck;
import dials.datastore.DataStore;

public class DataStoreHealthCheck extends HealthCheck {

    private DataStore dataStore;

    public DataStoreHealthCheck(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    @Override
    protected Result check() throws Exception {
        if (dataStore.isDataStoreAccessible()) {
            return Result.healthy();
        }

        return Result.unhealthy("Unable to access Dials datastore");
    }
}
