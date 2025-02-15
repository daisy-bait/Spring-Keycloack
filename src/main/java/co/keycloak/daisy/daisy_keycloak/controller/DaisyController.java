package co.keycloak.daisy.daisy_keycloak.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class DaisyController {

    @GetMapping("/hello-admin")
    public String helloAdmin() {
        return "Hello - ADMIN - in Spring Boot with Keycloak";
    }

    @GetMapping("/hello-user")
    public String helloUser() {
        return "Hello - USER - in Spring Boot with Keycloak";
    }

}
