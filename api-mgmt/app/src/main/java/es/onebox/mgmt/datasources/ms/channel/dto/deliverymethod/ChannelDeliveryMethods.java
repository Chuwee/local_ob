package es.onebox.mgmt.datasources.ms.channel.dto.deliverymethod;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;

public class ChannelDeliveryMethods implements Serializable {

    private static final long serialVersionUID = 2L;

    private List<ChannelDeliveryMethod> deliveryMethods;
    private Boolean useNFC;
    private Boolean forceMultiTicket;
    private EmailMode emailMode;
    private B2bExternalDownloadURL b2bExternalDownloadURL;
    private ReceiptTicketDisplay receiptTicketDisplay;
    private CheckoutTicketDisplay checkoutTicketDisplay;

    public List<ChannelDeliveryMethod> getDeliveryMethods() {
        return deliveryMethods;
    }

    public void setDeliveryMethods(List<ChannelDeliveryMethod> deliveryMethods) {
        this.deliveryMethods = deliveryMethods;
    }

    public Boolean getUseNFC() { return useNFC; }

    public void setUseNFC(Boolean useNFC) { this.useNFC = useNFC; }

    public EmailMode getEmailMode() {
        return emailMode;
    }

    public void setEmailMode(EmailMode emailMode) {
        this.emailMode = emailMode;
    }

    public B2bExternalDownloadURL getB2bExternalDownloadURL() {
        return b2bExternalDownloadURL;
    }

    public void setB2bExternalDownloadURL(B2bExternalDownloadURL b2bExternalDownloadURL) {
        this.b2bExternalDownloadURL = b2bExternalDownloadURL;
    }

    public ReceiptTicketDisplay getReceiptTicketDisplay() {
        return receiptTicketDisplay;
    }

    public void setReceiptTicketDisplay(ReceiptTicketDisplay receiptTicketDisplay) {
        this.receiptTicketDisplay = receiptTicketDisplay;
    }

    public CheckoutTicketDisplay getCheckoutTicketDisplay() {
        return checkoutTicketDisplay;
    }

    public void setCheckoutTicketDisplay(CheckoutTicketDisplay checkoutTicketDisplay) {
        this.checkoutTicketDisplay = checkoutTicketDisplay;
    }

    public Boolean getForceMultiTicket() {
        return forceMultiTicket;
    }

    public void setForceMultiTicket(Boolean forceMultiTicket) {
        this.forceMultiTicket = forceMultiTicket;
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
