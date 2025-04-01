package gtf.integradora.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import gtf.integradora.entity.Partido;

@Repository
public interface PartidoRepository extends MongoRepository<Partido, String> {

    List<Partido> findByTorneoIdAndEliminadoFalse(String torneoId);

    List<Partido> findByArbitroIdAndEliminadoFalse(String arbitroId);

    Optional<Partido> findByIdAndEliminadoFalse(String id);

    List<Partido> findByEstadoAndEliminadoFalse(String estado);

    List<Partido> findByArbitroIdAndFechaAndHoraAndEliminadoFalse(String arbitroId, LocalDate fecha, LocalTime hora);

    List<Partido> findByCampoIdAndFechaAndHoraAndEliminadoFalse(String campoId, LocalDate fecha, LocalTime hora);
}