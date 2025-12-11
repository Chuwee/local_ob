package es.onebox.mgmt.datasources.ms.event.dto.packs;

import es.onebox.mgmt.datasources.ms.channel.enums.PackItemType;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class PackItem implements Serializable {

    @Serial
    private static final long serialVersionUID = 5553824240649464486L;

    private Long packItemId;
    private Long itemId;
    private PackItemType type;
    private Boolean isMain;
    private Integer venueTemplateId;
    private Integer priceTypeId;
    private Map<Integer, List<Integer>> priceTypeMapping;
    private Integer variantId;
    private Integer deliveryPointId;
    private Boolean sharedBarcode;
    private Boolean displayItemInChannels;
    private Double informativePrice;

    public Long getPackItemId() {
        return packItemId;
    }

    public void setPackItemId(Long packItemId) {
        this.packItemId = packItemId;
    }

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

    public Boolean getMain() {
        return isMain;
    }

    public void setMain(Boolean main) {
        isMain = main;
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
