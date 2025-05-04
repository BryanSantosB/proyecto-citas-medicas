package com.citasmedicas.spring.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PacienteDTO(
    @NotNull Long id,
    @NotNull UserDTO usuario,
    @NotBlank String grupoSanguineo,
    @NotBlank String alergias,
    @NotBlank String enfermedadesCronicas,
    @NotBlank String numeroHistoriaClinica,
    @NotBlank String informacionAdicional
) {}
