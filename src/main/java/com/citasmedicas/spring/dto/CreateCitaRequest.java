package com.citasmedicas.spring.dto;

import jakarta.validation.constraints.NotNull;

public record CreateCitaRequest(
    @NotNull Long idPaciente,
    @NotNull Long idDisponibilidad,
    String motivo
) {}
