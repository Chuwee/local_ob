package es.onebox.mgmt.channels.promotions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class ChannelPromotionPriceTypeDTO extends IdNameDTO implements Serializable {

    private static final long serialVersionUID = -3924652869526825370L;

    @JsonProperty("venue_template")
    private IdNameDTO venueConfig;
    @JsonProperty("catalog_sale_request_id")
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
