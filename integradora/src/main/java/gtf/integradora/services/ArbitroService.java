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

    public Optional<Arbitro> obtenerPorCorreo(String correo) {
        return arbitroRepository.findByCorreoAndEliminadoFalse(correo);
    }

    public Arbitro actualizarArbitro(String id, Arbitro actualizado) {
        Arbitro arbitro = arbitroRepository.findByIdAndEliminadoFalse(id)
                .orElseThrow(() -> new RuntimeException("Árbitro no encontrado"));

        arbitro.setNombre(actualizado.getNombre());
        arbitro.setApellido(actualizado.getApellido());
        arbitro.setCelular(actualizado.getCelular());
        arbitro.setCorreo(actualizado.getCorreo());
        arbitro.setFotoUrl(actualizado.getFotoUrl());
        // Password no se actualiza aquí por seguridad

        return arbitroRepository.save(arbitro);
    }

    public void eliminarArbitro(String id) {
        Arbitro arbitro = arbitroRepository.findByIdAndEliminadoFalse(id)
                .orElseThrow(() -> new RuntimeException("Árbitro no encontrado"));
        arbitro.setEliminado(true);
        arbitroRepository.save(arbitro);
    }
}