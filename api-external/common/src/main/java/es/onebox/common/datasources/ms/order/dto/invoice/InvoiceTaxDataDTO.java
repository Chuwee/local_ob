package es.onebox.common.datasources.ms.order.dto.invoice;

import java.io.Serializable;
import java.math.BigDecimal;

public class InvoiceTaxDataDTO implements Serializable {

    private static final long serialVersionUID = 8695577251551293844L;

    private Double taxBase = 0.0;
    private BigDecimal taxBaseDecimals;
    private Double vat;
    private Double totalVAT;
    private BigDecimal totalVATDecimals;
    private Double total;
    private BigDecimal totalDecimals;

    public Double getTaxBase() {
        return taxBase;
    }

    public void setTaxBase(Double taxBase) {
        this.taxBase = taxBase;
    }

    public BigDecimal getTaxBaseDecimals() {
        return taxBaseDecimals;
    }

    public void setTaxBaseDecimals(BigDecimal taxBaseDecimals) {
        this.taxBaseDecimals = taxBaseDecimals;
    }

    public Double getVat() {
        return vat;
    }

    public void setVat(Double vat) {
        this.vat = vat;
    }

    public Double getTotalVAT() {
        return totalVAT;
    }

    public void setTotalVAT(Double totalVAT) {
        this.totalVAT = totalVAT;
    }

    public BigDecimal getTotalVATDecimals() {
        return totalVATDecimals;
    }

    public void setTotalVATDecimals(BigDecimal totalVATDecimals) {
        this.totalVATDecimals = totalVATDecimals;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public BigDecimal getTotalDecimals() {
        return totalDecimals;
    }

    public void setTotalDecimals(BigDecimal totalDecimals) {
        this.totalDecimals = totalDecimals;
    }
}
