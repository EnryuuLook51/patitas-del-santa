package pe.edu.uns.patitas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.uns.patitas.model.Especie;
import pe.edu.uns.patitas.model.Mascota;

import java.util.List;

@Repository
public interface MascotaRepository extends JpaRepository<Mascota, Long> {

    /**
     * Consulta personalizada del caso: todas las mascotas de un dueño a partir de su DNI.
     * Endpoint: GET /api/mascotas/dueno/{dni}
     */
    @Query("SELECT m FROM Mascota m WHERE m.dueno.dni = :dni ORDER BY m.nombre")
    List<Mascota> findByDuenoDni(@Param("dni") String dni);

    /** Mascotas de un dueño por su id. */
    List<Mascota> findByDuenoId(Long duenoId);

    /** Mascotas por especie (PERRO, GATO, ...). */
    List<Mascota> findByEspecie(Especie especie);
}
