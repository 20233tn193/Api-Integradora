package gtf.integradora.dto;

import java.util.List;

public class RegistroArbitroRequest {
    private String nombre;
    private String apellido;
    private String correo;
    private String password;
    private String fotoUrl;
    private List<String> roles;

    // Getters y setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFotoUrl() { return fotoUrl; }
    public void setFotoUrl(String fotoUrl) { this.fotoUrl = fotoUrl; }
    
    public List<String> getRoles() {return roles;}

    public void setRoles(List<String> roles) {this.roles = roles;}    
}