package es.onebox.common.datasources.distribution.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;

public class SeatsAutoRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = -4062608665788194794L;
    @JsonProperty("session_id")
    private Integer sessionId;
    @JsonProperty("quantity")
    private Long quantity;
    @JsonProperty("price_type_id")
    private Long priceTypeId;
    @JsonProperty("rate_id")
    private Integer rateId;
    @JsonProperty("venue_nnz_id")
    private Integer nnzId;
    @JsonProperty("venue_view_id")
    private Long venueViewId;
    @JsonProperty("venue_sector_id")
    private Long venueSectorId;
    @JsonProperty("session_preview_token")
    private String sessionPreviewToken;

    public SeatsAutoRequest() {
    }

    public SeatsAutoRequest(Integer sessionId, Long quantity, Long priceTypeId, Long venueSectorId, String sessionPreviewToken) {
        this.sessionId = sessionId;
        this.quantity = quantity;
        this.priceTypeId = priceTypeId;
        this.venueSectorId = venueSectorId;
        this.sessionPreviewToken = sessionPreviewToken;
    }

    public String getSessionPreviewToken() {
        return sessionPreviewToken;
    }

    public void setSessionPreviewToken(String sessionPreviewToken) {
        this.sessionPreviewToken = sessionPreviewToken;
    }

    public Integer getSessionId() {
        return sessionId;
    }

    public void setSessionId(Integer sessionId) {
        this.sessionId = sessionId;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public Long getPriceTypeId() {
        return priceTypeId;
    }

    public void setPriceTypeId(Long priceTypeId) {
        this.priceTypeId = priceTypeId;
    }

    public Integer getRateId() {
        return rateId;
    }

    public void setRateId(Integer rateId) {
        this.rateId = rateId;
    }

    public Integer getNnzId() {
        return nnzId;
    }

    public void setNnzId(Integer nnzId) {
        this.nnzId = nnzId;
    }

    public Long getVenueViewId() {
        return venueViewId;
    }

    public void setVenueViewId(Long venueViewId) {
        this.venueViewId = venueViewId;
    }

    public Long getVenueSectorId() {
        return venueSectorId;
    }

    public void setVenueSectorId(Long venueSectorId) {
        this.venueSectorId = venueSectorId;
    }
}
