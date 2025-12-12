package es.onebox.fifaqatar.adapter.dto.response.error;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;

public class ErrorResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 3844547370511282901L;

    @JsonProperty("user_error")
    private String userError;
    @JsonProperty("message")
    private String message;
    @JsonProperty("code")
    private String code;

    public String getUserError() {
        return userError;
    }

    public void setUserError(String userError) {
        this.userError = userError;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
