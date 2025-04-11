package gtf.integradora.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import gtf.integradora.entity.Arbitro;
import gtf.integradora.repository.ArbitroRepository;

@Service
public class ArbitroService {

    private final ArbitroRepository arbitroRepository;

    public ArbitroService(ArbitroRepository arbitroRepository) {
        this.arbitroRepository = arbitroRepository;
    }

    public Arbitro crearArbitro(Arbitro arbitro) {
        arbitro.setEliminado(false);
        return arbitroRepository.save(arbitro);
    }

    public List<Arbitro> obtenerTodos() {
        return arbitroRepository.findByEliminadoFalse();
    }

    public Optional<Arbitro> obtenerPorId(String id) {
        return arbitroRepository.findByIdAndEliminadoFalse(id);
    }

    public Arbitro actualizarArbitro(String id, Arbitro actualizado) {
        Arbitro arbitro = arbitroRepository.findByIdAndEliminadoFalse(id)
                .orElseThrow(() -> new RuntimeException("Árbitro no encontrado"));

        arbitro.setNombre(actualizado.getNombre());
        arbitro.setApellido(actualizado.getApellido());
        arbitro.setCelular(actualizado.getCelular());
        arbitro.setFotoUrl(actualizado.getFotoUrl());

        return arbitroRepository.save(arbitro);
    }

    public void eliminarArbitro(String id) {
        Arbitro arbitro = arbitroRepository.findByIdAndEliminadoFalse(id)
                .orElseThrow(() -> new RuntimeException("Árbitro no encontrado"));
        arbitro.setEliminado(true);
        arbitroRepository.save(arbitro);
    }

<<<<<<< HEAD
    public Optional<Arbitro> obtenerPorUsuarioId(String idUsuario) {
        return arbitroRepository.findByIdUsuario(idUsuario);
    }
=======
    public Optional<Arbitro> obtenerPorIdUsuario(String idUsuario) {
        return arbitroRepository.findByIdUsuario(idUsuario);
    }

>>>>>>> 52e9bdacdddc498053c771bfd6a2ad53d4cac822
}