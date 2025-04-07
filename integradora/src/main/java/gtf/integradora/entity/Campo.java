package gtf.integradora.entity;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonProperty;

@Document(collection = "campos")
public class Campo {
    @Id
    private String id;


    private String nombreCampo;

    @JsonProperty("canchas")
    private List<Cancha> canchas = new ArrayList<>();

    private double latitud;
    private double longitud;
    private boolean eliminado = false;
    private boolean disponible = true;

    public Campo() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    public String getNombreCampo() {
        return nombreCampo;
    }

    public void setNombreCampo(String nombreCampo) {
        this.nombreCampo = nombreCampo;
    }

    public List<Cancha> getCanchas() {
        return canchas;
    }

    public void setCanchas(List<Cancha> canchas) {
        this.canchas = canchas;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public boolean isEliminado() {
        return eliminado;
    }

    public void setEliminado(boolean eliminado) {
        this.eliminado = eliminado;
    }

    @Override
    public String toString() {
        return "Campo{" +
                "id='" + id + '\'' +
                ", nombreCampo='" + nombreCampo + '\'' +
                ", canchas=" + canchas +
                ", latitud=" + latitud +
                ", longitud=" + longitud +
                ", eliminado=" + eliminado +
                ", disponible=" + disponible +
                '}';
    }
}