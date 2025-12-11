package es.onebox.mgmt.events.eventchannel.b2b.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.common.IdNameCodeDTO;

import java.io.Serializable;

public class B2BSeatPublishingConfigDTO implements Serializable {

    private Boolean enabled;
    @JsonProperty("published_seat_quota")
    private IdNameCodeDTO publishedSeatQuota;

    @JsonProperty("published_seat_price_type")
    private PublishedSeatPriceTypeDTO publishedSeatPriceType;

    public Boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public IdNameCodeDTO getPublishedSeatQuota() {
        return publishedSeatQuota;
    }

    public void setPublishedSeatQuota(IdNameCodeDTO publishedSeatQuota) {
        this.publishedSeatQuota = publishedSeatQuota;
    }

    public PublishedSeatPriceTypeDTO getPublishedSeatPriceType() {
        return publishedSeatPriceType;
    }

    public void setPublishedSeatPriceType(PublishedSeatPriceTypeDTO publishedSeatPriceType) {
        this.publishedSeatPriceType = publishedSeatPriceType;
    }
}
