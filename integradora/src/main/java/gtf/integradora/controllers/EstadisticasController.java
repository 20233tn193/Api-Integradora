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
import gtf.integradora.entity.TablaPosicionDTO;
import gtf.integradora.entity.Tarjeta;
import gtf.integradora.repository.GoleadorRepository;
import gtf.integradora.repository.TablaPosicionRepository;
import gtf.integradora.repository.TarjetaRepository;
import gtf.integradora.services.PartidoService;
import gtf.integradora.repository.JugadorRepository;
import gtf.integradora.repository.EquipoRepository; // ⬅️ Importar


@RestController
@RequestMapping("/api/estadisticas")
public class EstadisticasController {

    private final JugadorRepository jugadorRepository; // ⬅️ Declarar
    private final TablaPosicionRepository tablaPosicionRepository;
    private final GoleadorRepository goleadorRepository;
    private final TarjetaRepository tarjetaRepository;
    private final PartidoService partidoService;
    private final EquipoRepository equipoRepository; // ⬅️ Agregado aquí

    public EstadisticasController(
        TablaPosicionRepository tablaPosicionRepository,
        GoleadorRepository goleadorRepository,
        TarjetaRepository tarjetaRepository,
        PartidoService partidoService,
        JugadorRepository jugadorRepository // ⬅️ Agregado aquí
        , EquipoRepository equipoRepository // ⬅️ Agregado aquí
    ) {
        this.tablaPosicionRepository = tablaPosicionRepository;
        this.goleadorRepository = goleadorRepository;
        this.tarjetaRepository = tarjetaRepository;
        this.partidoService = partidoService;
        this.jugadorRepository = jugadorRepository; // ⬅️ Guardar
        this.equipoRepository = equipoRepository; // ⬅️ Guardar
    }

   @GetMapping("/tabla-posiciones/{torneoId}")
public ResponseEntity<List<TablaPosicionDTO>> obtenerTablaPosiciones(@PathVariable String torneoId) {
    List<TablaPosicion> tabla = tablaPosicionRepository.findByTorneoIdAndEliminadoFalse(torneoId);

    tabla.sort((a, b) -> {
        int cmp = Integer.compare(b.getPuntos(), a.getPuntos());
        if (cmp == 0) cmp = Integer.compare(b.getDiferenciaGoles(), a.getDiferenciaGoles());
        if (cmp == 0) cmp = Integer.compare(b.getGolesFavor(), a.getGolesFavor());
        return cmp;
    });

    List<TablaPosicionDTO> respuesta = tabla.stream().map(pos -> {
        TablaPosicionDTO dto = new TablaPosicionDTO();

        equipoRepository.findByIdAndEliminadoFalse(pos.getEquipoId()).ifPresent(equipo -> {
            dto.setNombreEquipo(equipo.getNombre());
            dto.setLogoUrl(equipo.getLogoUrl());
        });

        dto.setFase(pos.getFase());
        dto.setPartidosJugados(pos.getPartidosJugados());
        dto.setGanados(pos.getGanados());
        dto.setPerdidos(pos.getPerdidos());
        dto.setGolesFavor(pos.getGolesFavor());
        dto.setGolesContra(pos.getGolesContra());
        dto.setDiferenciaGoles(pos.getDiferenciaGoles());
        dto.setPuntos(pos.getPuntos());

        return dto;
    }).toList();

    return ResponseEntity.ok(respuesta);
}
    @GetMapping("/goleadores/{torneoId}")
    public ResponseEntity<List<Map<String, Object>>> obtenerGoleadores(@PathVariable String torneoId) {
        List<Goleador> goleadores = goleadorRepository.findByTorneoIdAndEliminadoFalse(torneoId);
        goleadores.sort((a, b) -> Integer.compare(b.getGoles(), a.getGoles()));
    
        List<Map<String, Object>> respuesta = goleadores.stream().map(g -> {
            Map<String, Object> datos = new HashMap<>();
            datos.put("goles", g.getGoles());
            datos.put("jugadorId", g.getJugadorId());
    
            jugadorRepository.findById(g.getJugadorId()).ifPresent(jugador -> {
                datos.put("nombre", jugador.getNombre());
                datos.put("apellido", jugador.getApellido());
                datos.put("fotoUrl", jugador.getFotoUrl());
                datos.put("equipoId", jugador.getEquipoId());
            
                equipoRepository.findByIdAndEliminadoFalse(jugador.getEquipoId()).ifPresent(equipo -> {
                    datos.put("equipoEscudo", equipo.getLogoUrl()); // 👈 Esto agrega el escudo al JSON
                });
            });
            return datos;
        }).toList();
    
        return ResponseEntity.ok(respuesta);
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
    
            jugadorRepository.findById(t.getJugadorId()).ifPresent(jugador -> {
                datos.put("nombre", jugador.getNombre());
                datos.put("apellido", jugador.getApellido());
                datos.put("fotoUrl", jugador.getFotoUrl());
                datos.put("equipoId", jugador.getEquipoId());
    
                equipoRepository.findByIdAndEliminadoFalse(jugador.getEquipoId()).ifPresent(equipo -> {
                    datos.put("equipoNombre", equipo.getNombre());
                    datos.put("equipoEscudo", equipo.getLogoUrl()); // 👈 usa este
                });
            });
    
            return datos;
        }).toList();
    
        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/jugadores-suspendidos/{torneoId}")
    public ResponseEntity<List<Jugador>> obtenerJugadoresSuspendidos(@PathVariable String torneoId) {
        List<Jugador> suspendidos = partidoService.obtenerJugadoresSuspendidos(torneoId);
        return ResponseEntity.ok(suspendidos);
    }
}