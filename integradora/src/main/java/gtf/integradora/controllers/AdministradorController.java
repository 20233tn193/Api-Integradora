package gtf.integradora.controllers;

import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import gtf.integradora.entity.Administrador;
import gtf.integradora.entity.Usuario;
import gtf.integradora.repository.UsuarioRepository;
import gtf.integradora.services.AdministradorService;

@RestController
@RequestMapping("/api/administradores")
public class AdministradorController {

    private final AdministradorService administradorService;
    private final UsuarioRepository usuarioRepository;

    public AdministradorController(AdministradorService administradorService, UsuarioRepository usuarioRepository) {
        this.administradorService = administradorService;
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Administrador administrador) {
        if (administrador.getIdUsuario() == null) {
            return ResponseEntity.badRequest().body("El campo idUsuario es obligatorio.");
        }

        Optional<Usuario> usuarioOpt = usuarioRepository.findById(administrador.getIdUsuario());
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontró el usuario asociado.");
        }

        Administrador creado = administradorService.crearAdministrador(administrador);
        return ResponseEntity.ok(creado);
    }

    @GetMapping
    public ResponseEntity<List<Administrador>> obtenerTodos() {
        return ResponseEntity.ok(administradorService.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Administrador> obtenerPorId(@PathVariable String id) {
        return administradorService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable String id, @RequestBody Administrador administrador) {
        if (administrador.getIdUsuario() == null) {
            return ResponseEntity.badRequest().body("El campo idUsuario es obligatorio.");
        }

        Optional<Usuario> usuarioOpt = usuarioRepository.findById(administrador.getIdUsuario());
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontró el usuario asociado.");
        }

        return ResponseEntity.ok(administradorService.actualizarAdministrador(id, administrador));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        administradorService.eliminarAdministrador(id);
        return ResponseEntity.noContent().build();
    }
}