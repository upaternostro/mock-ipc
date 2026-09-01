package org.paternostro.mock.ipc;

import java.io.IOException;
import java.net.Socket;

/**
 * {@link Endpoint} implementation backed by a real {@link Socket} for use in servers.
 * <p>
 * Copyright Ugo Paternostro 2026. Licensed under the EUPL-1.2 or later.
 */
public class ServerSocketEndpointImpl extends AbstractSocketEndpointImpl {
    ServerSocketEndpointImpl() {
        super();
    }
    
    /**
     * Wraps this endpoint around an already-connected socket.
     * <p>
     * Typically used for the server side of a connection, with a socket
     * obtained from {@link java.net.ServerSocket#accept()}.
     *
     * @param socket the already-connected socket to wrap
     */
    void init(Socket socket) {
        this.socket = socket;
    }

    /**
     * {@inheritDoc}
     * <p>
     * No-op method. Has no effect on an endpoint that was
     * initialized via {@link #init(Socket)} with an already-connected
     * socket.
     */
    @Override
    public void connect() throws IOException {
        // No-op, borns connected via `accept`
    }
}
