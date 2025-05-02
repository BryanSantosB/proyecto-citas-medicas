package com.citasmedicas.spring.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.citasmedicas.spring.entities.DoctorEntity;


public interface DoctorRepository extends JpaRepository<DoctorEntity, Long>{

    boolean existsByCorreoElectronico(String correoElectronico);

    Optional<DoctorEntity> findByCorreoElectronico(String correoElectronico);

}
