package gtf.integradora.services;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
//import gtf.integradora.dto.RegistroPartidoDTO;
import gtf.integradora.entity.Equipo;
import gtf.integradora.entity.Goleador;
import gtf.integradora.entity.Jugador;
import gtf.integradora.entity.Partido;
import gtf.integradora.entity.PartidoScheduler;
import gtf.integradora.entity.RegistroJugador;
import gtf.integradora.entity.TablaPosicion;
import gtf.integradora.entity.Tarjeta;
import gtf.integradora.repository.ArbitroRepository;
import gtf.integradora.repository.CampoRepository;
import gtf.integradora.repository.EquipoRepository;
import gtf.integradora.repository.GoleadorRepository;
import gtf.integradora.repository.JugadorRepository;
import gtf.integradora.repository.PartidoRepository;
import gtf.integradora.repository.TablaPosicionRepository;
import gtf.integradora.repository.TarjetaRepository;
import gtf.integradora.entity.Torneo;
import gtf.integradora.repository.TorneoRepository;

@Service
public class PartidoService {

    private final PartidoRepository partidoRepository;
    private final JugadorRepository jugadorRepository;
    private final TablaPosicionRepository tablaPosicionRepository;
    private final GoleadorRepository goleadorRepository;
    private final TarjetaRepository tarjetaRepository;
    private final EquipoRepository equipoRepository;
    @SuppressWarnings("unused")
    private final CampoRepository campoRepository;
    @SuppressWarnings("unused")
    private final ArbitroRepository arbitroRepository;
    private final TorneoRepository torneoRepository;
    @SuppressWarnings("unused")
    private final PartidoScheduler partidoScheduler;
    private final PartidoGeneratorService partidoGeneratorService;

    public PartidoService(
            PartidoRepository partidoRepository,
            JugadorRepository jugadorRepository,
            TablaPosicionRepository tablaPosicionRepository,
            GoleadorRepository goleadorRepository,
            TarjetaRepository tarjetaRepository,
            EquipoRepository equipoRepository,
            CampoRepository campoRepository,
            ArbitroRepository arbitroRepository,
            TorneoRepository torneoRepository,
            PartidoScheduler partidoScheduler,
            PartidoGeneratorService partidoGeneratorService) {
        this.partidoRepository = partidoRepository;
        this.jugadorRepository = jugadorRepository;
        this.tablaPosicionRepository = tablaPosicionRepository;
        this.goleadorRepository = goleadorRepository;
        this.equipoRepository = equipoRepository;
        this.campoRepository = campoRepository;
        this.arbitroRepository = arbitroRepository;
        this.torneoRepository = torneoRepository;
        this.tarjetaRepository = tarjetaRepository;
        this.partidoScheduler = partidoScheduler;
        this.partidoGeneratorService = partidoGeneratorService;
    }

    public Partido crearPartido(Partido partido) {
        partido.setEliminado(false);
        partido.setEstado("pendiente");

        Torneo torneo = torneoRepository.findById(partido.getTorneoId())
                .orElseThrow(() -> new RuntimeException("Torneo no encontrado"));

        if (torneo.getFechaInicio() != null && partido.getFecha().isBefore(torneo.getFechaInicio())) {
            throw new RuntimeException("No se puede programar un partido antes del inicio del torneo.");
        }

        if (torneo.getFechaFin() != null && partido.getFecha().isAfter(torneo.getFechaFin())) {
            throw new RuntimeException("No se puede programar un partido después de que el torneo finalizó.");
        }

        // Validar si el árbitro ya tiene un partido asignado a esa hora
        List<Partido> partidosMismoHorario = partidoRepository.findByArbitroIdAndFechaAndHoraAndEliminadoFalse(
                partido.getArbitroId(), partido.getFecha(), partido.getHora());

        if (!partidosMismoHorario.isEmpty()) {
            throw new RuntimeException("El árbitro ya tiene un partido asignado en esa fecha y hora.");
        }

        // Validación: evitar que se programe más de un partido en el mismo campo,
        // fecha y hora
        List<Partido> conflictos = partidoRepository.findByCampoIdAndFechaAndHoraAndEliminadoFalse(
                partido.getCampoId(), partido.getFecha(), partido.getHora());

        if (!conflictos.isEmpty()) {
            throw new RuntimeException("Ya hay un partido programado en ese campo, fecha y hora.");
        }

        return partidoRepository.save(partido);
    }

