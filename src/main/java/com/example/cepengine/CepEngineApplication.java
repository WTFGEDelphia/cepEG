package com.example.cepengine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableKafka
@SpringBootApplication
@EnableTransactionManagement
public class CepEngineApplication {
    public static void main(String[] args) {
        SpringApplication.run(CepEngineApplication.class, args);
    }
}
