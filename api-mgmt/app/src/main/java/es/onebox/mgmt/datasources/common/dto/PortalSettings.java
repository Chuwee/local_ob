package es.onebox.mgmt.datasources.common.dto;


import es.onebox.mgmt.datasources.common.enums.AuthMode;
import es.onebox.mgmt.datasources.common.enums.AccountCreation;
import es.onebox.mgmt.datasources.common.enums.TriggerOn;

import java.util.List;

public class PortalSettings {

    private AuthMode mode;
    private List<TriggerOn> triggersOn;
    private Boolean userDataEditable;
    private AccountCreation accountCreation;
    private Boolean blockedCustomerTypesEnabled;
    private List<String> blockedCustomerTypes;

    public AuthMode getMode() {
        return mode;
    }

    public void setMode(AuthMode mode) {
        this.mode = mode;
    }

    public List<TriggerOn> getTriggersOn() {
        return triggersOn;
    }

    public void setTriggersOn(List<TriggerOn> triggersOn) {
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

    public List<String> getBlockedCustomerTypes() {
        return blockedCustomerTypes;
    }

    public void setBlockedCustomerTypes(List<String> blockedCustomerTypes) {
        this.blockedCustomerTypes = blockedCustomerTypes;
    }

    public Boolean getBlockedCustomerTypesEnabled() {
        return blockedCustomerTypesEnabled;
    }

    public void setBlockedCustomerTypesEnabled(Boolean blockedCustomerTypesEnabled) {
        this.blockedCustomerTypesEnabled = blockedCustomerTypesEnabled;
    }
}
