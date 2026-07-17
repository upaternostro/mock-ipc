package org.paternostro.mock.ipc;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

/**
 * TestClient
 * 
 * Simple client
 * 
 * Copyright Ugo Paternostro 2026. Licensed under the EUPL-1.2 or later.
 */
public class TestClient {
    private Endpoint endpoint;

    public TestClient(Endpoint endpoint) {
        this.endpoint = endpoint;
    }

    public void run() throws IOException {
        InputStream is = endpoint.getInputStream();
        OutputStream os = endpoint.getOutputStream();
        PrintWriter pw = new PrintWriter(os);
        StringBuilder textBuilder = new StringBuilder();
        String originalString = "Hello, world!";

        pw.write(originalString);
        pw.close();

        try (Reader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            int c = 0;

            while ((c = reader.read()) != -1) {
                textBuilder.append((char) c);
            }
        }

        assertEquals(originalString, textBuilder.toString());
    }
}
