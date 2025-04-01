package gtf.integradora.entity;

import java.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection = "torneos")
public class Torneo {
    @Id
    private String id;
    private String nombreTorneo;
    private int numeroEquipos;
    private String logoSeleccionado; // nombre o id del logo (por ejemplo: "logo1.png")
    private String estado; // abierto, cerrado, en_curso, finalizado
    private String informacion;
    private float costo;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;

    private boolean eliminado = false;

    // Constructor vacío
    public Torneo() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombreTorneo() {
        return nombreTorneo;
    }

    public void setNombreTorneo(String nombreTorneo) {
        this.nombreTorneo = nombreTorneo;
    }

    public int getNumeroEquipos() {
        return numeroEquipos;
    }

    public void setNumeroEquipos(int numeroEquipos) {
        this.numeroEquipos = numeroEquipos;
    }

    public String getLogoSeleccionado() {
        return logoSeleccionado;
    }

    public void setLogoSeleccionado(String logoSeleccionado) {
        this.logoSeleccionado = logoSeleccionado;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getInformacion() {
        return informacion;
    }

    public void setInformacion(String informacion) {
        this.informacion = informacion;
    }

    public float getCosto() {
        return costo;
    }

    public void setCosto(float costo) {
        this.costo = costo;
    }

    public boolean isEliminado() {
        return eliminado;
    }

    public void setEliminado(boolean eliminado) {
        this.eliminado = eliminado;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    
    
}