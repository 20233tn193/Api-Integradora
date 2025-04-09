package gtf.integradora.entity;

public class EquipoConDuenoDTO {
    private String equipoNombre;
    private String duenoNombre;
    private String pagoEstatus;

    public EquipoConDuenoDTO(String equipoNombre, String duenoNombre, String pagoEstatus) {
        this.equipoNombre = equipoNombre;
        this.duenoNombre = duenoNombre;
        this.pagoEstatus = pagoEstatus;
    }

    public String getEquipoNombre() {
        return equipoNombre;
    }

    public void setEquipoNombre(String equipoNombre) {
        this.equipoNombre = equipoNombre;
    }

    public String getDuenoNombre() {
        return duenoNombre;
    }

    public void setDuenoNombre(String duenoNombre) {
        this.duenoNombre = duenoNombre;
    }

    public String getPagoEstatus() {
        return pagoEstatus;
    }

    public void setPagoEstatus(String pagoEstatus) {
        this.pagoEstatus = pagoEstatus;
    }

}