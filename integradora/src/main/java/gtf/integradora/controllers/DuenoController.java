package gtf.integradora.controllers;

import java.util.*;
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
    public ResponseEntity<List<Map<String, Object>>> obtenerTodos() {
        List<Dueno> duenos = duenoService.obtenerTodos();
        List<Map<String, Object>> resultado = new ArrayList<>();

        for (Dueno dueno : duenos) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", dueno.getId());
            map.put("nombre", dueno.getNombre());
            map.put("apellido", dueno.getApellido());
            map.put("idUsuario", dueno.getIdUsuario());
            map.put("eliminado", dueno.isEliminado());

            Optional<Usuario> usuarioOpt = usuarioRepository.findById(dueno.getIdUsuario());
            usuarioOpt.ifPresent(usuario -> {
                map.put("correo", usuario.getEmail());
                map.put("contrasena", usuario.getPassword()); // Opcional
            });

            resultado.add(map);
        }

        return ResponseEntity.ok(resultado);
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
}