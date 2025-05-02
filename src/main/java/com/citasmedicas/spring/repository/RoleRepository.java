package com.citasmedicas.spring.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.citasmedicas.spring.entities.RoleEntity;
import com.citasmedicas.spring.entities.RoleEnum;

@Repository           
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {

    List<RoleEntity> findRoleEntitiesByRoleEnumIn(List<String> roleNames);
    Optional<RoleEntity> findByRoleEnum(RoleEnum roleEnum); // Método para buscar un rol por su nombre
} 
