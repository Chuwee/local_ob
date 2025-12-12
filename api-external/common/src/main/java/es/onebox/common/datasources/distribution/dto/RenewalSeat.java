package es.onebox.common.datasources.distribution.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;

public class RenewalSeat implements Serializable {

    @Serial
    private static final long serialVersionUID = 4227077296573127140L;

    private Long id;
    @JsonProperty("session_id")
    private Long sessionId;
    @JsonProperty("rate_id")
    private Long rateId;
    @JsonProperty("user_id")
    private String userId;
    @JsonProperty("renewal_id")
    private String renewalId;

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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRenewalId() {
        return renewalId;
    }

    public void setRenewalId(String renewalId) {
        this.renewalId = renewalId;
    }
}