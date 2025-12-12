package es.onebox.ath.integration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class LoginResponseDTO implements Serializable {

    private String token;
    private String message;
    @JsonProperty("error_code")
    private String errorCode;
    @JsonProperty("account_id")
    private String accountId;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }
}
