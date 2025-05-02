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

import com.citasmedicas.spring.dto.CreateCitaRequest;
import com.citasmedicas.spring.dto.EstadoCitaRequest;
import com.citasmedicas.spring.entities.CitaEntity;
import com.citasmedicas.spring.services.CitasService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/citas")
public class CitasController {

    @Autowired
    private CitasService citasService;

    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR')")
    @GetMapping
    public ResponseEntity<List<CitaEntity>> getAllCitas(){
        return new ResponseEntity<>(citasService.getAllCitas(), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR')")
    @PostMapping
    public ResponseEntity<CitaEntity> createCita(@RequestBody @Valid CreateCitaRequest citaRequest){
        return new ResponseEntity<>(citasService.createCitaEntity(citaRequest), HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR') or hasRole('PACIENTE')")
    @GetMapping("/{id}")
    public ResponseEntity<CitaEntity> getCitasById(@PathVariable Long id){
        return new ResponseEntity<>(citasService.getCitaById(id), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR')")
    @PutMapping("/{id}")
    public ResponseEntity<CitaEntity> updateCita(@PathVariable Long id, @RequestBody @Valid EstadoCitaRequest citaRequest){
        return new ResponseEntity<>(citasService.updateCitaEntity(id, citaRequest.nuevoEstado()), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCita(@PathVariable Long id){
        return new ResponseEntity<>(citasService.deleteCitaEntity(id), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR') or hasRole('PACIENTE')")
    @GetMapping("/paciente/{id}")
    public ResponseEntity<List<CitaEntity>> getCitasByPaciente(@PathVariable Long id){
        return new ResponseEntity<>(citasService.getCitasByPaciente(id), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR')")
    @GetMapping("/doctor/{id}")
    public ResponseEntity<List<CitaEntity>> getCitasByDoctor(@PathVariable Long id){
        return new ResponseEntity<>(citasService.getCitasByDoctor(id), HttpStatus.OK);
    }
}
 