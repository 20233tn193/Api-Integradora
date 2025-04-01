package gtf.integradora.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import gtf.integradora.entity.Goleador;

@Repository
public interface GoleadorRepository extends MongoRepository<Goleador, String> {
    Optional<Goleador> findByJugadorIdAndTorneoId(String jugadorId, String torneoId);

    List<Goleador> findByTorneoIdAndEliminadoFalse(String torneoId);
}