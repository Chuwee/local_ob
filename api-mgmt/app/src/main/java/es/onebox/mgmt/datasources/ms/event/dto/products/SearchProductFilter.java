package es.onebox.mgmt.datasources.ms.event.dto.products;

import es.onebox.core.serializer.dto.request.BaseRequestFilter;
import es.onebox.core.serializer.dto.request.SortOperator;
import es.onebox.mgmt.datasources.ms.event.dto.products.enums.ProductStockType;
import es.onebox.mgmt.products.enums.ProductState;
import es.onebox.mgmt.products.enums.ProductType;
import es.onebox.mgmt.products.enums.SelectionType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.util.List;

public class SearchProductFilter extends BaseRequestFilter {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long operatorId;
    private List<Long> entityIds;
    private String q;
    private List<ProductState> productState;
    private List<ProductStockType> stockType;
    private List<ProductType> productType;
    private Long currencyId;
    private SortOperator<String> sort;
    private List<Long> eventIds;
    private List<Long> sessionIds;
    private SelectionType eventSessionSelectionType;

    public List<Long> getEntityIds() {
        return entityIds;
    }

    public void setEntityIds(List<Long> entityIds) {
        this.entityIds = entityIds;
    }

    public String getQ() {
        return q;
    }

    public void setQ(String q) {
        this.q = q;
    }

    public List<ProductState> getProductState() {
        return productState;
    }

    public void setProductState(List<ProductState> productState) {
        this.productState = productState;
    }

    public List<ProductStockType> getStockType() {
        return stockType;
    }

    public void setStockType(List<ProductStockType> stockType) {
        this.stockType = stockType;
    }

    public List<ProductType> getProductType() {
        return productType;
    }

    public void setProductType(List<ProductType> productType) {
        this.productType = productType;
    }

    public SortOperator<String> getSort() {
        return sort;
    }

    public void setSort(SortOperator<String> sort) {
        this.sort = sort;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public Long getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(Long currencyId) {
        this.currencyId = currencyId;
    }

    public List<Long> getEventIds() {
        return eventIds;
    }

    public void setEventIds(List<Long> eventIds) {
        this.eventIds = eventIds;
    }

    public List<Long> getSessionIds() {
        return sessionIds;
    }

    public void setSessionIds(List<Long> sessionIds) {
        this.sessionIds = sessionIds;
    }

    public SelectionType getEventSessionSelectionType() {
        return eventSessionSelectionType;
    }

    public void setEventSessionSelectionType(SelectionType eventSessionSelectionType) {
        this.eventSessionSelectionType = eventSessionSelectionType;
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
