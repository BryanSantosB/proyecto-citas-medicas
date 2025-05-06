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

import com.citasmedicas.spring.dto.AuthCreateDoctorRequest;
import com.citasmedicas.spring.dto.AuthResponse;
import com.citasmedicas.spring.dto.DoctorDTO;
import com.citasmedicas.spring.services.DoctorService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/doctors")
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    // Crear un nuevo doctor
    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AuthResponse> createDoctor(@RequestBody @Valid AuthCreateDoctorRequest doctorRequest) {
        return new ResponseEntity<>(doctorService.createDoctorWithUser(doctorRequest), HttpStatus.CREATED);
    }

    // Obtener todos los doctores
    @GetMapping("/getdoctores")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<DoctorDTO>> getAllDoctors(){
        return new ResponseEntity<>(doctorService.getAllDoctores(), HttpStatus.OK);
    }

    // Obtener doctor por ID
    @GetMapping("/getdoctor/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DoctorDTO> getDoctorById(@PathVariable Long id){
        return new ResponseEntity<>(doctorService.getDoctorDtoById(id), HttpStatus.OK);
    }

    // Actualizar doctor
    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR')")
    public ResponseEntity<DoctorDTO> updateDoctor(@PathVariable Long id, @RequestBody @Valid AuthCreateDoctorRequest doctorRequest){
        return new ResponseEntity<>(doctorService.updateDoctor(id, doctorRequest), HttpStatus.OK);
    }

    // Eliminar doctor
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteDoctor(@PathVariable Long id){
        return new ResponseEntity<>(doctorService.deleteDoctor(id), HttpStatus.OK);
    }



}
