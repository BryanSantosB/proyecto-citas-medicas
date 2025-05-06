package com.citasmedicas.spring.dto.mappers;

import org.mapstruct.Mapper;

import com.citasmedicas.spring.dto.DoctorDTO;
import com.citasmedicas.spring.entities.DoctorEntity;

@Mapper(componentModel = "spring", uses = {UsuarioMapper.class})
public interface DoctorMapper {

    DoctorDTO toDoctorDTO(DoctorEntity doctorEntity);

}
