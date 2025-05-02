package com.citasmedicas.spring.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.citasmedicas.spring.entities.PermissionEntity;

public interface PermisosRepository extends JpaRepository<PermissionEntity, Long>{

    Optional<PermissionEntity> findByName(String name);  // Método para buscar un permiso por su nombre
    
} 
