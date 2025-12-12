package es.onebox.common.datasources.webhook.dto.fever;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.io.Serializable;

@JsonNaming(SnakeCaseStrategy.class)
public class UserDetailDTO implements Serializable {

    private Integer fvId;
    private Integer fvPartnerId;
    private String entityId;
    private String email;
    private String name;

    public Integer getFvPartnerId() {
        return fvPartnerId;
    }

    public void setFvPartnerId(Integer fvPartnerId) {
        this.fvPartnerId = fvPartnerId;
    }

    public Integer getFvId() {
        return fvId;
    }

    public void setFvId(Integer fvId) {
        this.fvId = fvId;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
