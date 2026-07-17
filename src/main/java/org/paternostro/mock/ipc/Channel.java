package org.paternostro.mock.ipc;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

/**
 * Channel
 * 
 * Connects a producer and a consumer via a (piped) stream.
 * 
 * Copyright Ugo Paternostro 2026. Licensed under the EUPL-1.2 or later.
 */
public class Channel {
    private final PipedOutputStream output;
    private final PipedInputStream  input;

    public Channel() throws IOException {
        this.output = new PipedOutputStream();
        this.input = new PipedInputStream(output); // Connect the streams
    }

    public OutputStream getOutputStream() {
        return output;
    }

    public InputStream getInputStream() {
        return input;
    }

    public void closeOutputStream() throws IOException {
        // do nothing here
    }

    public void closeInputStream() throws IOException {
        // do nothing here
    }
}
