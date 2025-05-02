package com.citasmedicas.spring.dto;

import org.springframework.lang.Nullable;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

public record AuthCreateUserRequest(@NotBlank String username, 
                            String password,
                            @Nullable @Valid AuthCreateRoleRequest roleRequest) {

}
                            