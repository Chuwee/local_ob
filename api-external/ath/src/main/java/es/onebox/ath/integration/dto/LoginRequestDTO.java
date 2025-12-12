package es.onebox.ath.integration.dto;

import jakarta.validation.constraints.NotNull;

public class LoginRequestDTO {

    @NotNull(message = "Missing username")
    private String username;
    @NotNull(message = "Missing password")
    private String pass;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }
}
