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
import gtf.integradora.dto.InscripcionRequestDTO;
import gtf.integradora.entity.Equipo;
import gtf.integradora.services.EquipoService;

@RestController
@RequestMapping("/api/equipos")
public class EquipoController {

    private final EquipoService equipoService;

    public EquipoController(EquipoService equipoService) {
        this.equipoService = equipoService;
    }

    @PostMapping
    public ResponseEntity<Equipo> crear(@RequestBody Equipo equipo) {
        if (equipoService.existeNombreEquipoEnTorneo(equipo.getNombre(), equipo.getTorneoId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
        return ResponseEntity.ok(equipoService.crearEquipo(equipo));
    }

    @GetMapping("/torneo/{torneoId}")
    public ResponseEntity<List<Equipo>> obtenerPorTorneo(@PathVariable String torneoId) {
        return ResponseEntity.ok(equipoService.obtenerPorTorneo(torneoId));
    }

    @GetMapping("/dueño/{dueñoId}")
    public ResponseEntity<List<Equipo>> obtenerPorDueño(@PathVariable String dueñoId) {
        return ResponseEntity.ok(equipoService.obtenerPorDueno(dueñoId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Equipo> obtenerPorId(@PathVariable String id) {
        return equipoService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Equipo> actualizar(@PathVariable String id, @RequestBody Equipo equipo) {
        return ResponseEntity.ok(equipoService.actualizarEquipo(id, equipo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        equipoService.eliminarEquipo(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/inscribirse")
    public ResponseEntity<String> inscribirEquipo(@RequestBody InscripcionRequestDTO request) {
        return ResponseEntity.ok(equipoService.inscribirEquipo(request));
    }
}