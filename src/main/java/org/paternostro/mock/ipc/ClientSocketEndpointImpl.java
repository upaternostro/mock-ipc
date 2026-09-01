package org.paternostro.mock.ipc;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

/**
 * {@link Endpoint} implementation backed by a real {@link Socket} for use in clients.
 * <p>
 * Copyright Ugo Paternostro 2026. Licensed under the EUPL-1.2 or later.
 */
public class ClientSocketEndpointImpl extends AbstractSocketEndpointImpl {
    private InetAddress inetAddr;
    private int         port;

    ClientSocketEndpointImpl() {
        super();

        this.inetAddr   = null;
        this.port       = 0;
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
     * {@inheritDoc}
     * <p>
     * Opens a new {@link Socket} to the address and port configured via
     * {@link #init(InetAddress, int)}.
     */
    @Override
    public void connect() throws IOException {
        socket = new Socket(inetAddr, port);
    }
}
