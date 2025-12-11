package es.onebox.event.datasources.ms.order.dto;

import es.onebox.core.serializer.dto.request.BaseRequestFilter;
import es.onebox.core.serializer.dto.request.SortOperator;
import es.onebox.core.serializer.validation.MaxLimit;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serial;
import java.util.List;

@MaxLimit(1000)
public class ProductActiveProductsRequest extends BaseRequestFilter {

    @Serial
    private static final long serialVersionUID = 1L;

    private List<Long> eventIds;
    private List<Long> channelEntityIds;
    private Boolean userProducts;

    private SortOperator<ProductSortableField> sort;

    public List<Long> getEventIds() {
        return eventIds;
    }

    public void setEventIds(List<Long> eventIds) {
        this.eventIds = eventIds;
    }

    public List<Long> getChannelEntityIds() {
        return channelEntityIds;
    }

    public void setChannelEntityIds(List<Long> channelEntityIds) {
        this.channelEntityIds = channelEntityIds;
    }

    public Boolean getUserProducts() {
        return userProducts;
    }

    public void setUserProducts(Boolean userProducts) {
        this.userProducts = userProducts;
    }

    public SortOperator<ProductSortableField> getSort() {
        return sort;
    }

    public void setSort(SortOperator<ProductSortableField> sort) {
        this.sort = sort;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
