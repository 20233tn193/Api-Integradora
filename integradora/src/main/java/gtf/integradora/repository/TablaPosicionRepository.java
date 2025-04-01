package gtf.integradora.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import gtf.integradora.entity.TablaPosicion;

@Repository
public interface TablaPosicionRepository extends MongoRepository<TablaPosicion, String> {
    Optional<TablaPosicion> findByEquipoIdAndTorneoId(String equipoId, String torneoId);

    List<TablaPosicion> findByTorneoIdAndEliminadoFalse(String torneoId);
}
