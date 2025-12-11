package es.onebox.mgmt.datasources.ms.channel.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class ChannelVouchers implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Boolean allowRedeemVouchers;
    private Boolean allowRefundToVouchers;
    private Boolean allowPurchaseGiftCard;
    private Long giftCardId;
    private List<CurrencyGiftCard> giftCardsIds;

    public Boolean getAllowRedeemVouchers() {
        return allowRedeemVouchers;
    }

    public void setAllowRedeemVouchers(Boolean allowRedeemVouchers) {
        this.allowRedeemVouchers = allowRedeemVouchers;
    }

    public Boolean getAllowRefundToVouchers() {
        return allowRefundToVouchers;
    }

    public void setAllowRefundToVouchers(Boolean allowRefundToVouchers) {
        this.allowRefundToVouchers = allowRefundToVouchers;
    }

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

    public List<CurrencyGiftCard> getGiftCardsIds() {
        return giftCardsIds;
    }

    public void setGiftCardsIds(List<CurrencyGiftCard> giftCardsIds) {
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
