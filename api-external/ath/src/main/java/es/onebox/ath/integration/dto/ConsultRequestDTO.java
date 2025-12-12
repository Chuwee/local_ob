package es.onebox.ath.integration.dto;

import jakarta.validation.constraints.NotNull;

public class ConsultRequestDTO {

    @NotNull(message = "Missing username")
    private String username;
    @NotNull(message = "Missing token")
    private String token;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
