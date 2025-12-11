package es.onebox.mgmt.datasources.ms.event.dto.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.common.IdNameCodeDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

public class EventRate implements Serializable {

    @Serial
    private static final long serialVersionUID = 8189795493152748561L;
    private Long id;
    private String name;
    private String description;
    private Boolean restrictive;
    @JsonProperty("default")
    private Boolean defaultRate;
    private Map<String, String> translations;
    private RateGroupData rateGroup;
    private Integer position;
    private IdNameCodeDTO externalRateType;

    public EventRate() {
    }

    public EventRate(Long id, String name, Boolean restrictive, Boolean defaultRate, Map<String, String> translations, IdNameCodeDTO externalRateType) {
        this.id = id;
        this.name = name;
        this.restrictive = restrictive;
        this.defaultRate = defaultRate;
        this.translations = translations;
        this.externalRateType = externalRateType;
    }

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean isRestrictive() {
        return restrictive;
    }

    public void setRestrictive(Boolean restrictive) {
        this.restrictive = restrictive;
    }

    public Boolean isDefaultRate() {
        return defaultRate;
    }

    public void setDefaultRate(Boolean defaultRate) {
        this.defaultRate = defaultRate;
    }

    public Map<String, String> getTranslations() {
        return translations;
    }

    public void setTranslations(Map<String, String> translations) {
        this.translations = translations;
    }

    public RateGroupData getRateGroup() {
        return rateGroup;
    }

    public void setRateGroup(RateGroupData rateGroup) {
        this.rateGroup = rateGroup;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public IdNameCodeDTO getExternalRateType() { return externalRateType; }

    public void setExternalRateType(IdNameCodeDTO externalRateType) { this.externalRateType = externalRateType; }

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
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }

}
