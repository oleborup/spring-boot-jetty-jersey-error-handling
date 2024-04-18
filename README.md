## Background

We use Spring Boot with embedded Jetty and Jersey for microservices running as Docker containers. For improved security we run Docker containers read-only.

## Version and settings

Jetty 12.0.7
Spring Boot 3.2.4
Java 17

## Problem

After upgrading to Jetty 12 with Spring Boot 3.2, Jetty fails to initialize, as temp directory creation fails on read-only filesystem.

Running embedded with Jetty does not require the use of the temp directory. Cannot find a way to allow Jetty to run without creating temp directory.

Previously this worked to allow read-only.

```java
@Bean
public ConfigurableServletWebServerFactory servletContainer() {
    JettyServletWebServerFactory factory = new JettyServletWebServerFactory();
    factory.setDocumentRoot(new File(System.getProperty("java.io.tmpdir")));
    return factory;
}
```

## To reproduce

    mvn clean install
    docker build -t sampleapp .
    docker run --read-only -p 8080:8080 sampleapp
    

```
org.springframework.context.ApplicationContextException: Unable to start web server
    ...
Caused by: org.springframework.boot.web.server.WebServerException: Unable to create tempDir. java.io.tmpdir is set to /tmp
    ...
Caused by: java.nio.file.FileSystemException: /tmp/jetty-docbase.8080.17697230866097709625: Read-only file system
    ...
```
    
