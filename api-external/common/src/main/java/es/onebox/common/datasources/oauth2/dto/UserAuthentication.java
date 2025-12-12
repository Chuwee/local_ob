package es.onebox.common.datasources.oauth2.dto;

import java.io.Serializable;

public class UserAuthentication implements Serializable {

    private static final long serialVersionUID = -8031054778044075045L;

    private String user;
    private String password;

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
