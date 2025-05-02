package com.citasmedicas.spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.citasmedicas.spring.entities.CitaEntity;
import com.citasmedicas.spring.entities.PacienteEntity;

import java.util.List;


public interface CitasRepository extends JpaRepository<CitaEntity, Long>{

    List<CitaEntity> findByPaciente(PacienteEntity paciente);

    @Query("SELECT c FROM CitaEntity c WHERE c.disponibilidad.doctor.id = :doctorId")
    List<CitaEntity> findByDoctorId(@Param("doctorId") Long doctorId);

}
