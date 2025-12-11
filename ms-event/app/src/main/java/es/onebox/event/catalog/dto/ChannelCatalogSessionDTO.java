package es.onebox.event.catalog.dto;

import es.onebox.event.secondarymarket.dto.SecondaryMarketConfigDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class ChannelCatalogSessionDTO extends ChannelCatalogEventSessionDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 3996389765459169724L;

    private Long eventId;
    private String eventName;
    private List<CatalogCommunicationElementDTO> eventCommunicationElements;
    private List<ChannelCatalogRateDTO> rates;
    private Boolean ticketHandling;
    private Long venueConfigId;
    private SecondaryMarketConfigDTO secondaryMarketConfig;
    private String eventDefaultLanguage;

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventDefaultLanguage() {
        return eventDefaultLanguage;
    }

    public void setEventDefaultLanguage(String eventDefaultLanguage) {
        this.eventDefaultLanguage = eventDefaultLanguage;
    }

    public List<CatalogCommunicationElementDTO> getEventCommunicationElements() {
        return eventCommunicationElements;
    }

    public void setEventCommunicationElements(List<CatalogCommunicationElementDTO> eventCommunicationElements) {
        this.eventCommunicationElements = eventCommunicationElements;
    }

    public List<ChannelCatalogRateDTO> getRates() {
        return rates;
    }

    public void setRates(List<ChannelCatalogRateDTO> rates) {
        this.rates = rates;
    }

    public Boolean getTicketHandling() {
        return ticketHandling;
    }

    public void setTicketHandling(Boolean ticketHandling) {
        this.ticketHandling = ticketHandling;
    }

    public SecondaryMarketConfigDTO getSecondaryMarketConfig() {
        return secondaryMarketConfig;
    }

    public void setSecondaryMarketConfig(SecondaryMarketConfigDTO secondaryMarketConfig) {
        this.secondaryMarketConfig = secondaryMarketConfig;
    }

    public Long getVenueConfigId() {
        return venueConfigId;
    }

    public void setVenueConfigId(Long venueConfigId) {
        this.venueConfigId = venueConfigId;
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
