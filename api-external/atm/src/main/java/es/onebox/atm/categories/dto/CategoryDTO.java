package es.onebox.atm.categories.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

public class CategoryDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -4328524946342206169L;

    private Long id;
    private String code;
    private String description;
    @JsonProperty("additional_info")
    private Map<String, CategoryAdditonalDataDTO> additionalInfo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, CategoryAdditonalDataDTO> getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(Map<String, CategoryAdditonalDataDTO> additionalInfo) {
        this.additionalInfo = additionalInfo;
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
        return ToStringBuilder.reflectionToString(this);
    }
}
