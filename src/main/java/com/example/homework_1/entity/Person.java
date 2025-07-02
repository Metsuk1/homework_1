package com.example.homework_1.entity;


import lombok.Data;

/*
Annotation @Data in Lombok includes methods like:
toString(),hashCode(),getters and setters,equals,etc..
Generates boilerplate code
 */
@Data
public class Person {
    private String name;
    private int age;
    private String email;
}
