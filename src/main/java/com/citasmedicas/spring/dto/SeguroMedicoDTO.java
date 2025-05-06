package com.citasmedicas.spring.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SeguroMedicoDTO(
    @NotNull Long id,
    @NotNull PacienteDTO paciente,
    @NotBlank String compania,
    @NotBlank String numeroPoliza,
    @NotNull LocalDate fechaVencimiento,
    @NotNull Boolean esPrincipal
) {}