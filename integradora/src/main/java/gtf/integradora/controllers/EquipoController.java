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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import gtf.integradora.dto.InscripcionRequestDTO;
import gtf.integradora.entity.Equipo;
import gtf.integradora.entity.EquipoConDuenoDTO;
import gtf.integradora.services.CredencialService;
import gtf.integradora.services.EquipoService;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@RestController
@RequestMapping("/api/equipos")
public class EquipoController {

    private final EquipoService equipoService;
    private final CredencialService credencialService;

    public EquipoController(EquipoService equipoService, CredencialService credencialService) {
        this.equipoService = equipoService;
        this.credencialService = credencialService;
    }

    @PostMapping
    public ResponseEntity<Equipo> crear(@RequestBody Equipo equipo) {
        if (equipoService.existeNombreEquipoEnTorneo(equipo.getNombre(), equipo.getTorneoId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
        return ResponseEntity.ok(equipoService.crearEquipo(equipo));
    }

    // ✅ Ruta única sin conflicto
    @GetMapping("/torneo/{torneoId}")
    public ResponseEntity<List<Equipo>> obtenerPorTorneo(@PathVariable String torneoId) {
        return ResponseEntity.ok(equipoService.obtenerPorTorneo(torneoId));
    }

    @GetMapping("/torneo-con-dueno/{torneoId}")
    public ResponseEntity<List<EquipoConDuenoDTO>> obtenerConDueno(@PathVariable String torneoId) {
        return ResponseEntity.ok(equipoService.obtenerEquiposConDuenoPorTorneo(torneoId));
    }

    @GetMapping("/dueño/{dueñoId}")
    public ResponseEntity<List<Equipo>> obtenerPorDueño(
            @PathVariable String dueñoId,
            HttpServletRequest request // ✅ aquí lo inyectas
    ) {
        System.out.println("🔎 Header Authorization: " + request.getHeader("Authorization"));
        return ResponseEntity.ok(equipoService.obtenerPorDueno(dueñoId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Equipo> obtenerPorId(@PathVariable String id) {
        return equipoService.obtenerPorId(id)
                .map(equipo -> {
                    String logo = equipo.getLogoUrl();
                    if (logo != null && !logo.startsWith("data:image")) {
                        equipo.setLogoUrl("data:image/jpeg;base64," + logo);
                    }
                    return ResponseEntity.ok(equipo);
                })
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

    @GetMapping("/{equipoId}/credenciales")
    public ResponseEntity<byte[]> generarCredenciales(@PathVariable String equipoId) {
        try {
            byte[] pdfBytes = credencialService.generarCredenciales(equipoId);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "inline; filename=credenciales.pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/inscribir-existente")
    public ResponseEntity<String> inscribirEquipoExistente(@RequestParam String equipoId,
            @RequestParam String torneoId) {
        try {
            String mensaje = equipoService.inscribirEquipoExistente(equipoId, torneoId);
            return ResponseEntity.ok(mensaje);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}