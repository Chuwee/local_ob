package es.onebox.common.datasources.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.common.datasources.orders.dto.SalesPrice;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

public class Price implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private BigDecimal base;

    private BigDecimal delivery;

    private BigDecimal insurance;

    @JsonProperty("final")
    private BigDecimal finalAmount;

    private String currency;

    private SalesPrice sales;

    private Charges charges;

    private B2BPrice b2b;

    @JsonProperty("channel_commission")
    private BigDecimal channelComission;

    private PriceTax tax;

    public BigDecimal getBase() {
        return base;
    }

    public void setBase(BigDecimal base) {
        this.base = base;
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

    public BigDecimal getFinalAmount() {
        return finalAmount;
    }

    public void setFinalAmount(BigDecimal finalAmount) {
        this.finalAmount = finalAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public SalesPrice getSales() {
        return sales;
    }

    public void setSales(SalesPrice sales) {
        this.sales = sales;
    }

    public Charges getCharges() {
        return charges;
    }

    public void setCharges(Charges charges) {
        this.charges = charges;
    }

    public B2BPrice getB2b() {
        return b2b;
    }

    public void setB2b(B2BPrice b2b) {
        this.b2b = b2b;
    }

    public BigDecimal getChannelComission() {
        return channelComission;
    }

    public void setChannelComission(BigDecimal channelComission) {
        this.channelComission = channelComission;
    }

    public PriceTax getTax() {
        return tax;
    }

    public void setTax(PriceTax tax) {
        this.tax = tax;
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
