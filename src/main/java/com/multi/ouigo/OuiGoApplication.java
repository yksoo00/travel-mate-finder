package com.multi.ouigo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class OuiGoApplication {

    public static void main(String[] args) {
        SpringApplication.run(OuiGoApplication.class, args);
    }

}
