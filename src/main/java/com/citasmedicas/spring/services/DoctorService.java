package com.citasmedicas.spring.services;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.citasmedicas.spring.dto.AuthCreateDoctorDetailsRequest;
import com.citasmedicas.spring.dto.AuthCreateDoctorRequest;
import com.citasmedicas.spring.dto.AuthCreateUserRequest;
import com.citasmedicas.spring.dto.AuthResponse;
import com.citasmedicas.spring.dto.DoctorDTO;
import com.citasmedicas.spring.dto.mappers.DoctorMapper;
import com.citasmedicas.spring.entities.DoctorEntity;
import com.citasmedicas.spring.entities.EspecialidadEntity;
import com.citasmedicas.spring.entities.UserEntity;
import com.citasmedicas.spring.exceptions.ResourceNotFoundException;
import com.citasmedicas.spring.repository.DoctorRepository;
import com.citasmedicas.spring.repository.EspecialidadRepository;
import com.citasmedicas.spring.repository.UserRepository;

@Service
public class DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private UserDetailServiceImpl userDetailServiceImpl;    

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DoctorMapper doctorMapper;

    @Autowired
    private EspecialidadRepository especialidadRepository;

    public List<DoctorDTO> getAllDoctores(){
        return doctorRepository.findAll().stream()
                .map(doctorMapper::toDoctorDTO).toList();
    }

    public DoctorEntity getDoctorEntityById(Long id){
        return doctorRepository.findById(id).orElseThrow(() -> 
                                new ResourceNotFoundException("Doctor con id " + id  + " no existe."));
    }

    public DoctorDTO getDoctorDtoById(Long id){
        DoctorEntity doctor = doctorRepository.findById(id).orElseThrow(() -> 
                                new ResourceNotFoundException("Doctor con id " + id  + " no existe."));
        return doctorMapper.toDoctorDTO(doctor);
    }

    @Transactional
    public AuthResponse createDoctorWithUser(AuthCreateDoctorRequest authCreateDoctorRequest) {

        // Obtener los datos de AuthCreateDoctorRequest
        String username = authCreateDoctorRequest.userRequest().username();
        String correo = authCreateDoctorRequest.userRequest().correoElectronico();    

        // Verificar existencia del usuario y correo
        userDetailServiceImpl.verificarExistenciaUsuarioCorreo(username, correo);

        // Crear el usuario asociado al doctor
        AuthCreateUserRequest userRequest = userDetailServiceImpl.asignarRolUserRequest(authCreateDoctorRequest.userRequest(), "DOCTOR");
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
    public DoctorDTO updateDoctor(Long id, AuthCreateDoctorRequest doctorRequest){
        DoctorEntity doctorEntity = getDoctorEntityById(id);

        // Actualizar los datos del usuario
        userDetailServiceImpl.updateUsuario(doctorEntity.getUsuario().getId(), doctorRequest.userRequest());

        // Actualizar los datos del doctor
        actualizarCamposDoctor(doctorEntity, doctorRequest.doctorDetails());
        
        return doctorMapper.toDoctorDTO(doctorRepository.save(doctorEntity));
    }

    @Transactional
    public String deleteDoctor(Long id){
        DoctorEntity doctor = getDoctorEntityById(id);
        
        UserEntity user = doctor.getUsuario();
        if (user == null){
            throw new ResourceNotFoundException("Usuario asociado al doctor no encontrado.");
        }

        userDetailServiceImpl.deleteUser(doctor.getUsuario().getId());
        return "El doctor con id " + id + " ha sido deshabilitado";
    }

    public DoctorEntity mapToDoctor(AuthCreateDoctorDetailsRequest doctorDetails){

        Set<EspecialidadEntity> especialidades = obtenerEspecialidades(doctorDetails);

        return DoctorEntity.builder()
        .especialidades(especialidades)
        .consultorio(doctorDetails.consultorio())
        .licenciaMedica(doctorDetails.licenciaMedica())
        .aniosExperiencia(doctorDetails.aniosExperiencia())
        .universidad(doctorDetails.universidad())
        .precioConsulta(doctorDetails.precioConsulta())
        .biografia(doctorDetails.biografia())
        .build(); 
    }

    public void actualizarCamposDoctor(DoctorEntity doctorEntity, AuthCreateDoctorDetailsRequest doctorDetails){

        Set<EspecialidadEntity> especialidades = obtenerEspecialidades(doctorDetails);

        doctorEntity.setEspecialidades(especialidades);
        doctorEntity.setConsultorio(doctorDetails.consultorio());
        doctorEntity.setLicenciaMedica(doctorDetails.licenciaMedica());
        doctorEntity.setAniosExperiencia(doctorDetails.aniosExperiencia());
        doctorEntity.setUniversidad(doctorDetails.universidad());
        doctorEntity.setPrecioConsulta(doctorDetails.precioConsulta());
        doctorEntity.setBiografia(doctorDetails.biografia());
    }

    public Set<EspecialidadEntity> obtenerEspecialidades(AuthCreateDoctorDetailsRequest doctorDetails){
        return doctorDetails.especialidades().stream()
            .map(especialidad -> especialidadRepository.findByEspecialidad(especialidad.getEspecialidad())
                .orElseThrow(() -> new ResourceNotFoundException("Especialidad " + especialidad + " no encontrada.")))
            .collect(Collectors.toSet());
    }

}
