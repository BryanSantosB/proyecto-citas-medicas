package com.citasmedicas.spring.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotNull;

public record FechaRequest(@NotNull @JsonFormat(pattern = "dd-MM-yyyy") LocalDate fecha) {

}
