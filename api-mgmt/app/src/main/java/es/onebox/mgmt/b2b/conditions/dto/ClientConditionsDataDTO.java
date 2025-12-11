package es.onebox.mgmt.b2b.conditions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class ClientConditionsDataDTO extends ConditionsDataDTO {

    private static final long serialVersionUID = 1L;
    @JsonProperty("client")
    private IdNameDTO client;

    public IdNameDTO getClient() {
        return client;
    }

    public void setClient(IdNameDTO client) {
        this.client = client;
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
