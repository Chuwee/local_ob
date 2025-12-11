package es.onebox.event.catalog.elasticsearch.dto.session;

import es.onebox.core.serializer.dto.common.IdNameCodeDTO;
import es.onebox.couchbase.annotations.Id;
import es.onebox.event.entity.templateszones.dto.EntityTemplateZonesDTO;
import es.onebox.event.catalog.dto.venue.container.VenueQuota;
import es.onebox.event.catalog.elasticsearch.dto.CustomersLimits;
import es.onebox.event.catalog.elasticsearch.dto.Entity;
import es.onebox.event.catalog.elasticsearch.dto.PriceZoneLimit;
import es.onebox.event.catalog.elasticsearch.dto.PriceZoneRestriction;
import es.onebox.event.catalog.elasticsearch.dto.RateRestrictions;
import es.onebox.event.catalog.elasticsearch.dto.VirtualQueue;
import es.onebox.event.catalog.elasticsearch.dto.session.external.ExternalData;
import es.onebox.event.catalog.elasticsearch.dto.session.presaleconfig.PresaleConfig;
import es.onebox.event.loyaltypoints.sessions.domain.SessionLoyaltyPointsConfig;
import es.onebox.event.priceengine.taxes.domain.SessionTaxInfo;
import es.onebox.event.secondarymarket.dto.SecondaryMarketConfigDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class Session implements Serializable {

    @Serial
    private static final long serialVersionUID = 1714974044748242916L;

    @Id
    private Long sessionId;
    private Long eventId;
    private String sessionName;
    private Byte sessionStatus;
    private Date beginSessionDate;
    private Date endSessionDate;
    private Date realEndSessionDate;
    private Date publishSessionDate;
    private Date beginBookingDate;
    private Date endBookingDate;
    private Date beginAdmissionDate;
    private Date endAdmissionDate;
    private Boolean published;
    private List<SessionRate> rates;
    private Long venueId;
    private IdNameCodeDTO externalVenue;
    private Long venueConfigId;
    private IdNameCodeDTO externalVenueConfig;
    private Integer venueTemplateType;
    private Boolean isGraphic;
    private List<SessionCommunicationElement> communicationElements;
    private List<Long> promotions;
    private String eventName;
    private Byte eventStatus;
    private Long entityId;
    private Long producerId;
    private Long eventType;
    private Boolean isSeasonPackSession;
    private List<Long> relatedSeasonSessionIds;
    private Byte eventSeasonType;
    private Boolean showDate;
    private Boolean showDateTime;
    private Boolean showUnconfirmedDate;
    private Boolean noFinalDate;
    private Boolean useCaptcha;
    private Boolean allowPartialRefund;
    private Boolean showSchedule;
    private Boolean checkOrphanSeats;
    private String externalReference;
    private String reference;
    private List<VenueQuota> venueQuotas;
    private Long sessionMaxTickets;
    private Long orderMaxTickets;
    private List<String> ipRestrictedCountries;
    private Map<Long, PriceZoneRestriction> priceZonesRestrictions;
    private Map<Long, RateRestrictions> ratesRestrictions;
    private Map<Long, PriceZoneLimit> priceZoneLimit;
    private CustomersLimits customersLimits;
    private VirtualQueue virtualQueue;
    private Entity promoter;
    private String eventDefaultLanguage;
    private List<String> eventLanguages;
    private String timeZone;
    private Boolean isSmartBooking;
    private Long relatedSessionId;
    private List<PresaleConfig> presales;
    private VenueProviderConfig venueProviderConfig;
    private SessionPackSettings sessionPackSettings;
    private SecondaryMarketConfigDTO secondaryMarketConfig;
    private ExternalData externalData;
    private SessionLoyaltyPointsConfig loyaltyPointsConfig;
    private PresalesSettings presalesSettings;
    private List<SessionTaxInfo> taxes;
    private List<SessionTaxInfo> invitationTaxes;
    private List<SessionTaxInfo> surchargesTaxes;
    private Map<Long, Set<EntityTemplateZonesDTO>> entityTemplatesZonesByPriceZoneId;


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

    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public Byte getSessionStatus() {
        return sessionStatus;
    }

    public void setSessionStatus(Byte sessionStatus) {
        this.sessionStatus = sessionStatus;
    }

    public Date getBeginSessionDate() {
        return beginSessionDate;
    }

    public void setBeginSessionDate(Date beginSessionDate) {
        this.beginSessionDate = beginSessionDate;
    }

    public Date getEndSessionDate() {
        return endSessionDate;
    }

    public void setEndSessionDate(Date endSessionDate) {
        this.endSessionDate = endSessionDate;
    }

    public Date getRealEndSessionDate() {
        return realEndSessionDate;
    }

    public void setRealEndSessionDate(Date realEndSessionDate) {
        this.realEndSessionDate = realEndSessionDate;
    }

    public Date getPublishSessionDate() {
        return publishSessionDate;
    }

    public Date getBeginBookingDate() {
        return beginBookingDate;
    }

    public void setBeginBookingDate(Date beginBookingDate) {
        this.beginBookingDate = beginBookingDate;
    }

    public Date getEndBookingDate() {
        return endBookingDate;
    }

    public void setEndBookingDate(Date endBookingDate) {
        this.endBookingDate = endBookingDate;
    }

    public Date getBeginAdmissionDate() {
        return beginAdmissionDate;
    }

    public void setBeginAdmissionDate(Date beginAdmissionDate) {
        this.beginAdmissionDate = beginAdmissionDate;
    }

    public Date getEndAdmissionDate() {
        return endAdmissionDate;
    }

    public void setEndAdmissionDate(Date endAdmissionDate) {
        this.endAdmissionDate = endAdmissionDate;
    }

    public void setPublishSessionDate(Date publishSessionDate) {
        this.publishSessionDate = publishSessionDate;
    }

    public Boolean getPublished() {
        return published;
    }

    public void setPublished(Boolean published) {
        this.published = published;
    }

    public List<SessionRate> getRates() {
        return rates;
    }

    public void setRates(List<SessionRate> rates) {
        this.rates = rates;
    }

    public Long getVenueId() {
        return venueId;
    }

    public void setVenueId(Long venueId) {
        this.venueId = venueId;
    }

    public IdNameCodeDTO getExternalVenue() { return externalVenue; }

    public void setExternalVenue(IdNameCodeDTO externalVenue) { this.externalVenue = externalVenue; }

    public Long getVenueConfigId() {
        return venueConfigId;
    }

    public void setVenueConfigId(Long venueConfigId) {
        this.venueConfigId = venueConfigId;
    }

    public IdNameCodeDTO getExternalVenueConfig() { return externalVenueConfig; }

    public void setExternalVenueConfig(IdNameCodeDTO externalVenueConfig) { this.externalVenueConfig = externalVenueConfig; }

    public Integer getVenueTemplateType() {
        return venueTemplateType;
    }

    public void setVenueTemplateType(Integer venueTemplateType) {
        this.venueTemplateType = venueTemplateType;
    }

    public List<SessionCommunicationElement> getCommunicationElements() {
        return communicationElements;
    }

    public void setCommunicationElements(List<SessionCommunicationElement> communicationElements) {
        this.communicationElements = communicationElements;
    }

    public List<Long> getPromotions() {
        return promotions;
    }

    public void setPromotions(List<Long> promotions) {
        this.promotions = promotions;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public Byte getEventStatus() {
        return eventStatus;
    }

    public void setEventStatus(Byte eventStatus) {
        this.eventStatus = eventStatus;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public Long getProducerId() {
        return producerId;
    }

    public void setProducerId(Long producerId) {
        this.producerId = producerId;
    }

    public Long getEventType() {
        return eventType;
    }

    public void setEventType(Long eventType) {
        this.eventType = eventType;
    }

    public Boolean getSeasonPackSession() {
        return isSeasonPackSession;
    }

    public void setSeasonPackSession(Boolean seasonPackSession) {
        isSeasonPackSession = seasonPackSession;
    }

    public List<Long> getRelatedSeasonSessionIds() {
        return relatedSeasonSessionIds;
    }

    public void setRelatedSeasonSessionIds(List<Long> relatedSeasonSessionIds) {
        this.relatedSeasonSessionIds = relatedSeasonSessionIds;
    }

    public Byte getEventSeasonType() {
        return eventSeasonType;
    }

    public void setEventSeasonType(Byte eventSeasonType) {
        this.eventSeasonType = eventSeasonType;
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

    public Boolean getNoFinalDate() {
        return noFinalDate;
    }

    public void setNoFinalDate(Boolean noFinalDate) {
        this.noFinalDate = noFinalDate;
    }

    public Boolean getUseCaptcha() {
        return useCaptcha;
    }

    public void setUseCaptcha(Boolean useCaptcha) {
        this.useCaptcha = useCaptcha;
    }

    public Boolean getAllowPartialRefund() {
        return allowPartialRefund;
    }

    public void setAllowPartialRefund(Boolean allowPartialRefund) {
        this.allowPartialRefund = allowPartialRefund;
    }

    public Boolean getShowSchedule() {
        return showSchedule;
    }

    public void setShowSchedule(Boolean showSchedule) {
        this.showSchedule = showSchedule;
    }

    public Boolean getCheckOrphanSeats() {
        return checkOrphanSeats;
    }

    public void setCheckOrphanSeats(Boolean checkOrphanSeats) {
        this.checkOrphanSeats = checkOrphanSeats;
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

    public Boolean getGraphic() {
        return isGraphic;
    }

    public void setGraphic(Boolean graphic) {
        isGraphic = graphic;
    }

    public Entity getPromoter() {
        return promoter;
    }

    public void setPromoter(Entity promoter) {
        this.promoter = promoter;
    }

    public String getEventDefaultLanguage() {
        return eventDefaultLanguage;
    }

    public void setEventDefaultLanguage(String eventDefaultLanguage) {
        this.eventDefaultLanguage = eventDefaultLanguage;
    }

    public List<String> getEventLanguages() {
        return eventLanguages;
    }

    public void setEventLanguages(List<String> eventLanguages) {
        this.eventLanguages = eventLanguages;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }


    public void setVenueQuotas(List<VenueQuota> venueQuotas) {
        this.venueQuotas = venueQuotas;
    }

    public List<VenueQuota> getVenueQuotas() {
        return venueQuotas;
    }

    public Boolean getSmartBooking() {
        return isSmartBooking;
    }

    public void setSmartBooking(Boolean smartBooking) {
        isSmartBooking = smartBooking;
    }

    public Long getRelatedSessionId() {
        return relatedSessionId;
    }

    public void setRelatedSessionId(Long relatedSessionId) {
        this.relatedSessionId = relatedSessionId;
    }

    public List<PresaleConfig> getPresales() {
        return presales;
    }

    public void setPresales(List<PresaleConfig> presales) {
        this.presales = presales;
    }

    public VenueProviderConfig getVenueProviderConfig() {
        return venueProviderConfig;
    }

    public void setVenueProviderConfig(VenueProviderConfig venueProviderConfig) {
        this.venueProviderConfig = venueProviderConfig;
    }

    public SessionPackSettings getSessionPackSettings() {
        return sessionPackSettings;
    }

    public void setSessionPackSettings(SessionPackSettings sessionPackSettings) {
        this.sessionPackSettings = sessionPackSettings;
    }

    public SecondaryMarketConfigDTO getSecondaryMarketConfig() {
        return secondaryMarketConfig;
    }

    public void setSecondaryMarketConfig(SecondaryMarketConfigDTO secondaryMarketConfig) {
        this.secondaryMarketConfig = secondaryMarketConfig;
    }

    public ExternalData getExternalData() {
        return externalData;
    }

    public void setExternalData(ExternalData externalData) {
        this.externalData = externalData;
    }

    public SessionLoyaltyPointsConfig getLoyaltyPointsConfig() {
        return loyaltyPointsConfig;
    }

    public void setLoyaltyPointsConfig(SessionLoyaltyPointsConfig loyaltyPointsConfig) {
        this.loyaltyPointsConfig = loyaltyPointsConfig;
    }

    public PresalesSettings getPresalesSettings() {
        return presalesSettings;
    }

    public void setPresalesSettings(PresalesSettings presalesSettings) { this.presalesSettings = presalesSettings; }

    public List<SessionTaxInfo> getTaxes() {
        return taxes;
    }

    public void setTaxes(List<SessionTaxInfo> taxes) {
        this.taxes = taxes;
    }

    public List<SessionTaxInfo> getInvitationTaxes() { return invitationTaxes; }

    public void setInvitationTaxes(List<SessionTaxInfo> invitationTaxes) { this.invitationTaxes = invitationTaxes; }

    public List<SessionTaxInfo> getSurchargesTaxes() {
        return surchargesTaxes;
    }

    public void setSurchargesTaxes(List<SessionTaxInfo> surchargesTaxes) {
        this.surchargesTaxes = surchargesTaxes;
    }

    public Map<Long, Set<EntityTemplateZonesDTO>> getEntityTemplatesZonesByPriceZoneId() {
        return entityTemplatesZonesByPriceZoneId;
    }

    public void setEntityTemplatesZonesByPriceZoneId(Map<Long, Set<EntityTemplateZonesDTO>> entityTemplatesZonesByPriceZoneId) {
        this.entityTemplatesZonesByPriceZoneId = entityTemplatesZonesByPriceZoneId;
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
