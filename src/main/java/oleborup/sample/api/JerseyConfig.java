package oleborup.sample.api;

import jakarta.ws.rs.ApplicationPath;
import oleborup.sample.api.resource.HelloResource;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

import java.util.logging.Level;

@Component
@ApplicationPath("/")
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
        super();

        // Debug logging
        java.util.logging.Logger logger = java.util.logging.Logger.getLogger(JerseyConfig.class.toString());
        LoggingFeature filter = new LoggingFeature(logger, Level.INFO, LoggingFeature.Verbosity.PAYLOAD_TEXT, 4095);
        register(filter, Integer.MIN_VALUE);

        register(HelloResource.class);
    }

}

