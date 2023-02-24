package com.ufutao.account.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lutong
 */
@SpringBootApplication
@RestController
public class AppAdmin {
    public static void main(String[] args) {
        SpringApplication.run(AppAdmin.class, args);
    }
}
