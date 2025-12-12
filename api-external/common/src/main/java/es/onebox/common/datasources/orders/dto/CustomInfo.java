package es.onebox.common.datasources.orders.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class CustomInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = -8409886185044972023L;

    @JsonProperty("pgp_sale_transaction_id")
    private String pgpSaleTransactionId;

    @JsonProperty("pgp_order_id")
    private String pgpOrderId;

    @JsonProperty("pgp_payments")
    private List<PGPPayment> pgpPayments;

    @JsonProperty("pgp_items")
    private List<PGPItem> pgpItems;

    public String getPgpSaleTransactionId() {
        return pgpSaleTransactionId;
    }

    public void setPgpSaleTransactionId(String pgpSaleTransactionId) {
        this.pgpSaleTransactionId = pgpSaleTransactionId;
    }

    public String getPgpOrderId() {
        return pgpOrderId;
    }

    public void setPgpOrderId(String pgpOrderId) {
        this.pgpOrderId = pgpOrderId;
    }

    public List<PGPPayment> getPgpPayments() {
        return pgpPayments;
    }

    public void setPgpPayments(List<PGPPayment> pgpPayments) {
        this.pgpPayments = pgpPayments;
    }

    public List<PGPItem> getPgpItems() {
        return pgpItems;
    }

    public void setPgpItems(List<PGPItem> pgpItems) {
        this.pgpItems = pgpItems;
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
