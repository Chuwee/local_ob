package es.onebox.mgmt.channels.catalog.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.mgmt.datasources.ms.channel.dto.catalog.ChannelEventStatus;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class ChannelEventDTO extends IdDTO {

    private static final long serialVersionUID = 1L;

    private ChannelEventInfoDTO event;
    private ChannelEventStatus status;
    private Boolean published;
    @JsonProperty("on_sale")
    private Boolean onSale;
    private ChannelEventCatalogDataDTO catalog;

    public ChannelEventInfoDTO getEvent() {
        return event;
    }

    public void setEvent(ChannelEventInfoDTO event) {
        this.event = event;
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

    public ChannelEventCatalogDataDTO getCatalog() {
        return catalog;
    }

    public void setCatalog(ChannelEventCatalogDataDTO catalog) {
        this.catalog = catalog;
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
