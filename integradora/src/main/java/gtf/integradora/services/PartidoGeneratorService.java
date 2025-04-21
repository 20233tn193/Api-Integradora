package gtf.integradora.services;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import gtf.integradora.entity.*;
import gtf.integradora.repository.*;

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
    private final PagoRepository pagoRepository;

    public PartidoGeneratorService(
            EquipoRepository equipoRepository,
            PartidoRepository partidoRepository,
            CampoRepository campoRepository,
            ArbitroRepository arbitroRepository,
            JugadorRepository jugadorRepository,
            PartidoScheduler partidoScheduler,
            TorneoRepository torneoRepository,
            TablaPosicionRepository tablaPosicionRepository,
            PagoRepository pagoRepository) {
        this.equipoRepository = equipoRepository;
        this.partidoRepository = partidoRepository;
        this.campoRepository = campoRepository;
        this.arbitroRepository = arbitroRepository;
        this.jugadorRepository = jugadorRepository;
        this.partidoScheduler = partidoScheduler;
        this.torneoRepository = torneoRepository;
        this.tablaPosicionRepository = tablaPosicionRepository;
        this.pagoRepository = pagoRepository;
    }

    public List<Partido> generarSiguienteJornada(String torneoId) {
        Torneo torneo = torneoRepository.findById(torneoId)
                .orElseThrow(() -> new RuntimeException("Torneo no encontrado"));

        if ("finalizado".equalsIgnoreCase(torneo.getEstado())) {
            throw new RuntimeException("No se puede generar jornadas para un torneo finalizado.");
        }

        List<Equipo> equipos = equipoRepository.findByTorneoIdAndEliminadoFalse(torneoId);
        List<Partido> partidosAnteriores = partidoRepository.findByTorneoIdAndEliminadoFalse(torneoId);
        List<Equipo> equiposElegibles = filtrarEquiposElegibles(equipos, partidosAnteriores);

        // 🏆 Si solo queda un equipo, declararlo campeón automáticamente
        if (equiposElegibles.size() == 1) {
            Equipo campeon = equiposElegibles.get(0);
            torneo.setCampeonId(campeon.getId());
            torneo.setEstado("FINALIZADO");
            torneoRepository.save(torneo);
            System.out.println("🏆 ¡Campeón declarado automáticamente!: " + campeon.getNombre());
            return List.of(); // No generar más partidos
        }

        int jornadaActual = partidosAnteriores.stream()
                .mapToInt(Partido::getJornada)
                .max()
                .orElse(0);

        int nuevaJornada = jornadaActual + 1;

        System.out.println("✅ Generando jornada " + nuevaJornada + " para torneo: " + torneoId);
        System.out.println("🔎 Total equipos inscritos: " + equipos.size());
        System.out.println("🔎 Equipos elegibles (menos de 2 derrotas): " + equiposElegibles.size());
        System.out.println("🔁 Total partidos anteriores: " + partidosAnteriores.size());

        List<Equipo> ganadores;
        List<Equipo> perdedores;

        if (partidosAnteriores.isEmpty()) {
            ganadores = new ArrayList<>(equiposElegibles);
            perdedores = new ArrayList<>();
        } else {
            ganadores = obtenerEquiposGanadores(equiposElegibles, partidosAnteriores);
            perdedores = obtenerEquiposPerdedores(equiposElegibles, partidosAnteriores);
        }

        Optional<Partido> granFinal = partidosAnteriores.stream()
                .filter(p -> "Gran Final".equalsIgnoreCase(p.getFase()) && "finalizado".equalsIgnoreCase(p.getEstado()))
                .findFirst();

        if (granFinal.isPresent()) {
            Partido finalPartido = granFinal.get();
            String invictoId = encontrarInvictoId(equipos, partidosAnteriores);
            int golesInvicto = contarGoles(finalPartido, invictoId);
            int golesRival = contarGoles(finalPartido,
                    finalPartido.getEquipoAId().equals(invictoId) ? finalPartido.getEquipoBId()
                            : finalPartido.getEquipoAId());

            if (golesRival > golesInvicto) {
                System.out.println("🔁 El invicto perdió en la Gran Final, generando revancha");
                List<Equipo> revanchistas = new ArrayList<>();
                revanchistas.add(equipoRepository.findById(finalPartido.getEquipoAId()).orElseThrow());
                revanchistas.add(equipoRepository.findById(finalPartido.getEquipoBId()).orElseThrow());

                List<Enfrentamiento> revancha = generarEnfrentamientos(revanchistas, Collections.emptyList(),
                        nuevaJornada);

                List<Campo> campos = campoRepository.findByDisponibleTrueAndEliminadoFalse();
                List<Arbitro> arbitros = arbitroRepository.findByEliminadoFalse();

                List<Partido> partidosRevancha = partidoScheduler.asignarPartidos(revancha, campos, arbitros, torneoId,
                        nuevaJornada);
                partidosRevancha.forEach(p -> p.setFase("Revancha Final"));
                partidosRevancha.forEach(this::crearPagosDePartido);
                return partidoRepository.saveAll(partidosRevancha);
            }
        }

        List<Enfrentamiento> enfrentamientos = generarEnfrentamientos(ganadores, perdedores, nuevaJornada);
        System.out.println("🧩 Enfrentamientos generados: " + enfrentamientos.size());

        List<Campo> campos = campoRepository.findByDisponibleTrueAndEliminadoFalse();
        List<Arbitro> arbitros = arbitroRepository.findByEliminadoFalse();

        List<Partido> nuevosPartidos = partidoScheduler.asignarPartidos(
                enfrentamientos, campos, arbitros, torneoId, nuevaJornada);

        System.out.println("📍 Nuevos partidos generados: " + nuevosPartidos.size());

        for (Partido partido : nuevosPartidos) {
            crearPagosDePartido(partido);
        }

        actualizarFaseEquipos(torneoId);

        return nuevosPartidos;
    }

    private String encontrarInvictoId(List<Equipo> equipos, List<Partido> partidos) {
        Map<String, Integer> derrotas = new HashMap<>();
        for (Partido partido : partidos) {
            if (!"finalizado".equalsIgnoreCase(partido.getEstado()) || partido.getRegistro() == null)
                continue;
            int golesA = contarGoles(partido, partido.getEquipoAId());
            int golesB = contarGoles(partido, partido.getEquipoBId());
            if (golesA > golesB) {
                derrotas.merge(partido.getEquipoBId(), 1, Integer::sum);
            } else if (golesB > golesA) {
                derrotas.merge(partido.getEquipoAId(), 1, Integer::sum);
            }
        }
        return equipos.stream()
                .filter(e -> derrotas.getOrDefault(e.getId(), 0) == 0)
                .map(Equipo::getId)
                .findFirst()
                .orElse("");
    }

    // Métodos auxiliares no modificados (crearPago, crearPagosDePartido,
    // contarGoles, etc.)
    // Aqui podemos cambiar el monto de los pagos
    private void crearPagosDePartido(Partido partido) {
        crearPago(partido, partido.getEquipoAId(), "arbitraje", 150);
        crearPago(partido, partido.getEquipoAId(), "uso_de_cancha", 100);
        crearPago(partido, partido.getEquipoBId(), "arbitraje", 150);
        crearPago(partido, partido.getEquipoBId(), "uso_de_cancha", 100);
    }

    private void crearPago(Partido partido, String equipoId, String tipo, float montoBase) {
        Equipo equipo = equipoRepository.findById(equipoId)
                .orElseThrow(() -> new RuntimeException("Equipo no encontrado para el pago"));

        Pago pago = new Pago();
        pago.setTorneoId(partido.getTorneoId());
        pago.setPartidoId(partido.getId());
        pago.setEquipoId(equipoId);
        pago.setDuenoId(equipo.getDuenoId());
        pago.setTipo(tipo);
        pago.setEstatus("pendiente");
        pago.setFechaPago(new Date());
        pago.setMonto(montoBase);
        pago.setEliminado(false);

        pagoRepository.save(pago);
    }

 
    private List<Equipo> obtenerEquiposGanadores(List<Equipo> equipos, List<Partido> partidos) {
        Map<String, Integer> victoriasPorEquipo = new HashMap<>();
        Set<String> equiposQueJugaron = new HashSet<>();

        for (Partido partido : partidos) {
            if (!"finalizado".equalsIgnoreCase(partido.getEstado()) || partido.getRegistro() == null)
                continue;

            int golesA = contarGoles(partido, partido.getEquipoAId());
            int golesB = contarGoles(partido, partido.getEquipoBId());

            equiposQueJugaron.add(partido.getEquipoAId());
            equiposQueJugaron.add(partido.getEquipoBId());

            if (golesA > golesB) {
                victoriasPorEquipo.merge(partido.getEquipoAId(), 1, Integer::sum);
            } else if (golesB > golesA) {
                victoriasPorEquipo.merge(partido.getEquipoBId(), 1, Integer::sum);
            }
        }

        // Ganadores: los que tienen al menos una victoria o no han jugado
        return equipos.stream()
                .filter(e -> victoriasPorEquipo.getOrDefault(e.getId(), 0) >= 1
                        || !equiposQueJugaron.contains(e.getId()))
                .collect(Collectors.toList());
    }

    private List<Equipo> obtenerEquiposPerdedores(List<Equipo> equipos, List<Partido> partidos) {
        Map<String, Integer> derrotas = new HashMap<>();
        Set<String> equiposQueJugaron = new HashSet<>();

        for (Partido partido : partidos) {
            if (!"finalizado".equalsIgnoreCase(partido.getEstado()) || partido.getRegistro() == null)
                continue;

            String eqA = partido.getEquipoAId();
            String eqB = partido.getEquipoBId();
            int golesA = contarGoles(partido, eqA);
            int golesB = contarGoles(partido, eqB);

            equiposQueJugaron.add(eqA);
            equiposQueJugaron.add(eqB);

            if (golesA > golesB) {
                derrotas.merge(eqB, 1, Integer::sum);
            } else if (golesB > golesA) {
                derrotas.merge(eqA, 1, Integer::sum);
            }
        }

        return equipos.stream()
                .filter(e -> derrotas.getOrDefault(e.getId(), 0) == 1)
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
                .filter(r -> equipoId.equals(r.getEquipoId()))
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