package com.citasmedicas.spring.entities;

import java.time.LocalDate;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "disponibilidad")
public class DisponibilidadEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private DoctorEntity doctor;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate fecha;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime horaInicio;
    
    @JsonFormat(pattern = "HH:mm")
    private LocalTime horaFin;

    @Column(name = "duracion_consulta_min")
    private Integer duracionConsultaMinutos;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private EstadoDisponibilidadEnum estado = EstadoDisponibilidadEnum.DISPONIBLE;

}
