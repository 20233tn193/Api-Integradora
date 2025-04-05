package gtf.integradora.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import gtf.integradora.entity.Administrador;

public interface AdministradorRepository extends MongoRepository<Administrador, String> {
    Optional<Administrador> findByIdUsuario(String idUsuario);
}