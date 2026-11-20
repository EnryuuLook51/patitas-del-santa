package pe.edu.uns.patitas.service;

import pe.edu.uns.patitas.exception.RecursoNoEncontradoException;
import pe.edu.uns.patitas.exception.ReglaNegocioException;
import pe.edu.uns.patitas.model.Veterinario;
import pe.edu.uns.patitas.repository.CitaRepository;
import pe.edu.uns.patitas.repository.VeterinarioRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class VeterinarioService {
    private final VeterinarioRepository veterinarioRepository;
    private final CitaRepository citaRepository;

    public VeterinarioService(VeterinarioRepository veterinarioRepository, CitaRepository citaRepository) {
        this.veterinarioRepository = veterinarioRepository;
        this.citaRepository = citaRepository;
    }
    public List<Veterinario> listar() { return veterinarioRepository.findAll(); }
    public Veterinario buscarPorId(Long id) {
        return veterinarioRepository.findById(id).orElseThrow(
            () -> new RecursoNoEncontradoException("No existe el veterinario con id " + id));
    }
    public Veterinario crear(Veterinario veterinario) {
        validarColegiatura(veterinario.getColegiatura(), null);
        return veterinarioRepository.save(veterinario);
    }
    public Veterinario actualizar(Long id, Veterinario datos) {
        Veterinario actual = buscarPorId(id);
        validarColegiatura(datos.getColegiatura(), id);
        actual.setColegiatura(datos.getColegiatura());
        actual.setNombres(datos.getNombres());
        actual.setApellidos(datos.getApellidos());
        actual.setEspecialidad(datos.getEspecialidad());
        actual.setActivo(datos.getActivo());
        return veterinarioRepository.save(actual);
    }
    public void eliminar(Long id) {
        Veterinario veterinario = buscarPorId(id);
        if (citaRepository.existsByVeterinarioId(id))
            throw new ReglaNegocioException(
                "El veterinario tiene citas registradas; desactívelo (activo=false) en lugar de eliminarlo");
        veterinarioRepository.delete(veterinario);
    }
    private void validarColegiatura(String colegiatura, Long idActual) {
        if (colegiatura == null || colegiatura.isBlank())
            throw new ReglaNegocioException("La colegiatura es obligatoria");
        veterinarioRepository.findByColegiatura(colegiatura).ifPresent(encontrado -> {
            if (idActual == null || !encontrado.getId().equals(idActual))
                throw new ReglaNegocioException("La colegiatura ya se encuentra registrada");
        });
    }
}
