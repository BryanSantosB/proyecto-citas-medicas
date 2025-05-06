package com.citasmedicas.spring.entities;

import java.util.List;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pacientes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PacienteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "usuario_id", referencedColumnName = "id", unique = true)
    private UserEntity usuario;

    @Column(name = "grupo_sanguineo")
    private String grupoSanguineo;

    private String alergias; 

    @Column(name = "enfermedades_cronicas")
    private String enfermedadesCronicas; 

    @Column(name = "numero_historia_clinica", unique = true)
    private String numeroHistoriaClinica; 

    @Column(length = 500, name = "informacion_adicional")
    private String informacionAdicional;

    @OneToMany(mappedBy = "paciente")
    private List  <SeguroMedicoEntity> segurosMedicos;


}
