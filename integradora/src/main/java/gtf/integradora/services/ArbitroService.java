package gtf.integradora.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gtf.integradora.entity.Arbitro;
import gtf.integradora.entity.Partido;
import gtf.integradora.repository.ArbitroRepository;
import gtf.integradora.repository.PartidoRepository;

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
        return arbitroRepository.findAll(); // ✅ Devuelve árbitros habilitados e inhabilitados
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

    public Optional<Arbitro> obtenerPorUsuarioId(String idUsuario) {
        return arbitroRepository.findByIdUsuario(idUsuario);
    }
    @Autowired
private PartidoRepository partidoRepository;

public List<Partido> obtenerPartidosAsignados(String arbitroId) {
    return partidoRepository.findByArbitroIdAndEliminadoFalse(arbitroId);
}
public void cambiarEstado(String id, boolean eliminado) {
    Arbitro arbitro = arbitroRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Árbitro no encontrado"));
    arbitro.setEliminado(eliminado);
    arbitroRepository.save(arbitro);
}
}