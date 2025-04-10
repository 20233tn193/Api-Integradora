package gtf.integradora.entity;


public class TablaPosicionDTO {
    private String nombreEquipo;
    private String logoUrl;
    private String fase;
    private int partidosJugados;
    private int ganados;
    private int perdidos;
    private int golesFavor;
    private int golesContra;
    private int diferenciaGoles;
    private int puntos;

    // Getters y setters
    public String getNombreEquipo() { return nombreEquipo; }
    public void setNombreEquipo(String nombreEquipo) { this.nombreEquipo = nombreEquipo; }

    public String getLogoUrl() { return logoUrl; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }

    public String getFase() { return fase; }
    public void setFase(String fase) { this.fase = fase; }

    public int getPartidosJugados() { return partidosJugados; }
    public void setPartidosJugados(int partidosJugados) { this.partidosJugados = partidosJugados; }

    public int getGanados() { return ganados; }
    public void setGanados(int ganados) { this.ganados = ganados; }

    public int getPerdidos() { return perdidos; }
    public void setPerdidos(int perdidos) { this.perdidos = perdidos; }

    public int getGolesFavor() { return golesFavor; }
    public void setGolesFavor(int golesFavor) { this.golesFavor = golesFavor; }

    public int getGolesContra() { return golesContra; }
    public void setGolesContra(int golesContra) { this.golesContra = golesContra; }

    public int getDiferenciaGoles() { return diferenciaGoles; }
    public void setDiferenciaGoles(int diferenciaGoles) { this.diferenciaGoles = diferenciaGoles; }

    public int getPuntos() { return puntos; }
    public void setPuntos(int puntos) { this.puntos = puntos; }
}