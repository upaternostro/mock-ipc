package org.paternostro.mock.ipc;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Abstract {@link Endpoint} implementation backed by a real {@link Socket}.
 * <p>
 * This is the implementation meant for production use. It can either wrap an
 * already-connected socket (e.g. one accepted by a {@link java.net.ServerSocket},
 * for the server side) or be configured with a target address and port and
 * open the connection itself when {@link #connect()} is called (for the
 * client side). Instances are normally obtained through
 * {@link EndpointFactory#getSocketEndpoint(InetAddress, int)} or
 * {@link EndpointFactory#getSocketEndpoint(Socket)}.
 * <p>
 * Copyright Ugo Paternostro 2026. Licensed under the EUPL-1.2 or later.
 *
 * @see EndpointFactory#getSocketEndpoint(InetAddress, int)
 * @see EndpointFactory#getSocketEndpoint(Socket)
 */
public abstract class AbstractSocketEndpointImpl implements Endpoint {
    protected Socket    socket;

    /**
     * Creates an uninitialized socket endpoint.
     * <p>
     * The endpoint is unusable until one of the {@code init} methods is
     * called; instances are normally obtained already initialized through
     * {@link EndpointFactory}.
     */
    AbstractSocketEndpointImpl() {
        this.socket     = null;
    }

    /**
     * {@inheritDoc}
     *
     * @return the underlying socket's output stream
     */
    @Override
    public OutputStream getOutputStream() throws IOException {
        return socket.getOutputStream();
    }

    /**
     * {@inheritDoc}
     *
     * @return the underlying socket's input stream
     */
    @Override
    public InputStream getInputStream() throws IOException {
        return socket.getInputStream();
    }

    /**
     * {@inheritDoc}
     *
     * @return the connection state of the underlying socket, as reported by
     *         {@link Socket#isConnected()}
     */
    @Override
    public boolean isConnected() {
        return socket.isConnected();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Closes the underlying socket.
     */
    @Override
    public void close() throws IOException {
        socket.close();
    }
}
