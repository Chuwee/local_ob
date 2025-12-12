package es.onebox.common.datasources.orders.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class RefundCustomInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = 746708166448813338L;

    @JsonProperty("pgp_refund_transaction_id")
    private String pgpRefundTransactionId;

    public String getPgpRefundTransactionId() {
        return pgpRefundTransactionId;
    }

    public void setPgpRefundTransactionId(String pgpRefundTransactionId) {
        this.pgpRefundTransactionId = pgpRefundTransactionId;
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
