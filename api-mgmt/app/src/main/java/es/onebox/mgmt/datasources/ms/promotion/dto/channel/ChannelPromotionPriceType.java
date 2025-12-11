package es.onebox.mgmt.datasources.ms.promotion.dto.channel;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class ChannelPromotionPriceType extends IdNameDTO implements Serializable {

    private static final long serialVersionUID = -8217362506394115364L;

    private IdNameDTO venueConfig;
    private Long catalogSaleRequestId;

    public IdNameDTO getVenueConfig() {
        return venueConfig;
    }

    public void setVenueConfig(IdNameDTO venueConfig) {
        this.venueConfig = venueConfig;
    }

    public Long getCatalogSaleRequestId() {
        return catalogSaleRequestId;
    }

    public void setCatalogSaleRequestId(Long catalogSaleRequestId) {
        this.catalogSaleRequestId = catalogSaleRequestId;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
