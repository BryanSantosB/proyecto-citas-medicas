package com.citasmedicas.spring.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.citasmedicas.spring.dto.AuthCreatePacienteDetailsRequest;
import com.citasmedicas.spring.dto.AuthCreatePacienteRequest;
import com.citasmedicas.spring.dto.AuthResponse;
import com.citasmedicas.spring.entities.PacienteEntity;
import com.citasmedicas.spring.services.PacienteService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/pacientes")
public class PacienteController {

    @Autowired
    private PacienteService pacienteService;

    @PreAuthorize("permitAll()")
    @PostMapping("/create")
    public ResponseEntity<AuthResponse> createPaciente(@RequestBody @Valid AuthCreatePacienteRequest pacienteRequest){
        return new ResponseEntity<>(pacienteService.createPacienteWithUser(pacienteRequest), HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/get")
    public ResponseEntity<List<PacienteEntity>> getAllPacientes(){
        return new ResponseEntity<>(pacienteService.getAllPacientes(), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/getpaciente/{id}")
    public ResponseEntity<PacienteEntity> getPacienteById(@PathVariable Long id){
        return new ResponseEntity<>(pacienteService.getPacienteById(id), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR') or hasRole('PACIENTE')")
    @PutMapping("/updatepaciente/{id}")
    public ResponseEntity<PacienteEntity> updatePaciente(@PathVariable Long id, @RequestBody @Valid AuthCreatePacienteDetailsRequest pacienteDetails){
        return new ResponseEntity<>(pacienteService.updatePaciente(id, pacienteDetails), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/deletepaciente/{id}")
    public ResponseEntity<String> deletePaciente(@PathVariable Long id){
        return new ResponseEntity<>(pacienteService.deletePaciente(id), HttpStatus.OK);
    }
}
