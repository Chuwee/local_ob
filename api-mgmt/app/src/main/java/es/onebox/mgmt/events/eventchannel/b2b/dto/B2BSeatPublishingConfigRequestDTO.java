package es.onebox.mgmt.events.eventchannel.b2b.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

public class B2BSeatPublishingConfigRequestDTO implements Serializable {

    private Boolean enabled;

    @NotNull(message = "publishedSeatQuotaId can not be null")
    @JsonProperty("published_seat_quota_id")
    private Long publishedSeatQuotaId;

    @JsonProperty("published_seat_price_type")
    private PublishedSeatPriceTypeRequestDTO publishedSeatPriceType;

    public Boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Long getPublishedSeatQuotaId() {
        return publishedSeatQuotaId;
    }

    public void setPublishedSeatQuotaId(Long publishedSeatQuotaId) {
        this.publishedSeatQuotaId = publishedSeatQuotaId;
    }

    public PublishedSeatPriceTypeRequestDTO getPublishedSeatPriceType() {
        return publishedSeatPriceType;
    }

    public void setPublishedSeatPriceType(PublishedSeatPriceTypeRequestDTO publishedSeatPriceType) {
        this.publishedSeatPriceType = publishedSeatPriceType;
    }
}
