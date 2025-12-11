package es.onebox.event.datasources.ms.order.dto;

import es.onebox.dal.dto.couch.enums.OrderState;

import java.util.List;

public class NumberOperationsRequest {

    List<Integer> sessionIds;
    List<OrderState> orderStates;
    List<Integer> rateIds;

    public List<Integer> getSessionIds() {
        return sessionIds;
    }

    public void setSessionIds(List<Integer> sessionIds) {
        this.sessionIds = sessionIds;
    }

    public List<OrderState> getOrderStates() {
        return orderStates;
    }

    public void setOrderStates(List<OrderState> orderStates) {
        this.orderStates = orderStates;
    }

    public List<Integer> getRateIds() {
        return rateIds;
    }

    public void setRateIds(List<Integer> rateIds) {
        this.rateIds = rateIds;
    }

}
