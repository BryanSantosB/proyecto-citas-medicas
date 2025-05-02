package com.citasmedicas.spring.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.citasmedicas.spring.dto.AuthCreatePacienteDetailsRequest;
import com.citasmedicas.spring.dto.AuthCreatePacienteRequest;
import com.citasmedicas.spring.dto.AuthCreateRoleRequest;
import com.citasmedicas.spring.dto.AuthCreateUserRequest;
import com.citasmedicas.spring.dto.AuthResponse;
import com.citasmedicas.spring.entities.PacienteEntity;
import com.citasmedicas.spring.entities.UserEntity;
import com.citasmedicas.spring.exceptions.BadRequestException;
import com.citasmedicas.spring.exceptions.ResourceNotFoundException;
import com.citasmedicas.spring.repository.PacienteRepository;
import com.citasmedicas.spring.repository.UserRepository;

@Service
public class PacienteService {

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserDetailServiceImpl userDetailServiceImpl;

    public List<PacienteEntity> getAllPacientes(){
        return pacienteRepository.findAll();
    }

    public PacienteEntity getPacienteById(Long id){
        return pacienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado"));
    }

    @Transactional
    public AuthResponse createPacienteWithUser(AuthCreatePacienteRequest authCreatePacienteRequest){

        // Obtener los datos de AuthCreatePacienteRequest
        String username = authCreatePacienteRequest.userRequest().username();
        String password = authCreatePacienteRequest.userRequest().password();
        String correo = authCreatePacienteRequest.pacienteDetails().correoElectronico();

        // Verificar existencia del usuario y correo
        verificarExistenciaUsuarioCorreo(username, correo);

        // Crear el usuario asociado al paciente
        AuthCreateUserRequest userRequest = new AuthCreateUserRequest(username, password, 
            new AuthCreateRoleRequest(List.of("PACIENTE")));
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
    public PacienteEntity updatePaciente(Long id, AuthCreatePacienteDetailsRequest pacienteDetails){

        PacienteEntity pacienteEntity = pacienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente con id " + id + " no encontrado."));
        String correo = pacienteDetails.correoElectronico();

        // Validar existencia única del correo
        verificarCorreoUnico(correo, pacienteEntity);

        // Actualizar los datos del paciente 
        actualizarCamposPaciente(pacienteEntity, pacienteDetails);

        return pacienteRepository.save(pacienteEntity);
    }

    @Transactional
    public String deletePaciente(Long id){
        PacienteEntity paciente = pacienteRepository.findById(id).orElseThrow(() -> 
                                new ResourceNotFoundException("Paciente con id " + id + " no existe."));
        
        UserEntity user = paciente.getUsuario();
        if (user == null){
            throw new ResourceNotFoundException("Usuario asociado al doctor no encontrado.");
        }

        pacienteRepository.delete(paciente);
        userRepository.delete(user);

        return "El paciente con id " + id + " ha sido eliminado";
    }

    public void verificarCorreoUnico(String newCorreo, PacienteEntity pacienteEntity){
        pacienteRepository.findByCorreoElectronico(newCorreo)
            .ifPresent(existingPaciente -> {
                if (!existingPaciente.getId().equals(pacienteEntity.getId())) {
                    throw new BadRequestException("El correo electrónico ya está en uso.");
                }
            });
    }

    public void actualizarCamposPaciente(PacienteEntity pacienteEntity, AuthCreatePacienteDetailsRequest pacienteDetails){
        pacienteEntity.setNombres(pacienteDetails.nombres());
        pacienteEntity.setApellidos(pacienteDetails.apellidos());
        pacienteEntity.setCorreoElectronico(pacienteDetails.correoElectronico());
        pacienteEntity.setFechaNacimiento(pacienteDetails.fechaNacimiento());
        pacienteEntity.setGenero(pacienteDetails.genero());
        pacienteEntity.setTelefonoContacto(pacienteDetails.telefonoContacto());
        pacienteEntity.setDireccion(pacienteDetails.direccion());
        pacienteEntity.setGrupoSanguineo(pacienteDetails.grupoSanguineo());
        pacienteEntity.setAlergias(pacienteDetails.alergias());
        pacienteEntity.setEnfermedadesCronicas(pacienteDetails.enfermedadesCronicas());
        pacienteEntity.setNumeroHistoriaClinica(pacienteDetails.numeroHistoriaClinica());
        pacienteEntity.setInformacionAdicional(pacienteDetails.informacionAdicional());
    }
    
    public void verificarExistenciaUsuarioCorreo(String username, String correo){
        if(userRepository.existsByUsername(username)){
            throw new BadRequestException("El nombre de usuario ya existe.");
        }
        if(pacienteRepository.existsByCorreoElectronico(correo)){
            throw new BadRequestException("El correo electrónico ya está en uso.");
        }
    }

    public PacienteEntity mapToPaciente(AuthCreatePacienteDetailsRequest pecienteDetails){
        return PacienteEntity.builder()
        .nombres(pecienteDetails.nombres())
        .apellidos(pecienteDetails.apellidos())
        .correoElectronico(pecienteDetails.correoElectronico())
        .fechaNacimiento(pecienteDetails.fechaNacimiento())
        .genero(pecienteDetails.genero())
        .telefonoContacto(pecienteDetails.telefonoContacto())
        .direccion(pecienteDetails.direccion())
        .grupoSanguineo(pecienteDetails.grupoSanguineo())
        .alergias(pecienteDetails.alergias())
        .enfermedadesCronicas(pecienteDetails.enfermedadesCronicas())
        .numeroHistoriaClinica(pecienteDetails.numeroHistoriaClinica())
        .informacionAdicional(pecienteDetails.informacionAdicional())
        .build();
    }
}
