package gtf.integradora.services;

import gtf.integradora.entity.Pago;
import gtf.integradora.entity.PagoDTO;
import gtf.integradora.repository.EquipoRepository;
import gtf.integradora.repository.TorneoRepository;
import gtf.integradora.repository.DuenoRepository;
import gtf.integradora.repository.PagoRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

@Service
public class PagoService {

    private final PagoRepository pagoRepository;
    private final TorneoRepository torneoRepository;
    private final EquipoRepository equipoRepository;
    private final DuenoRepository duenoRepository;

    public PagoService(
        PagoRepository pagoRepository,
        TorneoRepository torneoRepository,
        EquipoRepository equipoRepository,
        DuenoRepository duenoRepository
    ) {
        this.pagoRepository = pagoRepository;
        this.torneoRepository = torneoRepository;
        this.equipoRepository = equipoRepository;
        this.duenoRepository = duenoRepository;
    }

    public Pago crearPago(Pago pago) {
        pago.setEliminado(false);
        return pagoRepository.save(pago);
    }

    public List<PagoDTO> obtenerPagosDetalladosPorTorneo(String torneoId) {
        List<Pago> pagos = pagoRepository.findByTorneoIdAndEliminadoFalse(torneoId);

        return pagos.stream().map(p -> {
            PagoDTO dto = new PagoDTO();
            dto.setId(p.getId());
            dto.setTipo(p.getTipo());
            dto.setFechaPago(p.getFechaPago());
            dto.setEstatus(p.getEstatus());
            dto.setMonto(p.getMonto());

            equipoRepository.findById(p.getEquipoId()).ifPresent(e -> dto.setEquipoNombre(e.getNombre()));
            duenoRepository.findById(p.getDuenoId()).ifPresent(d -> dto.setDuenoNombre(d.getNombre()));
            torneoRepository.findById(p.getTorneoId()).ifPresent(t -> dto.setTorneoNombre(t.getNombreTorneo()));

            return dto;
        }).collect(Collectors.toList());
    }

    public List<PagoDTO> obtenerTodosLosPagosDetallados() {
        List<Pago> pagos = pagoRepository.findByEliminadoFalse();

        return pagos.stream().map(p -> {
            PagoDTO dto = new PagoDTO();
            dto.setId(p.getId());
            dto.setTipo(p.getTipo());
            dto.setFechaPago(p.getFechaPago());
            dto.setEstatus(p.getEstatus());
            dto.setMonto(p.getMonto());

            equipoRepository.findById(p.getEquipoId()).ifPresent(e -> dto.setEquipoNombre(e.getNombre()));
            duenoRepository.findById(p.getDuenoId()).ifPresent(d -> dto.setDuenoNombre(d.getNombre()));
            torneoRepository.findById(p.getTorneoId()).ifPresent(t -> dto.setTorneoNombre(t.getNombreTorneo()));

            return dto;
        }).collect(Collectors.toList());
    }

    public List<Pago> obtenerPorTorneo(String torneoId) {
        return pagoRepository.findByTorneoIdAndEliminadoFalse(torneoId);
    }

    public List<Pago> obtenerPorEquipo(String equipoId) {
        return pagoRepository.findByEquipoIdAndEliminadoFalse(equipoId);
    }

    public List<Pago> obtenerPorDueno(String duenoId) {
        return pagoRepository.findByDuenoIdAndEliminadoFalse(duenoId);
    }

    public Optional<Pago> obtenerPorId(String id) {
        return pagoRepository.findByIdAndEliminadoFalse(id);
    }

    public boolean existePagoInscripcionAprobado(String equipoId, String torneoId) {
        return pagoRepository.existsByEquipoIdAndTorneoIdAndTipoAndEstatus(equipoId, torneoId, "inscripcion", "pagado");
    }

    public Pago actualizarEstatus(String id, String nuevoEstatus) {
        Pago pago = pagoRepository.findByIdAndEliminadoFalse(id)
            .orElseThrow(() -> new RuntimeException("Pago no encontrado"));

        pago.setEstatus(nuevoEstatus);
        return pagoRepository.save(pago);
    }

    public void eliminarPago(String id) {
        Pago pago = pagoRepository.findByIdAndEliminadoFalse(id)
            .orElseThrow(() -> new RuntimeException("Pago no encontrado"));

        pago.setEliminado(true);
        pagoRepository.save(pago);
    }

    public boolean tienePagoAprobado(String equipoId) {
        return !pagoRepository.findByEquipoIdAndEstatusAndEliminadoFalse(equipoId, "pagado").isEmpty();
    }
}