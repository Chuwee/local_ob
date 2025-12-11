package es.onebox.mgmt.channels.promotions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.common.promotions.enums.PromotionTargetType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;

public class ChannelPromotionPriceTypesDTO  implements Serializable {

    private static final long serialVersionUID = -3775103556560676629L;

    private PromotionTargetType type;
    @JsonProperty("price_types")
    private List<ChannelPromotionPriceTypeDTO> priceTypes;
    @JsonProperty("catalog_sale_request_id")
    private Long catalogSaleRequestId;

    public PromotionTargetType getType() {
        return type;
    }

    public void setType(PromotionTargetType type) {
        this.type = type;
    }

    public List<ChannelPromotionPriceTypeDTO> getPriceTypes() {
        return priceTypes;
    }

    public void setPriceTypes(List<ChannelPromotionPriceTypeDTO> priceTypes) {
        this.priceTypes = priceTypes;
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
