package es.onebox.mgmt.exception;

import com.fasterxml.jackson.annotation.JsonProperty;

public class IntegrationMsErrorException extends RuntimeException {
    @JsonProperty("error")
    private String error;
    @JsonProperty("error_description")
    private String description;
    @JsonProperty("external_error_code")
    private String externalErrorCode;

    private Integer httpCode;

    public IntegrationMsErrorException() {
    }

    public String getMessage() {
        return error + " : " + description;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getHttpCode() {
        return httpCode;
    }

    public void setHttpCode(Integer httpCode) {
        this.httpCode = httpCode;
    }

    public String getExternalErrorCode() {
        return externalErrorCode;
    }

    public void setExternalErrorCode(String externalErrorCode) {
        this.externalErrorCode = externalErrorCode;
    }
}
