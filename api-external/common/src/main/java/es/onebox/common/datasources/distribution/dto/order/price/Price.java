package es.onebox.common.datasources.distribution.dto.order.price;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class Price implements Serializable {
    @Serial
    private static final long serialVersionUID = -6213726604122273526L;

    @JsonProperty("base")
    Double base;
    @JsonProperty("promotions")
    List<PromotionPrice> promotions;
    @JsonProperty("delivery")
    Double delivery;
    @JsonProperty("insurance")
    Double insurance;
    @JsonProperty("surcharges")
    List<SurchargePrice> surcharges;
    @JsonProperty("b2b")
    B2BPrice b2BPrice;
    @JsonProperty("final")
    Double finalPrice;
    @JsonProperty("currency")
    String currency;
    @JsonProperty("reallocation_refund")
    Double reallocationRefund;

    public Price() {
    }

    public Price(Double base, List<PromotionPrice> promotions, Double delivery, Double insurance, List<SurchargePrice> surcharges, B2BPrice b2BPrice, Double finalPrice, String currency) {
        this.base = base;
        this.promotions = promotions;
        this.delivery = delivery;
        this.insurance = insurance;
        this.surcharges = surcharges;
        this.b2BPrice = b2BPrice;
        this.finalPrice = finalPrice;
        this.currency = currency;
    }

    public Double getBase() {
        return base;
    }

    public void setBase(Double base) {
        this.base = base;
    }

    public List<PromotionPrice> getPromotions() {
        return promotions;
    }

    public void setPromotions(List<PromotionPrice> promotions) {
        this.promotions = promotions;
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

    public List<SurchargePrice> getSurcharges() {
        return surcharges;
    }

    public void setSurcharges(List<SurchargePrice> surcharges) {
        this.surcharges = surcharges;
    }

    public B2BPrice getB2BPrice() {
        return b2BPrice;
    }

    public void setB2BPrice(B2BPrice b2BPrice) {
        this.b2BPrice = b2BPrice;
    }

    public Double getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(Double finalPrice) {
        this.finalPrice = finalPrice;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Double getReallocationRefund() {
        return reallocationRefund;
    }

    public void setReallocationRefund(Double reallocationRefund) {
        this.reallocationRefund = reallocationRefund;
    }
}
