## Background

We use Spring Boot with Jetty and Jersey for microservices. For security reasons we want to expose as little server identification and implementation details as possible.

## Version and settings

Spring Boot 3.2.4
Jetty 12.0.7
Java 17

We use `spring.jersey.type=filter` but have same problem with `servlet`. Other than that no altered properties.

## Problem

Invalid HTTP requests result in Jetty standard error page with servlet and stacktrace. Spring Boot server properties should not allow this:

```
server.error.include-exception=false
server.error.include-message=never
server.error.include-stacktrace=never
```

## To reproduce

    mvn clean test
    
Two tests fail. One requests with invalid `Content-Type` the other with invalid URI `/}}`. 

First produces:

```html
<html>
<head>
<meta http-equiv="Content-Type" content="text/html;charset=ISO-8859-1"/>
<title>Error 400 Bad Request</title>
</head>
<body><h2>HTTP ERROR 400 Bad Request</h2>
<table>
<tr><th>URI:</th><td>/</td></tr>
<tr><th>STATUS:</th><td>400</td></tr>
<tr><th>MESSAGE:</th><td>Bad Request</td></tr>
<tr><th>SERVLET:</th><td>org.eclipse.jetty.ee10.servlet.ServletHandler$Default404Servlet-3d620a1</td></tr>
</table>

</body>
</html>
```

The sencond also shows stacktrace

```
<html>
<head>
<meta http-equiv="Content-Type" content="text/html;charset=ISO-8859-1"/>
<title>Error 500 jakarta.ws.rs.core.UriBuilderException: java.net.URISyntaxException: Illegal character in path at index 23: http://127.0.0.1:57208/}}</title>
</head>
<body><h2>HTTP ERROR 500 jakarta.ws.rs.core.UriBuilderException: java.net.URISyntaxException: Illegal character in path at index 23: http://127.0.0.1:57208/}}</h2>
<table>
<tr><th>URI:</th><td>/}}</td></tr>
<tr><th>STATUS:</th><td>500</td></tr>
<tr><th>MESSAGE:</th><td>jakarta.ws.rs.core.UriBuilderException: java.net.URISyntaxException: Illegal character in path at index 23: http://127.0.0.1:57208/}}</td></tr>
<tr><th>SERVLET:</th><td>org.eclipse.jetty.ee10.servlet.ServletHandler$Default404Servlet-6418e39e</td></tr>
<tr><th>CAUSED BY:</th><td>jakarta.ws.rs.core.UriBuilderException: java.net.URISyntaxException: Illegal character in path at index 23: http://127.0.0.1:57208/}}</td></tr>
<tr><th>CAUSED BY:</th><td>java.net.URISyntaxException: Illegal character in path at index 23: http://127.0.0.1:57208/}}</td></tr>
</table>
<h3>Caused by:</h3><pre>jakarta.ws.rs.core.UriBuilderException: java.net.URISyntaxException: Illegal character in path at index 23: http://127.0.0.1:57208/}}
	at org.glassfish.jersey.uri.internal.JerseyUriBuilder.createURI(JerseyUriBuilder.java:993)
    ...
```


## Observations

Works correctly with `spring-boot-starter-undertow`.

Using `spring.jersey.type=servlet` the second test does not show stack trace, but still:

```
<html>
<head>
<meta http-equiv="Content-Type" content="text/html;charset=ISO-8859-1"/>
<title>Error 400 Bad Request</title>
</head>
<body><h2>HTTP ERROR 400 Bad Request</h2>
<table>
<tr><th>URI:</th><td>/}}</td></tr>
<tr><th>STATUS:</th><td>400</td></tr>
<tr><th>MESSAGE:</th><td>Bad Request</td></tr>
<tr><th>SERVLET:</th><td>oleborup.sample.api.JerseyConfig</td></tr>
</table>

</body>
</html>
```

We have previously implemented custom Jetty error handler to not leak implementation details, but stopped working with upgrade to Spring Boot 3. Have not figured out how to make a custom error handler with Spring Boot 3.2 and Jetty 12, or if that could resolve the issue. Would prefer to only rely on standard Spring Boot properties.
