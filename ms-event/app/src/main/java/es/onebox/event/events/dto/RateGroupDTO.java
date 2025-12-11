package es.onebox.event.events.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

public class RateGroupDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -3561186295177369151L;
    private Long id;
    private String name;
    @JsonProperty("default")
    private Boolean defaultRate;
    private RateGroupType type;
    private Integer position;
    private Map<String, String> translations;

    private String externalDescription;

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

    public String getExternalDescription() {
        return externalDescription;
    }

    public void setExternalDescription(String externalDescription) {
        this.externalDescription = externalDescription;
    }

    public RateGroupType getType() {
        return type;
    }

    public void setType(RateGroupType type) {
        this.type = type;
    }

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

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
