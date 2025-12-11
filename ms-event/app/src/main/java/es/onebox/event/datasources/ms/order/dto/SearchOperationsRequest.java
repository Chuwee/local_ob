package es.onebox.event.datasources.ms.order.dto;

import es.onebox.dal.dto.couch.enums.OrderState;
import es.onebox.dal.dto.couch.enums.OrderType;

import java.util.List;

public class SearchOperationsRequest {

    private List<String> codes;
    private List<Integer> sessionIds;
    private List<OrderType> orderTypes;
    private List<OrderState> orderStates;
    private Boolean operationRefunded;

    private Integer pageNumber;
    private Integer pageSize;

    public List<String> getCodes() {
        return codes;
    }

    public void setCodes(List<String> codes) {
        this.codes = codes;
    }

    public List<Integer> getSessionIds() {
        return sessionIds;
    }

    public void setSessionIds(List<Integer> sessionIds) {
        this.sessionIds = sessionIds;
    }

    public List<OrderType> getOrderTypes() {
        return orderTypes;
    }

    public void setOrderTypes(List<OrderType> orderTypes) {
        this.orderTypes = orderTypes;
    }

    public List<OrderState> getOrderStates() {
        return orderStates;
    }

    public void setOrderStates(List<OrderState> orderStates) {
        this.orderStates = orderStates;
    }

    public Boolean getOperationRefunded() {
        return operationRefunded;
    }

    public void setOperationRefunded(Boolean operationRefunded) {
        this.operationRefunded = operationRefunded;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
