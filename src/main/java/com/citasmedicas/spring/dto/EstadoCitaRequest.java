package com.citasmedicas.spring.dto;

import com.citasmedicas.spring.entities.EstadoCitaEnum;

import jakarta.validation.constraints.NotNull;

public record EstadoCitaRequest(@NotNull EstadoCitaEnum nuevoEstado) {}
