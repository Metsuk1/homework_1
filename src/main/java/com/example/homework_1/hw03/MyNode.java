package com.example.homework_1.hw03;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MyNode<T> {
    private T data;
    private MyNode<T> next;
    private MyNode<T> prev;

    public MyNode(T data) {
        setData(data);
    }
}
