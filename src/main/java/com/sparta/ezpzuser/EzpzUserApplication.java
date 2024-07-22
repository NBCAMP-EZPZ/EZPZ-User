package com.sparta.ezpzuser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(
        exclude = { SecurityAutoConfiguration.class }
)
public class EzpzUserApplication {

    public static void main(String[] args) {
        SpringApplication.run(EzpzUserApplication.class, args);
    }

}
