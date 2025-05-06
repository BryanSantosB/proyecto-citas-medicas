package com.citasmedicas.spring.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import com.citasmedicas.spring.entities.EstadoDisponibilidadEnum;

import jakarta.validation.constraints.NotNull;

public record DisponibilidadDTO(
    @NotNull Long id,
    @NotNull DoctorDTO doctor,
    @NotNull LocalDate fecha,
    @NotNull LocalTime horaInicio,
    @NotNull LocalTime horaFin,
    @NotNull EstadoDisponibilidadEnum estado
) {}
