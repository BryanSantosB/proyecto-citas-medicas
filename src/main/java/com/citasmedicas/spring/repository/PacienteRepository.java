package com.citasmedicas.spring.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.citasmedicas.spring.entities.PacienteEntity;

public interface PacienteRepository extends JpaRepository<PacienteEntity, Long> {

    Optional<PacienteEntity> findPacienteEntityById(Long id);

    Optional<PacienteEntity> findByCorreoElectronico(String correoElectronico);

    boolean existsByCorreoElectronico(String correo);

}
