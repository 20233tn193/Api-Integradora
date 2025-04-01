package gtf.integradora.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import gtf.integradora.entity.Tarjeta;

@Repository
public interface TarjetaRepository extends MongoRepository<Tarjeta, String> {
    Optional<Tarjeta> findByJugadorIdAndTorneoId(String jugadorId, String torneoId);

    List<Tarjeta> findByTorneoIdAndEliminadoFalse(String torneoId);
}
