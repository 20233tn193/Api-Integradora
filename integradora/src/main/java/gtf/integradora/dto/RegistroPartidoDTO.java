package gtf.integradora.dto;

import java.util.List;
import gtf.integradora.entity.Jugador;
import gtf.integradora.entity.RegistroJugador;

public class RegistroPartidoDTO {
    private List<RegistroJugador> registro;
    private List<Jugador> jugadoresLocal;
    private List<Jugador> jugadoresVisitante;

    public List<RegistroJugador> getRegistro() {
        return registro;
    }

    public void setRegistro(List<RegistroJugador> registro) {
        this.registro = registro;
    }

    public List<Jugador> getJugadoresLocal() {
        return jugadoresLocal;
    }

    public void setJugadoresLocal(List<Jugador> jugadoresLocal) {
        this.jugadoresLocal = jugadoresLocal;
    }

    public List<Jugador> getJugadoresVisitante() {
        return jugadoresVisitante;
    }

    public void setJugadoresVisitante(List<Jugador> jugadoresVisitante) {
        this.jugadoresVisitante = jugadoresVisitante;
    }
}