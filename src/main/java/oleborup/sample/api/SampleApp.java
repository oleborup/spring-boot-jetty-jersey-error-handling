package oleborup.sample.api;

import org.eclipse.jetty.server.ConnectionFactory;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.ErrorHandler;
import org.eclipse.jetty.server.handler.StatisticsHandler;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;
import org.eclipse.jetty.webapp.WebAppContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.web.embedded.jetty.JettyServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

@Configuration
@ComponentScan
@EnableAutoConfiguration(exclude = {ErrorMvcAutoConfiguration.class})
public class SampleApp {

    /**
     * Custom Jetty container with no server tag and content empty error response
     */
    @Bean
    public ServletWebServerFactory servletContainer() {
        JettyServletWebServerFactory factory = new JettyServletWebServerFactory();
        factory.setDocumentRoot(new File(System.getProperty("java.io.tmpdir")));
        factory.addServerCustomizers(server -> {
            Handler handler = server.getHandler();
            StatisticsHandler stats = new StatisticsHandler();
            stats.setHandler(handler);
            server.setHandler(stats);
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
     * Error handler reviling nothing about the server and returning no content except bad request
     */
    private static class SilentErrorHandler extends ErrorHandler {
        @Override
        public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
                throws IOException {
            baseRequest.setHandled(true);
        }

    }

    public static void main(String[] args) {
        SpringApplication.run(SampleApp.class, args);
    }

}
