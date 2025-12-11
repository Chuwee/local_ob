package es.onebox.event.events.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.common.IdNameCodeDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;


/**
 * @author rcarrillo
 */
public class RateDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 6984047137750862643L;

    private Long id;
    private String name;
    private String description;
    private Boolean restrictive;
    @JsonProperty("default")
    private Boolean defaultRate;
    private Integer position;
    private Map<String, String> translations;
    private IdNameCodeDTO externalRateType;

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
