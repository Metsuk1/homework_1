package com.example.homework_1.entity;

import lombok.Builder;
import lombok.ToString;

/*
This annotation allows to create Objects using method call chains
 */
@Builder
@ToString
public class House {
    private String address;
    private int rooms;
    private double area;
    private int price;

    public static void main(String[]args){
        System.out.println("The house is:" + createHouse());
    }

    private static House createHouse(){
        House house = House.builder().address("Astana")
                .rooms(1)
                .area(15.5)
                .price(15000)
                .build();

        return house;
    }
}
