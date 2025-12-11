package es.onebox.mgmt.products.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.request.FilterWithOperator;
import es.onebox.core.serializer.dto.request.SortOperator;
import es.onebox.mgmt.common.BaseEntityRequestFilter;
import es.onebox.mgmt.products.enums.ProductSaleRequestsStatus;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

public class SearchProductSaleRequestFilterDTO extends BaseEntityRequestFilter implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private List<ProductSaleRequestsStatus> status;
    private String q;
    private SortOperator<String> sort;
    @JsonProperty("promoter_id")
    private Long promoterId;
    @JsonProperty("channel_id")
    private List<Long> channelId;
    @JsonProperty("channel_entity_id")
    private List<Long> channelEntityId;
    @JsonProperty("product_entity_id")
    private List<Long> productEntityId;
    @JsonProperty("entity_admin_id")
    private Long entityAdminId;
    @JsonProperty("request_date")
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

    public Long getEntityAdminId() {
        return entityAdminId;
    }

    public void setEntityAdminId(Long entityAdminId) {
        this.entityAdminId = entityAdminId;
    }

    public List<FilterWithOperator<ZonedDateTime>> getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(List<FilterWithOperator<ZonedDateTime>> requestDate) {
        this.requestDate = requestDate;
    }

    public List<Long> getProductEntityId() {
        return productEntityId;
    }

    public void setProductEntityId(List<Long> productEntityId) {
        this.productEntityId = productEntityId;
    }

    public List<Long> getChannelEntityId() {
        return channelEntityId;
    }

    public void setChannelEntityId(List<Long> channelEntityId) {
        this.channelEntityId = channelEntityId;
    }

    public SortOperator<String> getSort() {
        return sort;
    }

    public void setSort(SortOperator<String> sort) {
        this.sort = sort;
    }
}
