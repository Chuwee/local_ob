package es.onebox.mgmt.events.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class CreateEventRateRequestDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -6577866317498153223L;
    @JsonProperty("name")
    @Size(max = 50, message="Rate name may have up to 50 chars")
    private String name;

    @JsonProperty("default")
    private Boolean defaultRate;

    @JsonProperty("restrictive_access")
    private Boolean restrictiveAccess;

    @JsonProperty("texts")
    private RateTextsDTO texts;

    @JsonProperty("rate_group_id")
    private Integer rateGroupId;

    @JsonProperty("external_rate_type_id")
    private Long externalRateTypeId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getDefaultRate() {
        return defaultRate;
    }

    public void setDefaultRate(Boolean defaultRate) {
        this.defaultRate = defaultRate;
    }

    public Boolean getRestrictiveAccess() {
        return restrictiveAccess;
    }

    public void setRestrictiveAccess(Boolean restrictiveAccess) {
        this.restrictiveAccess = restrictiveAccess;
    }

    public RateTextsDTO getTexts() {
        return texts;
    }

    public void setTexts(RateTextsDTO texts) {
        this.texts = texts;
    }

    public Integer getRateGroupId() {
        return rateGroupId;
    }

    public void setRateGroupId(Integer rateGroupId) {
        this.rateGroupId = rateGroupId;
    }

    public Long getExternalRateTypeId() { return externalRateTypeId; }

    public void setExternalRateTypeId(Long externalRateTypeId) { this.externalRateTypeId = externalRateTypeId; }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
