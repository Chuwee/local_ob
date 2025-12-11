package es.onebox.mgmt.datasources.ms.payment.dto.benefits;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class BenefitGroupConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = 1696882893787606551L;

    private ValidityPeriod validityPeriod;
    private List<Integer> installmentOptions;
    private CheckoutCommunicationElements checkoutCommunicationElements;

    public BenefitGroupConfig() {
    }

    public ValidityPeriod getValidityPeriod() {
        return validityPeriod;
    }

    public void setValidityPeriod(ValidityPeriod validityPeriod) {
        this.validityPeriod = validityPeriod;
    }

    public List<Integer> getInstallmentOptions() {
        return installmentOptions;
    }

    public void setInstallmentOptions(List<Integer> installmentOptions) {
        this.installmentOptions = installmentOptions;
    }

    public CheckoutCommunicationElements getCheckoutCommunicationElements() {
        return checkoutCommunicationElements;
    }

    public void setCheckoutCommunicationElements(CheckoutCommunicationElements checkoutCommunicationElements) {
        this.checkoutCommunicationElements = checkoutCommunicationElements;
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
