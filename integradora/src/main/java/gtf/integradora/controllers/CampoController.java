package gtf.integradora.controllers;

import java.util.ArrayList;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;
import gtf.integradora.entity.Campo;
import gtf.integradora.entity.Cancha;
import gtf.integradora.services.CampoService;

@RestController
@RequestMapping("/api/campos")
public class CampoController {

    private final CampoService campoService;

    public CampoController(CampoService campoService) {
        this.campoService = campoService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Campo> crear(@RequestBody Campo field) {
        return ResponseEntity.ok(campoService.crearCampo(field));
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
    public ResponseEntity<Campo> actualizar(@PathVariable String id, @RequestBody Map<String, Object> body) {
        System.out.println("📥 JSON recibido en controller editar campo (raw):");
        System.out.println(body);
    
        Campo campo = new Campo();
        campo.setId(id);
        campo.setNombreCampo((String) body.get("nombreCampo"));
        campo.setLatitud(((Number) body.get("latitud")).doubleValue());
        campo.setLongitud(((Number) body.get("longitud")).doubleValue());
        campo.setDisponible((Boolean) body.get("disponible"));
        campo.setEliminado((Boolean) body.get("eliminado"));
    
        // 👇 Deserialización manual y segura de las canchas
        List<Map<String, String>> canchasData = (List<Map<String, String>>) body.get("canchas");
        List<Cancha> canchas = new ArrayList<>();
        for (Map<String, String> c : canchasData) {
            String nombre = c.get("nombreCancha");
            if (nombre != null && !nombre.trim().isEmpty()) {
                canchas.add(new Cancha(nombre));
            }
        }
        campo.setCanchas(canchas);
    
        return ResponseEntity.ok(campoService.actualizarCampo(id, campo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        campoService.eliminarCampo(id);
        return ResponseEntity.noContent().build();
    }
}
