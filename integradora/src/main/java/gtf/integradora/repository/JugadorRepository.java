package gtf.integradora.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import gtf.integradora.entity.Jugador;

@Repository
public interface JugadorRepository extends MongoRepository<Jugador, String> {
    Optional<Jugador> findById(String id);

    List<Jugador> findByEquipoIdAndEliminadoFalse(String equipoId);

    Optional<Jugador> findByCurpAndEliminadoFalse(String curp);

    Optional<Jugador> findByIdAndEliminadoFalse(String id);

    int countByEquipoIdAndEliminadoFalse(String equipoId);

    boolean existsByCurpAndEliminadoFalse(String curp);

    boolean existsByCurpIgnoreCaseAndEliminadoFalse(String curp);
}