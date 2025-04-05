package gtf.integradora.dto;

import java.util.List;

import gtf.integradora.entity.RegistroJugador;

public class RegistroPartidoDTO {
    private List<RegistroJugador> registro;

    public List<RegistroJugador> getRegistro() {
        return registro;
    }

    public void setRegistro(List<RegistroJugador> registro) {
        this.registro = registro;
    }
}
