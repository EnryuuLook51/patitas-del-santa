package pe.edu.uns.patitas.controller;

import pe.edu.uns.patitas.model.Cita;
import pe.edu.uns.patitas.service.CitaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/citas")
public class CitaController {

    private final CitaService citaService;

    public CitaController(CitaService citaService) {
        this.citaService = citaService;
    }

    @GetMapping
    public ResponseEntity<List<Cita>> listar() {
        return ResponseEntity.ok(citaService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cita> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(citaService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<Cita> crear(@Valid @RequestBody Cita cita) {
        return ResponseEntity.status(HttpStatus.CREATED).body(citaService.crear(cita));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cita> actualizar(
            @PathVariable Long id, @Valid @RequestBody Cita cita) {
        return ResponseEntity.ok(citaService.actualizar(id, cita));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        citaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/veterinario/{id}/programadas")
    public ResponseEntity<List<Cita>> listarProgramadasPorVeterinario(
            @PathVariable Long id) {
        return ResponseEntity.ok(citaService.listarProgramadasPorVeterinario(id));
    }
}
