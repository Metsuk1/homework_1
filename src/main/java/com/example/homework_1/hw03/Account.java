package com.example.homework_1.hw03;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Account {
    private Long balance;

    public Account(Long balance) {
       setBalance(balance);
    }
}
