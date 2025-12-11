package es.onebox.mgmt.packs.dto;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class UpdatePackItemDTO {

    @JsonProperty("price_type_id")
    private Integer priceTypeId;

    @JsonProperty("price_type_mapping")
    private List<PackItemPriceTypeMappingRequestDTO> priceTypeMapping;

    @JsonProperty("variant_id")
    private Integer variantId;

    @JsonProperty("delivery_point_id")
    private Integer deliveryPointId;

    @JsonProperty("shared_barcode")
    private Boolean sharedBarcode;

    @JsonProperty("display_item_in_channels")
    private Boolean displayItemInChannels;

    @JsonProperty("informative_price")
    private Double informativePrice;

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

    public Double getInformativePrice() {
        return informativePrice;
    }

    public void setInformativePrice(Double informativePrice) {
        this.informativePrice = informativePrice;
    }
}
