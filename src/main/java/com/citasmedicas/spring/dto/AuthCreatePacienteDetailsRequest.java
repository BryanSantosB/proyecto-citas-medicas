package com.citasmedicas.spring.dto;

import jakarta.validation.constraints.NotBlank;

public record AuthCreatePacienteDetailsRequest(
    @NotBlank String grupoSanguineo,
    @NotBlank String alergias,
    @NotBlank String enfermedadesCronicas,
    @NotBlank String numeroHistoriaClinica,
    @NotBlank String informacionAdicional,
    @NotBlank String seguroMedico,
    @NotBlank String numeroPoliza
) {}
