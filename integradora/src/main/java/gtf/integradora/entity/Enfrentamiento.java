package gtf.integradora.entity;

public class Enfrentamiento {
    private Equipo equipoA;
    private Equipo equipoB;
    private String fase;
    private int jornada;

    public Enfrentamiento(Equipo equipoA, Equipo equipoB, String fase, int jornada) {
        this.equipoA = equipoA;
        this.equipoB = equipoB;
        this.fase = fase;
        this.jornada = jornada;
    }

    public Equipo getEquipoA() {
        return equipoA;
    }

    public void setEquipoA(Equipo equipoA) {
        this.equipoA = equipoA;
    }

    public Equipo getEquipoB() {
        return equipoB;
    }

    public void setEquipoB(Equipo equipoB) {
        this.equipoB = equipoB;
    }

    public String getFase() {
        return fase;
    }

    public void setFase(String fase) {
        this.fase = fase;
    }

    public int getJornada() {
        return jornada;
    }

    public void setJornada(int jornada) {
        this.jornada = jornada;
    }

    // Getters y setters
    
}