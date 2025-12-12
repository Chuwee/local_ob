package es.onebox.common.datasources.ms.order.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class OrderPriceDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -4639424352217318112L;
    private String currency;
    private Double basePrice;
    private Double clientConditionsPrice;
    private Double clientCommission;
    private Double delivery;
    private Double insurance;
    private OrderPricePromotionsDTO promotions = new OrderPricePromotionsDTO();
    private OrderPriceChargesDTO charges = new OrderPriceChargesDTO();
    private Double finalPrice;
    private Integer externalPriceId;
    private List<OrderPriceFeeDTO> fees;
    private InformativePrices informativePrices;
    private Double tax;
    private Double chargesTax;
    private Double partialRefund;

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(Double basePrice) {
        this.basePrice = basePrice;
    }

    public Double getClientConditionsPrice() {
        return clientConditionsPrice;
    }

    public void setClientConditionsPrice(Double clientConditionsPrice) {
        this.clientConditionsPrice = clientConditionsPrice;
    }

    public Double getClientCommission() {
        return clientCommission;
    }

    public void setClientCommission(Double clientCommission) {
        this.clientCommission = clientCommission;
    }

    public Double getDelivery() {
        return delivery;
    }

    public void setDelivery(Double delivery) {
        this.delivery = delivery;
    }

    public Double getInsurance() {
        return insurance;
    }

    public void setInsurance(Double insurance) {
        this.insurance = insurance;
    }

    public OrderPricePromotionsDTO getPromotions() {
        return promotions;
    }

    public void setPromotions(OrderPricePromotionsDTO promotions) {
        this.promotions = promotions;
    }

    public OrderPriceChargesDTO getCharges() {
        return charges;
    }

    public void setCharges(OrderPriceChargesDTO charges) {
        this.charges = charges;
    }

    public Double getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(Double finalPrice) {
        this.finalPrice = finalPrice;
    }

    public Integer getExternalPriceId() {
        return externalPriceId;
    }

    public void setExternalPriceId(Integer externalPriceId) {
        this.externalPriceId = externalPriceId;
    }

    public List<OrderPriceFeeDTO> getFees() {
        return fees;
    }

    public void setFees(List<OrderPriceFeeDTO> fees) {
        this.fees = fees;
    }

    public Double getTax() {
        return tax;
    }

    public void setTax(Double tax) {
        this.tax = tax;
    }

    public Double getChargesTax() {
        return chargesTax;
    }

    public void setChargesTax(Double chargesTax) {
        this.chargesTax = chargesTax;
    }

    public Double getPartialRefund() {
        return partialRefund;
    }

    public void setPartialRefund(Double partialRefund) {
        this.partialRefund = partialRefund;
    }

    public InformativePrices getInformativePrices() {
        return informativePrices;
    }

    public void setInformativePrices(InformativePrices informativePrices) {
        this.informativePrices = informativePrices;
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
