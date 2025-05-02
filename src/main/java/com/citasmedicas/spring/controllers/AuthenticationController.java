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
import com.citasmedicas.spring.dto.AuthLoginRequest;
import com.citasmedicas.spring.dto.AuthResponse;
import com.citasmedicas.spring.services.UserDetailServiceImpl;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private UserDetailServiceImpl userDetailsServiceImpl;

    @PostMapping("/sign-up")
    @PreAuthorize("permitAll()")
    public ResponseEntity<AuthResponse> register(@RequestBody @Valid AuthCreateUserRequest authCreateUser) {
        AuthCreateUserRequest userRequestWithDefaultRole =
            new AuthCreateUserRequest(authCreateUser.username(), authCreateUser.password(), null);
        return new ResponseEntity<>(this.userDetailsServiceImpl.createUser(userRequestWithDefaultRole), HttpStatus.CREATED);
    }

    @PostMapping("/log-in")
    @PreAuthorize("permitAll()")
    public ResponseEntity<AuthResponse> login (@RequestBody @Valid AuthLoginRequest userRequest){
        return new ResponseEntity<>(this.userDetailsServiceImpl.loginUser(userRequest), HttpStatus.OK);
    }

}
