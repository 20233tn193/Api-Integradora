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
import gtf.integradora.entity.Partido;
import gtf.integradora.entity.Torneo;
import gtf.integradora.services.PartidoGeneratorService;
import gtf.integradora.services.TorneoService;

@RestController
@RequestMapping("/api/torneos")
public class TorneoController {

    private final TorneoService torneoService;
    private final PartidoGeneratorService partidoGeneratorService;

    public TorneoController(TorneoService torneoService, PartidoGeneratorService partidoGeneratorService) {
        this.torneoService = torneoService;
        this.partidoGeneratorService = partidoGeneratorService;
    }

    @PostMapping("/{torneoId}/generar-jornada")
    public ResponseEntity<List<Partido>> generarJornada(@PathVariable String torneoId) {
        System.out.println("📥 [POST] /generar-jornada");
        System.out.println("📌 torneoId recibido: " + torneoId);
        System.out.println("⚙️ Iniciando generación de jornada...");
    
        try {
            List<Partido> nuevosPartidos = partidoGeneratorService.generarSiguienteJornada(torneoId);
            System.out.println("✅ Jornada generada con éxito: " + nuevosPartidos.size() + " partidos creados");
            return ResponseEntity.ok(nuevosPartidos);
        } catch (Exception e) {
            System.err.println("❌ Error al generar jornada para torneoId " + torneoId);
            e.printStackTrace(); // muestra traza completa del error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/tarjetas-debug/{torneoId}")
    public ResponseEntity<String> debugTarjetas(@PathVariable String torneoId) {
        System.out.println("📦 Debug tarjetas llamado con torneoId: " + torneoId);
        return ResponseEntity.ok("Debug activado para torneoId: " + torneoId);
    }

    @PostMapping
    public ResponseEntity<Torneo> crear(@RequestBody Torneo torneo) {
        return ResponseEntity.ok(torneoService.crearTorneo(torneo));
    }

    @GetMapping
    public ResponseEntity<List<Torneo>> obtenerTodos() {
        return ResponseEntity.ok(torneoService.obtenerTorneosActivos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Torneo> obtenerPorId(@PathVariable String id) {
        return torneoService.obtenerTorneoPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Torneo> actualizar(@PathVariable String id, @RequestBody Torneo torneo) {
        return ResponseEntity.ok(torneoService.actualizarTorneo(id, torneo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        torneoService.eliminarTorneo(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/finalizar/{torneoId}")
    public ResponseEntity<Torneo> finalizarTorneo(@PathVariable String torneoId) {
        try {
            Torneo torneoFinalizado = torneoService.finalizarTorneo(torneoId);
            return ResponseEntity.ok(torneoFinalizado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    @PutMapping("/iniciar/{torneoId}")
public ResponseEntity<Torneo> iniciarTorneo(@PathVariable String torneoId) {
    try {
        Torneo torneo = torneoService.iniciarTorneo(torneoId);
        return ResponseEntity.ok(torneo);
    } catch (RuntimeException e) {
        return ResponseEntity.badRequest().build();
    }
}
} 
