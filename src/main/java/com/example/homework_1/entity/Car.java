package com.example.homework_1.entity;

import lombok.RequiredArgsConstructor;
import lombok.Setter;

/*
it will create constructor only with required arguments(final fields)
 */
@RequiredArgsConstructor
public class Car {
    private final String brand;
    private final int model;
    private int year;
    private String color;

//    public Car(String brand, int model) {
//        this.brand = brand;
//        this.model = model;
//    }

}
