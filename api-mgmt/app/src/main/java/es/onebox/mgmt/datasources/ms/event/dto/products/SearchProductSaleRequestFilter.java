package es.onebox.mgmt.datasources.ms.event.dto.products;

import es.onebox.core.serializer.dto.request.BaseRequestFilter;
import es.onebox.core.serializer.dto.request.FilterWithOperator;
import es.onebox.core.serializer.dto.request.SortOperator;
import es.onebox.mgmt.products.enums.ProductSaleRequestsStatus;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serial;
import java.time.ZonedDateTime;
import java.util.List;

public class SearchProductSaleRequestFilter extends BaseRequestFilter {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long operatorId;
    private List<ProductSaleRequestsStatus> status;
    private String q;
    private SortOperator<String> sort;
    private Long promoterId;
    private List<Long> entityIds;
    private List<Long> channelId;
    private List<Long> channelEntityId;
    private List<Long> productEntityId;
    private Long entityAdminId;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private List<FilterWithOperator<ZonedDateTime>> requestDate;

    public List<ProductSaleRequestsStatus> getStatus() {
        return status;
    }

    public void setStatus(List<ProductSaleRequestsStatus> status) {
        this.status = status;
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

    public Long getPromoterId() {
        return promoterId;
    }

    public void setPromoterId(Long promoterId) {
        this.promoterId = promoterId;
    }

    public List<Long> getChannelId() {
        return channelId;
    }

    public void setChannelId(List<Long> channelId) {
        this.channelId = channelId;
    }

    public List<FilterWithOperator<ZonedDateTime>> getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(List<FilterWithOperator<ZonedDateTime>> requestDate) {
        this.requestDate = requestDate;
    }

    public List<Long> getEntityIds() {
        return entityIds;
    }

    public void setEntityIds(List<Long> entityIds) {
        this.entityIds = entityIds;
    }

    public List<Long> getChannelEntityId() {
        return channelEntityId;
    }

    public void setChannelEntityId(List<Long> channelEntityId) {
        this.channelEntityId = channelEntityId;
    }

    public List<Long> getProductEntityId() {
        return productEntityId;
    }

    public void setProductEntityId(List<Long> productEntityId) {
        this.productEntityId = productEntityId;
    }

    public Long getEntityAdminId() {
        return entityAdminId;
    }

    public void setEntityAdminId(Long entityAdminId) {
        this.entityAdminId = entityAdminId;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
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
