package com.citasmedicas.spring.dto.mappers;

import org.mapstruct.Mapper;

import com.citasmedicas.spring.dto.DisponibilidadDTO;
import com.citasmedicas.spring.entities.DisponibilidadEntity;

@Mapper(componentModel = "spring", uses = {DoctorMapper.class})
public interface DisponibilidadMapper {

    DisponibilidadDTO toDisponibilidadDTO(DisponibilidadEntity disponibilidadEntity);

}
