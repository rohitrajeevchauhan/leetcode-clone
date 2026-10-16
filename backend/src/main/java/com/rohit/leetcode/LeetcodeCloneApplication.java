package com.rohit.leetcode;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.metamodel.EntityType;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class LeetcodeCloneApplication {

    public static void main(String[] args) {
        SpringApplication.run(LeetcodeCloneApplication.class, args);
    }

    @Bean
    CommandLineRunner checkEntities(EntityManagerFactory emf) {
        return args -> {
            System.out.println("===== ENTITIES =====");
            for (EntityType<?> entity : emf.getMetamodel().getEntities()) {
                System.out.println(entity.getName());
            }
        };
    }
}