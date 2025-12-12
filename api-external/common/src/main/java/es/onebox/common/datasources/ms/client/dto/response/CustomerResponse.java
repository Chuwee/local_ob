package es.onebox.common.datasources.ms.client.dto.response;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class CustomerResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = -6347333217866358555L;


    private String id;
    private String email;
    private String user;
    private String name;
    private String surname;
    private String status;
    private String apiKey;
    private Integer entityId;
    private List<AuthOrigin> origins;

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

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public Integer getEntityId() {
        return entityId;
    }

    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }

    public List<AuthOrigin> getOrigins() {
        return origins;
    }

    public void setOrigins(List<AuthOrigin> origins) {
        this.origins = origins;
    }
}
