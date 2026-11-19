package pe.edu.uns.patitas.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "citas")
public class Cita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** La regla "no puede ser una fecha pasada" se valida en CitaService. */
    @NotNull
    @Column(nullable = false)
    private LocalDateTime fechaHora;

    @NotBlank
    @Column(nullable = false, length = 150)
    private String motivo;

    /** Toda cita nace en PROGRAMADA (lo asigna CitaService al crear). */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 12)
    private EstadoCita estado = EstadoCita.PROGRAMADA;

    @Column(length = 500)
    private String diagnostico;

    /** Costo en soles, nunca negativo. */
    @PositiveOrZero
    @Column(precision = 10, scale = 2)
    private BigDecimal costo;

    @NotNull
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "mascota_id", nullable = false)
    private Mascota mascota;

    @NotNull
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "veterinario_id", nullable = false)
    private Veterinario veterinario;

    public Cita() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public EstadoCita getEstado() { return estado; }
    public void setEstado(EstadoCita estado) { this.estado = estado; }

    public String getDiagnostico() { return diagnostico; }
    public void setDiagnostico(String diagnostico) { this.diagnostico = diagnostico; }

    public BigDecimal getCosto() { return costo; }
    public void setCosto(BigDecimal costo) { this.costo = costo; }

    public Mascota getMascota() { return mascota; }
    public void setMascota(Mascota mascota) { this.mascota = mascota; }

    public Veterinario getVeterinario() { return veterinario; }
    public void setVeterinario(Veterinario veterinario) { this.veterinario = veterinario; }
}
