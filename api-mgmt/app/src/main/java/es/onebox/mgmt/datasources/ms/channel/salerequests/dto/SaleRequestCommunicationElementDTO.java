package es.onebox.mgmt.datasources.ms.channel.salerequests.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class SaleRequestCommunicationElementDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private List<PurchaseCommunicationElementDTO> communicationPurchaseElement;
    private List<ChannelCommunicationElementDTO> communicationChannelElement;
    private List<PaymentBenefitCommunicationElementDTO> communicationPaymentBenefitElement;

    public List<PurchaseCommunicationElementDTO> getCommunicationPurchaseElement() {
        return communicationPurchaseElement;
    }

    public void setCommunicationPurchaseElement(List<PurchaseCommunicationElementDTO> communicationPurchaseElement) {
        this.communicationPurchaseElement = communicationPurchaseElement;
    }

    public List<ChannelCommunicationElementDTO> getCommunicationChannelElement() {
        return communicationChannelElement;
    }

    public void setCommunicationChannelElement(List<ChannelCommunicationElementDTO> communicationChannelElement) {
        this.communicationChannelElement = communicationChannelElement;
    }

    public List<PaymentBenefitCommunicationElementDTO> getCommunicationPaymentBenefitElement() {
        return communicationPaymentBenefitElement;
    }

    public void setCommunicationPaymentBenefitElement(List<PaymentBenefitCommunicationElementDTO> communicationPaymentBenefitElement) {
        this.communicationPaymentBenefitElement = communicationPaymentBenefitElement;
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
