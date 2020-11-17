package com.example.cardgame;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

@SpringBootApplication
@RestController
public class CardgameApplication {

    public static void main(String[] args) {
        SpringApplication.run(CardgameApplication.class, args);

        String password = "hi";

        String hash = Controller.getInstance().hashPassword(password);

        System.out.println(hash);
        password = "hello";

        hash = Controller.getInstance().hashPassword(password);

        System.out.println(hash);
        password = "test";

        hash = Controller.getInstance().hashPassword(password);

        System.out.println(hash);
        try {
            System.out.println(Controller.getInstance().checkPassword("hi", hash));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/hello")
    public String sayHello(@RequestParam(value = "myName", defaultValue = "World") String name) {
        return String.format("Hello %s!", name);
    }
}
