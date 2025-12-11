package es.onebox.event.packs.dto;

import java.util.List;
import java.util.Map;

public class UpdatePackItemDTO {

    private Integer priceTypeId;
    private Map<Integer, List<Integer>> priceTypeMapping;
    private Integer variantId;
    private Integer deliveryPointId;
    private Boolean sharedBarcode;
    private Boolean displayItemInChannels;
    private Double informativePrice;
    private PriceTypeRange priceTypeRange;

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

    public PriceTypeRange getPriceTypeRange() {
        return priceTypeRange;
    }

    public void setPriceTypeRange(PriceTypeRange priceTypeRange) {
        this.priceTypeRange = priceTypeRange;
    }
}
