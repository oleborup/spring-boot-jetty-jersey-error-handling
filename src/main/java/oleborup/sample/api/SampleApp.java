package oleborup.sample.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.web.embedded.jetty.JettyServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Configuration
@ComponentScan
@EnableAutoConfiguration
public class SampleApp {

    public static void main(String[] args) {
        SpringApplication.run(SampleApp.class, args);
    }

    /**
     * Custom Jetty container that works with read-only containers
     */
    @Bean
    public ConfigurableServletWebServerFactory servletContainer() {
        JettyServletWebServerFactory factory = new JettyServletWebServerFactory();
        factory.setDocumentRoot(new File(System.getProperty("java.io.tmpdir")));
        return factory;
    }

}
