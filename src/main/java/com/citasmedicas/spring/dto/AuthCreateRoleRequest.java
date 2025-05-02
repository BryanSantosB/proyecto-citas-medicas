package com.citasmedicas.spring.dto;

import java.util.List;

import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Size;

@Validated
public record AuthCreateRoleRequest(
                @Size(max = 3, message = "El número máximo de roles es 3") List<String> roleListName) {

}
