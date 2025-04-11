package gtf.integradora.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gtf.integradora.dto.InscripcionRequestDTO;
import gtf.integradora.entity.Dueno;
import gtf.integradora.entity.Equipo;
import gtf.integradora.entity.EquipoConDuenoDTO;
import gtf.integradora.entity.Pago;
import gtf.integradora.entity.Torneo;
import gtf.integradora.repository.EquipoRepository;
import gtf.integradora.repository.PagoRepository;
import gtf.integradora.repository.TorneoRepository;
import gtf.integradora.repository.DuenoRepository;


@Service
public class EquipoService {

    private final EquipoRepository equipoRepository;
    private final TorneoRepository torneoRepository;
    private final PagoRepository pagoRepository;
    

    public EquipoService(
        EquipoRepository equipoRepository,
        TorneoRepository torneoRepository,
        PagoRepository pagoRepository
    ) {
        this.equipoRepository = equipoRepository;
        this.torneoRepository = torneoRepository;
        this.pagoRepository = pagoRepository;
    }
    @Autowired
private DuenoRepository duenoRepository;



    public Equipo crearEquipo(Equipo equipo) {
        equipo.setEliminado(false);
        return equipoRepository.save(equipo);
    }

    public List<Equipo> obtenerPorTorneo(String torneoId) {
        return equipoRepository.findByTorneoIdAndEliminadoFalse(torneoId);
    }

    public List<Equipo> obtenerPorDueno(String duenoId) {
        return equipoRepository.findByDuenoIdAndEliminadoFalse(duenoId);
    }

    public Optional<Equipo> obtenerPorId(String id) {
        return equipoRepository.findByIdAndEliminadoFalse(id);
    }

    public Equipo actualizarEquipo(String id, Equipo actualizado) {
        Equipo equipo = equipoRepository.findByIdAndEliminadoFalse(id)
                .orElseThrow(() -> new RuntimeException("Equipo no encontrado"));

        equipo.setNombre(actualizado.getNombre());
        equipo.setLogoUrl(actualizado.getLogoUrl());

        return equipoRepository.save(equipo);
    }

    public void eliminarEquipo(String id) {
        Equipo equipo = equipoRepository.findByIdAndEliminadoFalse(id)
                .orElseThrow(() -> new RuntimeException("Equipo no encontrado"));
        equipo.setEliminado(true);
        equipoRepository.save(equipo);
    }

    public boolean existeNombreEquipoEnTorneo(String nombre, String torneoId) {
        return equipoRepository.existsByNombreAndTorneoIdAndEliminadoFalse(nombre, torneoId);
    }

    public String inscribirEquipo(InscripcionRequestDTO request) {
        // Validar que el torneo esté abierto
        Torneo torneo = torneoRepository.findByIdAndEliminadoFalse(request.getTorneoId())
                .orElseThrow(() -> new RuntimeException("Torneo no encontrado"));

        if (!"abierto".equalsIgnoreCase(torneo.getEstado())) {
            throw new RuntimeException("El torneo ya no acepta inscripciones.");
        }

        // Crear el equipo
        Equipo equipo = new Equipo();
        equipo.setNombre(request.getNombreEquipo());
        equipo.setLogoUrl(request.getLogoUrl());
        equipo.setDuenoId(request.getDuenoId());
        equipo.setTorneoId(request.getTorneoId());
        equipo.setEliminado(false);
        equipoRepository.save(equipo);

        // Crear el pago de inscripción pendiente
        Pago pago = new Pago();
        pago.setEquipoId(equipo.getId());
        pago.setDuenoId(request.getDuenoId());
        pago.setTorneoId(request.getTorneoId());
        pago.setTipo("inscripcion");
        pago.setEstatus("pendiente");
        pago.setMonto(torneo.getCosto());
        pago.setFechaPago(null); // Aún no pagado
        pago.setEliminado(false);
        pagoRepository.save(pago);

        return "Equipo inscrito correctamente. Se generó un pago pendiente.";
    }
    public List<EquipoConDuenoDTO> obtenerEquiposConDuenoPorTorneo(String torneoId) {
    List<Equipo> equipos = equipoRepository.findByTorneoIdAndEliminadoFalse(torneoId);

    List<EquipoConDuenoDTO> resultado = new ArrayList<>();

    for (Equipo equipo : equipos) {
        String nombreDueno = "Sin nombre";

        if (equipo.getDuenoId() != null) {
            Dueno dueno = duenoRepository.findById(equipo.getDuenoId()).orElse(null);
            if (dueno != null) {
                nombreDueno = dueno.getNombre() + " " + dueno.getApellido();
            }
        }

        // Opcional: busca el pago de inscripción si lo manejas como entidad
      // Buscar estatus de pago
String estatusPago = "Pendiente"; // por defecto
Optional<Pago> pagoOptional = pagoRepository.findByEquipoIdAndTipo(equipo.getId(), "inscripcion");
if (pagoOptional.isPresent()) {
    estatusPago = pagoOptional.get().getEstatus();
}

        resultado.add(new EquipoConDuenoDTO(equipo.getNombre(), nombreDueno, estatusPago));
    }

    return resultado;
}
}