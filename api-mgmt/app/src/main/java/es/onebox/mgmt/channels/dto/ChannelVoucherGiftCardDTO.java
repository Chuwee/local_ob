package es.onebox.mgmt.channels.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class ChannelVoucherGiftCardDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("enable")
    private Boolean allowPurchaseGiftCard;
    @Min(value = 0, message = "gift_card_id must be above 0")
    @JsonProperty("id")
    private Long giftCardId;

    public Boolean getAllowPurchaseGiftCard() {
        return allowPurchaseGiftCard;
    }

    public void setAllowPurchaseGiftCard(Boolean allowPurchaseGiftCard) {
        this.allowPurchaseGiftCard = allowPurchaseGiftCard;
    }

    public Long getGiftCardId() {
        return giftCardId;
    }

    public void setGiftCardId(Long giftCardId) {
        this.giftCardId = giftCardId;
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
