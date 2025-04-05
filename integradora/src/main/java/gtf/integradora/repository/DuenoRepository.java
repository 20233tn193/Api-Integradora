package gtf.integradora.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import gtf.integradora.entity.Dueno;

@Repository
public interface DuenoRepository extends MongoRepository<Dueno, String> {

    Optional<Dueno> findByIdAndEliminadoFalse(String id);

    List<Dueno> findByEliminadoFalse();
}