package gtf.integradora.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import gtf.integradora.entity.Campo;
import gtf.integradora.repository.CampoRepository;

@Service
public class CampoService {

    private final CampoRepository campoRepository;

    public CampoService(CampoRepository campoRepository) {
        this.campoRepository = campoRepository;
    }

    public Campo crearCampo(Campo campo) {
        campo.setEliminado(false);
        return campoRepository.save(campo);
    }

    public List<Campo> obtenerTodos() {
        return campoRepository.findByEliminadoFalse();
    }

    public Optional<Campo> obtenerPorId(String id) {
        return campoRepository.findByIdAndEliminadoFalse(id);
    }

    public Campo actualizarCampo(String id, Campo actualizado) {
        Campo campo = campoRepository.findByIdAndEliminadoFalse(id)
                .orElseThrow(() -> new RuntimeException("Campo no encontrado"));

        campo.setNombreCampo(actualizado.getNombreCampo());
        campo.setLatitud(actualizado.getLatitud());
        campo.setLongitud(actualizado.getLongitud());
        campo.setCanchas(actualizado.getCanchas());

        return campoRepository.save(campo);
    }

    public void eliminarCampo(String id) {
        Campo campo = campoRepository.findByIdAndEliminadoFalse(id)
                .orElseThrow(() -> new RuntimeException("Campo no encontrado"));
        campo.setEliminado(true);
        campoRepository.save(campo);
    }
}