package gtf.integradora.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import gtf.integradora.entity.Goleador;
import gtf.integradora.entity.Jugador;
import gtf.integradora.entity.TablaPosicion;
import gtf.integradora.entity.Tarjeta;
import gtf.integradora.repository.GoleadorRepository;
import gtf.integradora.repository.TablaPosicionRepository;
import gtf.integradora.repository.TarjetaRepository;
import gtf.integradora.services.PartidoService;

@RestController
@RequestMapping("/api/estadisticas")
public class EstadisticasController {

    private final TablaPosicionRepository tablaPosicionRepository;
    private final GoleadorRepository goleadorRepository;
    private final TarjetaRepository tarjetaRepository;
    private final PartidoService partidoService;

    public EstadisticasController(
            TablaPosicionRepository tablaPosicionRepository,
            GoleadorRepository goleadorRepository,
            TarjetaRepository tarjetaRepository,
            PartidoService partidoService) {
        this.tablaPosicionRepository = tablaPosicionRepository;
        this.goleadorRepository = goleadorRepository;
        this.tarjetaRepository = tarjetaRepository;
        this.partidoService = partidoService;
    }

    // 🏆 Tabla de posiciones por torneo
    @GetMapping("/tabla-posiciones/{torneoId}")
    public ResponseEntity<List<TablaPosicion>> obtenerTablaPosiciones(@PathVariable String torneoId) {
        List<TablaPosicion> tabla = tablaPosicionRepository.findByTorneoIdAndEliminadoFalse(torneoId);

        // 🛠️ Aquí aplicas la mejora de ordenamiento
        tabla.sort((a, b) -> {
            int cmp = Integer.compare(b.getPuntos(), a.getPuntos());
            if (cmp == 0)
                cmp = Integer.compare(b.getDiferenciaGoles(), a.getDiferenciaGoles());
            if (cmp == 0)
                cmp = Integer.compare(b.getGolesFavor(), a.getGolesFavor());
            return cmp;
        });

        return ResponseEntity.ok(tabla);
    }

    // ⚽ Goleadores por torneo (TOP 10)
    @GetMapping("/goleadores/{torneoId}")
    public ResponseEntity<List<Goleador>> obtenerGoleadores(@PathVariable String torneoId) {
        List<Goleador> goleadores = goleadorRepository.findByTorneoIdAndEliminadoFalse(torneoId);
        goleadores.sort((a, b) -> Integer.compare(b.getGoles(), a.getGoles()));
        return ResponseEntity.ok(goleadores.stream().limit(10).toList());
    }

    @GetMapping("/tarjetas/{torneoId}")
    public ResponseEntity<List<Map<String, Object>>> obtenerTarjetas(@PathVariable String torneoId) {
        List<Tarjeta> tarjetas = tarjetaRepository.findByTorneoIdAndEliminadoFalse(torneoId);

        List<Map<String, Object>> respuesta = tarjetas.stream().map(t -> {
            Map<String, Object> datos = new HashMap<>();
            datos.put("jugadorId", t.getJugadorId());
            datos.put("torneoId", t.getTorneoId());
            datos.put("amarillas", t.getAmarillas());
            datos.put("rojas", t.getRojas());
            datos.put("suspendido", t.isSuspendido());
            datos.put("partidosSuspendido", t.getPartidosSuspendido());
            return datos;
        }).toList();

        return ResponseEntity.ok(respuesta);
    }

    // 🟥 Jugadores suspendidos por tarjetas rojas
    @GetMapping("/jugadores-suspendidos/{torneoId}")
    public ResponseEntity<List<Jugador>> obtenerJugadoresSuspendidos(@PathVariable String torneoId) {
        List<Jugador> suspendidos = partidoService.obtenerJugadoresSuspendidos(torneoId);
        return ResponseEntity.ok(suspendidos);
    }
}