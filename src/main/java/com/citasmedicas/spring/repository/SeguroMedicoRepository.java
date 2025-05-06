package com.citasmedicas.spring.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.citasmedicas.spring.entities.PacienteEntity;
import com.citasmedicas.spring.entities.SeguroMedicoEntity;

public interface SeguroMedicoRepository extends JpaRepository<SeguroMedicoEntity, Long>{

    List<SeguroMedicoEntity> findByPaciente(PacienteEntity paciente);

}
