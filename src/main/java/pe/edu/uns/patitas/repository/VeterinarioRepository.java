package pe.edu.uns.patitas.repository;

import pe.edu.uns.patitas.model.Veterinario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface VeterinarioRepository extends JpaRepository<Veterinario, Long> {
    Optional<Veterinario> findByColegiatura(String colegiatura);
}
