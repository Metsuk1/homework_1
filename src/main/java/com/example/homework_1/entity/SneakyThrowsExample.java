package com.example.homework_1.entity;

import lombok.SneakyThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SneakyThrowsExample {

    @SneakyThrows
    public void readFile(){
        Files.readAllLines(Paths.get("file.txt")); // IOException НЕ обрабатывается явно
    }

    public void readFileWithoutSneakyThrows() throws IOException {
        Files.readAllLines(Paths.get("file.txt"));
    }
}
