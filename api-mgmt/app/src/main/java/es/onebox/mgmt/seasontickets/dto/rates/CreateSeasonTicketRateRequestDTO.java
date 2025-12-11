package es.onebox.mgmt.seasontickets.dto.rates;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.events.dto.RateTextsDTO;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class CreateSeasonTicketRateRequestDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 4781234025994933620L;
    @JsonProperty("name")
    @Size(max = 50, message="Rate name may have up to 50 chars")
    private String name;

    @JsonProperty("default")
    private Boolean defaultRate;

    @JsonProperty("restrictive_access")
    private Boolean restrictiveAccess;

    @JsonProperty("texts")
    private RateTextsDTO texts;

    private Boolean enabled;

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

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
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