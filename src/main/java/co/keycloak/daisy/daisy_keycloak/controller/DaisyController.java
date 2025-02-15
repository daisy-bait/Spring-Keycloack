package co.keycloak.daisy.daisy_keycloak.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class DaisyController {

    @PreAuthorize("hasRole('admin_client_role')")
    @GetMapping("/hello-admin")
    public String helloAdmin() {
        return "Hello - ADMIN - in Spring Boot with Keycloak";
    }

    @PreAuthorize("hasAnyRole('user_client_role', 'admin_client_role')")
    @GetMapping("/hello-user")
    public String helloUser() {
        return "Hello - USER - in Spring Boot with Keycloak";
    }

}
