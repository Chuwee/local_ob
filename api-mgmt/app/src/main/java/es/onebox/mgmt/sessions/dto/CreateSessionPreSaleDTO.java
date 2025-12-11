package es.onebox.mgmt.sessions.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.sessions.enums.PresaleValidatorType;
import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;

public class CreateSessionPreSaleDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull
    private String name;
    @JsonProperty("validator_id")
    private Long validatorId;
    @NotNull
    @JsonProperty("validator_type")
    private PresaleValidatorType validatorType;
    @JsonProperty("additional_config")
    private AdditionalConfigDTO additionalConfig;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getValidatorId() {
        return validatorId;
    }

    public void setValidatorId(Long validatorId) {
        this.validatorId = validatorId;
    }

    public PresaleValidatorType getValidatorType() {
        return validatorType;
    }

    public void setValidatorType(PresaleValidatorType validatorType) {
        this.validatorType = validatorType;
    }

    public AdditionalConfigDTO getAdditionalConfig() {
        return additionalConfig;
    }

    public void setAdditionalConfig(AdditionalConfigDTO additionalConfig) {
        this.additionalConfig = additionalConfig;
    }

}
