package com.example.homework_1;

import com.example.homework_1.annotations.*;
import jakarta.servlet.ServletOutputStream;
import lombok.SneakyThrows;

import java.io.*;
import java.lang.reflect.Method;
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
    private final Map<String, HandlerMethod> routeHandlers = new HashMap<>();


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
            String key = request.getMethod() + ":" + request.getPath();
            HandlerMethod handlerMethod = routeHandlers.get(key);

            if(handlerMethod != null) {
                Object result = handlerMethod.method.invoke(handlerMethod.controller);

                if(result instanceof String str){
                    return HttpResponse.ok(str.getBytes(),"text/plain");
                } else if (result instanceof byte[] bytes) {
                    return HttpResponse.ok(bytes,"application/octet-stream");
                } else if (result != null) {
                    String json = result.toString();
                    return HttpResponse.ok(json.getBytes(),"application/json");
                }else{
                    return HttpResponse.ok("".getBytes(), "text/plain");
                }
            }

            //if route not found -> 404
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

    public void registerController(Object controller) {
        Class<?> clas = controller.getClass();

        if (!clas.isAnnotationPresent(CustomRestController.class)) {
            System.out.println(clas.getName() + " is not a @CustomRestController");
            return;
        }

        for (Method method : clas.getDeclaredMethods()) {
            if (method.isAnnotationPresent(CustomGetMapping.class)) {
                CustomGetMapping mapping = method.getAnnotation(CustomGetMapping.class);
                String key = "GET:" + mapping.value();
                routeHandlers.put(key, new HandlerMethod(controller, method, mapping.value(), "GET"));
            }
            if (method.isAnnotationPresent(CustomPostMapping.class)) {
                CustomPostMapping mapping = method.getAnnotation(CustomPostMapping.class);
                String key = "POST:" + mapping.value();
                routeHandlers.put(key, new HandlerMethod(controller, method, mapping.value(), "POST"));
            }
            if(method.isAnnotationPresent(CustomPutMapping.class)) {
                CustomPutMapping mapping = method.getAnnotation(CustomPutMapping.class);
                String key = "PUT:" + mapping.value();
                routeHandlers.put(key, new HandlerMethod(controller, method, mapping.value(), "PUT"));
            }
            if(method.isAnnotationPresent(CustomPatchMapping.class)) {
                CustomPatchMapping mapping = method.getAnnotation(CustomPatchMapping.class);
                String key = "PATCH:" + mapping.value();
                routeHandlers.put(key, new HandlerMethod(controller, method, mapping.value(), "PATCH"));
            }
            if(method.isAnnotationPresent(CustomDeleteMapping.class)) {
                CustomDeleteMapping mapping = method.getAnnotation(CustomDeleteMapping.class);
                String key = "DELETE:" + mapping.value();
                routeHandlers.put(key, new HandlerMethod(controller, method, mapping.value(), "DELETE"));
            }
        }
    }

    @SneakyThrows
    public static void main(String[] args) {
        CustomWebServer virtualServer = new CustomWebServer(8080,50,true);
        CustomWebServer platformServer = new CustomWebServer(8081,50,false);

        Method method = CustomWebServer.class.getMethod("registerController",Object.class);
        method.invoke(virtualServer,new Object());


        try{
            virtualServer.start();
            platformServer.start();

            System.out.println("CustomWebServer started");
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


