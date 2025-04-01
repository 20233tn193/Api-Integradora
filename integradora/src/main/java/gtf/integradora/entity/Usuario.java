package gtf.integradora.entity;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "usuarios")
public class Usuario {
    @Id
    private String id;

    private String email;
    private String password;
    private List<String> roles;  // Roles como 'ARBITRO', 'ADMIN', etc.

    private boolean eliminado = false;

    public Usuario() {}

    public Usuario(String id, String email, String password, List<String> roles, boolean eliminado) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.roles = roles;
        this.eliminado = eliminado;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public boolean isEliminado() {
        return eliminado;
    }

    public void setEliminado(boolean eliminado) {
        this.eliminado = eliminado;
    }

    

    
}