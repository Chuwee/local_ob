package es.onebox.mgmt.b2b.users.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.b2b.users.enums.ClientUserType;
import jakarta.validation.constraints.Email;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.validator.constraints.Length;

import java.io.Serial;
import java.io.Serializable;

public class UpdateClientUserRequestDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Length(max = 200, message = "name max size 200")
    private String name;

    @Length(max = 200, message = "email max size 200")
    @Email
    private String email;

    @JsonProperty("external_reference")
    @Length(max = 200, message = "email max size 200")
    private String externalReference;

    private ClientUserType type;

    @JsonProperty("entity_id")
    private Long entityId;

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

    public ClientUserType getType() {
        return type;
    }

    public void setType(ClientUserType type) {
        this.type = type;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public String getExternalReference() {
        return externalReference;
    }

    public void setExternalReference (String externalReference) {
        this.externalReference = externalReference;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
