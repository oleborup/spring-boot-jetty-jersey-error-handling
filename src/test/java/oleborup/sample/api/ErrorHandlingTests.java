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

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;

import static java.lang.System.out;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest(classes = SampleApp.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ErrorHandlingTests {

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
    void restHello() {
        Response response = getRequest("/hello").get();
        assertEquals(200, response.getStatus());
        assertEquals("{\"hello\":\"world\"}", response.readEntity(String.class));
    }

    @Test
    void invalidContentTypeEmptyPage() {
        String request = "GET / HTTP/1.0\r\nConnection: close\r\nContent-Type: invalid\r\n\r\n";
        try (Socket socket = new Socket("localhost", port)) {
            PrintWriter outputStream = new PrintWriter(socket.getOutputStream(), true);
            outputStream.print(request);
            outputStream.flush();
            String response = new String(socket.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            out.println("response:\n" + response);
            assertFalse(response.contains("SERVLET:"));
        } catch (IOException e) {
            fail(e);
        }
    }

    @Test
    void invalidUriEmptyPage() {
        String request = "GET /}} HTTP/1.0\r\nConnection: close\r\n\r\n";
        try (Socket socket = new Socket("localhost", port)) {
            PrintWriter outputStream = new PrintWriter(socket.getOutputStream(), true);
            outputStream.print(request);
            outputStream.flush();
            String response = new String(socket.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            out.println("response:\n" + response);
            assertFalse(response.contains("CAUSED BY"));
        } catch (IOException e) {
            fail(e);
        }
    }

}
