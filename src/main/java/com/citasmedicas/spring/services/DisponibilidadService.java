package com.citasmedicas.spring.services;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.citasmedicas.spring.dto.CreateDisponibilidadRequest;
import com.citasmedicas.spring.dto.DisponibilidadDTO;
import com.citasmedicas.spring.dto.mappers.DisponibilidadMapper;
import com.citasmedicas.spring.entities.DisponibilidadEntity;
import com.citasmedicas.spring.entities.DoctorEntity;
import com.citasmedicas.spring.entities.EstadoDisponibilidadEnum;
import com.citasmedicas.spring.exceptions.BadRequestException;
import com.citasmedicas.spring.exceptions.ResourceNotFoundException;
import com.citasmedicas.spring.repository.DisponibilidadRepository;
import com.fasterxml.jackson.annotation.JsonFormat;

@Service
public class DisponibilidadService {

    @Autowired
    private DisponibilidadRepository disponibilidadRepository;

    @Autowired
    private DisponibilidadMapper disponibilidadMapper;

    @Autowired
    private DoctorService doctorService;

    public List<DisponibilidadDTO> getAllDisponibilidad(){
        return disponibilidadRepository.findAll().stream()
                .map(disponibilidadMapper::toDisponibilidadDTO)
                .toList();
    }

    public DisponibilidadEntity getDisponibilidadEntityById (Long id){
        return disponibilidadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Disponibilidad no encontrada"));
    }

    public DisponibilidadDTO getDisponibilidadDtoById (Long id){
        DisponibilidadEntity disponibilidadEntity = disponibilidadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Disponibilidad no encontrada"));
        return disponibilidadMapper.toDisponibilidadDTO(disponibilidadEntity);
    }

    @Transactional
    public DisponibilidadDTO createDisponibilidad (CreateDisponibilidadRequest disponibilidadDetails){

        // Obtener y verificar doctor
        DoctorEntity doctorEntity = doctorService.getDoctorEntityById(disponibilidadDetails.idDoctor());

        // Crear disponibilidad
        DisponibilidadEntity disponibilidadEntity = DisponibilidadEntity.builder()
                .doctor(doctorEntity)
                .fecha(disponibilidadDetails.fecha())
                .horaInicio(disponibilidadDetails.horaInicio())
                .horaFin(disponibilidadDetails.horaFin())
                .build();

        // Verificar existencia del doctor en el horario proporcionado
        verificarSolapamientoHorario(disponibilidadEntity);

        return disponibilidadMapper.toDisponibilidadDTO(disponibilidadRepository.save(disponibilidadEntity));
    }

    @Transactional
    public DisponibilidadDTO updateDisponibilidad(Long id, EstadoDisponibilidadEnum nuevoEstado){
        // Obtener disponibilidad existente
        DisponibilidadEntity disponibilidadEntity = getDisponibilidadEntityById(id);

        // Verificar que el cambio sea válido
        validarCambioEstado(disponibilidadEntity, nuevoEstado);

        // Actualizar estado de la disponibilidad
        disponibilidadEntity.setEstado(nuevoEstado);

        // Guardar diponibilidad
        return disponibilidadMapper.toDisponibilidadDTO(disponibilidadRepository.save(disponibilidadEntity));
    }

    @Transactional
    public String deleteDisponibilidad(Long id){
        DisponibilidadEntity disponibilidadEntity = disponibilidadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Disponibilidad con id " + id + " no encontrada"));
        
        // Cambiar estado a CANCELADO
        disponibilidadEntity.setEstado(EstadoDisponibilidadEnum.CANCELADO);
        disponibilidadRepository.save(disponibilidadEntity);
        return "Disponibilidad con id " + id + " cancelada correctamente";
    }

    public List<DisponibilidadDTO> getDisponibilidadByFecha(@JsonFormat(pattern = "dd-MM-yyyy") LocalDate fecha){
        return disponibilidadRepository.findByFecha(fecha).stream()
                .filter(disponibilidad -> disponibilidad.getEstado().equals(EstadoDisponibilidadEnum.DISPONIBLE))
                .map(disponibilidadMapper::toDisponibilidadDTO)
                .collect(Collectors.toList());
    }

