package es.onebox.event.packs.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serial;
import java.io.Serializable;

public class CreatePackRateDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -7921914453255481888L;
    private String name;
    private String description;
    private Boolean restrictive;
    private Boolean defaultRate;
    private Integer relatedRateId;

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

    public Integer getRelatedRateId() {
        return relatedRateId;
    }

    public void setRelatedRateId(Integer relatedRateId) {
        this.relatedRateId = relatedRateId;
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
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
