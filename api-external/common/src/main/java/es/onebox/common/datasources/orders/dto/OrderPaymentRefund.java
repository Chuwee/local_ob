package es.onebox.common.datasources.orders.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.common.datasources.orders.enums.OrderPaymentRefundStatus;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Set;

public class OrderPaymentRefund implements Serializable {

    @Serial
    private static final long serialVersionUID = -8709529718630481406L;

    private ZonedDateTime date;
    @JsonProperty("transaction_id")
    private String transactionId;
    private BigDecimal amount;
    private String message;
    private OrderPaymentRefundStatus status;
    @JsonProperty("gateway_additional_info")
    private RefundCustomInfo customInfo;
    @JsonProperty("item_ids")
    private Set<Long> itemIds;

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public OrderPaymentRefundStatus getStatus() {
        return status;
    }

    public void setStatus(OrderPaymentRefundStatus status) {
        this.status = status;
    }

    public RefundCustomInfo getCustomInfo() {
        return customInfo;
    }

    public void setCustomInfo(RefundCustomInfo customInfo) {
        this.customInfo = customInfo;
    }

    public Set<Long> getItemIds() {
        return itemIds;
    }

    public void setItemIds(Set<Long> itemIds) {
        this.itemIds = itemIds;
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
