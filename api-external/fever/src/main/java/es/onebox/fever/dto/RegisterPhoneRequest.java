package es.onebox.fever.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;

public class RegisterPhoneRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("phone_number")
    private String phone;

    public String getPhone() { return phone; }

    public void setPhone(String phone) { this.phone = phone; }
}
