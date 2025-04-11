package gtf.integradora.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "equipos")
public class Equipo {
    @Id
    private String id;

    private String nombre;
    private String logoUrl;
    private String duenoId;
    private String torneoId;
    private String escudoUrl; // ✅ NUEVO CAMPO


    private boolean eliminado = false;

    private int partidosGanados = 0;
    private int partidosPerdidos = 0;

    public Equipo() {}

    public Equipo(String nombre, String logoUrl, String duenoId, String torneoId) {
        this.nombre = nombre;
        this.logoUrl = logoUrl;
        this.duenoId = duenoId;
        this.torneoId = torneoId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getDuenoId() {
        return duenoId;
    }

    public void setDuenoId(String dueñoId) {
        this.duenoId = dueñoId;
    }

    public String getTorneoId() {
        return torneoId;
    }

    public void setTorneoId(String torneoId) {
        this.torneoId = torneoId;
    }

    public boolean isEliminado() {
        return eliminado;
    }

    public void setEliminado(boolean eliminado) {
        this.eliminado = eliminado;
    }

    // Getters y setters para partidos ganados y perdidos
    public int getPartidosGanados() {
        return partidosGanados;
    }

    public void setPartidosGanados(int partidosGanados) {
        this.partidosGanados = partidosGanados;
    }

    public int getPartidosPerdidos() {
        return partidosPerdidos;
    }

    public void setPartidosPerdidos(int partidosPerdidos) {
        this.partidosPerdidos = partidosPerdidos;
    }
}