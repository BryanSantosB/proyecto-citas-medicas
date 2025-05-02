package com.citasmedicas.spring.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotNull;
public record CreateDisponibilidadRequest(
    @NotNull Long idDoctor,
    @NotNull @JsonFormat(pattern = "dd-MM-yyyy") LocalDate fecha,
    @NotNull @JsonFormat(pattern = "HH:mm") LocalTime horaInicio,
    @NotNull @JsonFormat(pattern = "HH:mm") LocalTime horaFin
) {}
