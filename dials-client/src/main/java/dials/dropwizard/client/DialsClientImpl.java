package dials.dropwizard.client;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

import java.io.IOException;
import java.net.URI;

public class DialsClientImpl implements DialsClient {

    private final Client client;
    private final DialsResource dialsResource;
    private final WebResource pingResource;

    public DialsClientImpl(Client client, URI baseURI) {
        WebResource appWebResource = client.resource(baseURI);
        this.client = client;
        this.dialsResource = new DialsResourceImpl(appWebResource);
        this.pingResource = client.resource(baseURI + "/ping");
    }

    @Override
    public boolean isReachable() {
        return "pong".equals(pingResource.get(String.class).trim());
    }

    @Override
    public DialsResource getDialsResource() {
        return dialsResource;
    }

    @Override
    public void close() throws IOException {
        client.destroy();
        client.getExecutorService().shutdown();
    }
}
