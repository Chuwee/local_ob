package es.onebox.fever.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class FvUserAuth {
    private Long id;
    private Integer fvId;
    private String email;
    private Long obEntityId;
    private Integer fvPartnerId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getFvId() {
        return fvId;
    }

    public void setFvId(Integer fvId) {
        this.fvId = fvId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getObEntityId() {
        return obEntityId;
    }

    public void setObEntityId(Long obEntityId) {
        this.obEntityId = obEntityId;
    }

    public Integer getFvPartnerId() {
        return fvPartnerId;
    }

    public void setFvPartnerId(Integer fvPartnerId) {
        this.fvPartnerId = fvPartnerId;
    }
}
