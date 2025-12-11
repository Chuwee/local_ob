package es.onebox.event.events.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class CreateEventRateDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -7921914453255481888L;
    private Long id;
    private String name;
    private String description;
    private Boolean restrictive;
    @JsonProperty("default")
    private Boolean defaultRate;
    private Integer rateGroupId;
    private List<Integer> rateGroupIds;
    private Map<String, String> translations;
    private Long externalRateTypeId;

    public CreateEventRateDTO() {
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

    public Boolean getRestrictive() {
        return restrictive;
    }

    public void setRestrictive(Boolean restrictive) {
        this.restrictive = restrictive;
    }

    public Boolean getDefaultRate() {
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

    public Integer getRateGroupId() {
        return rateGroupId;
    }

    public void setRateGroupId(Integer rateGroupId) {
        this.rateGroupId = rateGroupId;
    }

    public List<Integer> getRateGroupIds() {
        return rateGroupIds;
    }

    public void setRateGroupIds(List<Integer> rateGroupIds) {
        this.rateGroupIds = rateGroupIds;
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

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
