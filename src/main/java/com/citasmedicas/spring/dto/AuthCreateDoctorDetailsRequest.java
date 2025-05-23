package com.citasmedicas.spring.dto;

import java.math.BigDecimal;
import java.util.Set;

import com.citasmedicas.spring.entities.EspecialidadEntity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AuthCreateDoctorDetailsRequest(
    @NotNull Set<EspecialidadEntity> especialidades,
    @NotBlank String consultorio,
    @NotBlank String licenciaMedica,
    @NotNull Integer aniosExperiencia,
    @NotBlank String universidad,
    @NotNull BigDecimal precioConsulta,
    @NotBlank String biografia
) {}
