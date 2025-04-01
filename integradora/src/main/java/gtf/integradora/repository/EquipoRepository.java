package gtf.integradora.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import gtf.integradora.entity.Equipo;

@Repository
public interface EquipoRepository extends MongoRepository<Equipo, String> {

    List<Equipo> findByTorneoIdAndEliminadoFalse(String torneoId);

    Optional<Equipo> findByIdAndEliminadoFalse(String id);

    List<Equipo> findByDuenoIdAndEliminadoFalse(String duenoId);

    boolean existsByNombreAndTorneoIdAndEliminadoFalse(String nombre, String torneoId);
}