    private void verificarSolapamientoHorario(DisponibilidadEntity disponibilidad) {
        LocalDate fecha = disponibilidad.getFecha();
        LocalTime horaInicio = disponibilidad.getHoraInicio();
        LocalTime horaFin = disponibilidad.getHoraFin();
        Long doctorId = disponibilidad.getDoctor().getId();
        
        // Validar que la hora de fin sea posterior a la hora de inicio
        if (horaFin.isBefore(horaInicio) || horaFin.equals(horaInicio)) {
            throw new BadRequestException("La hora de fin debe ser posterior a la hora de inicio");
        }
        
        // Buscar disponibilidades existentes del doctor en la misma fecha
        List<DisponibilidadEntity> disponibilidadesExistentes = 
            disponibilidadRepository.findByDoctorIdAndFecha(doctorId, fecha);
        
        // Verificar si hay solapamiento con algún horario existente
        for (DisponibilidadEntity disp : disponibilidadesExistentes) {
            
            boolean seSuperpone = 
                // Caso 1: hora inicio nueva está dentro del rango existente
                (horaInicio.isAfter(disp.getHoraInicio()) && horaInicio.isBefore(disp.getHoraFin())) ||
                // Caso 2: hora fin nueva está dentro del rango existente
                (horaFin.isAfter(disp.getHoraInicio()) && horaFin.isBefore(disp.getHoraFin())) ||
                // Caso 3: nuevo horario contiene al existente
                (horaInicio.isBefore(disp.getHoraInicio()) && horaFin.isAfter(disp.getHoraFin())) ||
                // Caso 4: nuevo horario está contenido en existente
                (horaInicio.equals(disp.getHoraInicio()) || horaFin.equals(disp.getHoraFin()));
                
            if (seSuperpone) {
                throw new BadRequestException("El doctor ya tiene una disponibilidad que se solapa con el horario " +
                        horaInicio + " - " + horaFin + " en la fecha " + fecha);
            }
        }
    }

    private void validarCambioEstado(DisponibilidadEntity disponibilidad, EstadoDisponibilidadEnum nuevoEstado) {
        EstadoDisponibilidadEnum estadoActual = disponibilidad.getEstado();
        
        if (estadoActual == nuevoEstado) {
            return;
        }
        
        // Intentar cambiar una disponibilidad RESERVADA a DISPONIBLE
        if (estadoActual == EstadoDisponibilidadEnum.RESERVADO && nuevoEstado == EstadoDisponibilidadEnum.DISPONIBLE) {
            // Verificar si la fecha ya pasó
            if (disponibilidad.getFecha().isBefore(LocalDate.now())) {
                throw new BadRequestException("No se puede liberar una disponibilidad de una fecha pasada");
            }
            
            // Si está a menos de 24 horas, no permitir la liberación
            if (disponibilidad.getFecha().equals(LocalDate.now()) && 
                disponibilidad.getHoraInicio().isBefore(LocalTime.now().plusHours(24))) {
                throw new BadRequestException("No se puede liberar una cita con menos de 24 horas de anticipación");
            }
        }
        
        // Intentar marcar como NO_DISPONIBLE una disponibilidad RESERVADA
        if (estadoActual == EstadoDisponibilidadEnum.RESERVADO && nuevoEstado == EstadoDisponibilidadEnum.NO_DISPONIBLE) {
            throw new BadRequestException("No se puede bloquear una disponibilidad que ya tiene una cita reservada");
        }
        
        // Intentar reservar (DISPONIBLE → RESERVADO) una disponibilidad en fecha pasada
        if (estadoActual == EstadoDisponibilidadEnum.DISPONIBLE && nuevoEstado == EstadoDisponibilidadEnum.RESERVADO) {
            if (disponibilidad.getFecha().isBefore(LocalDate.now())) {
                throw new BadRequestException("No se puede reservar una disponibilidad en una fecha pasada");
            }
            
            if (disponibilidad.getFecha().equals(LocalDate.now()) && 
                disponibilidad.getHoraInicio().isBefore(LocalTime.now())) {
                throw new BadRequestException("No se puede reservar una disponibilidad en una hora pasada");
            }
        }
        
        // Validar transición desde estado CANCELADO
        if (estadoActual == EstadoDisponibilidadEnum.CANCELADO) {
            if (nuevoEstado == EstadoDisponibilidadEnum.RESERVADO) {
                throw new BadRequestException("No se puede reservar directamente una disponibilidad cancelada");
            }
        }
    }
}

