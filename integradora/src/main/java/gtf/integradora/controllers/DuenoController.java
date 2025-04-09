package gtf.integradora.controllers;

import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import gtf.integradora.entity.Dueno;
import gtf.integradora.entity.Usuario;
import gtf.integradora.repository.UsuarioRepository;
import gtf.integradora.services.DuenoService;

@RestController
@RequestMapping("/api/duenos")
public class DuenoController {

    private final DuenoService duenoService;
    private final UsuarioRepository usuarioRepository;

    public DuenoController(DuenoService duenoService, UsuarioRepository usuarioRepository) {
        this.duenoService = duenoService;
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Dueno dueno) {
        if (dueno.getIdUsuario() == null) {
            return ResponseEntity.badRequest().body("El campo idUsuario es obligatorio.");
        }

        Optional<Usuario> usuarioOpt = usuarioRepository.findById(dueno.getIdUsuario());
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontró el usuario asociado.");
        }

        Dueno creado = duenoService.crearDueno(dueno);
        return ResponseEntity.ok(creado);
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
    public ResponseEntity<?> actualizar(@PathVariable String id, @RequestBody Dueno dueno) {
        if (dueno.getIdUsuario() == null) {
            return ResponseEntity.badRequest().body("El campo idUsuario es obligatorio.");
        }

        Optional<Usuario> usuarioOpt = usuarioRepository.findById(dueno.getIdUsuario());
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontró el usuario asociado.");
        }

        return ResponseEntity.ok(duenoService.actualizarDueno(id, dueno));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        duenoService.eliminarDueno(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<Dueno> obtenerPorIdUsuario(@PathVariable String idUsuario) {
        return duenoService.obtenerPorIdUsuario(idUsuario)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}