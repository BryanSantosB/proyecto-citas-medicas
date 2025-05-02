package com.citasmedicas.spring.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.citasmedicas.spring.dto.AuthCreateDoctorDetailsRequest;
import com.citasmedicas.spring.dto.AuthCreateDoctorRequest;
import com.citasmedicas.spring.dto.AuthCreateRoleRequest;
import com.citasmedicas.spring.dto.AuthCreateUserRequest;
import com.citasmedicas.spring.dto.AuthResponse;
import com.citasmedicas.spring.entities.DoctorEntity;
import com.citasmedicas.spring.entities.UserEntity;
import com.citasmedicas.spring.exceptions.BadRequestException;
import com.citasmedicas.spring.exceptions.ResourceNotFoundException;
import com.citasmedicas.spring.repository.DoctorRepository;
import com.citasmedicas.spring.repository.UserRepository;

@Service
public class DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private UserDetailServiceImpl userDetailServiceImpl;    

    @Autowired
    private UserRepository userRepository;

    public List<DoctorEntity> getAllDoctores(){
        return doctorRepository.findAll();
    }

    public DoctorEntity getDoctorById(Long id){
        return doctorRepository.findById(id).orElseThrow(() -> 
                                new ResourceNotFoundException("Doctor con id " + id  + " no existe."));
    }

    @Transactional
    public AuthResponse createDoctorWithUser(AuthCreateDoctorRequest authCreateDoctorRequest) {

        // Obtener los datos de AuthCreateDoctorRequest
        String username = authCreateDoctorRequest.userRequest().username();
        String password = authCreateDoctorRequest.userRequest().password();
        String correo = authCreateDoctorRequest.doctorDetails().correoElectronico();    

        // Verificar existencia del usuario y correo
        verificarExistenciaUsuarioCorreo(username, correo);

        // Crear el usuario asociado al doctor
        AuthCreateUserRequest userRequest = new AuthCreateUserRequest(username, password, 
            new AuthCreateRoleRequest(List.of("DOCTOR")));
        AuthResponse userResponse = userDetailServiceImpl.createUser(userRequest);

        // Crear el doctor y asociarlo al usuario
        UserEntity userEntity = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado después de la creación."));


        DoctorEntity doctorDetails = mapToDoctor(authCreateDoctorRequest.doctorDetails());

        doctorDetails.setUsuario(userEntity);
        doctorRepository.save(doctorDetails);
        return userResponse;
    }

    @Transactional
    public DoctorEntity updateDoctor(Long id, AuthCreateDoctorDetailsRequest doctorDetails){
        DoctorEntity doctorEntity = doctorRepository.findById(id).orElseThrow(() -> 
                                new ResourceNotFoundException("Doctor con id " + id + " no existe."));
        String newCorreo = doctorDetails.correoElectronico();

        // Verificar si el correo ya existe
        verificarCorreoUnico(newCorreo, doctorEntity);

        // Actualizar los datos del doctor
        actualizarCamposDoctor(doctorEntity, doctorDetails);
        
        return doctorRepository.save(doctorEntity);
    }

    @Transactional
    public String deleteDoctor(Long id){
        DoctorEntity doctor = doctorRepository.findById(id).orElseThrow(() -> 
                                new ResourceNotFoundException("Doctor con id " + id + " no existe."));
        
        UserEntity user = doctor.getUsuario();
        if (user == null){
            throw new ResourceNotFoundException("Usuario asociado al doctor no encontrado.");
        }

        doctorRepository.delete(doctor);
        userRepository.delete(user);

        return "El doctor con id " + id + " ha sido eliminado";
    }

    public void verificarExistenciaUsuarioCorreo(String username, String correo){
        if (userRepository.existsByUsername(username)){
            throw new BadRequestException("El nombre de usuario ya existe.");
        }
        if (doctorRepository.existsByCorreoElectronico(correo)){
            throw new BadRequestException("El correo electrónico ya está en uso.");
        }
    }

    public DoctorEntity mapToDoctor(AuthCreateDoctorDetailsRequest doctorDetails){
        return DoctorEntity.builder()
        .nombre(doctorDetails.nombre())
        .apellido(doctorDetails.apellido())
        .correoElectronico(doctorDetails.correoElectronico())
        .telefono(doctorDetails.telefono())
        .direccion(doctorDetails.direccion())
        .especialidad(doctorDetails.especialidad())
        .consultorio(doctorDetails.consultorio())
        .build(); 
    }

    public void verificarCorreoUnico(String newCorreo, DoctorEntity doctorEntity){
        doctorRepository.findByCorreoElectronico(newCorreo)
            .ifPresent(existingDoctor -> {
                if (!existingDoctor.getId().equals(doctorEntity.getId())) {
                    throw new BadRequestException("El correo electrónico ya está en uso.");
                }
            });
    }

    public void actualizarCamposDoctor(DoctorEntity doctorEntity, AuthCreateDoctorDetailsRequest doctorDetails){
        doctorEntity.setCorreoElectronico(doctorDetails.correoElectronico());
        doctorEntity.setTelefono(doctorDetails.telefono());
        doctorEntity.setDireccion(doctorDetails.direccion());
        doctorEntity.setEspecialidad(doctorDetails.especialidad());
        doctorEntity.setConsultorio(doctorDetails.consultorio());
        doctorEntity.setApellido(doctorDetails.apellido());
        doctorEntity.setNombre(doctorDetails.nombre());
    }
}
