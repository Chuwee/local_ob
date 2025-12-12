package es.onebox.eci.ticketsales.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

public class Price implements Serializable {

    @Serial
    private static final long serialVersionUID = 2883949511155570185L;

    @JsonProperty("price_level")
    private String level;
    @JsonProperty("fee_and_commisions_specification")
    private String feeAndCommisions;
    private BigDecimal amount;
    @JsonProperty("gross_amount")
    private BigDecimal gross;
    @JsonProperty("net_amount")
    private BigDecimal net;
    @JsonProperty("price_currency")
    private String currency;
    @JsonProperty("tax_specification")
    private Tax tax;
    @JsonProperty("customer_fee")
    private Fee customerFee;
    @JsonProperty("provider_fee")
    private Fee providerFee;
    @JsonProperty("sponsor_fee")
    private Fee sponsorFee;

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getFeeAndCommisions() {
        return feeAndCommisions;
    }

    public void setFeeAndCommisions(String feeAndCommisions) {
        this.feeAndCommisions = feeAndCommisions;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getGross() {
        return gross;
    }

    public void setGross(BigDecimal gross) {
        this.gross = gross;
    }

    public BigDecimal getNet() {
        return net;
    }

    public void setNet(BigDecimal net) {
        this.net = net;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Tax getTax() {
        return tax;
    }

    public void setTax(Tax tax) {
        this.tax = tax;
    }

    public Fee getCustomerFee() {
        return customerFee;
    }

    public void setCustomerFee(Fee customerFee) {
        this.customerFee = customerFee;
    }

    public Fee getProviderFee() {
        return providerFee;
    }

    public void setProviderFee(Fee providerFee) {
        this.providerFee = providerFee;
    }

    public Fee getSponsorFee() {
        return sponsorFee;
    }

    public void setSponsorFee(Fee sponsorFee) {
        this.sponsorFee = sponsorFee;
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
