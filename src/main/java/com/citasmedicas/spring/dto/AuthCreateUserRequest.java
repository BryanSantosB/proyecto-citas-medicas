package com.citasmedicas.spring.dto;

import java.time.LocalDate;

import org.springframework.lang.Nullable;

import com.citasmedicas.spring.entities.GeneroEnum;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AuthCreateUserRequest(@NotBlank String username, 
                            String password,
                            @NotBlank String nombres,
                            @NotBlank String apellidos,
                            @NotBlank String correoElectronico,
                            @JsonFormat(pattern = "dd-MM-yyyy") @NotNull LocalDate fechaNacimiento,
                            @NotNull GeneroEnum genero,
                            @NotBlank String telefonoContacto,
                            @NotBlank String direccion,
                            @Nullable @Valid AuthCreateRoleRequest roleRequest) {

}
                            