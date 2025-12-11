package es.onebox.mgmt.b2b.conditions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;

public class DeleteClientsConditionsFilterDTO extends BaseConditionsFilterDTO {

    private static final long serialVersionUID = 1L;

    @JsonProperty("clients_ids")
    private List<Long> clientsIds;

    public List<Long> getClientsIds() {
        return clientsIds;
    }

    public void setClientsIds(List<Long> clientsIds) {
        this.clientsIds = clientsIds;
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
