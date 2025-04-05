package gtf.integradora.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import gtf.integradora.entity.Pago;
import gtf.integradora.repository.PagoRepository;

@Service
public class PagoService {

    private final PagoRepository pagoRepository;

    public PagoService(PagoRepository pagoRepository) {
        this.pagoRepository = pagoRepository;
    }

    public Pago crearPago(Pago pago) {
        pago.setEliminado(false);
        return pagoRepository.save(pago);
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