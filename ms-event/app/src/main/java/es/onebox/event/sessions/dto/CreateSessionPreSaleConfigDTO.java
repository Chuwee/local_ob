package es.onebox.event.sessions.dto;


import es.onebox.event.sessions.enums.PresaleValidatorType;

import java.io.Serializable;

public class CreateSessionPreSaleConfigDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private Long validatorId;
    private PresaleValidatorType validatorType;
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
