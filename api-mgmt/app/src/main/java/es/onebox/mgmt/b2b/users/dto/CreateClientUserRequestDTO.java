package es.onebox.mgmt.b2b.users.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.b2b.users.enums.ClientUserType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

public class CreateClientUserRequestDTO extends ClientUserBaseDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Length(max = 200, message = "username max size 200")
    @NotNull(message = "username can not be null")
    private String username;

    @Length(max = 200, message = "name max size 200")
    @NotNull(message = "name can not be null")
    private String name;

    @Email
    @Length(max = 200, message = "email max size 200")
    @NotNull(message = "email can not be null")
    private String email;

    private ClientUserType type;

    @JsonProperty("entity_id")
    private Integer entityId;

    @JsonProperty("external_reference")
    private String externalReference;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public ClientUserType getType() {
        return type;
    }

    public void setType(ClientUserType type) {
        this.type = type;
    }

    public Integer getEntityId() {
        return entityId;
    }

    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }

    public String getExternalReference() {
        return externalReference;
    }

    public void setExternalReference(String externalReference) {
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
