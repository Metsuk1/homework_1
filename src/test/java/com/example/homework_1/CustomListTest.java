package com.example.homework_1;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CustomListTest {
    private CustomList<?> list;

    @BeforeEach
    void setUp() {
        list = new CustomList<>();
    }


    @AfterEach
    void tearDown() {
        list.clear();
    }

}
