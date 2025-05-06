package com.citasmedicas.spring.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.citasmedicas.spring.entities.EspecialidadEntity;
import com.citasmedicas.spring.entities.EspecialidadEnum;

public interface EspecialidadRepository extends JpaRepository<EspecialidadEntity, Long> {

    Optional<EspecialidadEntity> findByEspecialidad(EspecialidadEnum especialidad);

}
