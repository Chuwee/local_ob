package es.onebox.event.priceengine.request;

import java.io.Serial;
import java.util.EnumSet;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import es.onebox.core.serializer.dto.request.BaseRequestFilter;
import es.onebox.core.serializer.dto.request.SortOperator;
import es.onebox.core.serializer.validation.DefaultLimit;
import es.onebox.core.serializer.validation.MaxLimit;

@MaxLimit(1000)
@DefaultLimit(50)
public class EventChannelSearchFilter extends BaseRequestFilter {

    @Serial
    private static final long serialVersionUID = -8452408089846014140L;

    private SortOperator<String> sort;
    private List<Long> id;
    private List<ChannelSubtype> subtype;
    private Long entityId;
    private String q;
    private EnumSet<StatusRequestType> requestStatus;
    private EnumSet<StatusSaleType> saleStatus;
    private EnumSet<StatusReleaseType> releaseStatus;

    public SortOperator<String> getSort() {
        return sort;
    }

    public void setSort(SortOperator<String> sort) {
        this.sort = sort;
    }

    public List<Long> getId() {
        return id;
    }

    public void setId(List<Long> id) {
        this.id = id;
    }

    public List<ChannelSubtype> getSubtype() {
        return subtype;
    }

    public void setSubtype(List<ChannelSubtype> subtype) {
        this.subtype = subtype;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public String getQ() {
        return q;
    }

    public void setQ(String q) {
        this.q = q;
    }

    public EnumSet<StatusRequestType> getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(EnumSet<StatusRequestType> requestStatus) {
        this.requestStatus = requestStatus;
    }

    public EnumSet<StatusSaleType> getSaleStatus() {
        return saleStatus;
    }

    public void setSaleStatus(EnumSet<StatusSaleType> saleStatus) {
        this.saleStatus = saleStatus;
    }

    public EnumSet<StatusReleaseType> getReleaseStatus() {
        return releaseStatus;
    }

    public void setReleaseStatus(EnumSet<StatusReleaseType> releaseStatus) {
        this.releaseStatus = releaseStatus;
    }

    @Override
    protected Long getDefaultLimit() {
        return 50L;
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
