package pe.edu.uns.patitas.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.uns.patitas.exception.RecursoNoEncontradoException;
import pe.edu.uns.patitas.exception.ReglaNegocioException;
import pe.edu.uns.patitas.model.Dueno;
import pe.edu.uns.patitas.model.Mascota;
import pe.edu.uns.patitas.repository.DuenoRepository;
import pe.edu.uns.patitas.repository.MascotaRepository;

import java.util.List;

@Service
public class MascotaService {

    private final MascotaRepository mascotaRepository;
    private final DuenoRepository duenoRepository;

    public MascotaService(MascotaRepository mascotaRepository, DuenoRepository duenoRepository) {
        this.mascotaRepository = mascotaRepository;
        this.duenoRepository = duenoRepository;
    }

    @Transactional(readOnly = true)
    public List<Mascota> listar() {
        return mascotaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Mascota obtener(Long id) {
        return mascotaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la mascota con id " + id));
    }

    /** Todas las mascotas de un dueño a partir de su DNI. 404 si el DNI no está registrado. */
    @Transactional(readOnly = true)
    public List<Mascota> listarPorDniDueno(String dni) {
        if (!duenoRepository.existsByDni(dni)) {
            throw new RecursoNoEncontradoException("No existe el dueño con DNI " + dni);
        }
        return mascotaRepository.findByDuenoDni(dni);
    }

    @Transactional
    public Mascota crear(Mascota mascota) {
        mascota.setId(null);
        Dueno dueno = resolverDueno(mascota.getDueno());
        mascota.setDueno(dueno);
        dueno.getMascotas().add(mascota); // mantener sincronizados ambos lados de la relación
        return mascotaRepository.save(mascota);
    }

    /** Actualiza los datos de la mascota (el peso cambia en cada visita). También permite cambiar de dueño. */
    @Transactional
    public Mascota actualizar(Long id, Mascota datos) {
        Mascota mascota = obtener(id);
        mascota.setNombre(datos.getNombre());
        mascota.setEspecie(datos.getEspecie());
        mascota.setRaza(datos.getRaza());
        mascota.setSexo(datos.getSexo());
        mascota.setFechaNacimiento(datos.getFechaNacimiento());
        mascota.setPeso(datos.getPeso());

        Dueno nuevoDueno = resolverDueno(datos.getDueno());
        if (!nuevoDueno.getId().equals(mascota.getDueno().getId())) {
            mascota.getDueno().getMascotas().remove(mascota);
            nuevoDueno.getMascotas().add(mascota);
            mascota.setDueno(nuevoDueno);
        }
        return mascotaRepository.save(mascota);
    }

    /** Al eliminar una mascota se eliminan también sus citas (cascade). */
    @Transactional
    public void eliminar(Long id) {
        Mascota mascota = obtener(id);
        mascota.getDueno().getMascotas().remove(mascota);
        mascotaRepository.delete(mascota);
    }

    /** Regla del caso: ninguna mascota se registra sin un dueño existente. */
    private Dueno resolverDueno(Dueno dueno) {
        if (dueno == null || dueno.getId() == null) {
            throw new ReglaNegocioException("La mascota debe tener un dueño: envía \"dueno\": { \"id\": ... }");
        }
        return duenoRepository.findById(dueno.getId())
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el dueño con id " + dueno.getId()));
    }
}
