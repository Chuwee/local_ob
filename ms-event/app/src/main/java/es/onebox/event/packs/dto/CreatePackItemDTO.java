package es.onebox.event.packs.dto;

import es.onebox.event.packs.enums.PackItemType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class CreatePackItemDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "itemId is mandatory and can not be null")
    private Long itemId;

    @NotNull(message = "type is mandatory and can not be null")
    private PackItemType type;

    @Min(value = 0, message = "venueTemplateId must be greater than 0")
    private Integer venueTemplateId;

    @Min(value = 0, message = "priceTypeId must be greater than 0")
    private Integer priceTypeId;

    private Map<Integer, List<Integer>> priceTypeMapping;

    @Size(max = 50, message = "subItemIds can not be greater than 50")
    private List<Integer> subItemIds;

    @Min(value = 0, message = "variantId must be greater than 0")
    private Integer variantId;

    @Min(value = 0, message = "deliveryPointId must be greater than 0")
    private Integer deliveryPointId;

    private Boolean sharedBarcode;

    @NotNull(message = "displayItemInChannels is mandatory and can not be null")
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

    public Integer getVenueTemplateId() {
        return venueTemplateId;
    }

    public void setVenueTemplateId(Integer venueTemplateId) {
        this.venueTemplateId = venueTemplateId;
    }

    public Integer getPriceTypeId() {
        return priceTypeId;
    }

    public void setPriceTypeId(Integer priceTypeId) {
        this.priceTypeId = priceTypeId;
    }

    public Map<Integer, List<Integer>> getPriceTypeMapping() {
        return priceTypeMapping;
    }

    public void setPriceTypeMapping(Map<Integer, List<Integer>> priceTypeMapping) {
        this.priceTypeMapping = priceTypeMapping;
    }

    public List<Integer> getSubItemIds() {
        return subItemIds;
    }

    public void setSubItemIds(List<Integer> subItemIds) {
        this.subItemIds = subItemIds;
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
