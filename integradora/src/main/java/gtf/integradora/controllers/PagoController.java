package gtf.integradora.controllers;
// PagoController.java

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gtf.integradora.entity.Pago;
import gtf.integradora.entity.PagoDTO;
import gtf.integradora.services.PagoService;

@RestController
@RequestMapping("/api/pagos")
public class PagoController {

    private final PagoService pagoService;

    public PagoController(PagoService pagoService) {
        this.pagoService = pagoService;
    }

    @PostMapping
    public ResponseEntity<Pago> crear(@RequestBody Pago pago) {
        return ResponseEntity.ok(pagoService.crearPago(pago));
    }

    @GetMapping("/torneo/{torneoId}")
    public ResponseEntity<List<Pago>> obtenerPorTorneo(@PathVariable String torneoId) {
        return ResponseEntity.ok(pagoService.obtenerPorTorneo(torneoId));
    }

    @GetMapping("/torneo/{torneoId}/detallado")
    public ResponseEntity<List<PagoDTO>> obtenerPagosDetallados(@PathVariable String torneoId) {
        return ResponseEntity.ok(pagoService.obtenerPagosDetalladosPorTorneo(torneoId));
    }

    @GetMapping("/equipo/{equipoId}")
    public ResponseEntity<List<Pago>> obtenerPorEquipo(@PathVariable String equipoId) {
        return ResponseEntity.ok(pagoService.obtenerPorEquipo(equipoId));
    }

    @GetMapping("/dueno/{duenoId}")
    public ResponseEntity<List<Pago>> obtenerPorDueno(@PathVariable String duenoId) {
        return ResponseEntity.ok(pagoService.obtenerPorDueno(duenoId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pago> obtenerPorId(@PathVariable String id) {
        return pagoService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/aprobar")
    public ResponseEntity<Pago> aprobarPago(@PathVariable String id) {
        return ResponseEntity.ok(pagoService.actualizarEstatus(id, "pagado"));
    }

    @PutMapping("/{id}/rechazar")
    public ResponseEntity<Pago> rechazarPago(@PathVariable String id) {
        return ResponseEntity.ok(pagoService.actualizarEstatus(id, "rechazado"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        pagoService.eliminarPago(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/detallados")
    public ResponseEntity<List<PagoDTO>> obtenerTodosLosPagosDetallados() {
        return ResponseEntity.ok(pagoService.obtenerTodosLosPagosDetallados());
    }
}
