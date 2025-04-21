package gtf.integradora.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import gtf.integradora.entity.Torneo;
import gtf.integradora.repository.TorneoRepository;

@Service
public class TorneoService {

    private final TorneoRepository torneoRepository;

    public TorneoService(TorneoRepository torneoRepository) {
        this.torneoRepository = torneoRepository;
    }

    public Torneo crearTorneo(Torneo torneo) {
        torneo.setEliminado(false);
        return torneoRepository.save(torneo);
    }

    public List<Torneo> obtenerTorneosActivos() {
        return torneoRepository.findByEliminadoFalse();
    }

    public Optional<Torneo> obtenerTorneoPorId(String id) {
        return torneoRepository.findByIdAndEliminadoFalse(id);
    }

    public Torneo actualizarTorneo(String id, Torneo datosActualizados) {
        Optional<Torneo> optional = torneoRepository.findByIdAndEliminadoFalse(id);
        if (optional.isEmpty())
            throw new RuntimeException("Torneo no encontrado");

        Torneo torneo = optional.get();
        torneo.setNombreTorneo(datosActualizados.getNombreTorneo());
        torneo.setNumeroEquipos(datosActualizados.getNumeroEquipos());
        torneo.setLogoSeleccionado(datosActualizados.getLogoSeleccionado());
        torneo.setEstado(datosActualizados.getEstado());
        torneo.setInformacion(datosActualizados.getInformacion());
        torneo.setCosto(datosActualizados.getCosto());
        torneo.setFechaInicio(datosActualizados.getFechaInicio());
        torneo.setFechaFin(datosActualizados.getFechaFin());

        return torneoRepository.save(torneo);
    }

    public void eliminarTorneo(String id) {
        Optional<Torneo> optional = torneoRepository.findByIdAndEliminadoFalse(id);
        if (optional.isEmpty())
            throw new RuntimeException("Torneo no encontrado");

        Torneo torneo = optional.get();
        torneo.setEliminado(true);
        torneoRepository.save(torneo);
    }

    public Torneo finalizarTorneo(String torneoId) {
        Torneo torneo = torneoRepository.findByIdAndEliminadoFalse(torneoId)
                .orElseThrow(() -> new RuntimeException("Torneo no encontrado"));
    
        torneo.setEstado("finalizado");
        torneo.setFechaFin(LocalDate.now()); // ✅ Guardamos la fecha de finalización
    
        return torneoRepository.save(torneo);
    }
    public Torneo iniciarTorneo(String torneoId) {
        Torneo torneo = torneoRepository.findByIdAndEliminadoFalse(torneoId)
                .orElseThrow(() -> new RuntimeException("Torneo no encontrado"));
    
        if (!"abierto".equalsIgnoreCase(torneo.getEstado())) {
            throw new RuntimeException("Solo se puede iniciar un torneo que esté en estado 'abierto'");
        }
    
        torneo.setEstado("Cerrado");
        torneo.setFechaInicio(LocalDate.now());
        return torneoRepository.save(torneo);
    }
}