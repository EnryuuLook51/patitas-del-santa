package pe.edu.uns.patitas.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "veterinarios")
public class Veterinario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Número de colegiatura del Colegio Médico Veterinario (único). */
    @NotBlank
    @Column(nullable = false, unique = true, length = 20)
    private String colegiatura;

    @NotBlank
    @Column(nullable = false, length = 80)
    private String nombres;

    @NotBlank
    @Column(nullable = false, length = 80)
    private String apellidos;

    @Column(length = 60)
    private String especialidad;

    @Column(nullable = false)
    private Boolean activo = true;

    @JsonIgnore
    @OneToMany(mappedBy = "veterinario")
    private List<Cita> citas = new ArrayList<>();

    public Veterinario() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getColegiatura() { return colegiatura; }
    public void setColegiatura(String colegiatura) { this.colegiatura = colegiatura; }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getEspecialidad() { return especialidad; }
    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }

    public List<Cita> getCitas() { return citas; }
    public void setCitas(List<Cita> citas) { this.citas = citas; }
}
