package gtf.integradora.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import gtf.integradora.entity.Arbitro;

@Repository
public interface ArbitroRepository extends MongoRepository<Arbitro, String> {

    Optional<Arbitro> findByIdAndEliminadoFalse(String id);

    List<Arbitro> findByEliminadoFalse();
}