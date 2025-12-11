package es.onebox.mgmt.common.surcharges.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class SaleRequestSurchargeDTO extends SurchargeDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 3201326990364482550L;

    @JsonProperty("producer_limit")
    private SurchargeLimitDTO producerLimit;

    public SurchargeLimitDTO getProducerLimit() {
        return producerLimit;
    }

    public void setProducerLimit(SurchargeLimitDTO producerLimit) {
        this.producerLimit = producerLimit;
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
