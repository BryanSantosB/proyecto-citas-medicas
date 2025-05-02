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

import com.citasmedicas.spring.dto.CreateDisponibilidadRequest;
import com.citasmedicas.spring.dto.EstadoRequest;
import com.citasmedicas.spring.dto.FechaRequest;
import com.citasmedicas.spring.entities.DisponibilidadEntity;
import com.citasmedicas.spring.services.DisponibilidadService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/disponibilidad")
public class DisponibilidadController {

    @Autowired
    private DisponibilidadService disponibilidadService;

    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR')")
    @PostMapping("/createdisponibilidad")
    public ResponseEntity<DisponibilidadEntity> createDisponibilidad(@RequestBody @Valid CreateDisponibilidadRequest disponibilidadDetails){
        return new ResponseEntity<>(disponibilidadService.createDisponibilidad(disponibilidadDetails), HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR')")
    @GetMapping("/getdisponibilidades")
    public ResponseEntity<List<DisponibilidadEntity>> getAllDisponibilidad(){
        return new ResponseEntity<>(disponibilidadService.getAllDisponibilidad(), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR')")
    @GetMapping("/getdisponibilidad/{id}")
    public ResponseEntity<DisponibilidadEntity> getDisponibilidadById(@PathVariable Long id){
        return new ResponseEntity<>(disponibilidadService.getDisponibilidadById(id), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR')")
    @PutMapping("/updatedisponibilidad/{id}")
    public ResponseEntity<DisponibilidadEntity> updateDisponibilidad(@PathVariable Long id, @RequestBody @Valid EstadoRequest nuevoEstado){
        return new ResponseEntity<>(disponibilidadService.updateDisponibilidad(id, nuevoEstado.nuevoEstado()), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR')")
    @DeleteMapping("/deletedisponibilidad/{id}")
    public ResponseEntity<String> deleteDisponibilidad(@PathVariable Long id){
        return new ResponseEntity<>(disponibilidadService.deleteDisponibilidad(id), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR') or hasRole('PACIENTE')")
    @GetMapping("/getdisponibilidadbyfecha")
    public ResponseEntity<List<DisponibilidadEntity>> getDisponibilidadByFecha(@RequestBody FechaRequest fecha){
        return new ResponseEntity<>(disponibilidadService.getDisponibilidadByFecha(fecha.fecha()), HttpStatus.OK);
    }

}
