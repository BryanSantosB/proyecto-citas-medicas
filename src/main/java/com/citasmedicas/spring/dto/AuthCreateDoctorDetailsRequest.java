package com.citasmedicas.spring.dto;

import jakarta.validation.constraints.NotBlank;

public record AuthCreateDoctorDetailsRequest(
    @NotBlank String nombre,
    @NotBlank String apellido,
    @NotBlank String correoElectronico,
    @NotBlank String telefono,
    @NotBlank String direccion,
    @NotBlank String especialidad,
    @NotBlank String consultorio
) {}
