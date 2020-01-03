package oleborup.sample.api;

import org.eclipse.jetty.server.ConnectionFactory;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.ErrorHandler;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.web.embedded.jetty.JettyServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;

@Configuration
@ComponentScan
@EnableAutoConfiguration
public class SampleApp {

    private static final Logger LOG = LoggerFactory.getLogger(SampleApp.class);

    /**
     * Custom Jetty container with no server tag and content empty error response
     */
    @Bean
    public ConfigurableServletWebServerFactory webServerFactory()  {
        JettyServletWebServerFactory factory = new JettyServletWebServerFactory();
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

    /**
     * Error handler revealing nothing about the server by returning no content
     */
    private static class SilentErrorHandler extends ErrorHandler {
        @Override
        public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) {
            LOG.info("Handling error with no content");
            baseRequest.setHandled(true);
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(SampleApp.class, args);
    }

}
