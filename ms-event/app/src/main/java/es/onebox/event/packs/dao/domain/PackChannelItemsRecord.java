package es.onebox.event.packs.dao.domain;

import es.onebox.jooq.cpanel.tables.records.CpanelPackCanalRecord;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class PackChannelItemsRecord extends CpanelPackCanalRecord {
    private Long itemId;
    private Integer itemType;
    private String itemName;
    private Boolean suggestedPack;
    private Boolean mainItem;

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Integer getItemType() {
        return itemType;
    }

    public void setItemType(Integer itemType) {
        this.itemType = itemType;
    }

    public Boolean getSuggestedPack() {
        return suggestedPack;
    }

    public void setSuggestedPack(Boolean suggestedPack) {
        this.suggestedPack = suggestedPack;
    }

    public Boolean getMainItem() {
        return mainItem;
    }

    public void setMainItem(Boolean mainItem) {
        this.mainItem = mainItem;
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
