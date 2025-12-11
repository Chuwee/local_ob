package es.onebox.mgmt.common.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.common.auth.enums.TriggerOnDTO;
import es.onebox.mgmt.datasources.common.enums.AuthMode;
import es.onebox.mgmt.datasources.common.enums.AccountCreation;
import es.onebox.mgmt.entities.dto.PhoneValidatorDTO;

import java.util.List;

public class PortalSettingsDTO {

    private AuthMode mode;
    @JsonProperty("triggers_on")
    private List<TriggerOnDTO> triggersOn;
    @JsonProperty("user_data_editable")
    private Boolean userDataEditable;
    @JsonProperty("account_creation")
    private AccountCreation accountCreation;
    @JsonProperty("blocked_customer_types_enabled")
    private Boolean blockedCustomerTypesEnabled;
    @JsonProperty("blocked_customer_types")
    private List<String> blockedCustomerTypes;
    @JsonProperty("phone_validator")
    private PhoneValidatorDTO phoneValidator;

    public AuthMode getMode() {
        return mode;
    }

    public void setMode(AuthMode mode) {
        this.mode = mode;
    }

    public List<TriggerOnDTO> getTriggersOn() {
        return triggersOn;
    }

    public void setTriggersOn(List<TriggerOnDTO> triggersOn) {
        this.triggersOn = triggersOn;
    }

    public Boolean getUserDataEditable() {
        return userDataEditable;
    }

    public void setUserDataEditable(Boolean userDataEditable) {
        this.userDataEditable = userDataEditable;
    }

    public AccountCreation getAccountCreation() {
        return accountCreation;
    }

    public void setAccountCreation(AccountCreation accountCreation) {
        this.accountCreation = accountCreation;
    }

    public Boolean getBlockedCustomerTypesEnabled() {return blockedCustomerTypesEnabled;}

    public void setBlockedCustomerTypesEnabled(Boolean blockedCustomerTypesEnabled) {this.blockedCustomerTypesEnabled = blockedCustomerTypesEnabled;}

    public List<String> getBlockedCustomerTypes() {
        return blockedCustomerTypes;
    }

    public void setBlockedCustomerTypes(List<String> blockedCustomerTypes) {this.blockedCustomerTypes = blockedCustomerTypes;}

    public PhoneValidatorDTO getPhoneValidator() { return phoneValidator; }

    public void setPhoneValidator(PhoneValidatorDTO phoneValidator) { this.phoneValidator = phoneValidator; }
}
