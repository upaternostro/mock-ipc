# mock-ipc

A small library to help unit testing TCP servers and clients.

## Introduction

This library abstracts `java.net.Socket` to make it possible to unit test servers and/or clients.

## Requirements

- Java 8 or later
- Maven

## License

This software is licensed under EUPL v1.2 or above. See LICENSE.md for full license text.

## Usage

Write your server (client) using an `Endpoint` instead of a `Socket`. When creating the server (client), choose the right implementation using `EndpointFactory`.

At the moment, just two kinds of `Endpoint`s are available:

1. Socket based `Endpoint`;
2. Pipe based `Endpoint`.

The first one should be used for production, while the second one for unit testing.

In this way, the code you write and use in production and in unit tests is exactly the same.

To allocate an `Endpoint`, use `EndpointFactory`:

```java
Endpoint serverEndpoint = EndpointFactory.getFactory().getSocketEndpoint(socket);
```

for a socket based endpoint, and

```java
Channel c2s = new Channel();
Channel s2c = new Channel();
Endpoint serverEndpoint = EndpointFactory.getFactory().getPipeEndpoint(s2c, c2s);
Endpoint clientEndpoint = EndpointFactory.getFactory().getPipeEndpoint(c2s, s2c);
```

for pipe based ones.

### Two flavors of socket endpoint

`EndpointFactory` offers two overloads of `getSocketEndpoint`, one for each side of a connection:

- `getSocketEndpoint(Socket socket)` — wraps an **already-connected** socket, typically the one returned by `ServerSocket.accept()`. Use this on the **server** side; the resulting endpoint is immediately usable, no need to call `connect()`.
- `getSocketEndpoint(InetAddress inetAddr, int port)` — configures an endpoint with a target address and port, but does **not** connect yet. Use this on the **client** side, then call `endpoint.connect()` before using its streams:

  ```java
  Endpoint clientEndpoint = EndpointFactory.getFactory().getSocketEndpoint(inetAddr, port);
  clientEndpoint.connect();
  ```

In the server code, use

```java
InputStream inputStream = serverEndpoint.getInputStream();
OutputStream outputStream = serverEndpoint.getOutputStream();
```

### Overriding the implementations

`EndpointFactory` resolves the concrete `Endpoint` classes (and its own implementation class) by name, and looks them up in an optional `EndpointFactory.properties` file placed next to `EndpointFactory` on the classpath. If the file, or a given property, is missing, the built-in defaults are used. The available keys are:

| Property | Overrides |
| --- | --- |
| `org.paternostro.mock.ipc.EndpointFactory.class` | the `EndpointFactory` implementation itself |
| `org.paternostro.mock.ipc.PipeEndpoint.class` | the pipe based `Endpoint` implementation |
| `org.paternostro.mock.ipc.SocketEndpoint.class` | the socket based `Endpoint` implementation |

## Example

See `TestServer` and/or `TestClient` for sample client/server code, and `TestMockIPC` for how they are wired together and exercised with pipe based endpoints in a JUnit test.

## Documentation

The public API is documented with Javadoc. Generate it locally with:

```
mvn javadoc:javadoc
```

The generated HTML is written to `target/site/apidocs`.