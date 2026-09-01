package org.paternostro.mock.ipc;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Wraps an IPC connection (i.e. a socket or something like that).
 * <p>
 * An {@code Endpoint} abstracts a bidirectional communication channel so that
 * client/server code can be written once and exercised both against a real
 * {@link java.net.Socket} (via {@link AbstractSocketEndpointImpl} and derived classes {@link ClientSocketEndpointImpl} and {@link ServerSocketEndpointImpl}) and against an
 * in-memory pipe (via {@link PipeEndpointImpl}) during unit tests.
 * <p>
 * Instances are normally obtained through {@link EndpointFactory} rather than
 * constructed directly.
 * <p>
 * Copyright Ugo Paternostro 2026. Licensed under the EUPL-1.2 or later.
 *
 * @see EndpointFactory
 * @see AbstractSocketEndpointImpl
 * @see ClientSocketEndpointImpl
 * @see ServerSocketEndpointImpl
 * @see PipeEndpointImpl
 */
public interface Endpoint {

    /**
     * Tells whether this endpoint is currently connected.
     *
     * @return {@code true} if the endpoint is connected and its streams can
     *         be used, {@code false} otherwise
     */
    boolean isConnected();

    /**
     * Establishes the underlying connection, if it is not already
     * established.
     * <p>
     * Endpoints that are connected by construction (such as
     * {@link PipeEndpointImpl}) may implement this as a no-op.
     *
     * @throws IOException if the connection cannot be established
     */
    void connect() throws IOException;

    /**
     * Returns the stream used to write data to the other end of the
     * connection.
     *
     * @return the output stream for this endpoint
     * @throws IOException if the stream cannot be obtained
     */
    OutputStream getOutputStream() throws IOException;

    /**
     * Returns the stream used to read data coming from the other end of the
     * connection.
     *
     * @return the input stream for this endpoint
     * @throws IOException if the stream cannot be obtained
     */
    InputStream getInputStream() throws IOException;

    /**
     * Closes the endpoint and releases any resource associated with it.
     *
     * @throws IOException if an I/O error occurs while closing the endpoint
     */
    void close() throws IOException;
}