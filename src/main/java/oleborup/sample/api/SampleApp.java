package oleborup.sample.api;

import org.eclipse.jetty.http.MimeTypes;
import org.eclipse.jetty.server.ConnectionFactory;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.ErrorHandler;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;
import org.eclipse.jetty.webapp.AbstractConfiguration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.web.embedded.jetty.JettyServletWebServerFactory;
import org.springframework.boot.web.server.MimeMappings;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Configuration
@ComponentScan
@EnableAutoConfiguration(exclude = {ErrorMvcAutoConfiguration.class})
public class SampleApp {

    private static final Logger LOG = LoggerFactory.getLogger(SampleApp.class);

    /**
     * Custom Jetty container with no server tag and content empty error response
     */
    @Bean
    public ConfigurableServletWebServerFactory webServerFactory()  {
        CustomJettyServletWebServerFactory factory = new CustomJettyServletWebServerFactory();
        factory.setDocumentRoot(new File(System.getProperty("java.io.tmpdir")));
        factory.addServerCustomizers(server -> {
            server.setErrorHandler(new SilentErrorHandler());
            Handler handler = server.getHandler();
            if (handler instanceof GzipHandler) {
                handler = ((GzipHandler) handler).getHandler();
            }
            WebAppContext context = (WebAppContext) handler;
            context.setErrorHandler(new SilentErrorHandler());
            for (Connector connector : server.getConnectors()) {
                for (ConnectionFactory connFac : connector.getConnectionFactories()) {
                    if (connFac instanceof HttpConnectionFactory) {
                        ((HttpConnectionFactory) connFac).getHttpConfiguration().setSendServerVersion(false);
                    }
                }
            }
        });

        return factory;
    }

    private static class CustomJettyServletWebServerFactory extends JettyServletWebServerFactory {

        @Override
        protected org.eclipse.jetty.webapp.Configuration[] getWebAppContextConfigurations(WebAppContext webAppContext, ServletContextInitializer... initializers) {
            List<org.eclipse.jetty.webapp.Configuration> configurations = new ArrayList<>();
            configurations.add(getServletContextInitializerConfiguration(webAppContext, initializers));
            configurations.addAll(getConfigurations());
            //configurations.add(getErrorPageConfiguration());
            configurations.add(getMimeTypeConfiguration());
            return configurations.toArray(new org.eclipse.jetty.webapp.Configuration[0]);
        }

        private org.eclipse.jetty.webapp.Configuration getMimeTypeConfiguration() {
            return new AbstractConfiguration() {
                @Override
                public void configure(WebAppContext context) {
                    MimeTypes mimeTypes = context.getMimeTypes();
                    for (MimeMappings.Mapping mapping : getMimeMappings()) {
                        mimeTypes.addMimeMapping(mapping.getExtension(), mapping.getMimeType());
                    }
                }
            };
        }

    }

    /**
     * Error handler revealing nothing about the server by returning no content
     */
    private static class SilentErrorHandler extends ErrorHandler {
        @Override
        public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
                throws IOException {
            LOG.info("Handling error with no content");
            baseRequest.setHandled(true);
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(SampleApp.class, args);
    }

}
