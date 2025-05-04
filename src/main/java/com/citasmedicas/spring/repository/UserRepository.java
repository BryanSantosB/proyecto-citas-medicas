package com.citasmedicas.spring.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.citasmedicas.spring.entities.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    
    Optional<UserEntity> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByCorreoElectronico(String correo);

    Optional<UserEntity> findByCorreoElectronico(String correo);

}
