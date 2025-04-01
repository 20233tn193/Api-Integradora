package gtf.integradora.controllers;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gtf.integradora.entity.Dueno;
import gtf.integradora.services.DuenoService;

@RestController
@RequestMapping("/api/duenos")
public class DuenoController {

    private final DuenoService duenoService;

    public DuenoController(DuenoService duenoService) {
        this.duenoService = duenoService;
    }

    @PostMapping
    public ResponseEntity<Dueno> crear(@RequestBody Dueno dueno) {
        if (duenoService.obtenerPorCorreo(dueno.getCorreo()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
        return ResponseEntity.ok(duenoService.crearDueno(dueno));
    }

    @GetMapping
    public ResponseEntity<List<Dueno>> obtenerTodos() {
        return ResponseEntity.ok(duenoService.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Dueno> obtenerPorId(@PathVariable String id) {
        return duenoService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Dueno> actualizar(@PathVariable String id, @RequestBody Dueno dueno) {
        return ResponseEntity.ok(duenoService.actualizarDueno(id, dueno));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        duenoService.eliminarDueno(id);
        return ResponseEntity.noContent().build();
    }
}
