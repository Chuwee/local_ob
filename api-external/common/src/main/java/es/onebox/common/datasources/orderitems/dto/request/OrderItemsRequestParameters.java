package es.onebox.common.datasources.orderitems.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.common.datasources.orderitems.enums.OrderItemState;

import java.util.List;

public class OrderItemsRequestParameters {

    private Integer limit;
    private Integer offset;

    private List<OrderItemState> state;
    @JsonProperty("customer_id")
    private List<String> customerId;
    @JsonProperty("order_code")
    private List<String> orderCode;
    @JsonProperty("session_id")
    private List<Integer> sessionId;

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public List<OrderItemState> getState() {
        return state;
    }

    public void setState(List<OrderItemState> state) {
        this.state = state;
    }

    public List<String> getCustomerId() {
        return customerId;
    }

    public void setCustomerId(List<String> customerId) {
        this.customerId = customerId;
    }

    public List<String> getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(List<String> orderCode) {
        this.orderCode = orderCode;
    }

    public List<Integer> getSessionId() {
        return sessionId;
    }

    public void setSessionId(List<Integer> sessionId) {
        this.sessionId = sessionId;
    }
}
