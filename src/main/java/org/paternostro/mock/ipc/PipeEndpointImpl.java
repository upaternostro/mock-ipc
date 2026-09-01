package org.paternostro.mock.ipc;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * {@link Endpoint} implementation that uses in-memory piped streams instead
 * of a real socket.
 * <p>
 * This is the implementation meant for unit testing: two cooperating
 * {@code PipeEndpointImpl} instances (typically representing a "client" and
 * a "server") are wired together by sharing a pair of {@link Channel}s with
 * write/read roles swapped, so that data written by one is read by the
 * other. Instances are normally created via
 * {@link EndpointFactory#getPipeEndpoint(Channel, Channel)} and are already
 * connected as soon as they are initialized.
 * <p>
 * Copyright Ugo Paternostro 2026. Licensed under the EUPL-1.2 or later.
 *
 * @see Channel
 * @see EndpointFactory#getPipeEndpoint(Channel, Channel)
 */
public class PipeEndpointImpl implements Endpoint {
    private Channel writeChannel;
    private Channel readChannel;

    /**
     * Creates an uninitialized pipe endpoint.
     * <p>
     * The endpoint is unusable until {@link #init(Channel, Channel)} is
     * called; instances are normally obtained already initialized through
     * {@link EndpointFactory}.
     */
    PipeEndpointImpl() {
        this.writeChannel = null;
        this.readChannel = null;
    }

    /**
     * Wires this endpoint to the given channels.
     *
     * @param writeChannel the channel this endpoint writes outgoing data to
     * @param readChannel the channel this endpoint reads incoming data from
     */
    void init(Channel writeChannel, Channel readChannel) {
        this.writeChannel = writeChannel;
        this.readChannel = readChannel;
    }

    /**
     * {@inheritDoc}
     * <p>
     * A pipe endpoint is connected as soon as it is initialized, so this
     * method is a no-op.
     */
    @Override
    public void connect() throws IOException {
        // Borns connected, do nothing here
    }

    /**
     * {@inheritDoc}
     *
     * @return the output stream of the write channel
     */
    @Override
    public OutputStream getOutputStream() {
        return writeChannel.getOutputStream();
    }

    /**
     * {@inheritDoc}
     *
     * @return the input stream of the read channel
     */
    @Override
    public InputStream getInputStream() {
        return readChannel.getInputStream();
    }

    /**
     * {@inheritDoc}
     *
     * @return always {@code true}, since a pipe endpoint is connected as
     *         soon as it is initialized
     */
    @Override
    public boolean isConnected() {
        return true;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Delegates to {@link Channel#closeOutputStream()} and
     * {@link Channel#closeInputStream()} on the write and read channels
     * respectively.
     */
    @Override
    public void close() throws IOException {
        writeChannel.closeOutputStream();
        readChannel.closeInputStream();
    }
}
