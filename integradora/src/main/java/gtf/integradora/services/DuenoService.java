package gtf.integradora.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import gtf.integradora.entity.Dueno;
import gtf.integradora.repository.DuenoRepository;

@Service
public class DuenoService {

    private final DuenoRepository duenoRepository;

    public DuenoService(DuenoRepository duenoRepository) {
        this.duenoRepository = duenoRepository;
    }

    public Dueno crearDueno(Dueno dueno) {
        dueno.setEliminado(false);
        return duenoRepository.save(dueno);
    }

    public List<Dueno> obtenerTodos() {
        return duenoRepository.findAll(); // ✅ trae todos, incluso los eliminados
    }

    public Optional<Dueno> obtenerPorId(String id) {
        return duenoRepository.findByIdAndEliminadoFalse(id);
    }

    public Dueno actualizarDueno(String id, Dueno actualizado) {
        Dueno dueno = duenoRepository.findByIdAndEliminadoFalse(id)
                .orElseThrow(() -> new RuntimeException("Dueño no encontrado"));

        dueno.setNombre(actualizado.getNombre());
        dueno.setApellido(actualizado.getApellido());
        // Ya no se modifica correo ni contraseña aquí

        return duenoRepository.save(dueno);
    }

    public void eliminarDueno(String id) {
        Dueno dueno = duenoRepository.findByIdAndEliminadoFalse(id)
                .orElseThrow(() -> new RuntimeException("Dueño no encontrado"));
        dueno.setEliminado(true);
        duenoRepository.save(dueno);
    }

    public Optional<Dueno> obtenerPorIdUsuario(String idUsuario) {
        return duenoRepository.findByIdUsuario(idUsuario);
    }
    public void cambiarEstado(String id, boolean eliminado) {
        Dueno dueno = duenoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dueño no encontrado"));
        dueno.setEliminado(eliminado);
        duenoRepository.save(dueno);
    }
}