package com.citasmedicas.spring.entities;

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
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "citas")
public class CitaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(targetEntity = PacienteEntity.class)
    @JoinColumn(name = "paciente_id", nullable = false)
    private PacienteEntity paciente;

    @ManyToOne(targetEntity = DisponibilidadEntity.class)
    @JoinColumn(name = "disponibilidad_id", nullable = false)
    private DisponibilidadEntity disponibilidad;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private EstadoCitaEnum estado = EstadoCitaEnum.PENDIENTE;

    private String motivo;
    private String observaciones;

}
