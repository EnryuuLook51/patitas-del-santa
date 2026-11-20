package pe.edu.uns.patitas.repository;

import pe.edu.uns.patitas.model.Cita;
import pe.edu.uns.patitas.model.EstadoCita;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CitaRepository extends JpaRepository<Cita, Long> {
    List<Cita> findByVeterinarioIdAndEstado(Long veterinarioId, EstadoCita estado);

    boolean existsByVeterinarioId(Long veterinarioId);
}
