package es.onebox.mgmt.packs.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.datasources.ms.channel.enums.PackItemType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class BasePackItemDTO extends IdNameDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 5553824240649464486L;

    @JsonProperty("item_id")
    private Long itemId;

    private PackItemType type;

    @JsonProperty("main")
    private Boolean isMain;

    @JsonProperty("display_item_in_channels")
    private Boolean displayItemInChannels;

    @JsonProperty("event_data")
    private PackItemEventDataDTO eventData;

    @JsonProperty("session_data")
    private PackItemSessionDataDTO sessionData;

    @JsonProperty("product_data")
    private PackItemProductDataDTO productData;

    @JsonProperty("informative_price")
    private Double informativePrice;

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

    public Boolean getDisplayItemInChannels() {
        return displayItemInChannels;
    }

    public void setDisplayItemInChannels(Boolean displayItemInChannels) {
        this.displayItemInChannels = displayItemInChannels;
    }

    public PackItemEventDataDTO getEventData() {
        return eventData;
    }

    public void setEventData(PackItemEventDataDTO eventData) {
        this.eventData = eventData;
    }

    public PackItemSessionDataDTO getSessionData() {
        return sessionData;
    }

    public void setSessionData(PackItemSessionDataDTO sessionData) {
        this.sessionData = sessionData;
    }

    public PackItemProductDataDTO getProductData() {
        return productData;
    }

    public void setProductData(PackItemProductDataDTO productData) {
        this.productData = productData;
    }

    public Double getInformativePrice() {
        return informativePrice;
    }

    public void setInformativePrice(Double informativePrice) {
        this.informativePrice = informativePrice;
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
