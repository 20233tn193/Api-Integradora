package gtf.integradora.controllers;

import java.util.List;
import java.util.Map;
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
import org.springframework.security.core.Authentication;
import gtf.integradora.dto.RegistroPartidoDTO;
import gtf.integradora.dto.ReprogramarPartidoDTO;
import gtf.integradora.entity.Partido;
import gtf.integradora.entity.PartidoDTO;
import gtf.integradora.services.PartidoGeneratorService;
import gtf.integradora.services.PartidoService;
import gtf.integradora.repository.EquipoRepository;

@RestController
@RequestMapping("/api/partidos")
public class PartidoController {

    private final PartidoService partidoService;
    private final PartidoGeneratorService partidoGeneratorService;
    private final EquipoRepository equipoRepository;

    public PartidoController(
        PartidoService partidoService,
        PartidoGeneratorService partidoGeneratorService,
        EquipoRepository equipoRepository
    ) {
        this.partidoService = partidoService;
        this.partidoGeneratorService = partidoGeneratorService;
        this.equipoRepository = equipoRepository;
    }

    @PostMapping("/generar-jornada/{torneoId}")
    public List<Partido> generarSiguienteJornada(@PathVariable String torneoId) {
        return partidoGeneratorService.generarSiguienteJornada(torneoId);
    }

    @PostMapping
    public ResponseEntity<Partido> crear(@RequestBody Partido partido) {
        return ResponseEntity.ok(partidoService.crearPartido(partido));
    }

    @GetMapping("/torneo/{torneoId}")
    public ResponseEntity<List<Partido>> obtenerPorTorneo(@PathVariable String torneoId) {
        return ResponseEntity.ok(partidoService.obtenerPorTorneo(torneoId));
    }

    @GetMapping("/arbitro/{arbitroId}")
    public ResponseEntity<List<Partido>> obtenerPorArbitro(@PathVariable String arbitroId) {
        return ResponseEntity.ok(partidoService.obtenerPorArbitro(arbitroId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Partido> obtenerPorId(@PathVariable String id) {
        return partidoService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Partido> actualizar(@PathVariable String id, @RequestBody Partido partido) {
        return ResponseEntity.ok(partidoService.actualizarPartido(id, partido));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        partidoService.eliminarPartido(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/registrar-resultado/{partidoId}")
    public ResponseEntity<Partido> registrarResultado(@PathVariable String partidoId,
            @RequestBody RegistroPartidoDTO datos,
            Authentication authentication) {
        if (!authentication.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("ROLE_ARBITRO"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            Partido partidoActualizado = partidoService.registrarResultado(partidoId, datos.getRegistro());
            return ResponseEntity.ok(partidoActualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/reprogramar/{id}")
    public ResponseEntity<Partido> reprogramar(
            @PathVariable String id,
            @RequestBody ReprogramarPartidoDTO datos) {
        try {
            Partido actualizado = partidoService.reprogramarPartido(
                    id,
                    datos.getNuevaFecha(),
                    datos.getNuevaHora(),
                    datos.getNuevoCampoId(),
                    datos.getNuevoNombreCampo(),
                    datos.getNuevaCancha(),
                    datos.getNuevoArbitroId(),
                    datos.getNuevoNombreArbitro());
            return ResponseEntity.ok(actualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/calendario/{torneoId}")
    public ResponseEntity<Map<Integer, List<PartidoDTO>>> obtenerCalendario(@PathVariable String torneoId) {
        Map<Integer, List<Partido>> calendarioOriginal = partidoService.obtenerCalendarioPorJornada(torneoId);
        Map<Integer, List<PartidoDTO>> calendarioDTO = new java.util.HashMap<>();

        for (Map.Entry<Integer, List<Partido>> entry : calendarioOriginal.entrySet()) {
            List<PartidoDTO> partidosDTO = entry.getValue().stream().map(p -> {
                PartidoDTO dto = new PartidoDTO();
                dto.id = p.getId();
                dto.nombreEquipoA = p.getNombreEquipoA();
                dto.nombreEquipoB = p.getNombreEquipoB();
                dto.nombreCampo = p.getNombreCampo();
                dto.nombreCancha = p.getNombreCancha();
                dto.nombreArbitro = p.getNombreArbitro();
                dto.fecha = p.getFecha();
                dto.hora = p.getHora();

                equipoRepository.findByIdAndEliminadoFalse(p.getEquipoAId()).ifPresent(eqA -> {
                    dto.logoEquipoA = eqA.getLogoUrl();
                });

                equipoRepository.findByIdAndEliminadoFalse(p.getEquipoBId()).ifPresent(eqB -> {
                    dto.logoEquipoB = eqB.getLogoUrl();
                });

                return dto;
            }).toList();

            calendarioDTO.put(entry.getKey(), partidosDTO);
        }

        return ResponseEntity.ok(calendarioDTO);
    }
} 
