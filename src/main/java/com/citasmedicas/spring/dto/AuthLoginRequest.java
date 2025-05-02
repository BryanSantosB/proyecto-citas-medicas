package com.citasmedicas.spring.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import jakarta.validation.constraints.NotBlank;

@JsonPropertyOrder({"username", "password"})
public record AuthLoginRequest(@NotBlank String username, 
                               @NotBlank String password) {

}
