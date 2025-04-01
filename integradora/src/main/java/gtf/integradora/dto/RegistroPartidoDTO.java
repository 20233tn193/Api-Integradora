package gtf.integradora.dto;

import java.util.List;

import gtf.integradora.entity.RegistroJugador;

public class RegistroPartidoDTO {
    private String partidoId;
    private List<RegistroJugador> registro;

    public String getPartidoId() {
        return partidoId;
    }

    public void setPartidoId(String partidoId) {
        this.partidoId = partidoId;
    }

    public List<RegistroJugador> getRegistro() {
        return registro;
    }

    public void setRegistro(List<RegistroJugador> registro) {
        this.registro = registro;
    }
}
