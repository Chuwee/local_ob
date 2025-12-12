package es.onebox.ath.integration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class SeatManagementResponseDTO implements Serializable {

    private static final long serialVersionUID = 7922489243317370672L;

    private String token;
    private String message;
    @JsonProperty("error_code")
    private String errorCode;
    @JsonProperty("transfer_seat_id")
    private String transferSeatId;

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

    public String getTransferSeatId() {
        return transferSeatId;
    }

    public void setTransferSeatId(String transferSeatId) {
        this.transferSeatId = transferSeatId;
    }
}
