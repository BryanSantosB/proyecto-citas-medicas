package com.citasmedicas.spring.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.citasmedicas.spring.dto.CreateSeguroMedicoRequest;
import com.citasmedicas.spring.dto.SeguroMedicoDTO;
import com.citasmedicas.spring.dto.mappers.SeguroMedicoMapper;
import com.citasmedicas.spring.entities.EstadoSeguro;
import com.citasmedicas.spring.entities.PacienteEntity;
import com.citasmedicas.spring.entities.SeguroMedicoEntity;
import com.citasmedicas.spring.exceptions.BadRequestException;
import com.citasmedicas.spring.exceptions.ResourceNotFoundException;
import com.citasmedicas.spring.repository.SeguroMedicoRepository;

import jakarta.transaction.Transactional;

@Service
public class SeguroMedicoService {

    @Autowired
    private SeguroMedicoRepository seguroMedicoRepository;

    @Autowired
    private SeguroMedicoMapper seguroMedicoMapper;

    @Autowired
    private PacienteService pacienteService;

    public List<SeguroMedicoDTO> getAllSegurosMedico(){
        return seguroMedicoRepository.findAll().stream()
                .map(seguroMedicoMapper::toSeguroMedicoDTO)
                .toList();
    }

    public SeguroMedicoDTO getSeguroMedicoDtoById(Long id){
        return seguroMedicoRepository.findById(id)
                .map(seguroMedicoMapper::toSeguroMedicoDTO).orElseThrow(
                    () -> new ResourceNotFoundException("Seguro Medico con id " + id + " no encontrado")
                );
    }

    public SeguroMedicoEntity getSeguroMedicoEntityById(Long id){
        return seguroMedicoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Seguro Medico con id " + id + " no encontrado"));
    }

    @Transactional
    public SeguroMedicoDTO createSeguroMedico(CreateSeguroMedicoRequest seguroMedicoRequest){

        // Validar y obtener paciente
        PacienteEntity paciente = pacienteService.getPacienteEnityById(seguroMedicoRequest.idPaciente());

        // Validar si es principal
        if(seguroMedicoRequest.esPrincipal()){
            desactivarPrincipalSeguroMedico(getSegurosMedicoEntityByPaciente(paciente.getId()));
        }

        // Validar fecha de vencimiento
        validarFechaVencimiento(seguroMedicoRequest.fechaVencimiento());

        // Crear seguro medico
        SeguroMedicoEntity seguroMedicoEntity = crearSeguroMedicoEntity(seguroMedicoRequest, paciente);

        return seguroMedicoMapper.toSeguroMedicoDTO(seguroMedicoRepository.save(seguroMedicoEntity));
    } 

    public List<SeguroMedicoDTO> getSegurosMedicoDtoByPaciente(Long idPaciente){
        return seguroMedicoRepository.findByPaciente(pacienteService.getPacienteEnityById(idPaciente)).stream()
                .map(seguroMedicoMapper::toSeguroMedicoDTO)
                .toList();
    }

    public List<SeguroMedicoEntity> getSegurosMedicoEntityByPaciente(Long idPaciente){
        return seguroMedicoRepository.findByPaciente(pacienteService.getPacienteEnityById(idPaciente));
    }

    @Transactional
    public SeguroMedicoDTO updateSeguroMedico(Long id, CreateSeguroMedicoRequest seguroMedicoRequest){

        // Validar y obtener seguro médico
        SeguroMedicoEntity seguroMedicoEntity = getSeguroMedicoEntityById(id);

        // Validar que no sea el principal actualmente
        validarPrincipal(seguroMedicoEntity, seguroMedicoRequest);

        // Validar fecha de vencimiento
        validarFechaVencimiento(seguroMedicoRequest.fechaVencimiento());

        // Actualizar seguro medico
        actualizarSeguroMedicoEntity(seguroMedicoEntity, seguroMedicoRequest);

        return seguroMedicoMapper.toSeguroMedicoDTO(seguroMedicoRepository.save(seguroMedicoEntity));
    }

    @Transactional
    public String deleteSeguroMedico(Long id){
        // Validar y obtener seguro médico
        SeguroMedicoEntity seguroMedicoEntity = getSeguroMedicoEntityById(id);

        // Validar estado del seguro medico
        validarEstadoSeguro(seguroMedicoEntity.getEstado());

        // Deshabilitar seguro medico
        seguroMedicoEntity.setEstado(EstadoSeguro.INACTIVO);
        seguroMedicoRepository.save(seguroMedicoEntity);

        return "El seguro medico con id " + id + " ha sido deshabilitado";
    }

    @Transactional
    public void updateEstadoSeguroPrincipal(SeguroMedicoEntity seguroMedicoEntity){

        // Obtener seguros médicos por paciente
        List<SeguroMedicoEntity> segurosMedicos = getSegurosMedicoEntityByPaciente(seguroMedicoEntity.getPaciente().getId());

        // Desactivar el pricipal existente
        desactivarPrincipalSeguroMedico(segurosMedicos);

        // Actualizar el principal
        seguroMedicoEntity.setEsPrincipal(true);
    }

    public void validarFechaVencimiento(LocalDate fechaVencimiento) {
        if (!(fechaVencimiento != null && fechaVencimiento.isAfter(LocalDate.now()))) {
            throw new BadRequestException("La fecha de vencimiento debe ser posterior a la fecha actual.");
        }
    }

    public SeguroMedicoEntity crearSeguroMedicoEntity(CreateSeguroMedicoRequest seguroMedicoRequest, PacienteEntity paciente ){
        return SeguroMedicoEntity.builder()
                .paciente(paciente)
                .compania(seguroMedicoRequest.compania())
                .numeroPoliza(seguroMedicoRequest.numeroPoliza())
                .fechaVencimiento(seguroMedicoRequest.fechaVencimiento())
                .esPrincipal(seguroMedicoRequest.esPrincipal())
                .build();
    }

    public void actualizarSeguroMedicoEntity(SeguroMedicoEntity seguroMedicoEntity, CreateSeguroMedicoRequest seguroMedicoRequest) {
        seguroMedicoEntity.setCompania(seguroMedicoRequest.compania());
        seguroMedicoEntity.setNumeroPoliza(seguroMedicoRequest.numeroPoliza());
        seguroMedicoEntity.setFechaVencimiento(seguroMedicoRequest.fechaVencimiento());
    }

    public void validarEstadoSeguro(EstadoSeguro estadoSeguro){
        if(estadoSeguro == EstadoSeguro.INACTIVO){
            throw new BadRequestException("El seguro medico ya está inactivo.");
        }
    }

    public void validarPrincipal(SeguroMedicoEntity seguroMedicoEntity, CreateSeguroMedicoRequest seguroMedicoRequest){
        if(!seguroMedicoEntity.getEsPrincipal() && seguroMedicoRequest.esPrincipal()){
            updateEstadoSeguroPrincipal(seguroMedicoEntity);
        }
    }

    public void desactivarPrincipalSeguroMedico(List<SeguroMedicoEntity> seguroMedicoEntities){
        Optional<SeguroMedicoEntity> seguro = seguroMedicoEntities.stream()
                .filter(seguroMedico -> seguroMedico.getEsPrincipal())
                .findFirst();

        seguro.ifPresent(seguroMedico -> {
            seguroMedico.setEsPrincipal(false);
            seguroMedicoRepository.save(seguroMedico);
        });
    }

}
