package es.onebox.mgmt.events.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;

public class TierExtendedDTO extends TierDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("quotas_limit")
    private List<TierQuotasLimitDTO> quotasLimit;

    public List<TierQuotasLimitDTO> getQuotasLimit() {
        return quotasLimit;
    }

    public void setQuotasLimit(List<TierQuotasLimitDTO> quotasLimit) {
        this.quotasLimit = quotasLimit;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return "TierExtendedDTO{" +
                "salesGroupLimits=" + quotasLimit +
                "} " + super.toString();
    }
}
