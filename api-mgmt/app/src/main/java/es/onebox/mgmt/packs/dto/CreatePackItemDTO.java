package es.onebox.mgmt.packs.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.datasources.ms.channel.enums.PackItemType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class CreatePackItemDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("item_id")
    @NotNull(message = "itemId is mandatory and can not be null")
    private Long itemId;

    @NotNull(message = "type is mandatory and can not be null")
    private PackItemType type;

    @JsonProperty("price_type_id")
    @Min(value = 0, message = "price_type_id must be greater than or equal to 0")
    private Integer priceTypeId;

    @JsonProperty("price_type_mapping")
    private List<PackItemPriceTypeMappingRequestDTO> priceTypeMapping;

    @JsonProperty("variant_id")
    @Min(value = 0, message = "variant_id must be greater than or equal to 0")
    private Integer variantId;

    @JsonProperty("delivery_point_id")
    @Min(value = 0, message = "delivery_point_id must be greater than or equal to 0")
    private Integer deliveryPointId;

    @JsonProperty("shared_barcode")
    private Boolean sharedBarcode;

    @JsonProperty("display_item_in_channels")
    @NotNull(message = "display_item_in_channels is mandatory and can not be null")
    private Boolean displayItemInChannels;

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public PackItemType getType() {
        return type;
    }

    public void setType(PackItemType type) {
        this.type = type;
    }

    public Integer getPriceTypeId() {
        return priceTypeId;
    }

    public void setPriceTypeId(Integer priceTypeId) {
        this.priceTypeId = priceTypeId;
    }

    public List<PackItemPriceTypeMappingRequestDTO> getPriceTypeMapping() {
        return priceTypeMapping;
    }

    public void setPriceTypeMapping(List<PackItemPriceTypeMappingRequestDTO> priceTypeMapping) {
        this.priceTypeMapping = priceTypeMapping;
    }

    public Integer getVariantId() {
        return variantId;
    }

    public void setVariantId(Integer variantId) {
        this.variantId = variantId;
    }

    public Integer getDeliveryPointId() {
        return deliveryPointId;
    }

    public void setDeliveryPointId(Integer deliveryPointId) {
        this.deliveryPointId = deliveryPointId;
    }

    public Boolean getSharedBarcode() {
        return sharedBarcode;
    }

    public void setSharedBarcode(Boolean sharedBarcode) {
        this.sharedBarcode = sharedBarcode;
    }

    public Boolean getDisplayItemInChannels() {
        return displayItemInChannels;
    }

    public void setDisplayItemInChannels(Boolean displayItemInChannels) {
        this.displayItemInChannels = displayItemInChannels;
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
