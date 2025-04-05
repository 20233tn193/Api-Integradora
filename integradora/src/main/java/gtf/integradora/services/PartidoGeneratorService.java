package gtf.integradora.services;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import gtf.integradora.entity.Arbitro;
import gtf.integradora.entity.Campo;
import gtf.integradora.entity.Enfrentamiento;
import gtf.integradora.entity.Equipo;
import gtf.integradora.entity.Jugador;
import gtf.integradora.entity.Partido;
import gtf.integradora.entity.PartidoScheduler;
import gtf.integradora.entity.RegistroJugador;
import gtf.integradora.entity.TablaPosicion;
import gtf.integradora.entity.Torneo;
import gtf.integradora.repository.ArbitroRepository;
import gtf.integradora.repository.CampoRepository;
import gtf.integradora.repository.EquipoRepository;
import gtf.integradora.repository.JugadorRepository;
import gtf.integradora.repository.PartidoRepository;
import gtf.integradora.repository.TablaPosicionRepository;
import gtf.integradora.repository.TorneoRepository;

@Service
public class PartidoGeneratorService {

    private final EquipoRepository equipoRepository;
    private final PartidoRepository partidoRepository;
    private final CampoRepository campoRepository;
    private final ArbitroRepository arbitroRepository;
    private final JugadorRepository jugadorRepository;
    private final PartidoScheduler partidoScheduler;
    private final TorneoRepository torneoRepository;
    private final TablaPosicionRepository tablaPosicionRepository;

    public PartidoGeneratorService(
            EquipoRepository equipoRepository,
            PartidoRepository partidoRepository,
            CampoRepository campoRepository,
            ArbitroRepository arbitroRepository,
            JugadorRepository jugadorRepository,
            PartidoScheduler partidoScheduler,
            TorneoRepository torneoRepository,
            TablaPosicionRepository tablaPosicionRepository) {
        this.equipoRepository = equipoRepository;
        this.partidoRepository = partidoRepository;
        this.campoRepository = campoRepository;
        this.arbitroRepository = arbitroRepository;
        this.jugadorRepository = jugadorRepository;
        this.partidoScheduler = partidoScheduler;
        this.torneoRepository = torneoRepository;
        this.tablaPosicionRepository = tablaPosicionRepository;
    }

    public List<Partido> generarSiguienteJornada(String torneoId) {
        // Validación aquí:
        Torneo torneo = torneoRepository.findById(torneoId)
                .orElseThrow(() -> new RuntimeException("Torneo no encontrado"));

        if ("finalizado".equalsIgnoreCase(torneo.getEstado())) {
            throw new RuntimeException("No se puede generar jornadas para un torneo finalizado.");
        }

        List<Equipo> equipos = equipoRepository.findByTorneoIdAndEliminadoFalse(torneoId);
        List<Partido> partidosAnteriores = partidoRepository.findByTorneoIdAndEliminadoFalse(torneoId);
        List<Equipo> equiposElegibles = filtrarEquiposElegibles(equipos, partidosAnteriores);

        int jornadaActual = partidosAnteriores.stream()
                .mapToInt(Partido::getJornada)
                .max()
                .orElse(0);

        int nuevaJornada = jornadaActual + 1;

        List<Equipo> ganadores = obtenerEquiposGanadores(equiposElegibles, partidosAnteriores);
        List<Equipo> perdedores = obtenerEquiposPerdedores(equiposElegibles, partidosAnteriores);

        List<Enfrentamiento> enfrentamientos = generarEnfrentamientos(ganadores, perdedores, nuevaJornada);

        List<Campo> campos = campoRepository.findByEliminadoFalse();
        List<Arbitro> arbitros = arbitroRepository.findByEliminadoFalse();

        List<Partido> nuevosPartidos = partidoScheduler.asignarPartidos(
                enfrentamientos, campos, arbitros, torneoId, nuevaJornada);

        actualizarFaseEquipos(torneoId);
        return partidoRepository.saveAll(nuevosPartidos);
    }

