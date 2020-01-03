## Background

We use Spring Boot with Jetty and Jersey for microservices. For security reasons we want to expose as little server identification as possible.
Jetty server is customized with silent error handler to avoid default output.

In some cases we need to serve static files in addition to REST resources. For this we use 

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

and 

```java
property(ServletProperties.FILTER_STATIC_CONTENT_REGEX, "(/.*\\.html)");
```

in `JerseyConfig`

For errors in the static match case the custom error handler registered in `SampleApp` is used.

## Problem

After upgrade from Spring Boot 2.2.1 to 2.2.2 the custom error handler is not invoked. Instead `JettyEmbeddedErrorHandler` is used which uses the Jetty default error handler.

## To reproduce

Verify tests are passing for Spring Boot 2.2.1

    mvn clean test
    
Change Spring Boot version to 2.2.2 in `pom.xml` and run tests again

    mvn clean test 

## Observations

Upgrading from 2.2.1 to 2.2.2 the default MVC error handler started to be invoked instead of Jetty custom error handler. To disable the request to `/error` the following was added 

    @EnableAutoConfiguration(exclude = {ErrorMvcAutoConfiguration.class})
    
## Solutions

Any solutions to achieve the same pre-2.2.2 functionality would be greatly appreciated.
