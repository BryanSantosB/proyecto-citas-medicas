package com.citasmedicas.spring.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.citasmedicas.spring.entities.EspecialidadEntity;
import com.citasmedicas.spring.entities.EspecialidadEnum;
import com.citasmedicas.spring.entities.GeneroEnum;
import com.citasmedicas.spring.entities.PermissionEntity;
import com.citasmedicas.spring.entities.RoleEntity;
import com.citasmedicas.spring.entities.RoleEnum;
import com.citasmedicas.spring.entities.UserEntity;
import com.citasmedicas.spring.repository.DisponibilidadRepository;
import com.citasmedicas.spring.repository.EspecialidadRepository;
import com.citasmedicas.spring.repository.PermisosRepository;
import com.citasmedicas.spring.repository.RoleRepository;
import com.citasmedicas.spring.repository.UserRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Set;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner init(UserRepository userRepository,
                           RoleRepository roleRepository,
                           PermisosRepository permissionRepository,
                           PasswordEncoder passwordEncoder,
                           DisponibilidadRepository disponibilidadRepository,
                           EspecialidadRepository especialidadRepository ) {
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

            // Crear especialidades si no existen
            createEspecialidadIfNotExists(especialidadRepository, EspecialidadEnum.CARDIOLOGIA);
            createEspecialidadIfNotExists(especialidadRepository, EspecialidadEnum.DERMATOLOGIA);
            createEspecialidadIfNotExists(especialidadRepository, EspecialidadEnum.ENDOCRINOLOGIA);
            createEspecialidadIfNotExists(especialidadRepository, EspecialidadEnum.GINECOLOGIA);
            createEspecialidadIfNotExists(especialidadRepository, EspecialidadEnum.NEUROLOGIA);
            createEspecialidadIfNotExists(especialidadRepository, EspecialidadEnum.OFTALMOLOGIA);
            createEspecialidadIfNotExists(especialidadRepository, EspecialidadEnum.PEDIATRIA);
            createEspecialidadIfNotExists(especialidadRepository, EspecialidadEnum.PSIQUIATRIA);
            createEspecialidadIfNotExists(especialidadRepository, EspecialidadEnum.TRAUMATOLOGIA);
            createEspecialidadIfNotExists(especialidadRepository, EspecialidadEnum.MEDICINA_GENERAL);

            // Crear usuarios base
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            createUserIfNotExists(userRepository, "admin", "admin","NombreAdmin", "ApellidoAdmin", "correoadmin@gmail.com", LocalDate.parse("15-05-1995", formatter), GeneroEnum.MASCULINO, "987654321", "Dirección",  adminRole, passwordEncoder);
            createUserIfNotExists(userRepository, "user", "user", "NombreAdmin", "ApellidoAdmin", "correouser@gmail.com", LocalDate.parse("16-06-1996", formatter), GeneroEnum.MASCULINO, "987654321", "Dirección", userRole, passwordEncoder);
            createUserIfNotExists(userRepository, "doctor", "doctor", "NombreAdmin", "ApellidoAdmin", "correodoctor@gmail.com", LocalDate.parse("17-07-1997", formatter), GeneroEnum.MASCULINO, "987654321", "Dirección", doctorRole, passwordEncoder);
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

    private EspecialidadEntity createEspecialidadIfNotExists(EspecialidadRepository repo, EspecialidadEnum especialidad) {
        return repo.findByEspecialidad(especialidad)
                .orElseGet(() -> {
                    EspecialidadEntity especialidadEnitity = EspecialidadEntity.builder()
                            .especialidad(especialidad)
                            .build();
                    return repo.save(especialidadEnitity);
                });
    }

    private void createUserIfNotExists(UserRepository userRepo, String username, String password, String nombres, String apellidos,
                                        String correoElectronico, LocalDate fechaNacimiento, GeneroEnum genero, String telefono, 
                                        String direccion,  RoleEntity role, PasswordEncoder passwordEncoder) {
        if (userRepo.findByUsername(username).isEmpty()) {
            UserEntity user = UserEntity.builder()
                        .username(username)
                        .password(passwordEncoder.encode(password))
                        .nombres(nombres)
                        .apellidos(apellidos)
                        .correoElectronico(correoElectronico)
                        .fechaNacimiento(fechaNacimiento)
                        .genero(genero)
                        .telefonoContacto(telefono)
                        .direccion(direccion)
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



