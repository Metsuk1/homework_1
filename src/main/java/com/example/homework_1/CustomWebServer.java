package com.example.homework_1;

import lombok.SneakyThrows;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class CustomWebServer {
    private final int port;
    private final CustomExecutorService executor;
    private ServerSocket serverSocket;
    private volatile boolean running = false;
    private long startTime;
    private long requestCount = 0;
    private boolean keepAlive = true;

    public CustomWebServer(int port, int threadPoolSize, boolean useVirtualThreads) {
        this.port = port;
        this.executor = new CustomExecutorService(threadPoolSize, useVirtualThreads);
    }

    public void start() throws IOException {
        serverSocket = new ServerSocket(port);
        running = true;
        executor.execute(this::serverLoop);
    }

    private void serverLoop() {
        while(running && !serverSocket.isClosed()) {
            try{
                Socket clientSocket = serverSocket.accept();
                executor.execute(() -> handleClient(clientSocket));
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @SneakyThrows
    private void handleClient(Socket clientSocket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             OutputStream out = clientSocket.getOutputStream()) {

            keepAlive = true;
            clientSocket.setSoTimeout(10000);

            while (keepAlive) {
                HttpRequest request = parseHttpRequest(in);
                if (request == null) break;

                HttpResponse response = responseRequest(request);
                if (response == null) break;

                String connectionHeader = request.getHeaders().getOrDefault("connection", "").toLowerCase();
                if ("close".equals(connectionHeader) || response.statusCode >= 400) {
                    keepAlive = false;
                }
                sendResponse(out, response, keepAlive);

            }

        } catch (Exception e){
            e.printStackTrace();
        } finally {
            clientSocket.close();
        }
    }

    private HttpRequest parseHttpRequest(BufferedReader in) throws IOException {
        String requestLine = in.readLine();
        if (requestLine == null || requestLine.isEmpty()) return null;


        String[] parts = requestLine.split(" ");
        String method = parts[0], path = parts[1];

        Map<String, String> headers = new HashMap<>();
        String line;
        while ((line = in.readLine()) != null && !line.trim().isEmpty()) {
            int idx = line.indexOf(":");
            if (idx > 0) {
                headers.put(line.substring(0, idx).trim().toLowerCase(), line.substring(idx + 1).trim());
            }
        }

        // Читаем body для post запросов
        String body = "";
        if ("POST".equals(method) && headers.containsKey("content-length")) {
            int length = Integer.parseInt(headers.get("content-length"));
            char[] buf = new char[length];
            in.read(buf, 0, length);
            body = new String(buf);
        }

        return new HttpRequest(method, path, headers, body);
    }

    private HttpResponse responseRequest(HttpRequest request) {
        requestCount++;

        try {
            //home page
            if("GET".equals(request.getMethod()) && "/".equals(request.getPath())) {
                Path file = Path.of("./static/index.html");
                if(Files.exists(file)){
                    return HttpResponse.ok(Files.readAllBytes(file),"text/html");
                }else{
                    return HttpResponse.notFound();
                }
            }

            // static files
            if (request.getPath().startsWith("/static/")) {
                Path file = Path.of("." + request.getPath());
                if (Files.exists(file)) {
                    String mime = guessMimeType(file);
                    return HttpResponse.ok(Files.readAllBytes(file), mime);
                } else {
                    return HttpResponse.notFound();
                }
            }

            // /api/time
            if ("GET".equals(request.getMethod()) && "/api/time".equals(request.getPath())) {
                String json = "{\"time\":\"" + Instant.now().toString() + "\"}";
                return HttpResponse.ok(json.getBytes(), "application/json");
            }


            // /api/stats
            if ("GET".equals(request.getMethod()) && "/api/stats".equals(request.getPath())) {
                long uptime = Duration.between(Instant.ofEpochMilli(startTime), Instant.now()).toSeconds();
                String json = String.format("{\"requests\":%d,\"uptime_sec\":%d}", requestCount, uptime);
                return HttpResponse.ok(json.getBytes(), "application/json");
            }


            // /api/echo
            if ("POST".equals(request.getMethod()) && "/api/echo".equals(request.getPath())) {
                return HttpResponse.ok(request.getBody().getBytes(), "application/json");
            }

            return HttpResponse.notFound();
        }catch (Exception e) {
            e.printStackTrace();
            return HttpResponse.serverError();
        }
    }

    @SneakyThrows
    private void sendResponse(OutputStream out, HttpResponse response, boolean keepAlive) {
        PrintWriter writer = new PrintWriter(out);
        writer.printf("HTTP/1.1 %d %s\r\n", response.statusCode, response.statusText);
        writer.printf("Content-Type: %s\r\n", response.contentType);
        writer.printf("Content-Length: %d\r\n", response.body.length);
        writer.printf("Connection: %s\r\n", keepAlive ? "keep-alive" : "close");
        writer.println();
        writer.flush();
        out.write(response.body);
        out.flush();
    }

    @SneakyThrows
    private String guessMimeType(Path file) {
        String mime = Files.probeContentType(file);
        return mime != null ? mime : "application/octet-stream";
    }

    public void stop() {
        running = false;

        try{
            if(serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            executor.shutdown();
        }
    }

    public static void main(String[] args) {
        CustomWebServer virtualServer = new CustomWebServer(8080,50,true);
        CustomWebServer platformServer = new CustomWebServer(8081,50,false);

        try{
            virtualServer.start();
            platformServer.start();

            System.out.println("com.example.homework_1.CustomWebServer started");
            System.out.println("Virtual:  http://localhost:8080");
            System.out.println("Platform: http://localhost:8081");

            Thread.sleep(60_000);

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            virtualServer.stop();
            platformServer.stop();
        }
    }
}