    public List<Partido> obtenerPorTorneo(String torneoId) {
        return partidoRepository.findByTorneoIdAndEliminadoFalse(torneoId);
    }

    public List<Partido> obtenerPorArbitro(String arbitroId) {
        return partidoRepository.findByArbitroIdAndEliminadoFalse(arbitroId);
    }

    public Optional<Partido> obtenerPorId(String id) {
        return partidoRepository.findByIdAndEliminadoFalse(id);
    }

    public List<Partido> obtenerPorEstado(String estado) {
        return partidoRepository.findByEstadoAndEliminadoFalse(estado);
    }

    public Partido actualizarPartido(String id, Partido datos) {
        Partido partido = partidoRepository.findByIdAndEliminadoFalse(id)
                .orElseThrow(() -> new RuntimeException("Partido no encontrado"));

        // Validar conflicto de horario si se cambia el árbitro o la hora/fecha
        if (!partido.getArbitroId().equals(datos.getArbitroId()) ||
                !partido.getFecha().equals(datos.getFecha()) ||
                !partido.getHora().equals(datos.getHora())) {

            List<Partido> conflictos = partidoRepository.findByArbitroIdAndFechaAndHoraAndEliminadoFalse(
                    datos.getArbitroId(), datos.getFecha(), datos.getHora());

            boolean hayConflicto = conflictos.stream()
                    .anyMatch(p -> !p.getId().equals(partido.getId()));

            if (hayConflicto) {
                throw new RuntimeException("El árbitro ya tiene un partido asignado en esa fecha y hora.");
            }
        }

        partido.setFecha(datos.getFecha());
        partido.setHora(datos.getHora());
        partido.setCampoId(datos.getCampoId());
        partido.setNombreCampo(datos.getNombreCampo());
        partido.setNombreCancha(datos.getNombreCancha());
        partido.setArbitroId(datos.getArbitroId());
        partido.setNombreArbitro(datos.getNombreArbitro());
        partido.setEstado(datos.getEstado());
        partido.setFase(datos.getFase());
        partido.setJornada(datos.getJornada());

        return partidoRepository.save(partido);
    }

    public void eliminarPartido(String id) {
        Partido partido = partidoRepository.findByIdAndEliminadoFalse(id)
                .orElseThrow(() -> new RuntimeException("Partido no encontrado"));
        partido.setEliminado(true);
        partidoRepository.save(partido);
    }

    private int contarGolesPorEquipo(List<RegistroJugador> registro, String equipoId) {
        return registro.stream()
                .filter(r -> {
                    Optional<Jugador> jugador = jugadorRepository.findById(r.getJugadorId());
                    return jugador.map(j -> equipoId.equals(j.getEquipoId())).orElse(false);
                })
                .mapToInt(RegistroJugador::getGoles)
                .sum();
    }

    private void actualizarTablaPosiciones(String equipoId, int golesFavor, int golesContra, boolean gano,
            String torneoId) {
        // Si no se encuentra una tabla de posiciones para ese equipo, crea una nueva,
        // pero ahora con torneoId
        TablaPosicion tabla = tablaPosicionRepository.findByEquipoIdAndTorneoId(equipoId, torneoId)
                .orElseGet(() -> new TablaPosicion(equipoId, torneoId)); // Aquí se pasa torneoId también

        tabla.setPartidosJugados(tabla.getPartidosJugados() + 1);
        tabla.setGolesFavor(tabla.getGolesFavor() + golesFavor);
        tabla.setGolesContra(tabla.getGolesContra() + golesContra);
        tabla.setDiferenciaGoles(tabla.getGolesFavor() - tabla.getGolesContra());

        if (gano) {
            tabla.setGanados(tabla.getGanados() + 1);
            tabla.setPuntos(tabla.getPuntos() + 3);
        } else {
            tabla.setPerdidos(tabla.getPerdidos() + 1);
        }

        tablaPosicionRepository.save(tabla);
    }

    private void actualizarGoleadores(List<RegistroJugador> registro, String torneoId) {
        for (RegistroJugador r : registro) {
            if (r.getGoles() <= 0)
                continue;

            Goleador goleador = goleadorRepository.findByJugadorIdAndTorneoId(r.getJugadorId(), torneoId)
                    .orElseGet(() -> new Goleador(r.getJugadorId(), torneoId));

            goleador.setGoles(goleador.getGoles() + r.getGoles());
            goleadorRepository.save(goleador);
        }
    }

