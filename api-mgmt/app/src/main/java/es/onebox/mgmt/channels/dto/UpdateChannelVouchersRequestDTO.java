package es.onebox.mgmt.channels.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class UpdateChannelVouchersRequestDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("allow_redeem_vouchers")
    private Boolean allowRedeemVouchers;
    @JsonProperty("allow_refund_to_vouchers")
    private Boolean allowRefundToVouchers;
    @JsonProperty("gift_card")
    private ChannelVoucherGiftCardDTO channelVoucherGiftCardDTO;
    @JsonProperty("gift_cards")
    private ChannelCurrencyVoucherGiftCardDTO channelCurrencyVoucherGiftCardDTO;

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

    public ChannelVoucherGiftCardDTO getChannelVoucherGiftCardDTO() {
        return channelVoucherGiftCardDTO;
    }

    public void setChannelVoucherGiftCardDTO(ChannelVoucherGiftCardDTO channelVoucherGiftCardDTO) {
        this.channelVoucherGiftCardDTO = channelVoucherGiftCardDTO;
    }

    public ChannelCurrencyVoucherGiftCardDTO getChannelCurrencyVoucherGiftCardDTO() {
        return channelCurrencyVoucherGiftCardDTO;
    }

    public void setChannelCurrencyVoucherGiftCardDTO(ChannelCurrencyVoucherGiftCardDTO channelCurrencyVoucherGiftCardDTO) {
        this.channelCurrencyVoucherGiftCardDTO = channelCurrencyVoucherGiftCardDTO;
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
