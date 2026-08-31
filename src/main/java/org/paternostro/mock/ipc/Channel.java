package org.paternostro.mock.ipc;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

/**
 * Connects a producer and a consumer via a (piped) stream.
 * <p>
 * A {@code Channel} wraps a connected {@link PipedOutputStream}/
 * {@link PipedInputStream} pair, representing one direction of communication
 * between two {@link PipeEndpointImpl} instances. A full duplex connection
 * between a "client" and a "server" is built from two {@code Channel}s: one
 * for each direction.
 * <p>
 * Copyright Ugo Paternostro 2026. Licensed under the EUPL-1.2 or later.
 *
 * @see PipeEndpointImpl
 * @see EndpointFactory#getPipeEndpoint(Channel, Channel)
 */
public class Channel {
    private final PipedOutputStream output;
    private final PipedInputStream  input;

    /**
     * Creates a new channel, allocating and connecting its underlying piped
     * streams.
     *
     * @throws IOException if the piped streams cannot be connected
     */
    public Channel() throws IOException {
        this.output = new PipedOutputStream();
        this.input = new PipedInputStream(output); // Connect the streams
    }

    /**
     * Returns the stream used to write data into this channel.
     *
     * @return the output stream of this channel
     */
    public OutputStream getOutputStream() {
        return output;
    }

    /**
     * Returns the stream used to read data out of this channel.
     *
     * @return the input stream of this channel
     */
    public InputStream getInputStream() {
        return input;
    }

    /**
     * Closes the write side of this channel.
     * <p>
     * Currently a no-op: piped streams are left open so both ends of the
     * pipe can keep being exercised independently by test code.
     *
     * @throws IOException never thrown by the current implementation, but
     *         declared for interface consistency and future changes
     */
    public void closeOutputStream() throws IOException {
        // do nothing here
    }

    /**
     * Closes the read side of this channel.
     * <p>
     * Currently a no-op: piped streams are left open so both ends of the
     * pipe can keep being exercised independently by test code.
     *
     * @throws IOException never thrown by the current implementation, but
     *         declared for interface consistency and future changes
     */
    public void closeInputStream() throws IOException {
        // do nothing here
    }
}
