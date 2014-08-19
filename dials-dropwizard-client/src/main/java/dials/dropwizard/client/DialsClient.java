package dials.dropwizard.client;

import java.io.Closeable;
import java.io.IOException;

public interface DialsClient extends Closeable {

    boolean isReachable();

    DialsResource getDialsResource();

    @Override
    void close() throws IOException;
}
