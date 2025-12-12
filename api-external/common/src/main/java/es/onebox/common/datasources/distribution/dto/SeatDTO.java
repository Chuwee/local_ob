package es.onebox.common.datasources.distribution.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;

public class SeatDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -503044424389597218L;
    @NotNull(message = "Seat id is required")
    private Long id;
    @NotNull(message = "Session id is required")
    @JsonProperty("session_id")
    private Long sessionId;
    @JsonProperty("rate_id")
    private Long rateId;
    @JsonProperty("session_preview_token")
    private String sessionPreviewToken;

    public String getSessionPreviewToken() {
        return sessionPreviewToken;
    }

    public void setSessionPreviewToken(String sessionPreviewToken) {
        this.sessionPreviewToken = sessionPreviewToken;
    }

    public SeatDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public Long getRateId() {
        return rateId;
    }

    public void setRateId(Long rateId) {
        this.rateId = rateId;
    }
}
