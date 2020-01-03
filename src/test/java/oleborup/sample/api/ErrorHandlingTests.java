package oleborup.sample.api;

import org.glassfish.jersey.logging.LoggingFeature;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.util.logging.Level;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SampleApp.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ErrorHandlingTests {

    @Value("${local.server.port}")
    protected int port;

    protected final Client client;

    public ErrorHandlingTests() {
        client = ClientBuilder.newClient();
        java.util.logging.Logger logger = java.util.logging.Logger.getLogger(ErrorHandlingTests.class.toString());
        client.register(new LoggingFeature(logger, Level.INFO, LoggingFeature.Verbosity.PAYLOAD_TEXT, 1024));
    }

    private Invocation.Builder getRequest(String path) {
        return client.target(getUri(path)).request();
    }

    private String getUri(String path) {
        return UriBuilder.fromPath(path).port(port).host("localhost").scheme("http").build().toString();
    }

    @Test
    public void staticContent() {
        Response response = getRequest("/index.html").get();
        assertEquals(200, response.getStatus());
        assertEquals("<html></html>", response.readEntity(String.class));
    }

    @Test
    public void staticContentNotFoundEmptyResponse() {
        Response response = getRequest("/nonexists.html").get();
        assertEquals(404, response.getStatus());
        assertEquals("", response.readEntity(String.class));
    }

}
