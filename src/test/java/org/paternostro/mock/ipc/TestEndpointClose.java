package org.paternostro.mock.ipc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import org.junit.Test;

/**
 * Checks the {@link java.io.Closeable} contract of {@link Endpoint}: closing
 * an endpoint that is already closed, or was never connected, has no effect,
 * and a closed endpoint is not connected any more.
 * <p>
 * Copyright Ugo Paternostro 2026. Licensed under the EUPL-1.2 or later.
 */
public class TestEndpointClose {
    private static final EndpointFactory FACTORY = EndpointFactory.getFactory();

    @Test
    public void neverConnectedClientSocketEndpoint() throws IOException {
        try (Endpoint endpoint = FACTORY.getSocketEndpoint(InetAddress.getLoopbackAddress(), 1)) {
            assertFalse(endpoint.isConnected());
            // close() at the end of the block must not fail
        }
    }

    @Test
    public void closeClientSocketEndpointTwice() throws IOException {
        Endpoint endpoint = FACTORY.getSocketEndpoint(InetAddress.getLoopbackAddress(), 1);

        endpoint.close();
        endpoint.close();
        assertFalse(endpoint.isConnected());
    }

    @Test
    public void connectedSocketEndpointsAreNotConnectedAfterClose() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(0, 1, InetAddress.getLoopbackAddress())) {
            Endpoint client = FACTORY.getSocketEndpoint(InetAddress.getLoopbackAddress(), serverSocket.getLocalPort());

            client.connect();

            Socket accepted = serverSocket.accept();
            Endpoint server = FACTORY.getSocketEndpoint(accepted);

            assertTrue(client.isConnected());
            assertTrue(server.isConnected());

            client.close();
            client.close();
            server.close();
            server.close();

            assertFalse(client.isConnected());
            assertFalse(server.isConnected());

            try {
                client.getInputStream();
                fail("A closed socket endpoint must not give its streams");
            } catch (IOException e) {
                // expected
            }
        }
    }

    @Test
    public void closePipeEndpointTwice() throws IOException {
        Endpoint endpoint = FACTORY.getPipeEndpoint(new Channel(), new Channel());

        assertTrue(endpoint.isConnected());

        endpoint.close();
        endpoint.close();

        assertFalse(endpoint.isConnected());
    }

    @Test
    public void closedPipeEndpointRefusesEverything() throws IOException {
        Endpoint endpoint = FACTORY.getPipeEndpoint(new Channel(), new Channel());

        endpoint.close();

        try {
            endpoint.getInputStream();
            fail("A closed pipe endpoint must not give its input stream");
        } catch (IOException e) {
            // expected
        }

        try {
            endpoint.getOutputStream();
            fail("A closed pipe endpoint must not give its output stream");
        } catch (IOException e) {
            // expected
        }

        try {
            endpoint.connect();
            fail("A closed pipe endpoint cannot be reconnected");
        } catch (IOException e) {
            // expected
        }
    }

    @Test
    public void closingPipeEndpointGivesEndOfStreamToPeer() throws IOException {
        Channel c2s = new Channel();
        Channel s2c = new Channel();
        Endpoint client = FACTORY.getPipeEndpoint(c2s, s2c);
        Endpoint server = FACTORY.getPipeEndpoint(s2c, c2s);

        client.getOutputStream().write(42);
        client.close();

        // Data already written is still delivered, then the end of the stream
        assertEquals(42, server.getInputStream().read());
        assertEquals(-1, server.getInputStream().read());
    }
}
