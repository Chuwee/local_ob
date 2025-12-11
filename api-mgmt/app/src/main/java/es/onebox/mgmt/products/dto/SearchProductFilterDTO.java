package es.onebox.mgmt.products.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.request.SortOperator;
import es.onebox.mgmt.common.BaseEntityRequestFilter;
import es.onebox.mgmt.datasources.ms.event.dto.products.enums.ProductStockType;
import es.onebox.mgmt.products.enums.ProductState;
import es.onebox.mgmt.products.enums.SelectionType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import es.onebox.mgmt.products.enums.ProductType;

import java.io.Serial;
import java.util.List;

public class SearchProductFilterDTO extends BaseEntityRequestFilter {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("product_state")
    private List<ProductState> productState;
    @JsonProperty("stock_type")
    private List<ProductStockType> stockType;
    @JsonProperty("product_type")
    private List<ProductType> productType;
    @JsonProperty("currency_code")
    private String currencyCode;
    private String q;
    private SortOperator<String> sort;
    @JsonProperty("event_ids")
    private List<Long> eventIds;
    @JsonProperty("session_ids")
    private List<Long> sessionIds;
    @JsonProperty("event_session_selection_type")
    private SelectionType eventSessionSelectionType;

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

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getQ() {
        return q;
    }

    public void setQ(String q) {
        this.q = q;
    }

    public SortOperator<String> getSort() {
        return sort;
    }

    public void setSort(SortOperator<String> sort) {
        this.sort = sort;
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