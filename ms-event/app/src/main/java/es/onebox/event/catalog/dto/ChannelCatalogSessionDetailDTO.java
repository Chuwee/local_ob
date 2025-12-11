package es.onebox.event.catalog.dto;

import es.onebox.event.attendants.dto.AttendantsConfig;
import es.onebox.event.catalog.dto.presales.ChannelSessionPresaleDTO;
import es.onebox.event.catalog.dto.promotion.CatalogPromotionDTO;
import es.onebox.event.catalog.elasticsearch.dto.event.EventAttendantField;
import es.onebox.event.catalog.elasticsearch.dto.session.VenueProviderConfig;
import es.onebox.event.secondarymarket.dto.EventSecondaryMarketConfigDTO;
import es.onebox.event.secondarymarket.dto.SecondaryMarketConfigDTO;
import es.onebox.event.sessions.dto.DynamicPriceTranslationDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.util.List;

public class ChannelCatalogSessionDetailDTO extends ChannelCatalogEventSessionDTO {

    @Serial
    private static final long serialVersionUID = 503802586488399937L;

    private List<ChannelCatalogRateDTO> rates;
    private List<CatalogPromotionDTO> promotions;
    private Long venueConfigId;
    private Long eventId;
    private Integer currencyId;
    private String eventName;
    private Long channelEntityId;
    private List<ChannelSessionPresaleDTO> presales;
    private VenueProviderConfig venueProviderConfig;
    private AttendantsConfig attendantsConfig;
    private List<EventAttendantField> eventAttendantField;
    private List<CatalogCommunicationElementDTO> eventCommunicationElements;
    private Boolean ticketHandling;
    private SecondaryMarketConfigDTO secondaryMarketConfig;
    private EventSecondaryMarketConfigDTO eventSecondaryMarketConfig;
    private String eventDefaultLanguage;
    private List<DynamicPriceTranslationDTO> dynamicPriceTranslations;

    public List<ChannelCatalogRateDTO> getRates() {
        return rates;
    }

    public Long getVenueConfigId() {
        return venueConfigId;
    }

    public void setVenueConfigId(Long venueConfigId) {
        this.venueConfigId = venueConfigId;
    }

    public void setRates(List<ChannelCatalogRateDTO> rates) {
        this.rates = rates;
    }

    public List<CatalogPromotionDTO> getPromotions() {
        return promotions;
    }

    public void setPromotions(List<CatalogPromotionDTO> promotions) {
        this.promotions = promotions;
    }
    
    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Integer getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(Integer currencyId) {
        this.currencyId = currencyId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public Long getChannelEntityId() { return channelEntityId; }

    public void setChannelEntityId(Long channelEntityId) { this.channelEntityId = channelEntityId; }

    public List<ChannelSessionPresaleDTO> getPresales() {
        return presales;
    }

    public void setPresales(List<ChannelSessionPresaleDTO> presales) {
        this.presales = presales;
    }

    public VenueProviderConfig getVenueProviderConfig() {
        return venueProviderConfig;
    }

    public void setVenueProviderConfig(VenueProviderConfig venueProviderConfig) {
        this.venueProviderConfig = venueProviderConfig;
    }

    public AttendantsConfig getAttendantsConfig() {
        return attendantsConfig;
    }

    public void setAttendantsConfig(AttendantsConfig attendantsConfig) {
        this.attendantsConfig = attendantsConfig;
    }

    public List<EventAttendantField> getEventAttendantField() {
        return eventAttendantField;
    }

    public void setEventAttendantField(List<EventAttendantField> eventAttendantField) {
        this.eventAttendantField = eventAttendantField;
    }

    public List<CatalogCommunicationElementDTO> getEventCommunicationElements() {
        return eventCommunicationElements;
    }

    public void setEventCommunicationElements(List<CatalogCommunicationElementDTO> eventCommunicationElements) {
        this.eventCommunicationElements = eventCommunicationElements;
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

    public EventSecondaryMarketConfigDTO getEventSecondaryMarketConfig() {
        return eventSecondaryMarketConfig;
    }

    public void setEventSecondaryMarketConfig(EventSecondaryMarketConfigDTO eventSecondaryMarketConfig) {
        this.eventSecondaryMarketConfig = eventSecondaryMarketConfig;
    }

    public String getEventDefaultLanguage() {
        return eventDefaultLanguage;
    }

    public void setEventDefaultLanguage(String eventDefaultLanguage) {
        this.eventDefaultLanguage = eventDefaultLanguage;
    }

    public List<DynamicPriceTranslationDTO> getDynamicPriceTranslations() {
        return dynamicPriceTranslations;
    }

    public void setDynamicPriceTranslations(List<DynamicPriceTranslationDTO> dynamicPriceTranslations) {
        this.dynamicPriceTranslations = dynamicPriceTranslations;
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
