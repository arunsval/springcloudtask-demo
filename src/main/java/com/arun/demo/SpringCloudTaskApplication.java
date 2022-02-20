package com.arun.demo;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@RequiredArgsConstructor
public class SpringCloudTaskApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringCloudTaskApplication.class,args);
    }
}
