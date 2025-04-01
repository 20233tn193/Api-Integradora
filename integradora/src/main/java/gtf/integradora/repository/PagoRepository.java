package gtf.integradora.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import gtf.integradora.entity.Pago;

@Repository
public interface PagoRepository extends MongoRepository<Pago, String> {

    List<Pago> findByTorneoIdAndEliminadoFalse(String torneoId);

    List<Pago> findByEquipoIdAndEliminadoFalse(String equipoId);

    List<Pago> findByDuenoIdAndEliminadoFalse(String duenoId);

    Optional<Pago> findByIdAndEliminadoFalse(String id);

    boolean existsByEquipoIdAndTorneoIdAndTipoAndEstatus(String equipoId, String torneoId, String tipo, String estatus);
}