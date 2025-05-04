package com.citasmedicas.spring.services;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.citasmedicas.spring.dto.CreateCitaRequest;
import com.citasmedicas.spring.entities.CitaEntity;
import com.citasmedicas.spring.entities.DisponibilidadEntity;
import com.citasmedicas.spring.entities.DoctorEntity;
import com.citasmedicas.spring.entities.EstadoCitaEnum;
import com.citasmedicas.spring.entities.PacienteEntity;
import com.citasmedicas.spring.exceptions.BadRequestException;
import com.citasmedicas.spring.exceptions.ResourceNotFoundException;
import com.citasmedicas.spring.repository.CitasRepository;

@Service
public class CitasService {

    @Autowired
    private CitasRepository citasRepository;

    @Autowired
    private PacienteService pacienteService;

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private DisponibilidadService disponibilidadService;

    public List<CitaEntity> getAllCitas(){
        return citasRepository.findAll();
    }

    @Transactional
    public CitaEntity createCitaEntity(CreateCitaRequest citaRequest){
        // Validar y obtener paciente
        PacienteEntity paciente = pacienteService.getPacienteEnityById(citaRequest.idPaciente());
        
        // Validar y obtener disponibilidad
        DisponibilidadEntity disponibilidad = disponibilidadService.getDisponibilidadById(citaRequest.idDisponibilidad()); 

        // Crear cita
        CitaEntity citaEntity = CitaEntity.builder()
                .paciente(paciente)
                .disponibilidad(disponibilidad)
                .motivo(citaRequest.motivo())
                .build();
        return citasRepository.save(citaEntity);
    }

    public CitaEntity getCitaById(Long id){
        return citasRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cita con id " + id + " no encontrada."));
    }

    @Transactional
    public CitaEntity updateCitaEntity(Long id, EstadoCitaEnum nuevoEstado){
        // Validar y obtener cita
        CitaEntity citaEntity = getCitaById(id);

        // Validar estado actual y nuevo estado
        validarEstadosCita(citaEntity.getEstado(), nuevoEstado);

        // Actualizar cita
        citaEntity.setEstado(nuevoEstado);
        return citasRepository.save(citaEntity);
    }
    
    @Transactional
    public String deleteCitaEntity(Long id){
        // Validar y obtener cita
        CitaEntity citaEntity = getCitaById(id);

        // Validar estado
        validarEstadosCita(citaEntity.getEstado(), EstadoCitaEnum.CANCELADA);

        // Eliminar cita
        citaEntity.setEstado(EstadoCitaEnum.CANCELADA);
        citasRepository.save(citaEntity);

        return "Cita con id " + id + " cancelada correctamente";
    }

    public List<CitaEntity> getCitasByPaciente(Long idPaciente){
        // Validar y obtener paciente
        PacienteEntity paciente = pacienteService.getPacienteEnityById(idPaciente);

        // Obtener citas por paciente
        return citasRepository.findByPaciente(paciente);
    }

    public List<CitaEntity> getCitasByDoctor(Long idDoctor){
        // Validar y obtener doctor
        DoctorEntity doctor = doctorService.getDoctorById(idDoctor);

        // Obtener citas por doctor
        return citasRepository.findByDoctorId(doctor.getId());
    }

    public void validarEstadosCita(EstadoCitaEnum estadoActual, EstadoCitaEnum nuevoEstado) {
        Map<EstadoCitaEnum, Set<EstadoCitaEnum>> transicionesValidas = Map.of(
            EstadoCitaEnum.PENDIENTE, Set.of(EstadoCitaEnum.CONFIRMADA, EstadoCitaEnum.CANCELADA),
            EstadoCitaEnum.CONFIRMADA, Set.of(EstadoCitaEnum.COMPLETADA, EstadoCitaEnum.CANCELADA),
            EstadoCitaEnum.CANCELADA, Set.of(),
            EstadoCitaEnum.COMPLETADA, Set.of()
        );
    
        if (!transicionesValidas.getOrDefault(estadoActual, Set.of()).contains(nuevoEstado)) {
            throw new BadRequestException("No se puede cambiar el estado de " + estadoActual + " a " + nuevoEstado);
        }
    }
    
}
