package com.citasmedicas.spring.dto.mappers;

import org.mapstruct.Mapper;

import com.citasmedicas.spring.dto.UserDTO;
import com.citasmedicas.spring.entities.UserEntity;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    UserDTO toUserDTO(UserEntity userEntity);

} 
