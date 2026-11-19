package pe.edu.uns.patitas.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.uns.patitas.model.Dueno;
import pe.edu.uns.patitas.service.DuenoService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/duenos")
public class DuenoController {

    private final DuenoService duenoService;

    public DuenoController(DuenoService duenoService) {
        this.duenoService = duenoService;
    }

    @GetMapping
    public List<Dueno> listar() {
        return duenoService.listar();
    }

    @GetMapping("/{id}")
    public Dueno obtener(@PathVariable Long id) {
        return duenoService.obtener(id);
    }

    @GetMapping("/dni/{dni}")
    public Dueno obtenerPorDni(@PathVariable String dni) {
        return duenoService.obtenerPorDni(dni);
    }

    @PostMapping
    public ResponseEntity<Dueno> crear(@Valid @RequestBody Dueno dueno) {
        Dueno creado = duenoService.crear(dueno);
        return ResponseEntity.created(URI.create("/api/duenos/" + creado.getId())).body(creado);
    }

    @PutMapping("/{id}")
    public Dueno actualizar(@PathVariable Long id, @Valid @RequestBody Dueno dueno) {
        return duenoService.actualizar(id, dueno);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        duenoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
