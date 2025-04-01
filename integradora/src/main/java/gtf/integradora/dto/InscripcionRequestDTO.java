package gtf.integradora.dto;

public class InscripcionRequestDTO {
    private String duenoId;
    private String torneoId;
    private String nombreEquipo;
    private String logoUrl;

    // Getters y setters
    public String getDuenoId() {
        return duenoId;
    }

    public void setDuenoId(String duenoId) {
        this.duenoId = duenoId;
    }

    public String getTorneoId() {
        return torneoId;
    }

    public void setTorneoId(String torneoId) {
        this.torneoId = torneoId;
    }

    public String getNombreEquipo() {
        return nombreEquipo;
    }

    public void setNombreEquipo(String nombreEquipo) {
        this.nombreEquipo = nombreEquipo;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }
}