package gtf.integradora.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import gtf.integradora.entity.Administrador;
import gtf.integradora.repository.AdministradorRepository;

@Service
public class AdministradorService {

    private final AdministradorRepository administradorRepository;

    public AdministradorService(AdministradorRepository administradorRepository) {
        this.administradorRepository = administradorRepository;
    }

    public Administrador crearAdministrador(Administrador administrador) {
        administrador.setEliminado(false);
        return administradorRepository.save(administrador);
    }

    public List<Administrador> obtenerTodos() {
        return administradorRepository.findAll(); // Todos los administradores, incluidos eliminados
    }

    public Optional<Administrador> obtenerPorId(String id) {
        return administradorRepository.findById(id);
    }

    public Optional<Administrador> obtenerPorIdUsuario(String idUsuario) {
        return administradorRepository.findByIdUsuario(idUsuario);
    }

    public Administrador actualizarAdministrador(String id, Administrador actualizado) {
        return administradorRepository.findById(id).map(existing -> {
            existing.setNombre(actualizado.getNombre());
            existing.setEliminado(actualizado.isEliminado());
            return administradorRepository.save(existing);
        }).orElseThrow(() -> new RuntimeException("Administrador no encontrado"));
    }

    public void eliminarAdministrador(String id) {
        administradorRepository.findById(id).ifPresent(admin -> {
            admin.setEliminado(true);
            administradorRepository.save(admin);
        });
    }
}