package es.onebox.mgmt.users.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class UserContactDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("primary_phone")
    private String primaryPhone;

    @JsonProperty("secondary_phone")
    private String secondaryPhone;

    private String fax;

    public String getPrimaryPhone() {
        return primaryPhone;
    }

    public void setPrimaryPhone(String primaryPhone) {
        this.primaryPhone = primaryPhone;
    }

    public String getSecondaryPhone() {
        return secondaryPhone;
    }

    public void setSecondaryPhone(String secondaryPhone) {
        this.secondaryPhone = secondaryPhone;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }
}
