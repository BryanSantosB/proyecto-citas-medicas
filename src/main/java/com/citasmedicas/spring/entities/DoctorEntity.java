package com.citasmedicas.spring.entities;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
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
@Table(name = "doctores")
public class DoctorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany
    @JoinTable(
        name = "doctor_especialidades",
        joinColumns = @JoinColumn(name = "doctor_id"),
        inverseJoinColumns = @JoinColumn(name = "especialidad_id")
    )
    @Builder.Default
    private Set<EspecialidadEntity> especialidades = new HashSet<>();

    @Column(nullable = false)
    private String consultorio;

    @Column(nullable = false, unique = true, length = 20)
    private String licenciaMedica;
    
    //@Column(nullable = false)
    @Builder.Default
    private Integer aniosExperiencia = 0;
    
    @Column(length = 100)
    private String universidad;
    
    @Column(name = "duracion_consulta_min")
    @Builder.Default
    private Integer duracionConsultaMinutos = 30; 
    
    @Column(precision = 10, scale = 2)
    private BigDecimal precioConsulta;
    
    @Column(length = 500)
    private String biografia;
    
    @Column(name = "foto_perfil_url")
    private String fotoPerfilUrl;

    @OneToOne(targetEntity = UserEntity.class)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity usuario;

}
