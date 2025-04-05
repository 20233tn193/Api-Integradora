package gtf.integradora.controllers;

import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import gtf.integradora.entity.Arbitro;
import gtf.integradora.entity.Usuario;
import gtf.integradora.repository.UsuarioRepository;
import gtf.integradora.services.ArbitroService;

@RestController
@RequestMapping("/api/arbitros")
public class ArbitroController {

    private final ArbitroService arbitroService;
    private final UsuarioRepository usuarioRepository;

    public ArbitroController(ArbitroService arbitroService, UsuarioRepository usuarioRepository) {
        this.arbitroService = arbitroService;
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Arbitro arbitro) {
        if (arbitro.getIdUsuario() == null) {
            return ResponseEntity.badRequest().body("El campo idUsuario es obligatorio.");
        }

        Optional<Usuario> usuarioOpt = usuarioRepository.findById(arbitro.getIdUsuario());
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontró el usuario asociado.");
        }

        Arbitro creado = arbitroService.crearArbitro(arbitro);
        return ResponseEntity.ok(creado);
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
    public ResponseEntity<?> actualizar(@PathVariable String id, @RequestBody Arbitro arbitro) {
        if (arbitro.getIdUsuario() == null) {
            return ResponseEntity.badRequest().body("El campo idUsuario es obligatorio.");
        }

        Optional<Usuario> usuarioOpt = usuarioRepository.findById(arbitro.getIdUsuario());
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontró el usuario asociado.");
        }

        return ResponseEntity.ok(arbitroService.actualizarArbitro(id, arbitro));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        arbitroService.eliminarArbitro(id);
        return ResponseEntity.noContent().build();
    }
}