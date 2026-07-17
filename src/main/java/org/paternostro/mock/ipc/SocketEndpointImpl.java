package org.paternostro.mock.ipc;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

/**
 * SocketEndpointImpl
 * 
 * Endpoint implementation that uses a socket
 * 
 * Copyright Ugo Paternostro 2026. Licensed under the EUPL-1.2 or later.
 */
public class SocketEndpointImpl implements Endpoint {
    private InetAddress inetAddr;
    private int         port;

    private Socket      socket;

    public SocketEndpointImpl() {
        this.inetAddr   = null;
        this.port       = 0;
        this.socket     = null;
    }

    public void init(InetAddress inetAddr, int port) {
        this.inetAddr   = inetAddr;
        this.port       = port;
    }

    public void init(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void connect() throws IOException {
        socket = new Socket(inetAddr, port);
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return socket.getOutputStream();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return socket.getInputStream();
    }

    @Override
    public boolean isConnected() {
        return socket.isConnected();
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }
}
