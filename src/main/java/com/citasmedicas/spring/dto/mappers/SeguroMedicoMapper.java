package com.citasmedicas.spring.dto.mappers;

import org.mapstruct.Mapper;

import com.citasmedicas.spring.dto.SeguroMedicoDTO;
import com.citasmedicas.spring.entities.SeguroMedicoEntity;

@Mapper(componentModel = "spring", uses = {PacienteMapper.class})
public interface SeguroMedicoMapper {

    SeguroMedicoDTO toSeguroMedicoDTO(SeguroMedicoEntity seguroMedicoEntity);

}
