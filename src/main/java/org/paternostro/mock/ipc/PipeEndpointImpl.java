package org.paternostro.mock.ipc;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * PipeEndpointImpl
 * 
 * Endpoint implementation that uses piped streams
 * 
 * Copyright Ugo Paternostro 2026. Licensed under the EUPL-1.2 or later.
 */
public class PipeEndpointImpl implements Endpoint {
    private Channel writeChannel;
    private Channel readChannel;

    public PipeEndpointImpl() {
        this.writeChannel = null;
        this.readChannel = null;
    }

    public void init(Channel writeChannel, Channel readChannel) {
        this.writeChannel = writeChannel;
        this.readChannel = readChannel;
    }

    @Override
    public void connect() throws IOException {
        // Borns connected, do nothing here
    }

    @Override
    public OutputStream getOutputStream() {
        return writeChannel.getOutputStream();
    }

    @Override
    public InputStream getInputStream() {
        return readChannel.getInputStream();
    }

    @Override
    public boolean isConnected() {
        return true;
    }

    @Override
    public void close() throws IOException {
        writeChannel.closeOutputStream();
        readChannel.closeInputStream();
    }
}
