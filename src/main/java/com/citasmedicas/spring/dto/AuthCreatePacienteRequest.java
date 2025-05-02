package com.citasmedicas.spring.dto;

import jakarta.validation.Valid;

public record AuthCreatePacienteRequest(@Valid AuthCreateUserRequest userRequest,
    @Valid AuthCreatePacienteDetailsRequest pacienteDetails ) {}
