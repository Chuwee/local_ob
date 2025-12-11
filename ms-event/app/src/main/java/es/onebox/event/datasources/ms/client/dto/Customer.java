package es.onebox.event.datasources.ms.client.dto;

import java.io.Serializable;

public class Customer implements Serializable {

    private String userId;
    private String email;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
