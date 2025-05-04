package com.citasmedicas.spring.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.citasmedicas.spring.dto.AuthCreateRoleRequest;
import com.citasmedicas.spring.dto.AuthCreateUserRequest;
import com.citasmedicas.spring.dto.AuthLoginRequest;
import com.citasmedicas.spring.dto.AuthResponse;
import com.citasmedicas.spring.dto.UserDTO;
import com.citasmedicas.spring.dto.mappers.UsuarioMapper;
import com.citasmedicas.spring.entities.RoleEntity;
import com.citasmedicas.spring.entities.UserEntity;
import com.citasmedicas.spring.exceptions.BadRequestException;
import com.citasmedicas.spring.exceptions.ResourceNotFoundException;
import com.citasmedicas.spring.repository.RoleRepository;
import com.citasmedicas.spring.repository.UserRepository;
import com.citasmedicas.spring.util.JwtUtil;

@Service
public class UserDetailServiceImpl implements UserDetailsService{

    @Autowired
    private JwtUtil  jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UsuarioMapper usuarioMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        
        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("El usuario " + username + " no existe."));

        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();

        userEntity.getRoles().forEach(role -> 
                authorityList.add(new SimpleGrantedAuthority("ROLE_".concat(role.getRoleEnum().name()))));

        userEntity.getRoles().stream().flatMap(role -> role.getPermissionList().stream())
                .forEach(permission -> authorityList.add(new SimpleGrantedAuthority(permission.getName())));

        return new User(userEntity.getUsername(), userEntity.getPassword(), 
                        userEntity.isEnabled(), userEntity.isAccountNoExpired(), 
                        userEntity.isCredentialsNoExpired(), userEntity.isAccountNoLocked(), 
                        authorityList);
    }

    public AuthResponse loginUser(AuthLoginRequest authLoginRequest ){
        String username = authLoginRequest.username();
        String password = authLoginRequest.password();

        Authentication authentication = this.authenticate(username, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = jwtUtil.createToken(authentication);

        AuthResponse authResponse = new AuthResponse(username, "User loged successfuly", accessToken, true);

        return authResponse;
    }

    public Authentication authenticate(String username, String password){
        UserDetails userdetails = this.loadUserByUsername(username);
        
        if(userdetails == null){
                throw new BadCredentialsException("Invalid username or password.");
        }

        if(!passwordEncoder.matches(password, userdetails.getPassword())){
                throw new BadCredentialsException("Invalid password.");
        }

        return new UsernamePasswordAuthenticationToken(username, userdetails.getPassword(), userdetails.getAuthorities());
    }

    public AuthResponse createUser(AuthCreateUserRequest authCreateUserRequest){
        List<String> roleRequest = authCreateUserRequest.roleRequest() != null 
                                        ? authCreateUserRequest.roleRequest().roleListName() 
                                        : List.of("USER");

        Set<RoleEntity> rolesEntitySet = roleRepository.findRoleEntitiesByRoleEnumIn(roleRequest).stream().collect(Collectors.toSet());
        
        if(rolesEntitySet.isEmpty()){
            throw new IllegalArgumentException("No roles found");
        }

        UserEntity userEntity = createUserEntity(authCreateUserRequest, rolesEntitySet);

        UserEntity userCreated = userRepository.save(userEntity);

        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();
        userCreated.getRoles().forEach(role -> 
                authorityList.add(new SimpleGrantedAuthority("ROLE_".concat(role.getRoleEnum().name()))));

        userCreated.getRoles()
                .stream()
                .flatMap(role -> role.getPermissionList().stream())
                .forEach(permission -> authorityList.add(new SimpleGrantedAuthority(permission.getName())));

        Authentication authentication = new UsernamePasswordAuthenticationToken(userCreated.getUsername(), userCreated.getPassword(), authorityList);

        String accessToken = jwtUtil.createToken(authentication);

        AuthResponse authResponse = new AuthResponse(userCreated.getUsername(), "User created successfuly", accessToken, true);

        return authResponse;

    }

    //-----------------------------------

    public UserDTO getUserById(Long id){
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("El usuario con id " + id + " no existe."));

        return usuarioMapper.toUserDTO(userEntity);
    }

    public AuthCreateUserRequest asignarRolUserRequest(AuthCreateUserRequest authCreateUserRequest, String role){
        return new AuthCreateUserRequest(authCreateUserRequest.username(), 
                                         authCreateUserRequest.password(), 
                                         authCreateUserRequest.nombres(), 
                                         authCreateUserRequest.apellidos(), 
                                         authCreateUserRequest.correoElectronico(), 
                                         authCreateUserRequest.fechaNacimiento(), 
                                         authCreateUserRequest.genero(), 
                                         authCreateUserRequest.telefonoContacto(), 
                                         authCreateUserRequest.direccion(),
            new AuthCreateRoleRequest(List.of(role.toUpperCase())));
    }

    public void verificarExistenciaUsuarioCorreo(String username, String correo){
        if(userRepository.existsByUsername(username)){
            throw new BadRequestException("El nombre de usuario ya existe.");
        }
        if(userRepository.existsByCorreoElectronico(correo)){
            throw new BadRequestException("El correo electrónico ya está en uso.");
        }
    }

    public void verificarCorreoUnico(String newCorreo, UserEntity userEntity){
        userRepository.findByCorreoElectronico(newCorreo)
            .ifPresent(existingPaciente -> {
                if (!existingPaciente.getId().equals(userEntity.getId())) {
                    throw new BadRequestException("El correo electrónico ya está en uso.");
                }
            });
    }

    public void verificarUsuarioUnico(String username, UserEntity userEntity){
        userRepository.findByUsername(username)
            .ifPresent(existingPaciente -> {
                if (!existingPaciente.getId().equals(userEntity.getId())) {
                    throw new BadRequestException("El correo electrónico ya está en uso.");
                }
            });
    }

    public UserDTO updateUsuario(Long id, AuthCreateUserRequest authCreateUserRequest){
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("El usuario con id " + id + " no existe."));

        // Verificar si el correo y el usuario son unicos
        verificarCorreoUnico(authCreateUserRequest.correoElectronico(), userEntity);
        verificarUsuarioUnico(authCreateUserRequest.username(), userEntity);
        
        // Actualizar los datos del usuario 
        updateUserFields(userEntity, authCreateUserRequest);

        return usuarioMapper.toUserDTO(userRepository.save(userEntity));
    }

    public UserEntity createUserEntity(AuthCreateUserRequest authCreateUserRequest, Set<RoleEntity> rolesEntitySet){
        return UserEntity.builder()
            .username(authCreateUserRequest.username())
            .password(passwordEncoder.encode(authCreateUserRequest.password()))
            .nombres(authCreateUserRequest.nombres())
            .apellidos(authCreateUserRequest.apellidos())
            .correoElectronico(authCreateUserRequest.correoElectronico())
            .fechaNacimiento(authCreateUserRequest.fechaNacimiento())
            .genero(authCreateUserRequest.genero())
            .telefonoContacto(authCreateUserRequest.telefonoContacto())
            .direccion(authCreateUserRequest.direccion())
            .roles(rolesEntitySet)
            .isEnabled(true)
            .accountNoLocked(true)
            .accountNoExpired(true)
            .credentialsNoExpired(true)
            .build();
    }

    public void updateUserFields(UserEntity userEntity, AuthCreateUserRequest authCreateUserRequest){
        userEntity.setUsername(authCreateUserRequest.username());
        userEntity.setNombres(authCreateUserRequest.nombres());
        userEntity.setApellidos(authCreateUserRequest.apellidos());
        userEntity.setCorreoElectronico(authCreateUserRequest.correoElectronico());
        userEntity.setFechaNacimiento(authCreateUserRequest.fechaNacimiento());
        userEntity.setGenero(authCreateUserRequest.genero());
        userEntity.setTelefonoContacto(authCreateUserRequest.telefonoContacto());
        userEntity.setDireccion(authCreateUserRequest.direccion());
    }

    public void deleteUser(Long id){
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("El usuario con id " + id + " no existe."));
        verificarEstadoUsuario(userEntity.isEnabled());
        userEntity.setEnabled(false);
        userRepository.save(userEntity);
    }

    public void verificarEstadoUsuario(boolean estado){
        if(!estado){
            throw new BadRequestException("El usuario no se encuentra activo.");
        }
    }
} 
