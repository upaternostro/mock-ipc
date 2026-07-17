package org.paternostro.mock.ipc;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Endpoint
 * 
 * Wraps a IPC connection (i.e. a socket or something like that)
 * 
 * Copyright Ugo Paternostro 2026. Licensed under the EUPL-1.2 or later.
 */
public interface Endpoint {
    boolean isConnected();
    void connect() throws IOException;
    OutputStream getOutputStream() throws IOException;
    InputStream getInputStream() throws IOException;
    void close() throws IOException;
}