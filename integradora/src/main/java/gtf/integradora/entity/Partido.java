package gtf.integradora.entity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "partidos")
public class Partido {

    @Id
    private String id;

    private String torneoId;
    private String equipoAId;
    private String equipoBId;
    private String nombreEquipoA;
    private String nombreEquipoB;
    private int golesEquipoA = 0;
    private int golesEquipoB = 0;

    private String campoId; // ID del campo
    private String nombreCampo; // Nombre del campo
    private String nombreCancha; // Nombre de la cancha

    private String arbitroId; // ID del árbitro
    private String nombreArbitro; // Nombre del árbitro

    private LocalDate fecha;
    private LocalTime hora;
    private int duracionHoras = 2;

    private String estado;
    private int jornada;
    private String fase;

    private boolean eliminado = false;

    private List<RegistroJugador> registro = new ArrayList<>();

    // Constructor vacío
    public Partido() {
    }

    // Constructor con los datos de los equipos completos y la jornada
    public Partido(Equipo equipoA, Equipo equipoB, int jornada) {
        this.equipoAId = equipoA.getId(); // Obtener el ID del equipo A
        this.equipoBId = equipoB.getId(); // Obtener el ID del equipo B
        this.nombreEquipoA = equipoA.getNombre(); // Nombre del equipo A
        this.nombreEquipoB = equipoB.getNombre(); // Nombre del equipo B
        this.jornada = jornada;
        this.estado = "pendiente"; // Estado por defecto
        this.eliminado = false;
    }

    // Getters y setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTorneoId() {
        return torneoId;
    }

    public void setTorneoId(String torneoId) {
        this.torneoId = torneoId;
    }

    public String getEquipoAId() {
        return equipoAId;
    }

    public void setEquipoAId(String equipoAId) {
        this.equipoAId = equipoAId;
    }

    public String getEquipoBId() {
        return equipoBId;
    }

    public void setEquipoBId(String equipoBId) {
        this.equipoBId = equipoBId;
    }

    public String getNombreEquipoA() {
        return nombreEquipoA;
    }

    public void setNombreEquipoA(String nombreEquipoA) {
        this.nombreEquipoA = nombreEquipoA;
    }

    public String getNombreEquipoB() {
        return nombreEquipoB;
    }

    public void setNombreEquipoB(String nombreEquipoB) {
        this.nombreEquipoB = nombreEquipoB;
    }

    public String getCampoId() {
        return campoId;
    }

    public void setCampoId(String campoId) {
        this.campoId = campoId;
    }

    public String getNombreCampo() {
        return nombreCampo;
    }

    public void setNombreCampo(String nombreCampo) {
        this.nombreCampo = nombreCampo;
    }

    public String getNombreCancha() {
        return nombreCancha;
    }

    public void setNombreCancha(String nombreCancha) {
        this.nombreCancha = nombreCancha;
    }

    public String getArbitroId() {
        return arbitroId;
    }

    public void setArbitroId(String arbitroId) {
        this.arbitroId = arbitroId;
    }

    public String getNombreArbitro() {
        return nombreArbitro;
    }

    public void setNombreArbitro(String nombreArbitro) {
        this.nombreArbitro = nombreArbitro;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalTime getHora() {
        return hora;
    }

    public void setHora(LocalTime hora) {
        this.hora = hora;
    }

    public int getDuracionHoras() {
        return duracionHoras;
    }

    public void setDuracionHoras(int duracionHoras) {
        this.duracionHoras = duracionHoras;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public int getJornada() {
        return jornada;
    }

    public void setJornada(int jornada) {
        this.jornada = jornada;
    }

    public String getFase() {
        return fase;
    }

    public void setFase(String fase) {
        this.fase = fase;
    }

    public boolean isEliminado() {
        return eliminado;
    }

    public void setEliminado(boolean eliminado) {
        this.eliminado = eliminado;
    }

    public List<RegistroJugador> getRegistro() {
        return registro;
    }

    public void setRegistro(List<RegistroJugador> registro) {
        this.registro = registro;
    }

    public int getGolesEquipoA() {
        return golesEquipoA;
    }

    public void setGolesEquipoA(int golesEquipoA) {
        this.golesEquipoA = golesEquipoA;
    }

    public int getGolesEquipoB() {
        return golesEquipoB;
    }

    public void setGolesEquipoB(int golesEquipoB) {
        this.golesEquipoB = golesEquipoB;
    }

    // Método setter para asignar el campo y la cancha
    public void setCampo(Campo campo) {
        this.campoId = campo.getId();
        this.nombreCampo = campo.getNombreCampo(); // Asignamos el nombre del campo

        // Si el campo tiene canchas, asignamos la primera
        if (campo.getCanchas() != null && !campo.getCanchas().isEmpty()) {
            this.nombreCancha = campo.getCanchas().get(0).getNombreCancha(); // Asignamos la primera cancha
        }
    }

    public void setArbitro(Arbitro arbitro) {
        this.arbitroId = arbitro.getId();
        this.nombreArbitro = arbitro.getNombre();
    }

}