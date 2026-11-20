package pe.edu.uns.patitas.service;

import pe.edu.uns.patitas.exception.RecursoNoEncontradoException;
import pe.edu.uns.patitas.exception.ReglaNegocioException;
import pe.edu.uns.patitas.model.Cita;
import pe.edu.uns.patitas.model.EstadoCita;
import pe.edu.uns.patitas.model.Mascota;
import pe.edu.uns.patitas.model.Veterinario;
import pe.edu.uns.patitas.repository.CitaRepository;
import pe.edu.uns.patitas.repository.MascotaRepository;
import pe.edu.uns.patitas.repository.VeterinarioRepository;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CitaService {
    private final CitaRepository citaRepository;
    private final MascotaRepository mascotaRepository;
    private final VeterinarioRepository veterinarioRepository;

    public CitaService(CitaRepository citaRepository, MascotaRepository mascotaRepository,
                       VeterinarioRepository veterinarioRepository) {
        this.citaRepository = citaRepository;
        this.mascotaRepository = mascotaRepository;
        this.veterinarioRepository = veterinarioRepository;
    }
    public List<Cita> listar() { return citaRepository.findAll(); }
    public Cita buscarPorId(Long id) {
        return citaRepository.findById(id).orElseThrow(
            () -> new RecursoNoEncontradoException("No existe la cita con id " + id));
    }
    public Cita crear(Cita datos) {
        validarFecha(datos.getFechaHora());
        validarCosto(datos.getCosto());
        datos.setMascota(buscarMascota(datos.getMascota()));
        datos.setVeterinario(buscarVeterinario(datos.getVeterinario()));
        datos.setEstado(EstadoCita.PROGRAMADA);
        datos.setId(null);
        return citaRepository.save(datos);
    }
    public Cita actualizar(Long id, Cita datos) {
        Cita actual = buscarPorId(id);
        // Solo se valida la fecha si se reprograma: una cita puede marcarse ATENDIDA después de su hora.
        if (datos.getFechaHora() == null || !datos.getFechaHora().isEqual(actual.getFechaHora())) {
            validarFecha(datos.getFechaHora());
        }
        validarCosto(datos.getCosto());
        Mascota mascota = buscarMascota(datos.getMascota());
        Veterinario veterinario = buscarVeterinario(datos.getVeterinario());
        actual.setFechaHora(datos.getFechaHora());
        actual.setMotivo(datos.getMotivo());
        actual.setEstado(datos.getEstado() == null ? actual.getEstado() : datos.getEstado());
        actual.setDiagnostico(datos.getDiagnostico());
        actual.setCosto(datos.getCosto());
        actual.setMascota(mascota);
        actual.setVeterinario(veterinario);
        return citaRepository.save(actual);
    }
    public void eliminar(Long id) { citaRepository.delete(buscarPorId(id)); }
    public List<Cita> listarProgramadasPorVeterinario(Long id) {
        buscarVeterinarioPorId(id);
        return citaRepository.findByVeterinarioIdAndEstado(id, EstadoCita.PROGRAMADA);
    }
    private void validarFecha(LocalDateTime fecha) {
        if (fecha == null) throw new ReglaNegocioException("La fecha y hora de la cita son obligatorias");
        if (fecha.isBefore(LocalDateTime.now()))
            throw new ReglaNegocioException("La fecha y hora de la cita no pueden estar en el pasado");
    }
    private void validarCosto(BigDecimal costo) {
        if (costo != null && costo.compareTo(BigDecimal.ZERO) < 0)
            throw new ReglaNegocioException("El costo de la cita no puede ser negativo");
    }
    private Mascota buscarMascota(Mascota ref) {
        if (ref == null || ref.getId() == null)
            throw new ReglaNegocioException("Debe indicar el id de una mascota");
        return mascotaRepository.findById(ref.getId()).orElseThrow(
            () -> new RecursoNoEncontradoException("No existe la mascota con id " + ref.getId()));
    }
    private Veterinario buscarVeterinario(Veterinario ref) {
        if (ref == null || ref.getId() == null)
            throw new ReglaNegocioException("Debe indicar el id de un veterinario");
        return buscarVeterinarioPorId(ref.getId());
    }
    private Veterinario buscarVeterinarioPorId(Long id) {
        return veterinarioRepository.findById(id).orElseThrow(
            () -> new RecursoNoEncontradoException("No existe el veterinario con id " + id));
    }
}
