package es.onebox.event.catalog.elasticsearch.context;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

import es.onebox.event.events.dao.bean.ChannelInfo;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import es.onebox.event.datasources.ms.ticket.dto.occupation.SessionOccupationVenueContainer;
import es.onebox.event.datasources.ms.ticket.dto.occupation.SessionPriceZoneOccupationDTO;

public class ChannelSessionPriceZones implements Serializable {

    @Serial
    private static final long serialVersionUID = -3414410067856686952L;

    private Long channelId;
    private ChannelInfo channel;
    private Long sessionId;
    private List<Long> quotas;
    private List<Long> priceZones;
    private List<Long> priceZonesWithAvailability;
    private List<SessionPriceZoneOccupationDTO> priceZoneOccupations;
    private List<SessionOccupationVenueContainer> containerOccupations;
    private Boolean mustBeIndexed;
    private Integer venueTemplateType;

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public List<Long> getQuotas() {
        return quotas;
    }

    public void setQuotas(List<Long> quotas) {
        this.quotas = quotas;
    }

    public List<Long> getPriceZones() {
        return priceZones;
    }

    public void setPriceZones(List<Long> priceZones) {
        this.priceZones = priceZones;
    }

    public List<Long> getPriceZonesWithAvailability() {
        return priceZonesWithAvailability;
    }

    public void setPriceZonesWithAvailability(List<Long> priceZonesWithAvailability) {
        this.priceZonesWithAvailability = priceZonesWithAvailability;
    }

    public List<SessionOccupationVenueContainer> getContainerOccupations() {
        return containerOccupations;
    }
    
    public void setPriceZoneOccupations(List<SessionPriceZoneOccupationDTO> priceZoneOccupations) {
        this.priceZoneOccupations = priceZoneOccupations;
    }
    
    public List<SessionPriceZoneOccupationDTO> getPriceZoneOccupations() {
        return priceZoneOccupations;
    }

    public void setContainerOccupations(List<SessionOccupationVenueContainer> containerOccupations) {
        this.containerOccupations = containerOccupations;
    }

    public ChannelInfo getChannel() {
        return channel;
    }

    public void setChannel(ChannelInfo channel) {
        this.channel = channel;
    }

    public Boolean getMustBeIndexed() {
        return mustBeIndexed;
    }

    public void setMustBeIndexed(Boolean mustBeIndexed) {
        this.mustBeIndexed = mustBeIndexed;
    }

    public Integer getVenueTemplateType() {
        return venueTemplateType;
    }

    public void setVenueTemplateType(Integer venueTemplateType) {
        this.venueTemplateType = venueTemplateType;
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
