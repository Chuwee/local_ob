package es.onebox.mgmt.entities.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;

import java.io.Serial;
import java.io.Serializable;

public class EntitySettingsBIUsers implements Serializable {

    @Serial
    private static final long serialVersionUID = 2L;

    public EntitySettingsBIUsers() {
    }

    public EntitySettingsBIUsers(Integer numBasic, Integer numAdvanced) {
        this.basicLimit = numBasic;
        this.advancedLimit = numAdvanced;
    }

    @JsonProperty("basic_permissions_limit")
    @Min(value = 0, message = "basic_permissions_limit must be bigger than 0")
    private Integer basicLimit;
    @JsonProperty("advanced_permissions_limit")
    @Min(value = 0, message = "advanced_permissions_limit must be bigger than 0")
    private Integer advancedLimit;

    public Integer getBasicLimit() {
        return basicLimit;
    }

    public void setBasicLimit(Integer basicLimit) {
        this.basicLimit = basicLimit;
    }

    public Integer getAdvancedLimit() {
        return advancedLimit;
    }

    public void setAdvancedLimit(Integer advancedLimit) {
        this.advancedLimit = advancedLimit;
    }
}
