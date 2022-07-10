package com.adg.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class AdgApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdgApiApplication.class, args);
    }

}
