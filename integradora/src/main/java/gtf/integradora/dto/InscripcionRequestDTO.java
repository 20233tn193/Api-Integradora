package gtf.integradora.dto;

public class InscripcionRequestDTO {
    private String equipoId;
    private String torneoId;

    
    public InscripcionRequestDTO() {
    }

    public InscripcionRequestDTO(String equipoId, String torneoId) {
        this.equipoId = equipoId;
        this.torneoId = torneoId;
    }

    public String getEquipoId() {
        return equipoId;
    }

    public void setEquipoId(String equipoId) {
        this.equipoId = equipoId;
    }

    public String getTorneoId() {
        return torneoId;
    }

    public void setTorneoId(String torneoId) {
        this.torneoId = torneoId;
    }
}