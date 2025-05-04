package com.citasmedicas.spring.dto.mappers;

import org.mapstruct.Mapper;

import com.citasmedicas.spring.dto.PacienteDTO;
import com.citasmedicas.spring.entities.PacienteEntity;

@Mapper(componentModel = "spring", uses = {UsuarioMapper.class})
public interface PacienteMapper {

    PacienteDTO toPacienteDTO(PacienteEntity pacienteEntity);

}
