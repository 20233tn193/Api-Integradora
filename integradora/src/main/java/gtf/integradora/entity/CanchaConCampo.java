package gtf.integradora.entity;

public class CanchaConCampo {
    private String campoId;
    private String nombreCampo;
    private String nombreCancha;

    public CanchaConCampo(String campoId, String nombreCampo, String nombreCancha) {
        this.campoId = campoId;
        this.nombreCampo = nombreCampo;
        this.nombreCancha = nombreCancha;
    }

    // Getters
    public String getCampoId() {
        return campoId;
    }

    public String getNombreCampo() {
        return nombreCampo;
    }

    public String getNombreCancha() {
        return nombreCancha;
    }
}