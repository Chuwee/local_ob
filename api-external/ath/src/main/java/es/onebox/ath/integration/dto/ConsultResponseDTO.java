package es.onebox.ath.integration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

public class ConsultResponseDTO implements Serializable {

    private static final long serialVersionUID = 7922489243317370672L;

    @JsonProperty("error_code")
    private String errorCode;
    private String message;
    private String token;
    @JsonProperty("transfer_seats")
    private List<TransferSeatDTO> transferSeats;

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public List<TransferSeatDTO> getTransferSeats() {
        return transferSeats;
    }

    public void setTransferSeats(List<TransferSeatDTO> transferSeats) {
        this.transferSeats = transferSeats;
    }
}
