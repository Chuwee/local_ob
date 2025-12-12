package es.onebox.fever.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;

public class UserPhoneInfoDataDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String phone;

    @JsonProperty("is_verified")
    private Boolean isVerified;

    public String getPhone() { return phone; }

    public void setPhone(String phone) { this.phone = phone; }

    public Boolean getVerified() { return isVerified; }

    public void setVerified(Boolean verified) { isVerified = verified; }
}
