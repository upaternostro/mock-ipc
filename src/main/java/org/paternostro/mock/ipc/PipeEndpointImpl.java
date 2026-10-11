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
     * Must be called once; a closed endpoint is not meant to be reused.
     *
     * @param writeChannel the channel this endpoint writes outgoing data to
     * @param readChannel the channel this endpoint reads incoming data from
     * @throws IllegalArgumentException if either channel is null
     * @throws IllegalStateException if already initialized
     */
    synchronized void init(Channel writeChannel, Channel readChannel) {
        if (writeChannel == null) throw new IllegalArgumentException("Null write channel");
        if (readChannel == null) throw new IllegalArgumentException("Null read channel");
        if (this.writeChannel != null) throw new IllegalStateException("Already initialized");
        
        this.writeChannel = writeChannel;
        this.readChannel = readChannel;
    }

    /**
     * {@inheritDoc}
     * <p>
     * A pipe endpoint is connected as soon as it is initialized, so this
     * method is a no-op, unless the endpoint has already been closed: a
     * closed pipe cannot be reopened.
     *
     * @throws IOException if the endpoint is not open (never initialized, or closed)
     */
    @Override
    public synchronized void connect() throws IOException {
        // Borns connected, do nothing here. Just check if we have been closed
        if (writeChannel == null) throw new IOException("Endpoint not open");
    }

    /**
     * {@inheritDoc}
     *
     * @return the output stream of the write channel
     * @throws IOException if the endpoint is not open (never initialized, or closed)
     */
    @Override
    public synchronized OutputStream getOutputStream() throws IOException {
        if (writeChannel == null) throw new IOException("Endpoint not open");

        return writeChannel.getOutputStream();
    }

    /**
     * {@inheritDoc}
     *
     * @return the input stream of the read channel
     * @throws IOException if the endpoint is not open (never initialized, or closed)
     */
    @Override
    public synchronized InputStream getInputStream() throws IOException {
        if (readChannel == null) throw new IOException("Endpoint not open");

        return readChannel.getInputStream();
    }

    /**
     * {@inheritDoc}
     *
     * @return {@code true} from initialization until the endpoint is closed,
     *         since a pipe endpoint is connected as soon as it is initialized
     */
    @Override
    public synchronized boolean isConnected() {
        return writeChannel != null;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Delegates to {@link Channel#closeOutputStream()} and
     * {@link Channel#closeInputStream()} on the write and read channels
     * respectively, so the peer sees the end of the stream.
     */
    @Override
    public synchronized void close() throws IOException {
        if (writeChannel == null) return; // already closed, or not inited

        try {
            try {
                writeChannel.closeOutputStream();
            } finally {
                readChannel.closeInputStream();
            }
        } finally {
            writeChannel = null;
            readChannel = null;
        }
    }
}
