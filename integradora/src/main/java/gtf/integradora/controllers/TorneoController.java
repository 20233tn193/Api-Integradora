package gtf.integradora.controllers;

import java.util.List;

import org.springframework.data.mongodb.core.aggregation.ComparisonOperators.Eq;
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
import gtf.integradora.repository.EquipoRepository;
import gtf.integradora.services.PartidoGeneratorService;
import gtf.integradora.services.TorneoService;

@RestController
@RequestMapping("/api/torneos")
public class TorneoController {

    private final TorneoService torneoService;
    private final PartidoGeneratorService partidoGeneratorService;
    private final EquipoRepository equipoRepository;

    public TorneoController(TorneoService torneoService, PartidoGeneratorService partidoGeneratorService, EquipoRepository equipoRepository) {
        this.torneoService = torneoService;
        this.partidoGeneratorService = partidoGeneratorService;
        this.equipoRepository = equipoRepository;
    }

    @PostMapping("/{torneoId}/generar-jornada")
    public ResponseEntity<List<Partido>> generarJornada(@PathVariable String torneoId) {
        try {
            List<Partido> nuevosPartidos = partidoGeneratorService.generarSiguienteJornada(torneoId);
            return ResponseEntity.ok(nuevosPartidos);
        } catch (Exception e) {
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
public ResponseEntity<?> obtenerPorId(@PathVariable String id) {
    return torneoService.obtenerTorneoPorId(id)
            .map(torneo -> {
                // Crear DTO personalizado
                var datos = new java.util.HashMap<String, Object>();
                datos.put("id", torneo.getId());
                datos.put("nombreTorneo", torneo.getNombreTorneo());
                datos.put("estado", torneo.getEstado());
                datos.put("logoSeleccionado", torneo.getLogoSeleccionado());
                datos.put("informacion", torneo.getInformacion());
                datos.put("costo", torneo.getCosto());
                datos.put("fechaInicio", torneo.getFechaInicio());
                datos.put("fechaFin", torneo.getFechaFin());

                if (torneo.getCampeonId() != null) {
                    equipoRepository.findById(torneo.getCampeonId()).ifPresent(equipo -> {
                        datos.put("campeonId", equipo.getId());
                        datos.put("campeonNombre", equipo.getNombre());
                        datos.put("campeonLogo", equipo.getLogoUrl());
                    });
                }

                return ResponseEntity.ok(datos);
            })
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
} 
