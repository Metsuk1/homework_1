package com.example.homework_1;

import java.lang.reflect.Method;

public class HandlerMethod {
    final Object controller;
    final Method method;
    final String path;
    final String httpMethod;

    HandlerMethod(Object controller, Method method, String path, String httpMethod) {
        this.controller = controller;
        this.method = method;
        this.path = path;
        this.httpMethod = httpMethod;
    }

}
