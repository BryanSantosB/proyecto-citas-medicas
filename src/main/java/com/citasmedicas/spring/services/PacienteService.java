package com.citasmedicas.spring.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.citasmedicas.spring.dto.AuthCreatePacienteDetailsRequest;
import com.citasmedicas.spring.dto.AuthCreatePacienteRequest;
import com.citasmedicas.spring.dto.AuthCreateUserRequest;
import com.citasmedicas.spring.dto.AuthResponse;
import com.citasmedicas.spring.dto.PacienteDTO;
import com.citasmedicas.spring.dto.mappers.PacienteMapper;
import com.citasmedicas.spring.entities.PacienteEntity;
import com.citasmedicas.spring.entities.UserEntity;
import com.citasmedicas.spring.exceptions.ResourceNotFoundException;
import com.citasmedicas.spring.repository.PacienteRepository;
import com.citasmedicas.spring.repository.UserRepository;

@Service
public class PacienteService {

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private PacienteMapper pacienteMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserDetailServiceImpl userDetailServiceImpl;

    public List<PacienteDTO> getAllPacientes(){
        return pacienteRepository.findAll().stream()
                .map(pacienteMapper::toPacienteDTO)
                .toList();
    }

    public PacienteDTO getPacienteDtoById(Long id){
        PacienteEntity paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado"));
        return pacienteMapper.toPacienteDTO(paciente);
    }

    public PacienteEntity getPacienteEnityById(Long id){
        return pacienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado"));
    }

    @Transactional
    public AuthResponse createPacienteWithUser(AuthCreatePacienteRequest authCreatePacienteRequest){

        // Obtener los datos de AuthCreatePacienteRequest
        String username = authCreatePacienteRequest.userRequest().username();
        String correo = authCreatePacienteRequest.userRequest().correoElectronico();

        // Verificar existencia del usuario y correo
        userDetailServiceImpl.verificarExistenciaUsuarioCorreo(username, correo);

        // Crear el usuario asociado al paciente
        AuthCreateUserRequest userRequest = userDetailServiceImpl.asignarRolUserRequest(authCreatePacienteRequest.userRequest(), "PACIENTE");
        AuthResponse userResponse = userDetailServiceImpl.createUser(userRequest);

        // Crear el paciente y asociarlo al usuario
        UserEntity userEntity = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado después de la creación."));

        // Mapear y guardar paciente
        PacienteEntity pacienteDetails = mapToPaciente(authCreatePacienteRequest.pacienteDetails());

        pacienteDetails.setUsuario(userEntity);
        pacienteRepository.save(pacienteDetails);
        return userResponse;
    }

    @Transactional
    public PacienteDTO updatePaciente(Long id, AuthCreatePacienteRequest pacienteRequest){

        PacienteEntity pacienteEntity = pacienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente con id " + id + " no encontrado."));

        userDetailServiceImpl.updateUsuario(pacienteEntity.getUsuario().getId(), pacienteRequest.userRequest());
        
        // Actualizar los datos del paciente 
        actualizarCamposPaciente(pacienteEntity, pacienteRequest.pacienteDetails());

        return pacienteMapper.toPacienteDTO(pacienteRepository.save(pacienteEntity));
    }

    @Transactional
    public String deletePaciente(Long id){
        PacienteEntity paciente = pacienteRepository.findById(id).orElseThrow(() -> 
                                new ResourceNotFoundException("Paciente con id " + id + " no existe."));
        
        UserEntity user = paciente.getUsuario();
        if (user == null){
            throw new ResourceNotFoundException("Usuario asociado al doctor no encontrado.");
        }

        userDetailServiceImpl.deleteUser(paciente.getUsuario().getId());
        return "El paciente con id " + id + " ha sido deshabilidado";
    }

    public void actualizarCamposPaciente(PacienteEntity pacienteEntity, AuthCreatePacienteDetailsRequest pacienteDetails){
        pacienteEntity.setGrupoSanguineo(pacienteDetails.grupoSanguineo());
        pacienteEntity.setAlergias(pacienteDetails.alergias());
        pacienteEntity.setEnfermedadesCronicas(pacienteDetails.enfermedadesCronicas());
        pacienteEntity.setNumeroHistoriaClinica(pacienteDetails.numeroHistoriaClinica());
        pacienteEntity.setInformacionAdicional(pacienteDetails.informacionAdicional());
    }

    public PacienteEntity mapToPaciente(AuthCreatePacienteDetailsRequest pecienteDetails){
        return PacienteEntity.builder()
        .grupoSanguineo(pecienteDetails.grupoSanguineo())
        .alergias(pecienteDetails.alergias())
        .enfermedadesCronicas(pecienteDetails.enfermedadesCronicas())
        .numeroHistoriaClinica(pecienteDetails.numeroHistoriaClinica())
        .informacionAdicional(pecienteDetails.informacionAdicional())
        .build();
    }
}
