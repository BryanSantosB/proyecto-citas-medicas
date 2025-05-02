package com.citasmedicas.spring.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.citasmedicas.spring.entities.PermissionEntity;
import com.citasmedicas.spring.entities.RoleEntity;
import com.citasmedicas.spring.entities.RoleEnum;
import com.citasmedicas.spring.entities.UserEntity;
import com.citasmedicas.spring.repository.DisponibilidadRepository;
import com.citasmedicas.spring.repository.PermisosRepository;
import com.citasmedicas.spring.repository.RoleRepository;
import com.citasmedicas.spring.repository.UserRepository;

import java.util.Set;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner init(UserRepository userRepository,
                           RoleRepository roleRepository,
                           PermisosRepository permissionRepository,
                           PasswordEncoder passwordEncoder,
                           DisponibilidadRepository disponibilidadRepository) {
        return args -> {

            // Crear permisos si no existen
            PermissionEntity createPermission = createPermisionIfNotExists(permissionRepository, "CREATE");
            PermissionEntity readPermission = createPermisionIfNotExists(permissionRepository, "READ");
            PermissionEntity updatePermission = createPermisionIfNotExists(permissionRepository, "UPDATE");
            PermissionEntity deletePermission = createPermisionIfNotExists(permissionRepository, "DELETE");

            // Crear roles si no existen
            RoleEntity adminRole = createRoleIfNotExists(roleRepository, RoleEnum.ADMIN, Set.of(createPermission, readPermission, updatePermission, deletePermission));
            RoleEntity userRole = createRoleIfNotExists(roleRepository, RoleEnum.USER, Set.of(createPermission, readPermission));
            RoleEntity doctorRole = createRoleIfNotExists(roleRepository, RoleEnum.DOCTOR, Set.of(createPermission, readPermission, updatePermission));
            createRoleIfNotExists(roleRepository, RoleEnum.PACIENTE, Set.of(createPermission, readPermission));

            // Crear usuarios base
            createUserIfNotExists(userRepository, "admin", "admin", adminRole, passwordEncoder);
            createUserIfNotExists(userRepository, "user", "user", userRole, passwordEncoder);
            createUserIfNotExists(userRepository, "doctor", "doctor", doctorRole, passwordEncoder);
        };
    }

    private RoleEntity createRoleIfNotExists(RoleRepository repo, RoleEnum roleEnum, Set<PermissionEntity> permissions) {
        return repo.findByRoleEnum(roleEnum)
                .orElseGet(() -> {
                    RoleEntity role = RoleEntity.builder()
                            .roleEnum(roleEnum)
                            .permissionList(permissions)
                            .build();
                    return repo.save(role);  
                });
    }

    private PermissionEntity createPermisionIfNotExists(PermisosRepository repo, String name) {
        return repo.findByName(name)
                .orElseGet(() -> {
                    PermissionEntity permission = PermissionEntity.builder()
                            .name(name)
                            .build();
                    return repo.save(permission);
                });
    }

    private void createUserIfNotExists(UserRepository userRepo, String username, String password, RoleEntity role, PasswordEncoder passwordEncoder) {
        if (userRepo.findByUsername(username).isEmpty()) {
            UserEntity user = UserEntity.builder()
                        .username(username)
                        .password(passwordEncoder.encode(password))
                        .roles(Set.of(role))
                        .isEnabled(true)
                        .accountNoExpired(true)
                        .accountNoLocked(true)
                        .credentialsNoExpired(true)
                        .build();
            userRepo.save(user);
        }
    }

}