    private List<Equipo> obtenerEquiposGanadores(List<Equipo> equipos, List<Partido> partidos) {
        Map<String, Integer> victoriasPorEquipo = new HashMap<>();

        for (Partido partido : partidos) {
            if (!"finalizado".equalsIgnoreCase(partido.getEstado()) || partido.getRegistro() == null)
                continue;

            int golesA = contarGoles(partido, partido.getEquipoAId());
            int golesB = contarGoles(partido, partido.getEquipoBId());

            if (golesA > golesB) {
                victoriasPorEquipo.merge(partido.getEquipoAId(), 1, Integer::sum);
            } else if (golesB > golesA) {
                victoriasPorEquipo.merge(partido.getEquipoBId(), 1, Integer::sum);
            }
        }

        return equipos.stream()
                .filter(e -> victoriasPorEquipo.getOrDefault(e.getId(), 0) >= 1)
                .collect(Collectors.toList());
    }

    private List<Equipo> obtenerEquiposPerdedores(List<Equipo> equipos, List<Partido> partidos) {
        Set<String> perdedoresIds = new HashSet<>();

        for (Partido partido : partidos) {
            if (!"finalizado".equalsIgnoreCase(partido.getEstado()) || partido.getRegistro() == null)
                continue;

            int golesA = contarGoles(partido, partido.getEquipoAId());
            int golesB = contarGoles(partido, partido.getEquipoBId());

            if (golesA > golesB) {
                perdedoresIds.add(partido.getEquipoBId());
            } else if (golesB > golesA) {
                perdedoresIds.add(partido.getEquipoAId());
            }
        }

        return equipos.stream()
                .filter(e -> perdedoresIds.contains(e.getId()))
                .collect(Collectors.toList());
    }

    private List<Equipo> filtrarEquiposElegibles(List<Equipo> equipos, List<Partido> partidos) {
        Map<String, Integer> derrotasPorEquipo = new HashMap<>();

        for (Partido partido : partidos) {
            if (!"finalizado".equalsIgnoreCase(partido.getEstado()) || partido.getRegistro() == null)
                continue;

            int golesA = contarGoles(partido, partido.getEquipoAId());
            int golesB = contarGoles(partido, partido.getEquipoBId());

            if (golesA > golesB) {
                derrotasPorEquipo.merge(partido.getEquipoBId(), 1, Integer::sum);
            } else if (golesB > golesA) {
                derrotasPorEquipo.merge(partido.getEquipoAId(), 1, Integer::sum);
            }
        }

        return equipos.stream()
                .filter(e -> derrotasPorEquipo.getOrDefault(e.getId(), 0) < 2)
                .collect(Collectors.toList());
    }

    private int contarGoles(Partido partido, String equipoId) {
        if (partido.getRegistro() == null)
            return 0;

        return partido.getRegistro().stream()
                .filter(r -> obtenerEquipoIdPorJugadorId(r.getJugadorId()).equals(equipoId))
                .mapToInt(RegistroJugador::getGoles)
                .sum();
    }

    private String obtenerEquipoIdPorJugadorId(String jugadorId) {
        return jugadorRepository.findById(jugadorId)
                .map(Jugador::getEquipoId)
                .orElse("");
    }

    private List<Enfrentamiento> generarEnfrentamientos(
            List<Equipo> ganadores, List<Equipo> perdedores, int jornada) {

        List<Enfrentamiento> enfrentamientos = new ArrayList<>();
        enfrentamientos.addAll(crearEnfrentamientosPorGrupo(ganadores, "Ganadores", jornada));
        enfrentamientos.addAll(crearEnfrentamientosPorGrupo(perdedores, "Perdedores", jornada));
        return enfrentamientos;
    }

