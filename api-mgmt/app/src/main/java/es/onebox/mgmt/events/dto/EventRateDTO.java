package es.onebox.mgmt.events.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.common.IdNameCodeDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class EventRateDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 2294447204609010195L;
    private Long id;

    private String name;

    @JsonProperty("default")
    private Boolean isDefault;

    @JsonProperty("restrictive_access")
    private Boolean restrictiveAccess;

    private RateTextsDTO texts;

    @JsonProperty("rate_group")
    private RateGroupDataDTO rateGroup;

    @JsonProperty("external_rate_type")
    private IdNameCodeDTO externalRateType;

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

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean aDefault) {
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

    public RateGroupDataDTO getRateGroup() {
        return rateGroup;
    }

    public void setRateGroup(RateGroupDataDTO rateGroup) {
        this.rateGroup = rateGroup;
    }

    public IdNameCodeDTO getExternalRateType() { return externalRateType; }

    public void setExternalRateType(IdNameCodeDTO externalRateType) { this.externalRateType = externalRateType; }

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
