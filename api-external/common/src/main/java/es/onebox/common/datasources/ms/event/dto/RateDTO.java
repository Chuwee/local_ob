package es.onebox.common.datasources.ms.event.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serial;
import java.io.Serializable;
import java.util.Map;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;


public class RateDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 6984047137750862643L;

    private Long id;
    private String name;
    private String description;
    private Boolean restrictive;
    @JsonProperty("default")
    private Boolean defaultRate;
    private Map<String, String> translations;

    public RateDTO() {
    }

    public RateDTO(Long id, String name, String description, Boolean restrictive, Boolean defaultRate, Map<String, String> translations) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.restrictive = restrictive;
        this.defaultRate = defaultRate;
        this.translations = translations;
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
