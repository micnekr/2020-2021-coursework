package com.example.cardgame;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CardgameApplication {

    public static void main(String[] args) {
        System.out.println("classpath");
        System.out.println(System.getProperty("java.class.path"));
        SpringApplication.run(CardgameApplication.class, args);
    }
}
