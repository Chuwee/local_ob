package es.onebox.mgmt.datasources.ms.entity.dto;

import java.io.Serializable;

public class RecoverForgotPasswordRequest implements Serializable {

    private static final long serialVersionUID = 2L;

    private String token;

    private String newPassword;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
