package es.onebox.mgmt.entities.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;

public class ExternalIntegration implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("auth_vendor")
    private AuthVendor authVendor;

    @JsonProperty("barcode")
    private ExternalBarcode externalBarcode;

    @JsonProperty("custom_managements")
    private CustomManagementsDTO customManagements;

    @JsonProperty("phone_validator")
    private PhoneValidatorDTO phoneValidator;

    public AuthVendor getAuthVendor() {
        return authVendor;
    }

    public void setAuthVendor(AuthVendor authVendor) {
        this.authVendor = authVendor;
    }

    public ExternalBarcode getExternalBarcode() {
        return externalBarcode;
    }

    public void setExternalBarcode(ExternalBarcode externalBarcode) {
        this.externalBarcode = externalBarcode;
    }

    public CustomManagementsDTO getCustomManagements() {
        return customManagements;
    }

    public void setCustomManagements(CustomManagementsDTO customManagements) {
        this.customManagements = customManagements;
    }

    public PhoneValidatorDTO getPhoneValidator() { return phoneValidator; }

    public void setPhoneValidator(PhoneValidatorDTO phoneValidator) { this.phoneValidator = phoneValidator; }
}
