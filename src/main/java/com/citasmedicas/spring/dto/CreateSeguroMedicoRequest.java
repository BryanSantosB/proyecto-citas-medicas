package com.citasmedicas.spring.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateSeguroMedicoRequest(
    Long idPaciente,
    @NotBlank String compania,
    @NotBlank String numeroPoliza,
    @NotNull @JsonFormat(pattern = "dd-MM-yyyy") LocalDate fechaVencimiento,
    @NotNull Boolean esPrincipal
) {}
