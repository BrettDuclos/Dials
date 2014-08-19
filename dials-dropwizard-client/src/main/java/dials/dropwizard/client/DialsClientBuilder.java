package dials.dropwizard.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.filter.GZIPContentEncodingFilter;
import com.sun.jersey.client.apache4.ApacheHttpClient4;
import com.sun.jersey.client.apache4.ApacheHttpClient4Handler;
import com.sun.jersey.client.apache4.config.ApacheHttpClient4Config;
import com.sun.jersey.client.apache4.config.DefaultApacheHttpClient4Config;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DialsClientBuilder {

    private final URI baseURI;
    private ExecutorService executorService;
    private Client client;

    private DialsClientBuilder(URI baseURI) {
        this.baseURI = baseURI;
    }

    public static DialsClientBuilder withURI(String uri) throws URISyntaxException {
        return withURI(new URI(uri));
    }

    public static DialsClientBuilder withURI(URI uri) {
        return new DialsClientBuilder(uri);
    }

    public DialsClientBuilder withClient(Client client) {
        this.client = client;
        return this;
    }

    public DialsClientBuilder withExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
        return this;
    }

    public DialsClient build() {
        return new DialsClientImpl(determineClient(), baseURI);
    }

    private Client determineClient() {
        return this.client == null ? buildDefaultClient() : this.client;
    }

    private ExecutorService determineExecutorService() {
        return this.executorService == null ? Executors.newCachedThreadPool() : this.executorService;
    }

    private Client buildDefaultClient() {
        Client client = new ApacheHttpClient4(buildClientHandler(buildHttpClient()), buildClientConfig());
        client.setExecutorService(determineExecutorService());
        client.addFilter(new GZIPContentEncodingFilter(true));
        return client;
    }

    private ApacheHttpClient4Config buildClientConfig() {
        ApacheHttpClient4Config clientConfig = new DefaultApacheHttpClient4Config();
        ObjectMapper objectMapper = buildObjectMapper();
        clientConfig.getSingletons().add(new JacksonJaxbJsonProvider(objectMapper, null));
        return clientConfig;
    }

    private ObjectMapper buildObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        return objectMapper;
    }

    private ApacheHttpClient4Handler buildClientHandler(HttpClient httpClient) {
        return new ApacheHttpClient4Handler(httpClient, null, true);
    }

    private HttpClient buildHttpClient() {
        return HttpClientBuilder.create().build();
    }

}