    private void actualizarTarjetas(List<RegistroJugador> registro, String torneoId) {
        for (RegistroJugador r : registro) {
            Tarjeta tarjeta = tarjetaRepository.findByJugadorIdAndTorneoId(r.getJugadorId(), torneoId)
                    .orElseGet(() -> new Tarjeta(r.getJugadorId(), torneoId));

            // Si el jugador está suspendido pero no asistió al partido, se descuenta 1
            // partido de suspensión
            if (!r.isAsistio() && tarjeta.isSuspendido()) {
                int restantes = tarjeta.getPartidosSuspendido() - 1;
                tarjeta.setPartidosSuspendido(Math.max(0, restantes));
                if (restantes <= 0) {
                    tarjeta.setSuspendido(false);
                }
            }

            // Si asistió al partido y recibió tarjetas
            if (r.isAsistio()) {
                tarjeta.setAmarillas(tarjeta.getAmarillas() + r.getAmarillas());
                tarjeta.setRojas(tarjeta.getRojas() + r.getRojas());

                // Si recibió tarjeta roja, queda suspendido 1 partido
                if (r.getRojas() > 0) {
                    tarjeta.setSuspendido(true);
                    tarjeta.setPartidosSuspendido(1);
                }
            }

            tarjetaRepository.save(tarjeta);
        }
    }

    // Método para obtener los equipos ganadores
    @SuppressWarnings("unused")
    private List<Equipo> obtenerEquiposGanadores(List<Equipo> equipos, int jornadaActual) {
        return equipos.stream()
                .filter(equipo -> equipo.getPartidosGanados() > equipo.getPartidosPerdidos()) // Aquí es una
                                                                                              // simplificación
                .collect(Collectors.toList());
    }

    // Método para obtener los equipos perdedores
    @SuppressWarnings("unused")
    private List<Equipo> obtenerEquiposPerdedores(List<Equipo> equipos, int jornadaActual) {
        return equipos.stream()
                .filter(equipo -> equipo.getPartidosPerdidos() > equipo.getPartidosGanados()) // Aquí es una
                                                                                              // simplificación
                .collect(Collectors.toList());
    }

    // Generar los enfrentamientos para la nueva jornada

    private List<Partido> generarEnfrentamientos(List<Equipo> ganadores, List<Equipo> perdedores, int jornada) {
        // Ejemplo básico de cómo emparejar a los equipos en partidos
        List<Partido> partidos = new ArrayList<>();

        // Emparejar ganadores y perdedores en partidos
        for (int i = 0; i < ganadores.size(); i++) {
            Equipo ganador = ganadores.get(i);
            Equipo perdedor = (i < perdedores.size()) ? perdedores.get(i) : null; // Si no hay más perdedores, puede ser
                                                                                  // nulo
            Partido partido = new Partido(ganador, perdedor, jornada);
            partidos.add(partido);
        }

        return partidos;
    }

    public Partido registrarResultado(String partidoId, List<RegistroJugador> registro) {
        Partido partido = partidoRepository.findByIdAndEliminadoFalse(partidoId)
                .orElseThrow(() -> new RuntimeException("Partido no encontrado"));

        for (RegistroJugador r : registro) {
            if (r.getEquipoId() == null || r.getEquipoId().isEmpty()) {
                throw new RuntimeException("Falta equipoId en registro de jugador " + r.getJugadorId());
            }
        }

        for (RegistroJugador r : registro) {
            Tarjeta tarjeta = tarjetaRepository.findByJugadorIdAndTorneoId(r.getJugadorId(), partido.getTorneoId())
                    .orElse(null);

            if (tarjeta != null && tarjeta.getRojas() > 0) {
                r.setSuspendido(true);
                r.setAsistio(false);
            }
        }

        partido.setRegistro(registro);
        partido.setEstado("finalizado");
        partidoRepository.save(partido);

        int golesEquipoA = contarGolesPorEquipo(registro, partido.getEquipoAId());
        int golesEquipoB = contarGolesPorEquipo(registro, partido.getEquipoBId());

        partido.setGolesEquipoA(golesEquipoA);
        partido.setGolesEquipoB(golesEquipoB);
        partidoRepository.save(partido);

        boolean ganaA = golesEquipoA > golesEquipoB;
        boolean ganaB = golesEquipoB > golesEquipoA;

        actualizarTablaPosiciones(partido.getEquipoAId(), golesEquipoA, golesEquipoB, ganaA, partido.getTorneoId());
        actualizarTablaPosiciones(partido.getEquipoBId(), golesEquipoB, golesEquipoA, ganaB, partido.getTorneoId());

        actualizarGoleadores(registro, partido.getTorneoId());
        actualizarTarjetas(registro, partido.getTorneoId());

        verificarYGenerarSiguienteJornada(partido.getTorneoId());

        partidoGeneratorService.actualizarFaseEquipos(partido.getTorneoId());
        return partido;
    }

