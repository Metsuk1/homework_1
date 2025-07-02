package com.example.homework_1.entity;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Book {
    private String title;
    private String author;
    private int pages;
    private double price;

    /*
    it's annotation @AllArgsConstructor
    this annotation includes all fields in constructor
    public Book(String title, String author, int pages, double price) {
        this.title = title;
        this.author = author;
        this.pages = pages;
        this.price = price;
    }
     */
}
