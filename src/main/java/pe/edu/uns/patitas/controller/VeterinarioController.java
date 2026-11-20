package pe.edu.uns.patitas.controller;

import pe.edu.uns.patitas.model.Veterinario;
import pe.edu.uns.patitas.service.VeterinarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/veterinarios")
public class VeterinarioController {

    private final VeterinarioService veterinarioService;

    public VeterinarioController(VeterinarioService veterinarioService) {
        this.veterinarioService = veterinarioService;
    }

    @GetMapping
    public ResponseEntity<List<Veterinario>> listar() {
        return ResponseEntity.ok(veterinarioService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Veterinario> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(veterinarioService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<Veterinario> crear(@Valid @RequestBody Veterinario veterinario) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(veterinarioService.crear(veterinario));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Veterinario> actualizar(
            @PathVariable Long id, @Valid @RequestBody Veterinario veterinario) {
        return ResponseEntity.ok(veterinarioService.actualizar(id, veterinario));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        veterinarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
