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

import com.citasmedicas.spring.dto.CreateSeguroMedicoRequest;
import com.citasmedicas.spring.dto.SeguroMedicoDTO;
import com.citasmedicas.spring.services.SeguroMedicoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/seguros")
public class SeguroMedicoController {

    @Autowired
    private SeguroMedicoService seguroMedicoService;

    @PreAuthorize("hasRole('ADMIN') or hasRole('PACIENTE') or hasRole('DOCTOR')")
    @PostMapping
    public ResponseEntity<SeguroMedicoDTO> createSeguroMedico(@RequestBody @Valid CreateSeguroMedicoRequest seguroMedicoRequest){
        return new ResponseEntity<>(seguroMedicoService.createSeguroMedico(seguroMedicoRequest), HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('PACIENTE') or hasRole('DOCTOR')")
    @GetMapping
    public ResponseEntity<List<SeguroMedicoDTO>> getSegurosMedicos() {
        return new ResponseEntity<>(seguroMedicoService.getAllSegurosMedico(), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('PACIENTE') or hasRole('DOCTOR')")
    @GetMapping("/paciente/{id}")
    public ResponseEntity<List<SeguroMedicoDTO>> getSegurosByPaciente(@PathVariable Long id) {
        return new ResponseEntity<>(seguroMedicoService.getSegurosMedicoDtoByPaciente(id), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('PACIENTE') or hasRole('DOCTOR')")
    @GetMapping("/{id}")
    public ResponseEntity<SeguroMedicoDTO> getSeguroById(@PathVariable Long id) {
        return new ResponseEntity<>(seguroMedicoService.getSeguroMedicoDtoById(id), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('PACIENTE') or hasRole('DOCTOR')")
    @PutMapping("/{id}")
    public ResponseEntity<SeguroMedicoDTO> updateSeguro(@PathVariable Long id, @RequestBody @Valid CreateSeguroMedicoRequest seguroMedicoRequest) {
        return new ResponseEntity<>(seguroMedicoService.updateSeguroMedico(id, seguroMedicoRequest), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('PACIENTE') or hasRole('DOCTOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSeguro(@PathVariable Long id) {
        return new ResponseEntity<>(seguroMedicoService.deleteSeguroMedico(id), HttpStatus.OK);
    }

}
