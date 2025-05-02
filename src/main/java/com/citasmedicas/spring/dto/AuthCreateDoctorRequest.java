package com.citasmedicas.spring.dto;

import jakarta.validation.Valid;

public record AuthCreateDoctorRequest(@Valid AuthCreateUserRequest userRequest,
                             @Valid AuthCreateDoctorDetailsRequest doctorDetails) { 
} 
