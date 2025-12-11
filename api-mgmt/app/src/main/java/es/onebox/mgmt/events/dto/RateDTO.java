package es.onebox.mgmt.events.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.common.IdNameCodeDTO;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class RateDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 4930464092462900870L;

    private Long id;

    private String name;

    @JsonProperty("default")
    private Boolean isDefault;

    @JsonProperty("restrictive_access")
    private Boolean restrictiveAccess;

    private RateTextsDTO texts;

    @JsonProperty("external_rate_type")
    private IdNameCodeDTO externalRateType;

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

    public IdNameCodeDTO getExternalRateType() { return externalRateType; }

    public void setExternalRateType(IdNameCodeDTO externalRateType) { this.externalRateType = externalRateType; }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RateDTO rateDTO = (RateDTO) o;
        return Objects.equals(id, rateDTO.id) &&
                Objects.equals(name, rateDTO.name) &&
                Objects.equals(isDefault, rateDTO.isDefault) &&
                Objects.equals(restrictiveAccess, rateDTO.restrictiveAccess) &&
                Objects.equals(texts, rateDTO.texts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, isDefault, restrictiveAccess, texts);
    }

    @Override
    public String toString() {
        return "RateDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", isDefault=" + isDefault +
                ", restrictiveAccess=" + restrictiveAccess +
                ", texts=" + texts +
                '}';
    }
}
