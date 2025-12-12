package es.onebox.common.datasources.ms.client.dto.request;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class CreateCustomerRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = -14622088935163779L;

    private String email;
    private Integer entityId;
    private String name;
    private String surname;
    private String status;
    private String type;
    private List<AuthOrigin> authOrigins;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getEntityId() {
        return entityId;
    }

    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
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

    public List<AuthOrigin> getAuthOrigins() {
        return authOrigins;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setAuthOrigins(List<AuthOrigin> authOrigins) {
        this.authOrigins = authOrigins;
    }
}
