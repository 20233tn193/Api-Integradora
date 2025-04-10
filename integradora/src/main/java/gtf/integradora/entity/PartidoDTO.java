package gtf.integradora.entity;

import java.util.Map;

public class PartidoDTO {
    public String id;
    public String nombreEquipoA;
    public String nombreEquipoB;
    public String logoEquipoA;
    public String logoEquipoB;
    public String nombreCampo;
    public String nombreCancha;
    public String nombreArbitro;
    public Object fecha;
    public Object hora;
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getNombreEquipoA() {
        return nombreEquipoA;
    }
    public void setNombreEquipoA(String nombreEquipoA) {
        this.nombreEquipoA = nombreEquipoA;
    }
    public String getNombreEquipoB() {
        return nombreEquipoB;
    }
    public void setNombreEquipoB(String nombreEquipoB) {
        this.nombreEquipoB = nombreEquipoB;
    }
    public String getLogoEquipoA() {
        return logoEquipoA;
    }
    public void setLogoEquipoA(String logoEquipoA) {
        this.logoEquipoA = logoEquipoA;
    }
    public String getLogoEquipoB() {
        return logoEquipoB;
    }
    public void setLogoEquipoB(String logoEquipoB) {
        this.logoEquipoB = logoEquipoB;
    }
    public String getNombreCampo() {
        return nombreCampo;
    }
    public void setNombreCampo(String nombreCampo) {
        this.nombreCampo = nombreCampo;
    }
    public String getNombreCancha() {
        return nombreCancha;
    }
    public void setNombreCancha(String nombreCancha) {
        this.nombreCancha = nombreCancha;
    }
    public String getNombreArbitro() {
        return nombreArbitro;
    }
    public void setNombreArbitro(String nombreArbitro) {
        this.nombreArbitro = nombreArbitro;
    }
    public Object getFecha() {
        return fecha;
    }
    public void setFecha(Object fecha) {
        this.fecha = fecha;
    }
    public Object getHora() {
        return hora;
    }
    public void setHora(Object hora) {
        this.hora = hora;
    }

    // Getters y setters opcionales si usas @Data o similares
}