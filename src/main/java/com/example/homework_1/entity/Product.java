package com.example.homework_1.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/*
These annotations generates getters and setters and also method toString
 */
@Getter
@Setter
@ToString
public class Product {
    private int id;
    private String name;
    private double price;
}