    // Agregue este metodo para hacer automatico la generacion
    // de jornadas - Abril 03 - 3:41 am

    public void verificarYGenerarSiguienteJornada(String torneoId) {
        List<Partido> partidos = partidoRepository.findByTorneoIdAndEliminadoFalse(torneoId);
        if (partidos.isEmpty())
            return;

        int jornadaActual = partidos.stream()
                .mapToInt(Partido::getJornada)
                .max()
                .orElse(0);

        boolean todosFinalizados = partidos.stream()
                .filter(p -> p.getJornada() == jornadaActual)
                .allMatch(p -> "finalizado".equalsIgnoreCase(p.getEstado()));

        if (todosFinalizados) {
            partidoGeneratorService.generarSiguienteJornada(torneoId);
            System.out.println("Nueva jornada generada automáticamente");
        }
    }

    public List<Jugador> obtenerJugadoresSuspendidos(String torneoId) {
        List<Tarjeta> tarjetas = tarjetaRepository.findByTorneoIdAndEliminadoFalse(torneoId);

        return tarjetas.stream()
                .filter(t -> t.getRojas() >= 1)
                .map(t -> jugadorRepository.findById(t.getJugadorId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    public Partido reprogramarPartido(
            String id,
            LocalDate nuevaFecha,
            LocalTime nuevaHora,
            String nuevoCampoId,
            String nuevoNombreCampo,
            String nuevaCancha,
            String nuevoArbitroId,
            String nuevoNombreArbitro) {

        Partido partido = partidoRepository.findByIdAndEliminadoFalse(id)
                .orElseThrow(() -> new RuntimeException("Partido no encontrado"));

        // Validar que la nueva fecha esté dentro del rango del torneo
        Torneo torneo = equipoRepository.findById(partido.getEquipoAId())
                .flatMap(equipo -> Optional.ofNullable(equipo.getTorneoId()))
                .flatMap(torneoId -> torneoRepository.findById(torneoId))
                .orElseThrow(() -> new RuntimeException("Torneo no encontrado para el partido"));

        if ((torneo.getFechaInicio() != null && nuevaFecha.isBefore(torneo.getFechaInicio())) ||
                (torneo.getFechaFin() != null && nuevaFecha.isAfter(torneo.getFechaFin()))) {
            throw new RuntimeException("La nueva fecha está fuera del rango permitido por el torneo.");
        }

        // Validar conflicto de horario para el árbitro
        List<Partido> conflictos = partidoRepository.findByArbitroIdAndFechaAndHoraAndEliminadoFalse(
                nuevoArbitroId, nuevaFecha, nuevaHora);

        boolean hayConflicto = conflictos.stream()
                .anyMatch(p -> !p.getId().equals(partido.getId()));

        if (hayConflicto) {
            throw new RuntimeException("El árbitro ya tiene un partido asignado en esa fecha y hora.");
        }

        partido.setFecha(nuevaFecha);
        partido.setHora(nuevaHora);
        partido.setCampoId(nuevoCampoId);
        partido.setNombreCampo(nuevoNombreCampo);
        partido.setNombreCancha(nuevaCancha);
        partido.setArbitroId(nuevoArbitroId);
        partido.setNombreArbitro(nuevoNombreArbitro);

        return partidoRepository.save(partido);
    }

    public Map<Integer, List<Partido>> obtenerCalendarioPorJornada(String torneoId) {
        List<Partido> partidos = partidoRepository.findByTorneoIdAndEliminadoFalse(torneoId);

        return partidos.stream()
                .collect(Collectors.groupingBy(Partido::getJornada));
    }

}