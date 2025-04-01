package gtf.integradora.entity;

public class LoginRequest {

    private String email;
    private String password;

    // Constructor
    public LoginRequest() {}

    // Getters y setters
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
}