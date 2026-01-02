package org.pollub.branch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "org.pollub")
public class BranchServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(BranchServiceApplication.class, args);
    }
}
