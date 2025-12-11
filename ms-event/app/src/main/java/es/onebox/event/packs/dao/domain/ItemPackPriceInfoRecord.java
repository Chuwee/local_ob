package es.onebox.event.packs.dao.domain;

import java.util.Objects;

public class ItemPackPriceInfoRecord {

    private Integer packItemId;
    private Integer itemRateId;
    private Integer mainPriceZone;
    private String mainPriceZoneName;
    private Double itemPrice;

    public ItemPackPriceInfoRecord() {
    }

    public ItemPackPriceInfoRecord(Integer packItemId, Integer itemRateId, Integer mainPriceZone, String mainPriceZoneName, Double itemPrice) {
        this.packItemId = packItemId;
        this.itemRateId = itemRateId;
        this.mainPriceZone = mainPriceZone;
        this.mainPriceZoneName = mainPriceZoneName;
        this.itemPrice = itemPrice;
    }

    public Integer getPackItemId() {
        return packItemId;
    }

    public void setPackItemId(Integer packItemId) {
        this.packItemId = packItemId;
    }

    public Integer getItemRateId() {
        return itemRateId;
    }

    public void setItemRateId(Integer itemRateId) {
        this.itemRateId = itemRateId;
    }

    public Integer getMainPriceZone() {
        return mainPriceZone;
    }

    public void setMainPriceZone(Integer mainPriceZone) {
        this.mainPriceZone = mainPriceZone;
    }

    public String getMainPriceZoneName() {
        return mainPriceZoneName;
    }

    public void setMainPriceZoneName(String mainPriceZoneName) {
        this.mainPriceZoneName = mainPriceZoneName;
    }

    public Double getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(Double itemPrice) {
        this.itemPrice = itemPrice;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ItemPackPriceInfoRecord that = (ItemPackPriceInfoRecord) o;
        return Objects.equals(packItemId, that.packItemId)
                && Objects.equals(itemRateId, that.itemRateId)
                && Objects.equals(mainPriceZone, that.mainPriceZone)
                && Objects.equals(mainPriceZoneName, that.mainPriceZoneName)
                && Objects.equals(itemPrice, that.itemPrice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(packItemId, itemRateId, mainPriceZone, mainPriceZoneName, itemPrice);
    }
}
