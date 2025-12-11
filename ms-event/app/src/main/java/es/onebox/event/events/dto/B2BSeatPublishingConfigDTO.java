package es.onebox.event.events.dto;

import java.io.Serial;
import java.io.Serializable;

public class B2BSeatPublishingConfigDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Boolean enabled;
    private Long publishedSeatQuotaId;
    private PublishedSeatPriceTypeDTO publishedSeatPriceType;

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

    public PublishedSeatPriceTypeDTO getPublishedSeatPriceType() {
        return publishedSeatPriceType;
    }

    public void setPublishedSeatPriceType(PublishedSeatPriceTypeDTO publishedSeatPriceType) {
        this.publishedSeatPriceType = publishedSeatPriceType;
    }
}
