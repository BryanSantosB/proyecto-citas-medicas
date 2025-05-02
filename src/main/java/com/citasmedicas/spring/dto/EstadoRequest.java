package com.citasmedicas.spring.dto;

import com.citasmedicas.spring.entities.EstadoDisponibilidadEnum;

import jakarta.validation.constraints.NotNull;

public record EstadoRequest(@NotNull EstadoDisponibilidadEnum nuevoEstado) {

}
