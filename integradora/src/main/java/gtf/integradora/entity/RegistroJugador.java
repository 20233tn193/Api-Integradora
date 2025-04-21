package gtf.integradora.entity;

public class RegistroJugador {

    private String jugadorId;
    private String equipoId;
    private boolean asistio;
    private Integer goles;
    private Integer amarillas;
    private Integer rojas;
    private boolean suspendido = false;

    public RegistroJugador() {
    }

    public RegistroJugador(String jugadorId, boolean asistio, Integer goles, Integer amarillas, Integer rojas,
            boolean suspendido) {
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

    public String getEquipoId() {
        return equipoId;
    }

    public void setEquipoId(String equipoId) {
        this.equipoId = equipoId;
    }

    public Integer getGoles() {
        return goles;
    }

    public void setGoles(Integer goles) {
        this.goles = goles;
    }

    public Integer getAmarillas() {
        return amarillas;
    }

    public void setAmarillas(Integer amarillas) {
        this.amarillas = amarillas;
    }

    public Integer getRojas() {
        return rojas;
    }

    public void setRojas(Integer rojas) {
        this.rojas = rojas;
    }

}