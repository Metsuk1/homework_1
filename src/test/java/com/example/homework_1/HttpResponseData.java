package com.example.homework_1;

// Helper class to store response data
public class HttpResponseData {
    final int statusCode;
    final String body;

    //package constructor
    HttpResponseData(int statusCode, String body) {
        this.statusCode = statusCode;
        this.body = body;
    }
}
