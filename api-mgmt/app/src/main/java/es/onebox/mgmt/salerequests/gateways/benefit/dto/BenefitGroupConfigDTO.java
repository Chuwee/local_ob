package es.onebox.mgmt.salerequests.gateways.benefit.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class BenefitGroupConfigDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 7851390472801769998L;

    @JsonProperty("validity_period")
    private ValidityPeriodDTO validityPeriod;

    @JsonProperty("installment_options")
    private List<Integer> installmentOptions;

    @JsonProperty("custom_valid_period")
    @NotNull
    private Boolean customValidPeriod;

    @JsonProperty("checkout_communication_elements")
    private CheckoutCommunicationElementsDTO checkoutCommunicationElements;

    public BenefitGroupConfigDTO() {
    }

    public ValidityPeriodDTO getValidityPeriod() {
        return validityPeriod;
    }

    public void setValidityPeriod(ValidityPeriodDTO validityPeriod) {
        this.validityPeriod = validityPeriod;
    }

    public List<Integer> getInstallmentOptions() {
        return installmentOptions;
    }

    public void setInstallmentOptions(List<Integer> installmentOptions) {
        this.installmentOptions = installmentOptions;
    }

    public Boolean getCustomValidPeriod() {
        return customValidPeriod;
    }

    public void setCustomValidPeriod(Boolean customValidPeriod) {
        this.customValidPeriod = customValidPeriod;
    }

    public CheckoutCommunicationElementsDTO getCheckoutCommunicationElements() {
        return checkoutCommunicationElements;
    }

    public void setCheckoutCommunicationElements(CheckoutCommunicationElementsDTO checkoutCommunicationElements) {
        this.checkoutCommunicationElements = checkoutCommunicationElements;
    }
}
