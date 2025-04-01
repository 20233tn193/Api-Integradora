package gtf.integradora.entity;

public class Cancha {
    private String nombreCancha; // Ej: "Cancha 1", "Cancha A", etc.

    public Cancha() {}

    public Cancha(String nombreCancha) {
        this.nombreCancha = nombreCancha;
    }

    public String getNombreCancha() {
        return nombreCancha;
    }

    public void setNombreCancha(String nombreCancha) {
        this.nombreCancha = nombreCancha;
    }
}