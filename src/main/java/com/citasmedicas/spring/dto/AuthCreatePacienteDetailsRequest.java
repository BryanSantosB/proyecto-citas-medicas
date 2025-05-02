package com.citasmedicas.spring.dto;

import java.time.LocalDate;


import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;

public record AuthCreatePacienteDetailsRequest(
    @NotBlank String nombres,
    @NotBlank String apellidos,
    @NotBlank String correoElectronico,
    @JsonFormat(pattern = "dd-MM-yyyy") LocalDate fechaNacimiento,
    @NotBlank String genero,
    @NotBlank String telefonoContacto,
    @NotBlank String direccion,
    @NotBlank String grupoSanguineo,
    @NotBlank String alergias,
    @NotBlank String enfermedadesCronicas,
    @NotBlank String numeroHistoriaClinica,
    @NotBlank String informacionAdicional
) {}
