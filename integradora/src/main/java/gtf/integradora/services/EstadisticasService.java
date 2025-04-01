package gtf.integradora.services;

import java.util.List;

import org.springframework.stereotype.Service;

import gtf.integradora.entity.TablaPosicion;
import gtf.integradora.repository.TablaPosicionRepository;

@Service
public class EstadisticasService {

    private final TablaPosicionRepository tablaPosicionRepository;

    public EstadisticasService(TablaPosicionRepository tablaPosicionRepository) {
        this.tablaPosicionRepository = tablaPosicionRepository;
    }

    public List<TablaPosicion> obtenerTablaPosiciones(String torneoId) {
        return tablaPosicionRepository.findByTorneoIdAndEliminadoFalse(torneoId);
    }
}