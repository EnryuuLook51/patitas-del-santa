package pe.edu.uns.patitas.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.uns.patitas.exception.RecursoNoEncontradoException;
import pe.edu.uns.patitas.exception.ReglaNegocioException;
import pe.edu.uns.patitas.model.Dueno;
import pe.edu.uns.patitas.repository.DuenoRepository;

import java.util.List;

@Service
public class DuenoService {

    private final DuenoRepository duenoRepository;

    public DuenoService(DuenoRepository duenoRepository) {
        this.duenoRepository = duenoRepository;
    }

    @Transactional(readOnly = true)
    public List<Dueno> listar() {
        return duenoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Dueno obtener(Long id) {
        return duenoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el dueño con id " + id));
    }

    @Transactional(readOnly = true)
    public Dueno obtenerPorDni(String dni) {
        return duenoRepository.findByDni(dni)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el dueño con DNI " + dni));
    }

    @Transactional
    public Dueno crear(Dueno dueno) {
        if (duenoRepository.existsByDni(dueno.getDni())) {
            throw new ReglaNegocioException("Ya existe un dueño registrado con DNI " + dueno.getDni());
        }
        dueno.setId(null); // el id lo genera la base de datos
        return duenoRepository.save(dueno);
    }

    @Transactional
    public Dueno actualizar(Long id, Dueno datos) {
        Dueno dueno = obtener(id);
        if (duenoRepository.existsByDniAndIdNot(datos.getDni(), id)) {
            throw new ReglaNegocioException("Ya existe otro dueño registrado con DNI " + datos.getDni());
        }
        dueno.setDni(datos.getDni());
        dueno.setNombres(datos.getNombres());
        dueno.setApellidos(datos.getApellidos());
        dueno.setCelular(datos.getCelular());
        dueno.setCorreo(datos.getCorreo());
        dueno.setDireccion(datos.getDireccion());
        return duenoRepository.save(dueno);
    }

    /** Al eliminar un dueño se eliminan también sus mascotas y las citas de estas (cascade). */
    @Transactional
    public void eliminar(Long id) {
        Dueno dueno = obtener(id);
        duenoRepository.delete(dueno);
    }
}
