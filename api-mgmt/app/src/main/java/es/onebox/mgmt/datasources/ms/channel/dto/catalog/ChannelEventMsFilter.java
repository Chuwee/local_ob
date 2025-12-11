package es.onebox.mgmt.datasources.ms.channel.dto.catalog;

import es.onebox.core.serializer.dto.request.BaseRequestFilter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class ChannelEventMsFilter extends BaseRequestFilter {

    private static final long serialVersionUID = 1L;

    private ChannelEventStatus status;
    private Boolean published;
    private Boolean onSale;
    private String q;

    public String getQ() {
        return q;
    }

    public void setQ(String q) {
        this.q = q;
    }

    public ChannelEventStatus getStatus() {
        return status;
    }

    public void setStatus(ChannelEventStatus status) {
        this.status = status;
    }

    public Boolean getPublished() {
        return published;
    }

    public void setPublished(Boolean published) {
        this.published = published;
    }

    public Boolean getOnSale() {
        return onSale;
    }

    public void setOnSale(Boolean onSale) {
        this.onSale = onSale;
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
