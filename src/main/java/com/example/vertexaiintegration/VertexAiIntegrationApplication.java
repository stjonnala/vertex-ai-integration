package com.example.vertexaiintegration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class VertexAiIntegrationApplication {

    public static void main(String[] args) {
        SpringApplication.run(VertexAiIntegrationApplication.class, args);
    }
}
