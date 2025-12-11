package es.onebox.mgmt.channels.deliverymethods.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class ChannelDeliveryMethodsDTO implements Serializable {

    @Serial private static final long serialVersionUID = 1L;

    @Valid
    @JsonProperty("methods")
    private List<ChannelDeliveryMethodDTO> deliveryMethods;
    @JsonProperty("use_nfc")
    private Boolean useNFC;
    @JsonProperty("allow_session_pack_multi_ticket")
    private Boolean allowSessionPackMultiTicket;
    @JsonProperty("purchase_email_content")
    private EmailModeDTO emailMode;
    @JsonProperty("b2b_external_download_url")
    private B2bExternalDownloadURLDTO b2bExternalDownloadURL;
    @JsonProperty("receipt_ticket_display")
    private ReceiptTicketDisplayDTO receiptTicketDisplayDTO;
    @JsonProperty("checkout_ticket_display")
    private CheckoutTicketDisplayDTO checkoutTicketDisplayDTO;

    public List<ChannelDeliveryMethodDTO> getDeliveryMethods() {
        return deliveryMethods;
    }

    public void setDeliveryMethods(List<ChannelDeliveryMethodDTO> deliveryMethods) {
        this.deliveryMethods = deliveryMethods;
    }

    public Boolean getUseNFC() { return useNFC; }

    public void setUseNFC(Boolean useNFC) { this.useNFC = useNFC; }

    public Boolean getAllowSessionPackMultiTicket() {
        return allowSessionPackMultiTicket;
    }

    public void setAllowSessionPackMultiTicket(Boolean allowSessionPackMultiTicket) {
        this.allowSessionPackMultiTicket = allowSessionPackMultiTicket;
    }

    public EmailModeDTO getEmailMode() {
        return emailMode;
    }

    public void setEmailMode(EmailModeDTO emailMode) {
        this.emailMode = emailMode;
    }

    public B2bExternalDownloadURLDTO getB2bExternalDownloadURL() {
        return b2bExternalDownloadURL;
    }

    public void setB2bExternalDownloadURL(B2bExternalDownloadURLDTO b2bExternalDownloadURL) {
        this.b2bExternalDownloadURL = b2bExternalDownloadURL;
    }

    public ReceiptTicketDisplayDTO getReceiptTicketDisplayDTO() {
        return receiptTicketDisplayDTO;
    }

    public void setReceiptTicketDisplayDTO(ReceiptTicketDisplayDTO receiptTicketDisplayDTO) {
        this.receiptTicketDisplayDTO = receiptTicketDisplayDTO;
    }

    public CheckoutTicketDisplayDTO getCheckoutTicketDisplayDTO() {
        return checkoutTicketDisplayDTO;
    }

    public void setCheckoutTicketDisplayDTO(CheckoutTicketDisplayDTO checkoutTicketDisplayDTO) {
        this.checkoutTicketDisplayDTO = checkoutTicketDisplayDTO;
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
