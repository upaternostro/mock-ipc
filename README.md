# mock-ipc

A small library to help unit testing TCP servers and clients.

## Introduction

This library abstracts java.net.Socket to make it possible unit testing servers and/or clients.

## License

This software is licensed under EUPL v1.2 or above. See LICENSE.md for full license text.

## Usage

Write your server (client) using an Endpoint instead of a Socket. When using the server (client), choose the right implementation using EndpointFactory.

At the moment, just two kinds of Enpoints are available:

1. Socket based Endpoint;
2. Pipe based Endpoint.

The first one should be used for production, while the second one for unit testing.

In ths way, the code you write and use in production and unit testing is exactly the same.

To allocate and Endpoint, use EndpointFactory:

    Endpoint serverEndpoint = EndpointFactory.getFactory().getSocketEndpoint(socket);

for socket base endpoint and

    Channel c2s = new Channel();
    Channel s2c = new Channel();
    Endpoint serverEndpoint = EndpointFactory.getFactory().getPipeEndpoint(s2c, c2s);
    Endpoint clientEndpoint = EndpointFactory.getFactory().getPipeEndpoint(c2s, s2c);

for pipe based ones.

In the server code, use

    InputStream inputStream = serverEndpoint.getInputStream();
    OutputStream outputStream = serverEndpoint.getOutputStream();

## Example

See TestServer and/or TestClient
