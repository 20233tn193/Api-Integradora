package gtf.integradora.controllers;

import gtf.integradora.entity.Arbitro;
import gtf.integradora.entity.Usuario;
import gtf.integradora.repository.UsuarioRepository;
import gtf.integradora.services.ArbitroService;
import gtf.integradora.util.EncryptUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

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

        try {
            if (arbitro.getFotoUrl() != null && !arbitro.getFotoUrl().startsWith("ENC(")) {
                String encrypted = EncryptUtil.encrypt(arbitro.getFotoUrl());
                arbitro.setFotoUrl(encrypted);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al encriptar imagen en creación");
        }

        Arbitro creado = arbitroService.crearArbitro(arbitro);
        return ResponseEntity.ok(creado);
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> obtenerTodos() {
        List<Arbitro> arbitros = arbitroService.obtenerTodos();
        List<Map<String, Object>> resultado = new ArrayList<>();

        for (Arbitro arbitro : arbitros) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", arbitro.getId());
            map.put("nombre", arbitro.getNombre());
            map.put("apellido", arbitro.getApellido());
            map.put("celular", arbitro.getCelular());
            map.put("idUsuario", arbitro.getIdUsuario());
            map.put("eliminado", arbitro.isEliminado());

            // 🔐 Desencriptar imagen
            try {
                if (arbitro.getFotoUrl() != null && arbitro.getFotoUrl().startsWith("ENC(")) {
                    String desencriptada = EncryptUtil.decrypt(arbitro.getFotoUrl());
                    map.put("fotoUrl", desencriptada);
                } else {
                    map.put("fotoUrl", arbitro.getFotoUrl());
                }
            } catch (Exception e) {
                map.put("fotoUrl", null);
                System.out.println(
                        "❌ Error al desencriptar imagen del árbitro " + arbitro.getId() + ": " + e.getMessage());
            }

            Optional<Usuario> usuarioOpt = usuarioRepository.findById(arbitro.getIdUsuario());
            usuarioOpt.ifPresent(usuario -> {
                map.put("correo", usuario.getEmail());
                map.put("contrasena", usuario.getPassword());
            });

            resultado.add(map);
        }

        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Arbitro> obtenerPorId(@PathVariable String id) {
        Optional<Arbitro> arbitroOpt = arbitroService.obtenerPorId(id);
        if (arbitroOpt.isEmpty())
            return ResponseEntity.notFound().build();

        Arbitro arbitro = arbitroOpt.get();
        try {
            if (arbitro.getFotoUrl() != null && arbitro.getFotoUrl().startsWith("ENC(")) {
                arbitro.setFotoUrl(EncryptUtil.decrypt(arbitro.getFotoUrl()));
            }
        } catch (Exception e) {
            arbitro.setFotoUrl(null);
        }

        return ResponseEntity.ok(arbitro);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable String id, @RequestBody Arbitro arbitro) {
        System.out.println("📤 [ACTUALIZAR ÁRBITRO] Datos recibidos para ID: " + id);
        System.out.println("ID Usuario: " + arbitro.getIdUsuario());
        System.out.println("Nombre: " + arbitro.getNombre());
        System.out.println("Apellido: " + arbitro.getApellido());
        System.out.println("Foto (primeros 100 chars): " + (arbitro.getFotoUrl() != null
                ? arbitro.getFotoUrl().substring(0, Math.min(100, arbitro.getFotoUrl().length())) + "..."
                : "null"));

        if (arbitro.getIdUsuario() == null) {
            return ResponseEntity.badRequest().body("El campo idUsuario es obligatorio.");
        }

        Optional<Usuario> usuarioOpt = usuarioRepository.findById(arbitro.getIdUsuario());
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontró el usuario asociado.");
        }

        try {
            String nuevaImagen = arbitro.getFotoUrl();
            if (nuevaImagen != null && !nuevaImagen.isEmpty() && !nuevaImagen.startsWith("ENC(")) {
                arbitro.setFotoUrl(EncryptUtil.encrypt(nuevaImagen));
                System.out.println("🔐 Imagen actualizada y encriptada.");
            } else {
                System.out.println("ℹ️ Imagen no modificada o ya encriptada.");
            }
        } catch (Exception e) {
            System.out.println("❌ Error al encriptar imagen: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al encriptar la imagen del árbitro durante actualización.");
        }

        Arbitro actualizado = arbitroService.actualizarArbitro(id, arbitro);
        System.out.println("✅ Árbitro actualizado con ID: " + actualizado.getId());
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        arbitroService.eliminarArbitro(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/usuario/{idUsuario}")
<<<<<<< HEAD
public ResponseEntity<?> obtenerPorUsuarioId(@PathVariable String idUsuario) {
    Optional<Arbitro> arbitroOpt = arbitroService.obtenerPorUsuarioId(idUsuario);
    if (arbitroOpt.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontró el árbitro para el usuario dado.");
    }

    Arbitro arbitro = arbitroOpt.get();

    try {
        if (arbitro.getFotoUrl() != null && arbitro.getFotoUrl().startsWith("ENC(")) {
            arbitro.setFotoUrl(EncryptUtil.decrypt(arbitro.getFotoUrl()));
        }
    } catch (Exception e) {
        arbitro.setFotoUrl(null);
    }

    return ResponseEntity.ok(arbitro);
}
=======
    public ResponseEntity<Arbitro> obtenerPorIdUsuario(@PathVariable String idUsuario) {
        Optional<Arbitro> arbitroOpt = arbitroService.obtenerPorIdUsuario(idUsuario);
        if (arbitroOpt.isEmpty())
            return ResponseEntity.notFound().build();

        Arbitro arbitro = arbitroOpt.get();
        try {
            if (arbitro.getFotoUrl() != null && arbitro.getFotoUrl().startsWith("ENC(")) {
                arbitro.setFotoUrl(EncryptUtil.decrypt(arbitro.getFotoUrl()));
            }
        } catch (Exception e) {
            arbitro.setFotoUrl(null);
        }

        return ResponseEntity.ok(arbitro);
    }

>>>>>>> 52e9bdacdddc498053c771bfd6a2ad53d4cac822
}
