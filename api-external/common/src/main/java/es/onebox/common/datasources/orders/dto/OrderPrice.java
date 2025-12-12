package es.onebox.common.datasources.orders.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

public class OrderPrice implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String currency;

    @JsonProperty("base")
    private BigDecimal basePrice;
    @JsonProperty("final")
    private BigDecimal finalPrice;

    private BigDecimal delivery;

    private BigDecimal insurance;

    private BigDecimal clientConditionsPrice;

    private BigDecimal clientCommission;
    @JsonProperty("sales")
    private OrderPricePromotions promotions;

    private OrderPriceCharges charges;

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }

    public BigDecimal getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(BigDecimal finalPrice) {
        this.finalPrice = finalPrice;
    }

    public BigDecimal getDelivery() {
        return delivery;
    }

    public void setDelivery(BigDecimal delivery) {
        this.delivery = delivery;
    }

    public BigDecimal getInsurance() {
        return insurance;
    }

    public void setInsurance(BigDecimal insurance) {
        this.insurance = insurance;
    }

    public BigDecimal getClientConditionsPrice() {
        return clientConditionsPrice;
    }

    public void setClientConditionsPrice(BigDecimal clientConditionsPrice) {
        this.clientConditionsPrice = clientConditionsPrice;
    }

    public BigDecimal getClientCommission() {
        return clientCommission;
    }

    public void setClientCommission(BigDecimal clientCommission) {
        this.clientCommission = clientCommission;
    }

    public OrderPricePromotions getPromotions() {
        return promotions;
    }

    public void setPromotions(OrderPricePromotions promotions) {
        this.promotions = promotions;
    }

    public OrderPriceCharges getCharges() {
        return charges;
    }

    public void setCharges(OrderPriceCharges charges) {
        this.charges = charges;
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
