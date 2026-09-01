package org.paternostro.mock.ipc;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

/**
 * {@link Endpoint} implementation backed by a real {@link Socket}.
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
public class SocketEndpointImpl implements Endpoint {
    private InetAddress inetAddr;
    private int         port;

    private Socket      socket;

    /**
     * Creates an uninitialized socket endpoint.
     * <p>
     * The endpoint is unusable until one of the {@code init} methods is
     * called; instances are normally obtained already initialized through
     * {@link EndpointFactory}.
     */
    SocketEndpointImpl() {
        this.inetAddr   = null;
        this.port       = 0;
        this.socket     = null;
    }

    /**
     * Configures this endpoint to connect to the given address and port when
     * {@link #connect()} is called.
     * <p>
     * Typically used for the client side of a connection.
     *
     * @param inetAddr the address to connect to
     * @param port the port to connect to
     */
    void init(InetAddress inetAddr, int port) {
        this.inetAddr   = inetAddr;
        this.port       = port;
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
     * Opens a new {@link Socket} to the address and port configured via
     * {@link #init(InetAddress, int)}. Has no effect on an endpoint that was
     * initialized via {@link #init(Socket)} with an already-connected
     * socket, other than replacing it with a new connection.
     */
    @Override
    public void connect() throws IOException {
        socket = new Socket(inetAddr, port);
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
