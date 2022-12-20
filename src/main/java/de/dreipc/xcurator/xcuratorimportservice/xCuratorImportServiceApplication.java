package de.dreipc.xcurator.xcuratorimportservice;

import io.mongock.runner.springboot.EnableMongock;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableMongock
public class xCuratorImportServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(xCuratorImportServiceApplication.class, args);
    }

}
