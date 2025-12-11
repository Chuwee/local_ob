package es.onebox.mgmt.events.eventchannel.b2b.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;

public class QuotaClientAssignationDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("all_clients")
    private Boolean allClients;

    @NotNull(message = "quota can not be null")
    private IdNameDTO quota;
    private List<IdNameDTO> clients;

    public Boolean getAllClients() {
        return allClients;
    }

    public void setAllClients(Boolean allClients) {
        this.allClients = allClients;
    }

    public IdNameDTO getQuota() {
        return quota;
    }

    public void setQuota(IdNameDTO quota) {
        this.quota = quota;
    }

    public List<IdNameDTO> getClients() {
        return clients;
    }

    public void setClients(List<IdNameDTO> clients) {
        this.clients = clients;
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
