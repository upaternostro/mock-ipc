package org.paternostro.mock.ipc;

import java.io.IOException;

import org.junit.Test;

/**
 * TestMockIPC
 * 
 * JUnit test
 * 
 * Copyright Ugo Paternostro 2026. Licensed under the EUPL-1.2 or later.
 */
public class TestMockIPC {
    @Test
    public void test() throws IOException {
        Channel c2s = new Channel();
        Channel s2c = new Channel();

        TestServer server = new TestServer(EndpointFactory.getFactory().getPipeEndpoint(s2c, c2s));
        server.start();

        TestClient client = new TestClient(EndpointFactory.getFactory().getPipeEndpoint(c2s, s2c));
        client.run();
    }
}
