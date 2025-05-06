package com.citasmedicas.spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.citasmedicas.spring.entities.DoctorEntity;


public interface DoctorRepository extends JpaRepository<DoctorEntity, Long>{

}
