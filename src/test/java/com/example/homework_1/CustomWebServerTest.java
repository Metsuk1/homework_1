package com.example.homework_1;


import lombok.SneakyThrows;
import org.junit.jupiter.api.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class CustomWebServerTest {
    private static CustomWebServer server;
    private static final int PORT = 8080;

    @BeforeEach
    void startServer() throws InterruptedException {
        server = new CustomWebServer(PORT,50,true);
        new Thread(() -> {
            try {
                server.start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
        Thread.sleep(5000);
    }

    @AfterEach
    void stopServer() {
        server.stop();
    }

    @Test
    void testIndexPageServed()throws Exception{
        URL url = new URL("http://localhost:" + PORT + "/");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        assertEquals(200, conn.getResponseCode());

        String body = new BufferedReader(new InputStreamReader(conn.getInputStream()))
                .lines()
                .reduce("", (acc, line) -> acc + line + "\n");

        assertTrue(body.contains("<html"));
    }

    @Test
    void testNotFound()throws Exception{
        URL url = new URL("http://localhost:" + PORT + "/notfound");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        assertEquals(404, connection.getResponseCode());
    }
}
