package gtf.integradora.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import gtf.integradora.entity.Campo;
import gtf.integradora.entity.Cancha;
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
        return campoRepository.findAll(); // ✅ Devuelve TODOS los campos, incluso los eliminados
    }

    public Optional<Campo> obtenerPorId(String id) {
        return campoRepository.findByIdAndEliminadoFalse(id);
    }

    public Campo actualizarCampo(String id, Campo actualizado) {
        // 🔍 Buscar el campo en la base de datos (no eliminado)
        Campo campo = campoRepository.findByIdAndEliminadoFalse(id)
                .orElseThrow(() -> new RuntimeException("Campo no encontrado"));
    
        // 📋 Logs para verificar lo recibido
        System.out.println("📥 Campo recibido service  en Service:");
        System.out.println("Nombre: " + actualizado.getNombreCampo());
        System.out.println("Latitud: " + actualizado.getLatitud());
        System.out.println("Longitud: " + actualizado.getLongitud());
        System.out.println("✅ Canchas recibidas:");
    
        // 🧹 Limpiar y filtrar solo canchas válidas
        List<Cancha> canchasFiltradas = new ArrayList<>();
        if (actualizado.getCanchas() != null) {
            for (Cancha c : actualizado.getCanchas()) {
                if (c != null && c.getNombreCancha() != null && !c.getNombreCancha().trim().isEmpty()) {
                    System.out.println(" - " + c.getNombreCancha());
                    canchasFiltradas.add(new Cancha(c.getNombreCancha().trim()));
                }
            }
        } else {
            System.out.println(" - Lista vacía");
        }
    
        // 🛠️ Actualizar valores en el campo original
        campo.setNombreCampo(actualizado.getNombreCampo());
        campo.setLatitud(actualizado.getLatitud());
        campo.setLongitud(actualizado.getLongitud());
        campo.setCanchas(canchasFiltradas); // ✅ Solo las canchas válidas
        campo.setDisponible(actualizado.isDisponible());
        campo.setEliminado(actualizado.isEliminado());
    
        // 💾 Guardar en base de datos
        Campo guardado = campoRepository.save(campo);
        System.out.println("📝 Campo guardado correctamente: " + guardado);
    
        return guardado;
    }
    public void eliminarCampo(String id) {
        Campo campo = campoRepository.findByIdAndEliminadoFalse(id)
                .orElseThrow(() -> new RuntimeException("Campo no encontrado"));
        campo.setEliminado(true);
        campoRepository.save(campo);
    }
    public void cambiarEstadoCampo(String id, boolean eliminado) {
        Campo campo = campoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Campo no encontrado"));
        campo.setEliminado(eliminado);
        campoRepository.save(campo);
    }
}