package com.example.homework_1.handlers;

import com.example.homework_1.http.HttpRequest;
import com.example.homework_1.http.HttpResponse;
import com.example.homework_1.annotations.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class RequestHandler {
    private final Map<String, HandlerMethod> routeHandlers;
    private final ObjectMapper objectMapper;
    private final String staticResourcesPath = "src/main/resources/static";

    public RequestHandler(Map<String, HandlerMethod> routeHandlers, ObjectMapper objectMapper) {
        this.routeHandlers = routeHandlers;
        this.objectMapper = objectMapper;
    }

    public HttpRequest parseHttpRequest(BufferedReader in) throws IOException {
        String requestLine = in.readLine();
        if (requestLine == null || requestLine.isEmpty()) return null;

        String[] parts = requestLine.split(" ");
        if (parts.length < 2) return null; // Malformed request
        String method = parts[0].toUpperCase();
        String path = parts[1];

        Map<String, String> headers = new HashMap<>();
        String line;
        while ((line = in.readLine()) != null && !line.trim().isEmpty()) {
            int idx = line.indexOf(":");
            if (idx > 0) {
                headers.put(line.substring(0, idx).trim().toLowerCase(), line.substring(idx + 1).trim());
            }
        }

        String body = "";
        if ("POST".equals(method) && headers.containsKey("content-length")) {
            int length;
            try {
                length = Integer.parseInt(headers.get("content-length"));
            } catch (NumberFormatException e) {
                return null; // Invalid content-length
            }
            char[] buf = new char[length];
            int read = in.read(buf, 0, length);
            if (read == length) {
                body = new String(buf);
            }
        }

        return new HttpRequest(method, path, headers, body);
    }

    @SneakyThrows
    public HttpResponse responseRequest(HttpRequest request) {
        try {
            String rawPath = request.getPath();
            String pathOnly = rawPath.contains("?") ? rawPath.substring(0, rawPath.indexOf("?")) : rawPath;
            String key = request.getMethod() + ":" + pathOnly;
            HandlerMethod handlerMethod = routeHandlers.get(key);

            // Handle static resources
            if (handlerMethod == null && request.getMethod().equals("GET")) {
                String resourcePath = pathOnly.equals("/") ? "/index.html" : pathOnly;
                Path filePath = Path.of(staticResourcesPath, resourcePath);
                if (Files.exists(filePath)) {
                    byte[] content = Files.readAllBytes(filePath);
                    String mimeType = Files.probeContentType(filePath);
                    return HttpResponse.ok(content, mimeType != null ? mimeType : "text/html");
                }
                return HttpResponse.notFound();
            }

            Method method = handlerMethod.method;
            Object[] args = new Object[method.getParameterCount()];
            Map<String, String> queryParams = parseQueryParams(request.getPath());
            Map<String, String> pathVariables = matchPathVariables(handlerMethod.path, request.getPath());

            var params = method.getParameters();
            for (int i = 0; i < params.length; i++) {
                if (params[i].isAnnotationPresent(CustomRequestBody.class)) {
                    if (request.getBody() != null && !request.getBody().isEmpty()) {
                        Class<?> paramType = params[i].getType();
                        if (paramType == String.class) {
                            args[i] = request.getBody();
                        } else {
                            args[i] = objectMapper.readValue(request.getBody(), paramType);
                        }
                    } else {
                        args[i] = null;
                    }
                } else if (params[i].isAnnotationPresent(CustomRequestParam.class)) {
                    String name = params[i].getAnnotation(CustomRequestParam.class).value();
                    args[i] = queryParams.get(name);
                    if (args[i] != null && params[i].getType() != String.class) {
                        args[i] = convertToType(args[i].toString(), params[i].getType());
                    }
                } else if (params[i].isAnnotationPresent(CustomPathVariable.class)) {
                    String name = params[i].getAnnotation(CustomPathVariable.class).value();
                    args[i] = pathVariables.get(name);
                    if (args[i] != null && params[i].getType() != String.class) {
                        args[i] = convertToType(args[i].toString(), params[i].getType());
                    }
                }
            }

            Object result = method.invoke(handlerMethod.controller, args);

            if (result == null) {
                return HttpResponse.ok("".getBytes(), "text/plain");
            } else if (result instanceof String str) {
                return HttpResponse.ok(str.getBytes(), "text/plain");
            } else if (result instanceof byte[] bytes) {
                return HttpResponse.ok(bytes, "application/octet-stream");
            } else {
                String json = objectMapper.writeValueAsString(result);
                return HttpResponse.ok(json.getBytes(), "application/json");
            }

        } catch (IllegalArgumentException e) {
            return HttpResponse.badRequest("Invalid parameter types: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return HttpResponse.serverError();
        }
    }

    private Object convertToType(String value, Class<?> targetType) {
        if (value == null) return null;
        if (targetType == Integer.class || targetType == int.class) {
            return Integer.parseInt(value);
        } else if (targetType == Long.class || targetType == long.class) {
            return Long.parseLong(value);
        } else if (targetType == Double.class || targetType == double.class) {
            return Double.parseDouble(value);
        } else if (targetType == Boolean.class || targetType == boolean.class) {
            return Boolean.parseBoolean(value);
        }
        return value; // Default to String
    }

    private Map<String, String> parseQueryParams(String path) {
        Map<String, String> params = new HashMap<>();
        if (path.contains("?")) {
            String query = path.substring(path.indexOf("?") + 1);
            for (String pair : query.split("&")) {
                String[] kv = pair.split("=");
                if (kv.length == 2) {
                    params.put(kv[0], kv[1]);
                }
            }
        }
        return params;
    }

    private Map<String, String> matchPathVariables(String mappingPath, String requestPath) {
        Map<String, String> variables = new HashMap<>();
        String[] mappingParts = mappingPath.split("/");
        String[] requestParts = requestPath.contains("?")
                ? requestPath.substring(0, requestPath.indexOf("?")).split("/")
                : requestPath.split("/");

        if (mappingParts.length != requestParts.length) {
            return variables;
        }

        for (int i = 0; i < mappingParts.length; i++) {
            if (mappingParts[i].startsWith("{") && mappingParts[i].endsWith("}")) {
                String varName = mappingParts[i].substring(1, mappingParts[i].length() - 1);
                variables.put(varName, requestParts[i]);
            }
        }

        return variables;
    }
}
