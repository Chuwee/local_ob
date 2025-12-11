package es.onebox.event.catalog.elasticsearch.dto.channelsession;

import es.onebox.couchbase.annotations.Id;
import es.onebox.event.catalog.elasticsearch.dto.ChannelCatalogSessionInfo;
import es.onebox.event.catalog.elasticsearch.dto.session.SessionCommunicationElement;
import es.onebox.event.datasources.ms.ticket.dto.occupation.SessionOccupationVenueContainer;
import es.onebox.event.datasources.ms.ticket.dto.occupation.SessionPriceZoneOccupationDTO;
import es.onebox.event.packs.dto.RelatedPackDTO;
import es.onebox.event.secondarymarket.dto.SecondaryMarketConfigDTO;
import es.onebox.event.sessions.domain.sessionconfig.SessionDynamicPriceConfig;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.util.List;
import java.util.Map;

public class ChannelSession extends ChannelCatalogSessionInfo {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(index = 1)
    private Long channelId;
    @Id(index = 2)
    private Long sessionId;
    private Long eventId;
    private List<SessionPriceZoneOccupationDTO> priceZoneOccupations;
    private List<SessionOccupationVenueContainer> containerOccupations;
    private List<Long> productIds;
    private Map<Long, RelatedPackDTO> relatedPacksByPackId;
    private Boolean isSeasonPackSession;
    private Boolean presale;
    private Boolean preview;
    private SecondaryMarketConfigDTO secondaryMarketConfig;
    private Long venueConfigId;
    private List<Long> quotas;
    private SessionDynamicPriceConfig sessionDynamicPriceConfig;
    private List<SessionCommunicationElement> communicationElements;
    private ChannelSessionCustomersLimits customersLimits;
    private ChannelTaxes channelTaxes;


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

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }
    
    public List<SessionPriceZoneOccupationDTO> getPriceZoneOccupations() {
        return priceZoneOccupations;
    }
    
    public void setPriceZoneOccupations(List<SessionPriceZoneOccupationDTO> priceZoneOccupations) {
        this.priceZoneOccupations = priceZoneOccupations;
    }

    public List<SessionOccupationVenueContainer> getContainerOccupations() {
        return containerOccupations;
    }

    public void setContainerOccupations(List<SessionOccupationVenueContainer> containerOccupations) {
        this.containerOccupations = containerOccupations;
    }

    public List<Long> getProductIds() {
        return productIds;
    }

    public void setProductIds(List<Long> productIds) {
        this.productIds = productIds;
    }

    public Boolean getSeasonPackSession() {
        return isSeasonPackSession;
    }

    public void setSeasonPackSession(Boolean seasonPackSession) {
        isSeasonPackSession = seasonPackSession;
    }

    public Boolean getPresale() {
        return presale;
    }

    public void setPresale(Boolean presale) {
        this.presale = presale;
    }

    public Boolean getPreview() {
        return preview;
    }

    public void setPreview(Boolean preview) {
        this.preview = preview;
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

    public List<Long> getQuotas() { return quotas; }

    public void setQuotas(List<Long> quotas) { this.quotas = quotas; }

    public SessionDynamicPriceConfig getSessionDynamicPriceConfig() {
        return sessionDynamicPriceConfig;
    }

    public void setSessionDynamicPriceConfig(SessionDynamicPriceConfig sessionDynamicPriceConfig) {
        this.sessionDynamicPriceConfig = sessionDynamicPriceConfig;
    }

    public List<SessionCommunicationElement> getCommunicationElements() {return communicationElements;}

    public void setCommunicationElements(List<SessionCommunicationElement> communicationElements) {
        this.communicationElements = communicationElements;
    }

    public ChannelSessionCustomersLimits getCustomersLimits() {
        return customersLimits;
    }

    public void setCustomersLimits(ChannelSessionCustomersLimits customersLimits) {
        this.customersLimits = customersLimits;
    }

    public Map<Long, RelatedPackDTO> getRelatedPacksByPackId() {
        return relatedPacksByPackId;
    }

    public void setRelatedPacksByPackId(Map<Long, RelatedPackDTO> relatedPacksByPackId) {
        this.relatedPacksByPackId = relatedPacksByPackId;
    }

    public ChannelTaxes getChannelTaxes() {
        return channelTaxes;
    }

    public void setChannelTaxes(ChannelTaxes channelTaxes) {
        this.channelTaxes = channelTaxes;
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
