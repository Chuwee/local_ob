package es.onebox.mgmt.events.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class UpdateEventRateDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -3306893298971905690L;
    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    @Size(max = 50, message = "Rate name may have up to 50 chars")
    private String name;

    @JsonProperty("default")
    private Boolean isDefault;

    @JsonProperty("restrictive_access")
    private Boolean restrictiveAccess;

    @JsonProperty("texts")
    private RateTextsDTO texts;

    @JsonProperty("rate_group_id")
    private Integer rateGroupId;

    @JsonProperty("external_rate_type_id")
    private Long externalRateTypeId;

    @JsonProperty("position")
    private Integer position;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getDefault() {
        return isDefault;
    }

    public void setDefault(Boolean aDefault) {
        isDefault = aDefault;
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

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
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
