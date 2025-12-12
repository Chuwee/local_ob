package es.onebox.common.datasources.ms.order.dto.invoice;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

public class InvoiceSessionDataDTO implements Serializable {

    private static final long serialVersionUID = -8817309205696354925L;

    private String name;
    private ZonedDateTime date;
    private Double tax = 0.0;
    private Double price = 0.0;
    private Integer amount = 0;
    private Double totalPrice;
    private BigDecimal decimalPrice;
    private BigDecimal decimalTotalPrice;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public Double getTax() {
        return tax;
    }

    public void setTax(Double tax) {
        this.tax = tax;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public BigDecimal getDecimalPrice() {
        return decimalPrice;
    }

    public void setDecimalPrice(BigDecimal decimalPrice) {
        this.decimalPrice = decimalPrice;
    }

    public BigDecimal getDecimalTotalPrice() {
        return decimalTotalPrice;
    }

    public void setDecimalTotalPrice(BigDecimal decimalTotalPrice) {
        this.decimalTotalPrice = decimalTotalPrice;
    }
}
