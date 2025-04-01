package gtf.integradora.entity;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class PartidoScheduler {

    private static final LocalTime HORA_INICIO_BASE = LocalTime.of(10, 0);
    private static final int DURACION_PARTIDO_HORAS = 2;

    private Map<String, List<Partido>> partidosPorArbitro = new HashMap<>();
    private int canchaIndex = 0;
    private int arbitroIndex = 0;
    private int turnoHorario = 0;

    public List<Partido> asignarPartidos(
            List<Enfrentamiento> enfrentamientos,
            List<Campo> campos,
            List<Arbitro> arbitros,
            String torneoId,
            int jornada
    ) {
        List<Partido> partidosAsignados = new ArrayList<>();

        List<CanchaConCampo> canchasDisponibles = obtenerCanchasDisponibles(campos);

        for (@SuppressWarnings("unused") Enfrentamiento enfrentamiento : enfrentamientos) {
            // Asignar cancha
            CanchaConCampo canchaAsignada = canchasDisponibles.get(canchaIndex % canchasDisponibles.size());
            canchaIndex++;

            // Asignar horario
            LocalDate fecha = LocalDate.now().plusDays(jornada); // Simulación
            LocalTime hora = HORA_INICIO_BASE.plusHours(turnoHorario * DURACION_PARTIDO_HORAS);
            turnoHorario++;

            // Asignar árbitro disponible
            Arbitro arbitroAsignado = encontrarArbitroDisponible(arbitros, fecha, hora);
            if (arbitroAsignado == null) {
                turnoHorario++;
                hora = HORA_INICIO_BASE.plusHours(turnoHorario * DURACION_PARTIDO_HORAS);
                arbitroAsignado = encontrarArbitroDisponible(arbitros, fecha, hora);
            }

            // Registrar partido al árbitro
            partidosPorArbitro.computeIfAbsent(arbitroAsignado.getId(), k -> new ArrayList<>())
                    .add(new Partido()); // lo actualizaremos abajo

            // Actualizar campos del partido
            Partido partido = new Partido();
            partido.setCampoId(canchaAsignada.getCampoId());
            partido.setNombreCampo(canchaAsignada.getNombreCampo());
            partido.setNombreCancha(canchaAsignada.getNombreCancha());

            partido.setArbitroId(arbitroAsignado.getId());
            partido.setNombreArbitro(arbitroAsignado.getNombre() + " " + arbitroAsignado.getApellido());

            partido.setFecha(fecha);
            partido.setHora(hora);
            partido.setDuracionHoras(DURACION_PARTIDO_HORAS);
            partido.setEstado("pendiente");
            partido.setTorneoId(torneoId);
            partido.setJornada(jornada);
            partido.setEliminado(false);

            // Reemplazar el partido en la lista del árbitro
            partidosPorArbitro.get(arbitroAsignado.getId()).set(
                partidosPorArbitro.get(arbitroAsignado.getId()).size() - 1, partido
            );

            partidosAsignados.add(partido);
        }

        return partidosAsignados;
    }

    private List<CanchaConCampo> obtenerCanchasDisponibles(List<Campo> campos) {
        List<CanchaConCampo> lista = new ArrayList<>();
        for (Campo campo : campos) {
            for (Cancha cancha : campo.getCanchas()) {
                lista.add(new CanchaConCampo(campo.getId(), campo.getNombreCampo(), cancha.getNombreCancha()));
            }
        }
        return lista;
    }

    private Arbitro encontrarArbitroDisponible(List<Arbitro> arbitros, LocalDate fecha, LocalTime hora) {
        int intentos = 0;
        while (intentos < arbitros.size()) {
            Arbitro arbitro = arbitros.get(arbitroIndex % arbitros.size());
            arbitroIndex++;

            List<Partido> asignados = partidosPorArbitro.getOrDefault(arbitro.getId(), new ArrayList<>());
            boolean disponible = asignados.stream().noneMatch(p ->
                p.getFecha().equals(fecha) &&
                Math.abs(Duration.between(p.getHora(), hora).toHours()) < DURACION_PARTIDO_HORAS
            );

            if (disponible) return arbitro;
            intentos++;
        }

        return null;
    }
}