package es.onebox.mgmt.salerequests.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.channels.dto.ChannelLanguagesDTO;
import es.onebox.mgmt.salerequests.enums.SaleRequestsStatus;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.time.ZonedDateTime;

public class SaleRequestDetailDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private SaleRequestsStatus status;
    private ZonedDateTime date;
    private ChannelLanguagesDTO languages;
    private ChannelSaleRequestDetailDTO channel;
    private EventSaleRequestDetailDTO event;
    @JsonProperty("subscription_list")
    private SubscriptionListSalesRequestDTO subscriptionList;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SaleRequestsStatus getStatus() {
        return status;
    }

    public void setStatus(SaleRequestsStatus status) {
        this.status = status;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public ChannelLanguagesDTO getLanguages() {
        return languages;
    }

    public void setLanguages(ChannelLanguagesDTO languages) {
        this.languages = languages;
    }

    public ChannelSaleRequestDetailDTO getChannel() {
        return channel;
    }

    public void setChannel(ChannelSaleRequestDetailDTO channel) {
        this.channel = channel;
    }

    public EventSaleRequestDetailDTO getEvent() {
        return event;
    }

    public void setEvent(EventSaleRequestDetailDTO event) {
        this.event = event;
    }

    public SubscriptionListSalesRequestDTO getSubscriptionList() {
        return subscriptionList;
    }

    public void setSubscriptionList(SubscriptionListSalesRequestDTO subscriptionList) {
        this.subscriptionList = subscriptionList;
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
