package es.onebox.mgmt.channels.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.datasources.ms.channel.dto.CurrencyGiftCardDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class ChannelCurrencyVoucherGiftCardDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("enable")
    private Boolean allowPurchaseGiftCard;
    @JsonProperty("gift_card_ids")
    private List<CurrencyGiftCardDTO> giftCardsIds;

    public Boolean getAllowPurchaseGiftCard() {
        return allowPurchaseGiftCard;
    }

    public void setAllowPurchaseGiftCard(Boolean allowPurchaseGiftCard) {
        this.allowPurchaseGiftCard = allowPurchaseGiftCard;
    }

    public List<CurrencyGiftCardDTO> getGiftCardsIds() {
        return giftCardsIds;
    }

    public void setGiftCardsIds(List<CurrencyGiftCardDTO> giftCardsIds) {
        this.giftCardsIds = giftCardsIds;
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
