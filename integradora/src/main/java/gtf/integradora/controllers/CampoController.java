package gtf.integradora.controllers;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import gtf.integradora.entity.Campo;
import gtf.integradora.services.CampoService;

@RestController
@RequestMapping("/api/campos")
public class CampoController {

    private final CampoService campoService;

    public CampoController(CampoService campoService) {
        this.campoService = campoService;
    }

    @PostMapping
    public ResponseEntity<Campo> crear(@RequestBody Campo campo) {
        return ResponseEntity.ok(campoService.crearCampo(campo));
    }

    @GetMapping
    public ResponseEntity<List<Campo>> obtenerTodos() {
        return ResponseEntity.ok(campoService.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Campo> obtenerPorId(@PathVariable String id) {
        return campoService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Campo> actualizar(@PathVariable String id, @RequestBody Campo campo) {
        return ResponseEntity.ok(campoService.actualizarCampo(id, campo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        campoService.eliminarCampo(id);
        return ResponseEntity.noContent().build();
    }
}