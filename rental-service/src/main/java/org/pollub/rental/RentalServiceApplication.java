package org.pollub.rental;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "org.pollub")
public class RentalServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(RentalServiceApplication.class, args);
    }
}
