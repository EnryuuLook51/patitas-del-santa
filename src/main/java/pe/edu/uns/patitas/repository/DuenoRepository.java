package pe.edu.uns.patitas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.uns.patitas.model.Dueno;

import java.util.Optional;

@Repository
public interface DuenoRepository extends JpaRepository<Dueno, Long> {

    /** Busca un dueño por su DNI (único). */
    Optional<Dueno> findByDni(String dni);

    /** Para validar que el DNI no se repita al registrar. */
    boolean existsByDni(String dni);

    /** Para validar que el DNI no se repita al actualizar (excluye al propio dueño). */
    boolean existsByDniAndIdNot(String dni, Long id);
}
