package com.citasmedicas.spring.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.citasmedicas.spring.entities.DisponibilidadEntity;
import com.citasmedicas.spring.entities.EstadoDisponibilidadEnum;
import com.fasterxml.jackson.annotation.JsonFormat;

public interface DisponibilidadRepository extends JpaRepository<DisponibilidadEntity, Long>{

    List<DisponibilidadEntity> findByDoctorIdAndFecha(Long id, LocalDate fecha);
    Optional<DisponibilidadEntity> findByEstado(EstadoDisponibilidadEnum estado);
    List<DisponibilidadEntity> findByFecha(@JsonFormat(pattern = "dd-MM-yyyy") LocalDate fecha);
}