    private List<Enfrentamiento> crearEnfrentamientosPorGrupo(List<Equipo> grupo, String fase, int jornada) {
        List<Enfrentamiento> lista = new ArrayList<>();
        List<Equipo> disponibles = new ArrayList<>(grupo);
        Collections.shuffle(disponibles);

        if (disponibles.size() % 2 != 0) {
            Equipo descansado = disponibles.remove(0);
            System.out.println("DESCANSO en " + fase + ": " + descansado.getNombre());
        }

        for (int i = 0; i < disponibles.size(); i += 2) {
            Equipo equipoA = disponibles.get(i);
            Equipo equipoB = disponibles.get(i + 1);
            lista.add(new Enfrentamiento(equipoA, equipoB, fase, jornada));
        }

        return lista;
    }

    public void actualizarFaseEquipos(String torneoId) {
        List<Partido> partidos = partidoRepository.findByTorneoIdAndEliminadoFalse(torneoId);
        List<TablaPosicion> tabla = tablaPosicionRepository.findByTorneoIdAndEliminadoFalse(torneoId);

        Map<String, Long> derrotasPorEquipo = new HashMap<>();
        Map<String, Long> victoriasPorEquipo = new HashMap<>();

        for (Partido partido : partidos) {
            if (!"finalizado".equalsIgnoreCase(partido.getEstado()) || partido.getRegistro() == null)
                continue;

            int golesA = contarGoles(partido, partido.getEquipoAId());
            int golesB = contarGoles(partido, partido.getEquipoBId());

            if (golesA > golesB) {
                victoriasPorEquipo.merge(partido.getEquipoAId(), 1L, Long::sum);
                derrotasPorEquipo.merge(partido.getEquipoBId(), 1L, Long::sum);
            } else if (golesB > golesA) {
                victoriasPorEquipo.merge(partido.getEquipoBId(), 1L, Long::sum);
                derrotasPorEquipo.merge(partido.getEquipoAId(), 1L, Long::sum);
            }
        }

        // Fase provisional
        String posibleCampeonId = null;

        for (TablaPosicion pos : tabla) {
            String equipoId = pos.getEquipoId();
            long derrotas = derrotasPorEquipo.getOrDefault(equipoId, 0L);
            long victorias = victoriasPorEquipo.getOrDefault(equipoId, 0L);

            if (derrotas >= 2) {
                pos.setFase("Eliminado");
            } else if (victorias >= 3 && derrotas == 0) {
                pos.setFase("Final G");
            } else if (victorias >= 3 && derrotas == 1) {
                pos.setFase("Final P");
            } else if (victorias >= 4 && derrotas == 1) {
                pos.setFase("Gran Final");
                posibleCampeonId = equipoId;
            } else if (victorias == 0 && derrotas == 0) {
                pos.setFase("Ronda 1 G");
            } else if (derrotas == 1) {
                pos.setFase("Ronda " + (victorias + 1) + " P");
            } else {
                pos.setFase("Ronda " + (victorias + 1) + " G");
            }

            tablaPosicionRepository.save(pos);
        }

        // Verificar si hay solo un equipo activo (no eliminado)
        List<TablaPosicion> activos = tabla.stream()
                .filter(tp -> !"Eliminado".equals(tp.getFase()))
                .toList();

        if (activos.size() == 1 && posibleCampeonId != null) {
            TablaPosicion campeon = tabla.stream()
                    .filter(tp -> tp.getEquipoId().equals(activos.get(0)))
                    .findFirst()
                    .orElse(null);

            if (campeon != null) {
                campeon.setFase("Campeón");
                tablaPosicionRepository.save(campeon);

            }

            Torneo torneo = torneoRepository.findById(torneoId)
                    .orElseThrow(() -> new RuntimeException("Torneo no encontrado"));

            torneo.setEstado("Finalizado");
            torneo.setFechaFin(LocalDate.now());
            torneoRepository.save(torneo);

            // Después de setFase("Campeón");
            String nombreEquipo = equipoRepository.findById(posibleCampeonId)
                    .map(Equipo::getNombre)
                    .orElse("Desconocido");

            String nombreTorneo = torneo.getNombreTorneo();

            System.out.println(
                    "🏆 ¡El equipo " + nombreEquipo + " ha sido declarado CAMPEÓN del torneo " + nombreTorneo + "!");
        }
    }
}