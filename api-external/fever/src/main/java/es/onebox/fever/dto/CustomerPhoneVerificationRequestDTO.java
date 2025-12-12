package es.onebox.fever.dto;

import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;


public class CustomerPhoneVerificationRequestDTO implements Serializable {


    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "clientId must not be null")
    private String clientId;

    @NotNull(message = "prefix must not be null")
    private String prefix;

    @NotNull(message = "phone must not be null")
    private String phone;

    @NotNull(message = "accessToken must not be null")
    private String accessToken;

    public CustomerPhoneVerificationRequestDTO(String prefix, String phone, String accessToken) {
        this.prefix = prefix;
        this.phone = phone;
        this.accessToken = accessToken;
    }

    public CustomerPhoneVerificationRequestDTO() {
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public @NotNull(message = "clientId must not be null") String getClientId() {
        return clientId;
    }

    public void setClientId(@NotNull(message = "clientId must not be null") String clientId) {
        this.clientId = clientId;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

}
