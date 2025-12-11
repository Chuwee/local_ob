package es.onebox.event.catalog.dto.packs;

import es.onebox.event.packs.enums.PackItemType;

public class ChannelPackPriceItemInfoDTO {

    private Long itemId;
    private PackItemType type;
    private Double itemPrice;
    private Double itemPackPrice;

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

    public Double getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(Double itemPrice) {
        this.itemPrice = itemPrice;
    }

    public Double getItemPackPrice() {
        return itemPackPrice;
    }

    public void setItemPackPrice(Double itemPackPrice) {
        this.itemPackPrice = itemPackPrice;
    }
}
