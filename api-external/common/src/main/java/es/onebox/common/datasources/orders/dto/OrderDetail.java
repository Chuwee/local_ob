package es.onebox.common.datasources.orders.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.util.List;

public class OrderDetail extends Order {

    @Serial
    private static final long serialVersionUID = -170363577966658311L;

    @JsonProperty("payment_detail")
    private OrderPaymentDetailExtended paymentDetail;

    private List<OrderDetailItem> items;

    public List<OrderDetailItem> getItems() {
        return items;
    }

    public void setItems(List<OrderDetailItem> items) {
        this.items = items;
    }

    public OrderPaymentDetailExtended getPaymentDetail() {
        return paymentDetail;
    }

    public void setPaymentDetail(OrderPaymentDetailExtended paymentDetail) {
        this.paymentDetail = paymentDetail;
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
