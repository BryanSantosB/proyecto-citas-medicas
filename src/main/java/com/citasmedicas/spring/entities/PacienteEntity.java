package com.citasmedicas.spring.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;


import com.fasterxml.jackson.annotation.JsonFormat;

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

    private String nombres;
    private String apellidos;

    @Column(unique = true, nullable = false, name = "correo_electronico")
    private String correoElectronico;

    @Column(nullable = false, name = "fecha_nacimiento")
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate fechaNacimiento;

    @Column(nullable = false)
    private String genero; 

    @Column(name = "telefono_contacto", nullable = false)
    private String telefonoContacto;

    private String direccion;

    @Column(name = "grupo_sanguineo")
    private String grupoSanguineo;

    private String alergias; 

    @Column(name = "enfermedades_cronicas")
    private String enfermedadesCronicas; 

    @Column(name = "numero_historia_clinica", unique = true)
    private String numeroHistoriaClinica; 

    @Column(length = 500, name = "informacion_adicional")
    private String informacionAdicional;

}
