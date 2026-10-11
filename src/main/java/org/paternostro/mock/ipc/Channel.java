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
     * The reading side sees the end of the stream once it has consumed the
     * data already written.
     *
     * @throws IOException if an I/O error occurs while closing the stream
     */
    public void closeOutputStream() throws IOException {
        output.close();
    }

    /**
     * Closes the read side of this channel.
     * <p>
     * Any further attempt to write on the other side fails.
     *
     * @throws IOException if an I/O error occurs while closing the stream
     */
    public void closeInputStream() throws IOException {
        input.close();
    }
}
