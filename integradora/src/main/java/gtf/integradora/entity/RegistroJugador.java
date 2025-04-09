package gtf.integradora.entity;

public class RegistroJugador {

    private String jugadorId;
    private boolean asistio;
    private int goles;
    private int amarillas;
    private int rojas;
    private boolean suspendido = false;

    public RegistroJugador() {
    }

    public RegistroJugador(String jugadorId, boolean asistio, int goles, int amarillas, int rojas, boolean suspendido) {
        this.jugadorId = jugadorId;
        this.asistio = asistio;
        this.goles = goles;
        this.amarillas = amarillas;
        this.rojas = rojas;
        this.suspendido = suspendido;
    }
    

    public boolean isSuspendido() {
        return suspendido;
    }

    public void setSuspendido(boolean suspendido) {
        this.suspendido = suspendido;
    }

    public String getJugadorId() {
        return jugadorId;
    }

    public void setJugadorId(String jugadorId) {
        this.jugadorId = jugadorId;
    }

    public boolean isAsistio() {
        return asistio;
    }

    public void setAsistio(boolean asistio) {
        this.asistio = asistio;
    }

    public int getGoles() {
        return goles;
    }

    public void setGoles(int goles) {
        this.goles = goles;
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

}