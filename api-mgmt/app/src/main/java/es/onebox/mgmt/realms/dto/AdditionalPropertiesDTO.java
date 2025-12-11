package es.onebox.mgmt.realms.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class AdditionalPropertiesDTO implements Serializable {
    @Serial private static final long serialVersionUID = 1L;

    @JsonProperty("producer_ids")
    private List<Long> producerIds;

    public List<Long> getProducerIds() {
        return producerIds;
    }
    public void setProducerIds(List<Long> producerIds) {
        this.producerIds = producerIds;
    }
}
