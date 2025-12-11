package es.onebox.event.catalog.dto;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.event.catalog.elasticsearch.dto.CustomersLimits;
import es.onebox.event.catalog.elasticsearch.dto.Entity;
import es.onebox.event.catalog.elasticsearch.dto.PriceZoneLimit;
import es.onebox.event.catalog.elasticsearch.dto.PriceZoneRestriction;
import es.onebox.event.catalog.elasticsearch.dto.RateRestrictions;
import es.onebox.event.catalog.elasticsearch.dto.VirtualQueue;
import es.onebox.event.catalog.elasticsearch.dto.session.PresalesSettings;
import es.onebox.event.entity.templateszones.dto.EntityTemplateZonesDTO;
import es.onebox.event.events.enums.EventType;
import es.onebox.event.loyaltypoints.sessions.dto.SessionLoyaltyPointsConfigDTO;
import es.onebox.event.packs.dto.RelatedPackDetailDTO;
import es.onebox.event.sessions.dto.external.ExternalDataDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ChannelCatalogEventSessionDTO extends IdNameDTO {

    @Serial
    private static final long serialVersionUID = 3996389765459169724L;

    private Byte status;
    private Boolean forSale;
    private Boolean soldOut;
    private Boolean purchaseChannelEvent;
    private Boolean purchaseSecondaryMarketChannelEvent;
    private Boolean preview;
    private String externalReference;
    private String reference;
    private Boolean mandatoryAttendants;
    private String timeZone;
    private Boolean isSeasonPackSession;
    private ZonedDateTime publishDate;
    private ZonedDateTime startSaleDate;
    private ZonedDateTime endSaleDate;
    private ZonedDateTime startDate;
    private ZonedDateTime endDate;
    private ZonedDateTime startBookingDate;
    private ZonedDateTime endBookingDate;
    private CatalogVenueDTO venue;
    private List<CatalogCommunicationElementDTO> communicationElements;
    private CatalogPrices prices;
    private Boolean useCaptcha;
    private Boolean showDate;
    private Boolean showDateTime;
    private Boolean showUnconfirmedDate;
    private Boolean checkOrphanSeats;
    private Boolean isGraphic;
    private Boolean noFinalDate;
    private Long sessionMaxTickets;
    private Long orderMaxTickets;
    private List<String> ipRestrictedCountries;
    private Map<Long, PriceZoneRestriction> priceZonesRestrictions;
    private Map<Long, RateRestrictions> ratesRestrictions;
    private Map<Long, Set<String>> priceTypeTags;
    private Map<Long, Set<EntityTemplateZonesDTO>> priceZonesTemplatesZones;
    private List<CatalogPriceZoneOccupationDTO> priceZoneOccupations;
    private Map<Long, PriceZoneLimit> priceZoneLimit;
    private CustomersLimits customersLimits;
    private VirtualQueue virtualQueue;
    private Entity promoter;
    private SessionPackSettingsDTO sessionPackSettingsDTO;
    private Long relatedSessionId;
    private EventType eventType;
    private Boolean smartBooking;
    private ExternalDataDTO externalData;
    private VenueTemplateType venueTemplateType;
    private List<Long> quotas;
    private SessionLoyaltyPointsConfigDTO loyaltyPointsConfig;
    private Long eventEntityId;
    private PresalesSettings presalesSettings;
    private List<CatalogTaxInfoDTO> taxes;
    private List<CatalogTaxInfoDTO> invitationTaxes;
    private List<CatalogTaxInfoDTO> surchargesTaxes;
    private ChannelTaxesDTO channelTaxes;
    private Boolean hasProducts;
    private List<RelatedPackDetailDTO> relatedPacks;

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public Boolean getForSale() {
        return forSale;
    }

    public void setForSale(Boolean forSale) {
        this.forSale = forSale;
    }

    public Boolean getPreview() {
        return preview;
    }

    public void setPreview(Boolean preview) {
        this.preview = preview;
    }

    public Boolean getSoldOut() {
        return soldOut;
    }

    public void setSoldOut(Boolean soldOut) {
        this.soldOut = soldOut;
    }

    public Boolean getPurchaseChannelEvent() {
        return purchaseChannelEvent;
    }

    public void setPurchaseChannelEvent(Boolean purchaseChannelEvent) {
        this.purchaseChannelEvent = purchaseChannelEvent;
    }

    public Boolean getPurchaseSecondaryMarketChannelEvent() {
        return purchaseSecondaryMarketChannelEvent;
    }

    public void setPurchaseSecondaryMarketChannelEvent(Boolean purchaseSecondaryMarketChannelEvent) {
        this.purchaseSecondaryMarketChannelEvent = purchaseSecondaryMarketChannelEvent;
    }

    public Boolean getMandatoryAttendants() {
        return mandatoryAttendants;
    }

    public void setMandatoryAttendants(Boolean mandatoryAttendants) {
        this.mandatoryAttendants = mandatoryAttendants;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public ZonedDateTime getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(ZonedDateTime publishDate) {
        this.publishDate = publishDate;
    }

    public ZonedDateTime getStartSaleDate() {
        return startSaleDate;
    }

    public void setStartSaleDate(ZonedDateTime startSaleDate) {
        this.startSaleDate = startSaleDate;
    }

    public ZonedDateTime getEndSaleDate() {
        return endSaleDate;
    }

    public void setEndSaleDate(ZonedDateTime endSaleDate) {
        this.endSaleDate = endSaleDate;
    }

    public ZonedDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(ZonedDateTime startDate) {
        this.startDate = startDate;
    }

    public ZonedDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(ZonedDateTime endDate) {
        this.endDate = endDate;
    }

    public ZonedDateTime getStartBookingDate() {
        return startBookingDate;
    }

    public void setStartBookingDate(ZonedDateTime startBookingDate) {
        this.startBookingDate = startBookingDate;
    }

    public ZonedDateTime getEndBookingDate() {
        return endBookingDate;
    }

    public void setEndBookingDate(ZonedDateTime endBookingDate) {
        this.endBookingDate = endBookingDate;
    }

    public String getExternalReference() {
        return externalReference;
    }

    public void setExternalReference(String externalReference) {
        this.externalReference = externalReference;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public CatalogVenueDTO getVenue() {
        return venue;
    }

    public void setVenue(CatalogVenueDTO venue) {
        this.venue = venue;
    }

    public List<CatalogCommunicationElementDTO> getCommunicationElements() {
        return communicationElements;
    }

    public void setCommunicationElements(List<CatalogCommunicationElementDTO> communicationElements) {
        this.communicationElements = communicationElements;
    }

    public CatalogPrices getPrices() {
        return prices;
    }

    public void setPrices(CatalogPrices prices) {
        this.prices = prices;
    }

    public Boolean getUseCaptcha() {
        return useCaptcha;
    }

    public void setUseCaptcha(Boolean useCaptcha) {
        this.useCaptcha = useCaptcha;
    }

    public Boolean getShowDate() {
        return showDate;
    }

    public void setShowDate(Boolean showDate) {
        this.showDate = showDate;
    }

    public Boolean getShowDateTime() {
        return showDateTime;
    }

    public void setShowDateTime(Boolean showDateTime) {
        this.showDateTime = showDateTime;
    }

    public Boolean getShowUnconfirmedDate() {
        return showUnconfirmedDate;
    }

    public void setShowUnconfirmedDate(Boolean showUnconfirmedDate) {
        this.showUnconfirmedDate = showUnconfirmedDate;
    }

    public Boolean getCheckOrphanSeats() {
        return checkOrphanSeats;
    }

    public void setCheckOrphanSeats(Boolean checkOrphanSeats) {
        this.checkOrphanSeats = checkOrphanSeats;
    }

    public Boolean getGraphic() {
        return isGraphic;
    }

    public void setGraphic(Boolean graphic) {
        isGraphic = graphic;
    }

    public Long getSessionMaxTickets() {
        return sessionMaxTickets;
    }

    public void setSessionMaxTickets(Long sessionMaxTickets) {
        this.sessionMaxTickets = sessionMaxTickets;
    }

    public Long getOrderMaxTickets() {
        return orderMaxTickets;
    }

    public void setOrderMaxTickets(Long orderMaxTickets) {
        this.orderMaxTickets = orderMaxTickets;
    }

    public List<String> getIpRestrictedCountries() {
        return ipRestrictedCountries;
    }

    public void setIpRestrictedCountries(List<String> ipRestrictedCountries) {
        this.ipRestrictedCountries = ipRestrictedCountries;
    }

    public Map<Long, PriceZoneRestriction> getPriceZonesRestrictions() {
        return priceZonesRestrictions;
    }

    public void setPriceZonesRestrictions(Map<Long, PriceZoneRestriction> priceZonesRestrictions) {
        this.priceZonesRestrictions = priceZonesRestrictions;
    }

    public Map<Long, RateRestrictions> getRatesRestrictions() {
        return ratesRestrictions;
    }

    public void setRatesRestrictions(Map<Long, RateRestrictions> ratesRestrictions) {
        this.ratesRestrictions = ratesRestrictions;
    }

    public Map<Long, Set<String>> getPriceTypeTags() {
        return priceTypeTags;
    }

    public void setPriceTypeTags(Map<Long, Set<String>> priceTypeTags) {
        this.priceTypeTags = priceTypeTags;
    }

    public void setPriceZoneOccupations(List<CatalogPriceZoneOccupationDTO> priceZonesOccupation) {
        this.priceZoneOccupations = priceZonesOccupation;
    }
    
    public List<CatalogPriceZoneOccupationDTO> getPriceZoneOccupations() {
        return priceZoneOccupations;
    }

    public Map<Long, PriceZoneLimit> getPriceZoneLimit() {
        return priceZoneLimit;
    }

    public void setPriceZoneLimit(Map<Long, PriceZoneLimit> priceZoneLimit) {
        this.priceZoneLimit = priceZoneLimit;
    }

    public CustomersLimits getCustomersLimits() {
        return customersLimits;
    }

    public void setCustomersLimits(CustomersLimits customersLimits) {
        this.customersLimits = customersLimits;
    }

    public VirtualQueue getVirtualQueue() {
        return virtualQueue;
    }

    public void setVirtualQueue(VirtualQueue virtualQueue) {
        this.virtualQueue = virtualQueue;
    }

    public Entity getPromoter() {
        return promoter;
    }

    public void setPromoter(Entity promoter) {
        this.promoter = promoter;
    }

    public Boolean getSeasonPackSession() {
        return isSeasonPackSession;
    }

    public void setSeasonPackSession(Boolean seasonPackSession) {
        isSeasonPackSession = seasonPackSession;
    }

    public Boolean getNoFinalDate() {
        return noFinalDate;
    }

    public void setNoFinalDate(Boolean noFinalDate) {
        this.noFinalDate = noFinalDate;
    }

    public SessionPackSettingsDTO getSessionPackSettings() {
        return sessionPackSettingsDTO;
    }

    public void setSessionPackSettings(SessionPackSettingsDTO sessionPackSettingsDTO) {
        this.sessionPackSettingsDTO = sessionPackSettingsDTO;
    }

    public Long getRelatedSessionId() {
        return relatedSessionId;
    }

    public void setRelatedSessionId(Long relatedSessionId) {
        this.relatedSessionId = relatedSessionId;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public Boolean getSmartBooking() {
        return smartBooking;
    }

    public void setSmartBooking(Boolean smartBooking) {
        this.smartBooking = smartBooking;
    }

    public ExternalDataDTO getExternalData() {
        return externalData;
    }

    public void setExternalData(ExternalDataDTO externalData) {
        this.externalData = externalData;
    }

    public VenueTemplateType getVenueTemplateType() {
        return venueTemplateType;
    }

    public void setVenueTemplateType(VenueTemplateType venueTemplateType) {
        this.venueTemplateType = venueTemplateType;
    }

    public List<Long> getQuotas() { return quotas; }

    public void setQuotas(List<Long> quotas) { this.quotas = quotas; }

    public SessionPackSettingsDTO getSessionPackSettingsDTO() {
        return sessionPackSettingsDTO;
    }

    public void setSessionPackSettingsDTO(SessionPackSettingsDTO sessionPackSettingsDTO) {
        this.sessionPackSettingsDTO = sessionPackSettingsDTO;
    }

    public SessionLoyaltyPointsConfigDTO getLoyaltyPointsConfig() {
        return loyaltyPointsConfig;
    }

    public void setLoyaltyPointsConfig(SessionLoyaltyPointsConfigDTO loyaltyPointsConfig) {
        this.loyaltyPointsConfig = loyaltyPointsConfig;
    }

    public PresalesSettings getPresalesSettings() {
        return presalesSettings;
    }

    public void setPresalesSettings(
        PresalesSettings presalesSettings) {
        this.presalesSettings = presalesSettings;
    }

    public Long getEventEntityId() {
        return eventEntityId;
    }

    public void setEventEntityId(Long eventEntityId) {
        this.eventEntityId = eventEntityId;
    }

    public List<CatalogTaxInfoDTO> getTaxes() {
        return taxes;
    }

    public void setTaxes(List<CatalogTaxInfoDTO> taxes) {
        this.taxes = taxes;
    }

    public List<CatalogTaxInfoDTO> getInvitationTaxes() { return invitationTaxes; }

    public void setInvitationTaxes(List<CatalogTaxInfoDTO> invitationTaxes) { this.invitationTaxes = invitationTaxes; }

    public List<CatalogTaxInfoDTO> getSurchargesTaxes() {
        return surchargesTaxes;
    }

    public void setSurchargesTaxes(List<CatalogTaxInfoDTO> surchargesTaxes) {
        this.surchargesTaxes = surchargesTaxes;
    }

    public Boolean getHasProducts() {return hasProducts;}

    public void setHasProducts(Boolean hasProducts) {this.hasProducts = hasProducts;}

    public Map<Long, Set<EntityTemplateZonesDTO>> getPriceZonesTemplatesZones() {
        return priceZonesTemplatesZones;
    }

    public void setPriceZonesTemplatesZones(Map<Long, Set<EntityTemplateZonesDTO>> priceZonesTemplatesZones) {
        this.priceZonesTemplatesZones = priceZonesTemplatesZones;
    }

    public ChannelTaxesDTO getChannelTaxes() {
        return channelTaxes;
    }

    public void setChannelTaxes(ChannelTaxesDTO channelTaxes) {
        this.channelTaxes = channelTaxes;
    }

    public List<RelatedPackDetailDTO> getRelatedPacks() {
        return relatedPacks;
    }

    public void setRelatedPacks(List<RelatedPackDetailDTO> relatedPacks) {
        this.relatedPacks = relatedPacks;
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
