package com.citasmedicas.spring.entities;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "citas")
public class CitaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(targetEntity = PacienteEntity.class)
    @JoinColumn(name = "paciente_id", nullable = false)
    private PacienteEntity paciente;

    @ManyToOne(targetEntity = DisponibilidadEntity.class)
    @JoinColumn(name = "disponibilidad_id", nullable = false)
    private DisponibilidadEntity disponibilidad;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private EstadoCitaEnum estado = EstadoCitaEnum.PENDIENTE;

    private String motivo;
    private String observaciones;
    
    // --- Información clínica ---
    @Column(length = 500)
    private String diagnostico;
    
    @Column(name = "sintomas_reportados", length = 1000)
    private String sintomasReportados;
    
    @Column(name = "indicaciones_post_consulta", length = 1000)
    private String indicacionesPostConsulta;
    
    // --- Información administrativa ---
    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pago")
    private MetodoPagoEnum metodoPago;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_pago")
    @Builder.Default
    private EstadoPagoEnum estadoPago = EstadoPagoEnum.PENDIENTE;
    
    @Column(name = "monto_cobrado", precision = 10, scale = 2)
    private BigDecimal montoCobrado;
    
    @Column(name = "usa_seguro")
    @Builder.Default
    private Boolean usaSeguro = false;
    
    @Column(name = "datos_seguro", length = 255)
    private String datosSeguro;
    
    // --- Información de seguimiento ---
    @Column(name = "requiere_seguimiento")
    @Builder.Default
    private Boolean requiereSeguimiento = false;
    
    @Column(name = "fecha_proxima_cita")
    private LocalDate fechaProximaCita;
    
    // --- Información de control ---
    @Column(name = "paciente_asistio")
    private Boolean pacienteAsistio;
    
    @Column(name = "calificacion_paciente")
    private Integer calificacionPaciente; // 1-5 estrellas
    
    @Column(name = "comentario_paciente", length = 500)
    private String comentarioPaciente;
    
    @Column(name = "duracion_real_minutos")
    private Integer duracionRealMinutos;

}
