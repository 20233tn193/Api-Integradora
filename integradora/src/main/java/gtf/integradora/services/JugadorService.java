package gtf.integradora.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import gtf.integradora.entity.Equipo;
import gtf.integradora.entity.Jugador;
import gtf.integradora.repository.EquipoRepository;
import gtf.integradora.repository.JugadorRepository;
import gtf.integradora.repository.PagoRepository;

@Service
public class JugadorService {

    private final JugadorRepository jugadorRepository;
    private final EquipoRepository equipoRepository;
    private final PagoRepository pagoRepository; // Para validar inscripción pagada

    public JugadorService(JugadorRepository jugadorRepository, EquipoRepository equipoRepository,
            PagoRepository pagoRepository) {
        this.jugadorRepository = jugadorRepository;
        this.equipoRepository = equipoRepository;
        this.pagoRepository = pagoRepository;
    }

    public Jugador crearJugador(Jugador jugador) {
        String equipoId = jugador.getEquipoId();

        Equipo equipo = equipoRepository.findByIdAndEliminadoFalse(equipoId)
                .orElseThrow(() -> new RuntimeException("Equipo no encontrado"));

        // Validar CURP única dentro del torneo
        String torneoId = equipo.getTorneoId();
        boolean curpDuplicada = jugadorRepository.findAll().stream()
                .filter(j -> !j.isEliminado())
                .filter(j -> j.getCurp().equalsIgnoreCase(jugador.getCurp()))
                .anyMatch(j -> {
                    Optional<Equipo> eq = equipoRepository.findByIdAndEliminadoFalse(j.getEquipoId());
                    return eq.map(e -> torneoId.equals(e.getTorneoId())).orElse(false);
                });

        if (curpDuplicada) {
            throw new RuntimeException("El CURP ya ha sido registrado en este torneo.");
        }

        // Validar máximo 20 jugadores
        int cantidad = jugadorRepository.countByEquipoIdAndEliminadoFalse(equipoId);
        if (cantidad >= 20) {
            throw new RuntimeException("Este equipo ya tiene el máximo de 20 jugadores.");
        }

        // Validar pago de inscripción aprobado
        boolean tienePago = pagoRepository.existsByEquipoIdAndTorneoIdAndTipoAndEstatus(
                equipoId, torneoId, "inscripcion", "pagado");

        if (!tienePago) {
            throw new RuntimeException(
                    "No puedes registrar jugadores hasta que el administrador apruebe el pago de inscripción.");
        }

        jugador.setEliminado(false);
        return jugadorRepository.save(jugador);
    }

    public List<Jugador> obtenerPorEquipo(String equipoId) {
        return jugadorRepository.findByEquipoIdAndEliminadoFalse(equipoId);
    }

    public Optional<Jugador> obtenerPorId(String id) {
        return jugadorRepository.findByIdAndEliminadoFalse(id);
    }

    public Jugador actualizarJugador(String id, Jugador actualizado) {
        Jugador jugador = jugadorRepository.findByIdAndEliminadoFalse(id)
                .orElseThrow(() -> new RuntimeException("Jugador no encontrado"));

        jugador.setNombre(actualizado.getNombre());
        jugador.setApellido(actualizado.getApellido());
        jugador.setFotoUrl(actualizado.getFotoUrl());
        jugador.setFechaNacimiento(actualizado.getFechaNacimiento());

        return jugadorRepository.save(jugador);
    }

    public void eliminarJugador(String id) {
        Jugador jugador = jugadorRepository.findByIdAndEliminadoFalse(id)
                .orElseThrow(() -> new RuntimeException("Jugador no encontrado"));

        jugador.setEliminado(true);
        jugadorRepository.save(jugador);
    }
}