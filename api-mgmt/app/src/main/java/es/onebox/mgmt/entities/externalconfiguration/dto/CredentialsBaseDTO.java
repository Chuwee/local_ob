package es.onebox.mgmt.entities.externalconfiguration.dto;

import java.io.Serial;
import java.io.Serializable;

public class CredentialsBaseDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -8868414312116474481L;

    private String username;
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
