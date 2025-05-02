package com.citasmedicas.spring.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.citasmedicas.spring.dto.AuthCreateUserRequest;
import com.citasmedicas.spring.dto.AuthResponse;
import com.citasmedicas.spring.services.UserDetailServiceImpl;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserDetailServiceImpl userDetailsServiceImpl;

    @PostMapping("/create-user")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AuthResponse> registerAdmin (@RequestBody @Valid AuthCreateUserRequest authCreateUser){
        return new ResponseEntity<>(this.userDetailsServiceImpl.createUser(authCreateUser), HttpStatus.CREATED);
    }

}
