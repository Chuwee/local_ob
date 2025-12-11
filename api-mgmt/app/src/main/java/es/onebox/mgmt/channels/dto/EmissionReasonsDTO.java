package es.onebox.mgmt.channels.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;

public class EmissionReasonsDTO {

    @JsonProperty("emission_reasons")
    private List<IdNameDTO> emissionReasons;

    public List<IdNameDTO> getEmissionReasons() { return emissionReasons; }

    public void setEmissionReasons(List<IdNameDTO> emissionReasons) { this.emissionReasons = emissionReasons; }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
