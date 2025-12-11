package es.onebox.mgmt.channels.deliverymethods.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class ChannelDeliveryMethodsUpdateDTO implements Serializable {

    @Serial private static final long serialVersionUID = 1L;

    @Valid
    @JsonProperty("methods")
    private List<ChannelDeliveryMethodDTO> deliveryMethods;
    @JsonProperty("use_nfc")
    private Boolean useNFC;
    @JsonProperty("purchase_email_content")
    private EmailModeDTO emailMode;
    @JsonProperty("b2b_external_download_url")
    private B2bExternalDownloadURLUpdateDTO b2bExternalDownloadUrlUpdate;
    @JsonProperty("receipt_ticket_display")
    private ReceiptTicketDisplayDTO receiptTicketDisplay;
    @JsonProperty("checkout_ticket_display")
    private CheckoutTicketDisplayDTO checkoutTicketDisplay;

    public List<ChannelDeliveryMethodDTO> getDeliveryMethods() {
        return deliveryMethods;
    }

    public void setDeliveryMethods(List<ChannelDeliveryMethodDTO> deliveryMethods) {
        this.deliveryMethods = deliveryMethods;
    }

    public Boolean getUseNFC() { return useNFC; }

    public void setUseNFC(Boolean useNFC) { this.useNFC = useNFC; }

    public EmailModeDTO getEmailMode() {
        return emailMode;
    }

    public void setEmailMode(EmailModeDTO emailMode) {
        this.emailMode = emailMode;
    }

    public B2bExternalDownloadURLUpdateDTO getB2bExternalDownloadUrlUpdate() {
        return b2bExternalDownloadUrlUpdate;
    }

    public void setB2bExternalDownloadUrlUpdate(B2bExternalDownloadURLUpdateDTO b2bExternalDownloadUrlUpdate) {
        this.b2bExternalDownloadUrlUpdate = b2bExternalDownloadUrlUpdate;
    }

    public ReceiptTicketDisplayDTO getReceiptTicketDisplay() {
        return receiptTicketDisplay;
    }

    public void setReceiptTicketDisplay(ReceiptTicketDisplayDTO receiptTicketDisplay) {
        this.receiptTicketDisplay = receiptTicketDisplay;
    }

    public CheckoutTicketDisplayDTO getCheckoutTicketDisplay() {
        return checkoutTicketDisplay;
    }

    public void setCheckoutTicketDisplay(CheckoutTicketDisplayDTO checkoutTicketDisplay) {
        this.checkoutTicketDisplay = checkoutTicketDisplay;
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
