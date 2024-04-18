package oleborup.sample.api;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import org.glassfish.jersey.logging.LoggingFeature;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.logging.Level;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = SampleApp.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HelloResourceTests {

    @Value("${local.server.port}")
    protected int port;

    protected final Client client;

    public HelloResourceTests() {
        client = ClientBuilder.newClient();
        java.util.logging.Logger logger = java.util.logging.Logger.getLogger(HelloResourceTests.class.toString());
        client.register(new LoggingFeature(logger, Level.INFO, LoggingFeature.Verbosity.PAYLOAD_TEXT, 1024));
    }

    private Invocation.Builder getRequest(String path) {
        return client.target(getUri(path)).request();
    }

    private String getUri(String path) {
        return UriBuilder.fromPath(path).port(port).host("localhost").scheme("http").build().toString();
    }

    @Test
    void restHello() {
        Response response = getRequest("/hello").get();
        assertEquals(200, response.getStatus());
        assertEquals("{\"hello\":\"world\"}", response.readEntity(String.class));
    }

}
