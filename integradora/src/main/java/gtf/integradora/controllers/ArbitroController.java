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
import gtf.integradora.entity.Arbitro;
import gtf.integradora.services.ArbitroService;

@RestController
@RequestMapping("/api/arbitros")
public class ArbitroController {

    private final ArbitroService arbitroService;

    public ArbitroController(ArbitroService arbitroService) {
        this.arbitroService = arbitroService;
    }

    @PostMapping
    public ResponseEntity<Arbitro> crear(@RequestBody Arbitro arbitro) {
        if (arbitroService.obtenerPorCorreo(arbitro.getCorreo()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
        return ResponseEntity.ok(arbitroService.crearArbitro(arbitro));
    }

    @GetMapping
    public ResponseEntity<List<Arbitro>> obtenerTodos() {
        return ResponseEntity.ok(arbitroService.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Arbitro> obtenerPorId(@PathVariable String id) {
        return arbitroService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Arbitro> actualizar(@PathVariable String id, @RequestBody Arbitro arbitro) {
        return ResponseEntity.ok(arbitroService.actualizarArbitro(id, arbitro));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        arbitroService.eliminarArbitro(id);
        return ResponseEntity.noContent().build();
    }
}