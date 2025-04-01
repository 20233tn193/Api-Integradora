package gtf.integradora.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "tarjetas")
public class Tarjeta {
    @Id
    private String id;

    private String jugadorId;
    private String torneoId;

    private int amarillas = 0;
    private int rojas = 0;

    private boolean eliminado = false;

    private int partidosSuspendido = 0; // cuántos partidos le faltan por cumplir de suspensión

    private boolean suspendido = false;

    public Tarjeta() {
    }

    public Tarjeta(String jugadorId, String torneoId) {
        this.jugadorId = jugadorId;
        this.torneoId = torneoId;
    }


    
    public int getPartidosSuspendido() {
        return partidosSuspendido;
    }

    public void setPartidosSuspendido(int partidosSuspendido) {
        this.partidosSuspendido = partidosSuspendido;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJugadorId() {
        return jugadorId;
    }

    public void setJugadorId(String jugadorId) {
        this.jugadorId = jugadorId;
    }

    public String getTorneoId() {
        return torneoId;
    }

    public void setTorneoId(String torneoId) {
        this.torneoId = torneoId;
    }

    public int getAmarillas() {
        return amarillas;
    }

    public void setAmarillas(int amarillas) {
        this.amarillas = amarillas;
    }

    public int getRojas() {
        return rojas;
    }

    public void setRojas(int rojas) {
        this.rojas = rojas;
    }

    public boolean isEliminado() {
        return eliminado;
    }

    public void setEliminado(boolean eliminado) {
        this.eliminado = eliminado;
    }

    public boolean isSuspendido() {
        return suspendido;
    }

    public void setSuspendido(boolean suspendido) {
        this.suspendido = suspendido;
    }

}