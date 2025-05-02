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

import com.citasmedicas.spring.dto.AuthCreateUserRequest;
import com.citasmedicas.spring.dto.AuthLoginRequest;
import com.citasmedicas.spring.dto.AuthResponse;
import com.citasmedicas.spring.entities.RoleEntity;
import com.citasmedicas.spring.entities.UserEntity;
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
    UserRepository userRepository;

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
        String username = authCreateUserRequest.username();
        String password = authCreateUserRequest.password();
        List<String> roleRequest = authCreateUserRequest.roleRequest() != null 
                                        ? authCreateUserRequest.roleRequest().roleListName() 
                                        : List.of("USER");

        Set<RoleEntity> rolesEntitySet = roleRepository.findRoleEntitiesByRoleEnumIn(roleRequest).stream().collect(Collectors.toSet());
        
        if(rolesEntitySet.isEmpty()){
            throw new IllegalArgumentException("No roles found");
        }

        UserEntity userEntity = UserEntity.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .roles(rolesEntitySet)
                .isEnabled(true)
                .accountNoLocked(true)
                .accountNoExpired(true)
                .credentialsNoExpired(true)
                .build();

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
}
