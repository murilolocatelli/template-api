package com.example.template.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = TemplateApiApplication.BASE_APPLICATION_PACKAGE)
public class TemplateApiApplication /*extends SwaggerConfiguration*/ {

    static final String BASE_APPLICATION_PACKAGE = "com.example";
    //private static final String BASE_CONTROLLER_PACKAGE = "com.example.template.api.controller";

    public static void main(String[] args) {
        SpringApplication.run(TemplateApiApplication.class, args);
    }

    /*@Override
    protected String baseControllerPackage() {
        return BASE_CONTROLLER_PACKAGE;
    }*/

}
