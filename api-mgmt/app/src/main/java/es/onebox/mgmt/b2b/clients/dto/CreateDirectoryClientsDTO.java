package es.onebox.mgmt.b2b.clients.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;

public class CreateDirectoryClientsDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("entity_id")
    private Long entityId;

    @NotNull(message = "clients can not be null")
    private List<CreateDirectoryClientDTO> clients;

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public List<CreateDirectoryClientDTO> getClients() {
        return clients;
    }

    public void setClients(List<CreateDirectoryClientDTO> clients) {
        this.clients = clients;
    }

    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
