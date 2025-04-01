package gtf.integradora.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import gtf.integradora.entity.Campo;

@Repository
public interface CampoRepository extends MongoRepository<Campo, String> {

    List<Campo> findByEliminadoFalse();

    Optional<Campo> findByIdAndEliminadoFalse(String id);
}