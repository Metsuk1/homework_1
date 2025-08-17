package com.example.homework_1;

import com.example.homework_1.hw03.CustomList;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

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
