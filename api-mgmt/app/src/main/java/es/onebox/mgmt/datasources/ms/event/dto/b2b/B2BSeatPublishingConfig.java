package es.onebox.mgmt.datasources.ms.event.dto.b2b;

import java.io.Serial;
import java.io.Serializable;

public class B2BSeatPublishingConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Boolean enabled;
    private Long publishedSeatQuotaId;
    private PublishedSeatPriceType publishedSeatPriceType;

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

    public PublishedSeatPriceType getPublishedSeatPriceType() {
        return publishedSeatPriceType;
    }

    public void setPublishedSeatPriceType(PublishedSeatPriceType publishedSeatPriceType) {
        this.publishedSeatPriceType = publishedSeatPriceType;
    }
}
