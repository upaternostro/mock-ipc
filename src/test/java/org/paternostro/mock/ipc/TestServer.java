package org.paternostro.mock.ipc;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * TestServer
 * 
 * Simple echo server
 * 
 * Copyright Ugo Paternostro 2026. Licensed under the EUPL-1.2 or later.
 */
public class TestServer extends Thread {
    private Endpoint endpoint;

    public TestServer(Endpoint endpoint) {
        this.endpoint = endpoint;
    }

    @Override
    public void run() {
        InputStream is = null;
        OutputStream os = null;
        int c;

        try {
            is = endpoint.getInputStream();
            os = endpoint.getOutputStream();

            while ((c = is.read()) != -1) {
                os .write(c);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (os != null) os.close();
            } catch (IOException e) {
            }

            try {
                if (is != null) is.close();
            } catch (IOException e) {
            }

            try {
                endpoint.close();
            } catch (IOException e) {
            }
        }
    }
}
