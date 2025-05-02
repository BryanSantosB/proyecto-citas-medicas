package com.citasmedicas.spring.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"username", "message", "jwt", "status"})
public record AuthResponse(String username,
                           String message, 
                           String jwt, 
                           boolean status) {} 
