package gtf.integradora.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class ReprogramarPartidoDTO {
    private LocalDate nuevaFecha;
    private LocalTime nuevaHora;
    private String nuevoCampoId;
    private String nuevoNombreCampo;
    private String nuevaCancha;
    private String nuevoArbitroId;
    private String nuevoNombreArbitro;

    // Getters y setters
    public LocalDate getNuevaFecha() { return nuevaFecha; }
    public void setNuevaFecha(LocalDate nuevaFecha) { this.nuevaFecha = nuevaFecha; }

    public LocalTime getNuevaHora() { return nuevaHora; }
    public void setNuevaHora(LocalTime nuevaHora) { this.nuevaHora = nuevaHora; }

    public String getNuevoCampoId() { return nuevoCampoId; }
    public void setNuevoCampoId(String nuevoCampoId) { this.nuevoCampoId = nuevoCampoId; }

    public String getNuevoNombreCampo() { return nuevoNombreCampo; }
    public void setNuevoNombreCampo(String nuevoNombreCampo) { this.nuevoNombreCampo = nuevoNombreCampo; }

    public String getNuevaCancha() { return nuevaCancha; }
    public void setNuevaCancha(String nuevaCancha) { this.nuevaCancha = nuevaCancha; }

    public String getNuevoArbitroId() { return nuevoArbitroId; }
    public void setNuevoArbitroId(String nuevoArbitroId) { this.nuevoArbitroId = nuevoArbitroId; }

    public String getNuevoNombreArbitro() { return nuevoNombreArbitro; }
    public void setNuevoNombreArbitro(String nuevoNombreArbitro) { this.nuevoNombreArbitro = nuevoNombreArbitro; }
}