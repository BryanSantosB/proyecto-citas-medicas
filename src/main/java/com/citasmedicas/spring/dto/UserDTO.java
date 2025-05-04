package com.citasmedicas.spring.dto;

import java.time.LocalDate;

import org.mapstruct.Mapping;

import com.citasmedicas.spring.entities.GeneroEnum;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserDTO(
    Long id,
    @NotBlank String username,
    @NotBlank String nombres,
    @NotBlank String apellidos,
    @NotBlank String correoElectronico,
    @JsonFormat(pattern = "dd-MM-yyyy") @NotNull LocalDate fechaNacimiento,
    @NotNull GeneroEnum genero,
    @NotBlank String telefonoContacto,
    @NotBlank String direccion,
    @Mapping(source = "isEnabled", target = "enabled") @NotNull boolean enabled,
    @NotNull boolean accountNoExpired,
    @NotNull boolean accountNoLocked,
    @NotNull boolean credentialsNoExpired
) {}
