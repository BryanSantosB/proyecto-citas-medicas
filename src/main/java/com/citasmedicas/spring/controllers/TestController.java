package com.citasmedicas.spring.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class TestController {

    @GetMapping
    @PreAuthorize("permitAll()")
    public String index(){
        return "Hola Mundo";
    }

    @GetMapping("/get")
    @PreAuthorize("permitAll()")
    public String helloGet() {
        return "Hello World - GET"; 
    }

    @PostMapping("/post")
    @PreAuthorize("hasAuthority('CREATE')")
    public String helloPost() {
        return "Hello World - POST";
    }

    @PutMapping ("/put")
    @PreAuthorize("hasAuthority('UPDATE')")
    public String helloPut() {
        return "Hello World - PUT";
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAuthority('DELETE')")
    public String helloDelete() {
        return "Hello World - DELETE";
    }

    @PatchMapping("/patch")
    @PreAuthorize("hasAuthority('REFACTOR')")
    public String helloPatch() {
        return "Hello World - PATCH";
    }

}
