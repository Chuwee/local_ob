package es.onebox.common.datasources.ms.venue.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;

public class CreateVenueTemplateTag implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String name;
    private String code;
    private String color;
    private Long priority;
    private Boolean isDefault;
    private AdditionalConfigPriceType additionalConfig;

    public CreateVenueTemplateTag() {
    }

    public CreateVenueTemplateTag(@NotNull String name, String code, String color, Long priority, Boolean isDefault,
                                  PriceTypeAdditionalConfigDTO priceTypeAdditionalConfigDTO) {
        this.name = name;
        this.code = code;
        this.color = color;
        this.priority = priority;
        this.isDefault = isDefault;
        if (priceTypeAdditionalConfigDTO != null &&
                (priceTypeAdditionalConfigDTO.getRestrictiveAccess() != null ||
                priceTypeAdditionalConfigDTO.getGateId() != null)) {
            this.additionalConfig = new AdditionalConfigPriceType();
            additionalConfig.setGateId(priceTypeAdditionalConfigDTO.getGateId());
            additionalConfig.setRestrictiveAccess(priceTypeAdditionalConfigDTO.getRestrictiveAccess());
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Long getPriority() {
        return priority;
    }

    public void setPriority(Long priority) {
        this.priority = priority;
    }

    public Boolean getDefault() {
        return isDefault;
    }

    public void setDefault(Boolean aDefault) {
        isDefault = aDefault;
    }

    public AdditionalConfigPriceType getAdditionalConfig() {
        return additionalConfig;
    }

    public void setAdditionalConfig(AdditionalConfigPriceType additionalConfig) {
        this.additionalConfig = additionalConfig;
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
