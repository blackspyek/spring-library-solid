package org.pollub.library;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class LibraryApplication {
    private final Environment environment;

    public LibraryApplication(Environment environment) {
        this.environment = environment;
    }

    @PostConstruct
    public void printDatasourceUrl() {
        System.out.println("Datasource URL: " + environment.getProperty("spring.datasource.url"));
    }

    public static void main(String[] args) {
        SpringApplication.run(LibraryApplication.class, args);
    }

}
