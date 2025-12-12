package es.onebox.common.datasources.ms.entity.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class AdditionalProperties implements Serializable {

    @Serial
    private static final long serialVersionUID = -7901382853928429101L;
    private List<Long> producerIds;

    public List<Long> getProducerIds() {
        return producerIds;
    }
    public void setProducerIds(List<Long> producerIds) {
        this.producerIds = producerIds;
    }
}
