package dials.dropwizard.server.datastore;

import dials.datastore.DataStore;
import dials.datastore.JdbcDataStore;
import dials.dropwizard.server.DialsApplicationConfiguration;
import io.dropwizard.setup.Environment;

public class DataStoreSelecter {

    //Currently only support jdbc data sources
    public DataStore selectDataStore(DialsApplicationConfiguration configuration, Environment environment) throws ClassNotFoundException {
        return new JdbcDataStore(configuration.getDataSourceFactory().build(environment.metrics(), "database"));
    }
}
