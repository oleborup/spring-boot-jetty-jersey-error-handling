package oleborup.sample.api;

import oleborup.sample.api.resource.HelloResource;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.ws.rs.ApplicationPath;


@Component
@ApplicationPath("/")
public class JerseyConfig extends ResourceConfig {

    private static final Logger LOG = LoggerFactory.getLogger(JerseyConfig.class);

    public JerseyConfig() {
        super();

        property(ServletProperties.FILTER_STATIC_CONTENT_REGEX, "(/\\.well-known.*)|(/(image|js|css|fonts)/?.*)|(/.*\\.html)|(/favicon\\.ico)|(/robots\\.txt)");

        register(HelloResource.class);
    }

}

