package gtf.integradora.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import gtf.integradora.entity.Torneo;

@Repository
public interface TorneoRepository extends MongoRepository<Torneo, String> {

    // Buscar solo los torneos no eliminados
    List<Torneo> findByEliminadoFalse();

    Optional<Torneo> findByIdAndEliminadoFalse(String id);
}