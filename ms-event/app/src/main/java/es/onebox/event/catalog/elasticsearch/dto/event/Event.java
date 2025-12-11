package es.onebox.event.catalog.elasticsearch.dto.event;

import es.onebox.couchbase.annotations.CouchDocument;
import es.onebox.couchbase.annotations.Id;
import es.onebox.event.attendants.dto.AttendantsConfig;
import es.onebox.event.catalog.dto.ChangeSeatsConfig;
import es.onebox.event.catalog.dto.venue.container.VenueQuota;
import es.onebox.event.catalog.elasticsearch.dto.Entity;
import es.onebox.event.catalog.elasticsearch.dto.Promotion;
import es.onebox.event.catalog.elasticsearch.dto.RateGroup;
import es.onebox.event.catalog.elasticsearch.dto.Venue;
import es.onebox.event.catalog.elasticsearch.dto.VenueTemplatePrice;
import es.onebox.event.events.domain.eventconfig.EventWhitelabelSettings;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * User: MMolinero Date: 13/02/12 Time: 10:55
 */
@CouchDocument
public final class Event implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private Long eventId;
    private String eventName;
    private String eventDescription;
    private Byte eventType;
    private Integer eventStatus;

    //dates
    private Date purchaseEventDate;
    private String purchaseEventDateOlsonId;
    private Date beginEventDate;
    private String beginEventDateOlsonId;
    private Date endEventDate;
    private String endEventDateOlsonId;
    private Date createEventDate;
    private Date publishEventDate;
    private String publishEventDateOlsonId;
    private Date modificationEventDate;
    private Date statusModificationEventDate;

    private String eventDefaultLanguage;
    private List<String> eventLanguages;
    private Integer currency;
    private String externalReference;
    private String promoterRef;
    private String chargePersonName;
    private String chargePersonSurname;
    private String chargePersonEmail;
    private String chargePersonPhone;
    private String chargePersonPosition;
    private Integer eventCapacity;
    private Boolean archived;
    private Byte eventSeasonType;
    private Boolean enabledBookingEvent;
    private Byte typeExpirationBookingEvent;
    private Integer unitsExpirationBookingEvent;
    private Byte typeUnitsExpirationBookingEvent;
    private Byte typeLimitDateBookingEvent;
    private Integer unitsLimitBookingEvent;
    private Byte typeUnitsLimitBookingEvent;
    private Byte typeLimitBookingEvent;
    private Date limitBookingEventDate;
    private Date beginBookingEventDate;
    private String beginBookingEventDateOlsonId;
    private Date endBookingEventDate;
    private String endBookingEventDateOlsonId;
    private Boolean useCommunicationElementsTour;
    private String admissionAge;
    private String codeAdmissionAge;
    private Boolean isSupraEvent;
    private Boolean isGiftTicket;
    private Boolean useTieredPricing;
    private Long invoicePrefixId;
    private String invoicePrefix;

    //Venue
    private List<Venue> venues;
    private Boolean multiVenue;
    private Boolean multiLocation;

    //Event attributes
    private List<Integer> eventAttributesId;
    private List<Integer> eventAttributesValueId;

    //Event entity
    private Entity entity;

    //Event promoter entity
    private Entity promoter;

    private Boolean usePromoterFiscalData;

    //Promoter Entity (app-rest legacy)
    private Integer operatorId;
    private Integer operatorStatus;
    private Integer entityId;
    private String entityName;
    private String entityCorporateName;
    private Integer entityStatus;
    private Boolean entityUsesExternalManagement;
    private String entityFiscalCode;
    private String entityAddress;
    private String entityCity;
    private String entityPostalCode;
    private Integer entityCountryId;
    private String entityCountryName;
    private String entityCountryCode;
    private Integer entityCountrySubdivisionId;
    private String entityCountrySubdivisionName;
    private String entityCountrySubdivisionCode;

    //Taxonomy
    private Integer taxonomyId;
    private String taxonomyCode;
    private String taxonomyDescription;
    private Integer taxonomyParentId;
    private String taxonomyParentDescription;
    private String taxonomyParentCode;
    private Integer customTaxonomyId;
    private String customTaxonomyDescription;
    private String customTaxonomyCode;

    //User
    private Integer ownerUserId;
    private String ownerUserName;
    private Integer modifyUserId;
    private String modifyUserName;

    //Tour
    private Integer tourId;
    private String tourName;
    private String tourPromoterRef;
    private Integer tourEntityId;
    private Integer tourOperatorId;

    // Elementos de comunicacion
    private List<EventCommunicationElement> communicationElements;
    private List<EventCommunicationElement> emailCommunicationElements;

    //Promotions
    private List<Promotion> promotions;

    //Rates
    private List<EventRate> rates;
    private List<RateGroup> rateGroups;

    //Prices by venue template
    private List<VenueTemplatePrice> prices;

    //Venues Templates
    private List<Long> venueTemplateIds;
    private List<VenueQuota> venueQuotas;

    private Boolean mandatoryLogin;
    private Integer customerMaxSeats;

    //Attendants
    private List<EventAttendantField> attendantFields;
    private AttendantsConfig attendantsConfig;

    private Boolean allowChannelUseAlternativeCharges;
    private EventWhitelabelSettings whitelabelSettings;

    private SeasonPackSettings seasonPackSettings;

    private ChangeSeatsConfig changeSeatConfig;

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

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public Byte getEventType() {
        return eventType;
    }

    public void setEventType(Byte eventType) {
        this.eventType = eventType;
    }

    public Integer getEventStatus() {
        return eventStatus;
    }

    public void setEventStatus(Integer eventStatus) {
        this.eventStatus = eventStatus;
    }

    public Date getPurchaseEventDate() {
        return purchaseEventDate;
    }

    public void setPurchaseEventDate(Date purchaseEventDate) {
        this.purchaseEventDate = purchaseEventDate;
    }

    public String getPurchaseEventDateOlsonId() {
        return purchaseEventDateOlsonId;
    }

    public void setPurchaseEventDateOlsonId(String purchaseEventDateOlsonId) {
        this.purchaseEventDateOlsonId = purchaseEventDateOlsonId;
    }

    public Date getBeginEventDate() {
        return beginEventDate;
    }

    public void setBeginEventDate(Date beginEventDate) {
        this.beginEventDate = beginEventDate;
    }

    public String getBeginEventDateOlsonId() {
        return beginEventDateOlsonId;
    }

    public void setBeginEventDateOlsonId(String beginEventDateOlsonId) {
        this.beginEventDateOlsonId = beginEventDateOlsonId;
    }

    public Date getEndEventDate() {
        return endEventDate;
    }

    public void setEndEventDate(Date endEventDate) {
        this.endEventDate = endEventDate;
    }

    public String getEndEventDateOlsonId() {
        return endEventDateOlsonId;
    }

    public void setEndEventDateOlsonId(String endEventDateOlsonId) {
        this.endEventDateOlsonId = endEventDateOlsonId;
    }

    public Date getCreateEventDate() {
        return createEventDate;
    }

    public void setCreateEventDate(Date createEventDate) {
        this.createEventDate = createEventDate;
    }

    public Date getPublishEventDate() {
        return publishEventDate;
    }

    public void setPublishEventDate(Date publishEventDate) {
        this.publishEventDate = publishEventDate;
    }

    public String getPublishEventDateOlsonId() {
        return publishEventDateOlsonId;
    }

    public void setPublishEventDateOlsonId(String publishEventDateOlsonId) {
        this.publishEventDateOlsonId = publishEventDateOlsonId;
    }

    public Date getModificationEventDate() {
        return modificationEventDate;
    }

    public void setModificationEventDate(Date modificationEventDate) {
        this.modificationEventDate = modificationEventDate;
    }

    public Date getStatusModificationEventDate() {
        return statusModificationEventDate;
    }

    public void setStatusModificationEventDate(Date statusModificationEventDate) {
        this.statusModificationEventDate = statusModificationEventDate;
    }

    public String getEventDefaultLanguage() {
        return eventDefaultLanguage;
    }

    public void setEventDefaultLanguage(String eventDefaultLanguage) {
        this.eventDefaultLanguage = eventDefaultLanguage;
    }

    public Integer getCurrency() {
        return currency;
    }

    public void setCurrency(Integer currency) {
        this.currency = currency;
    }

    public List<String> getEventLanguages() {
        return eventLanguages;
    }

    public void setEventLanguages(List<String> eventLanguages) {
        this.eventLanguages = eventLanguages;
    }

    public String getExternalReference() {
        return externalReference;
    }

    public void setExternalReference(String externalReference) {
        this.externalReference = externalReference;
    }

    public String getPromoterRef() {
        return promoterRef;
    }

    public void setPromoterRef(String promoterRef) {
        this.promoterRef = promoterRef;
    }

    public String getChargePersonName() {
        return chargePersonName;
    }

    public void setChargePersonName(String chargePersonName) {
        this.chargePersonName = chargePersonName;
    }

    public String getChargePersonSurname() {
        return chargePersonSurname;
    }

    public void setChargePersonSurname(String chargePersonSurname) {
        this.chargePersonSurname = chargePersonSurname;
    }

    public String getChargePersonEmail() {
        return chargePersonEmail;
    }

    public void setChargePersonEmail(String chargePersonEmail) {
        this.chargePersonEmail = chargePersonEmail;
    }

    public String getChargePersonPhone() {
        return chargePersonPhone;
    }

    public void setChargePersonPhone(String chargePersonPhone) {
        this.chargePersonPhone = chargePersonPhone;
    }

    public String getChargePersonPosition() {
        return chargePersonPosition;
    }

    public void setChargePersonPosition(String chargePersonPosition) {
        this.chargePersonPosition = chargePersonPosition;
    }

    public Integer getEventCapacity() {
        return eventCapacity;
    }

    public void setEventCapacity(Integer eventCapacity) {
        this.eventCapacity = eventCapacity;
    }

    public Boolean getArchived() {
        return archived;
    }

    public void setArchived(Boolean archived) {
        this.archived = archived;
    }

    public Byte getEventSeasonType() {
        return eventSeasonType;
    }

    public void setEventSeasonType(Byte eventSeasonType) {
        this.eventSeasonType = eventSeasonType;
    }

    public Boolean getEnabledBookingEvent() {
        return enabledBookingEvent;
    }

    public void setEnabledBookingEvent(Boolean enabledBookingEvent) {
        this.enabledBookingEvent = enabledBookingEvent;
    }

    public Byte getTypeExpirationBookingEvent() {
        return typeExpirationBookingEvent;
    }

    public void setTypeExpirationBookingEvent(Byte typeExpirationBookingEvent) {
        this.typeExpirationBookingEvent = typeExpirationBookingEvent;
    }

    public Integer getUnitsExpirationBookingEvent() {
        return unitsExpirationBookingEvent;
    }

    public void setUnitsExpirationBookingEvent(Integer unitsExpirationBookingEvent) {
        this.unitsExpirationBookingEvent = unitsExpirationBookingEvent;
    }

    public Byte getTypeUnitsExpirationBookingEvent() {
        return typeUnitsExpirationBookingEvent;
    }

    public void setTypeUnitsExpirationBookingEvent(Byte typeUnitsExpirationBookingEvent) {
        this.typeUnitsExpirationBookingEvent = typeUnitsExpirationBookingEvent;
    }

    public Byte getTypeLimitDateBookingEvent() {
        return typeLimitDateBookingEvent;
    }

    public void setTypeLimitDateBookingEvent(Byte typeLimitDateBookingEvent) {
        this.typeLimitDateBookingEvent = typeLimitDateBookingEvent;
    }

    public Integer getUnitsLimitBookingEvent() {
        return unitsLimitBookingEvent;
    }

    public void setUnitsLimitBookingEvent(Integer unitsLimitBookingEvent) {
        this.unitsLimitBookingEvent = unitsLimitBookingEvent;
    }

    public Byte getTypeUnitsLimitBookingEvent() {
        return typeUnitsLimitBookingEvent;
    }

    public void setTypeUnitsLimitBookingEvent(Byte typeUnitsLimitBookingEvent) {
        this.typeUnitsLimitBookingEvent = typeUnitsLimitBookingEvent;
    }

    public Byte getTypeLimitBookingEvent() {
        return typeLimitBookingEvent;
    }

    public void setTypeLimitBookingEvent(Byte typeLimitBookingEvent) {
        this.typeLimitBookingEvent = typeLimitBookingEvent;
    }

    public Date getLimitBookingEventDate() {
        return limitBookingEventDate;
    }

    public void setLimitBookingEventDate(Date limitBookingEventDate) {
        this.limitBookingEventDate = limitBookingEventDate;
    }

    public Date getBeginBookingEventDate() {
        return beginBookingEventDate;
    }

    public void setBeginBookingEventDate(Date beginBookingEventDate) {
        this.beginBookingEventDate = beginBookingEventDate;
    }

    public String getBeginBookingEventDateOlsonId() {
        return beginBookingEventDateOlsonId;
    }

    public void setBeginBookingEventDateOlsonId(String beginBookingEventDateOlsonId) {
        this.beginBookingEventDateOlsonId = beginBookingEventDateOlsonId;
    }

    public Date getEndBookingEventDate() {
        return endBookingEventDate;
    }

    public void setEndBookingEventDate(Date endBookingEventDate) {
        this.endBookingEventDate = endBookingEventDate;
    }

    public String getEndBookingEventDateOlsonId() {
        return endBookingEventDateOlsonId;
    }

    public void setEndBookingEventDateOlsonId(String endBookingEventDateOlsonId) {
        this.endBookingEventDateOlsonId = endBookingEventDateOlsonId;
    }

    public Boolean getUseCommunicationElementsTour() {
        return useCommunicationElementsTour;
    }

    public void setUseCommunicationElementsTour(Boolean useCommunicationElementsTour) {
        this.useCommunicationElementsTour = useCommunicationElementsTour;
    }

    public String getAdmissionAge() {
        return admissionAge;
    }

    public void setAdmissionAge(String admissionAge) {
        this.admissionAge = admissionAge;
    }

    public String getCodeAdmissionAge() {
        return codeAdmissionAge;
    }

    public void setCodeAdmissionAge(String codeAdmissionAge) {
        this.codeAdmissionAge = codeAdmissionAge;
    }

    public Boolean getSupraEvent() {
        return isSupraEvent;
    }

    public void setSupraEvent(Boolean supraEvent) {
        isSupraEvent = supraEvent;
    }

    public Boolean getGiftTicket() {
        return isGiftTicket;
    }

    public void setGiftTicket(Boolean giftTicket) {
        isGiftTicket = giftTicket;
    }

    public Boolean getMultiVenue() {
        return multiVenue;
    }

    public void setMultiVenue(Boolean multiVenue) {
        this.multiVenue = multiVenue;
    }

    public Boolean getMultiLocation() {
        return multiLocation;
    }

    public void setMultiLocation(Boolean multiLocation) {
        this.multiLocation = multiLocation;
    }

    public Integer getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }

    public Integer getOperatorStatus() {
        return operatorStatus;
    }

    public void setOperatorStatus(Integer operatorStatus) {
        this.operatorStatus = operatorStatus;
    }

    public Integer getEntityId() {
        return entityId;
    }

    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getEntityCorporateName() {
        return entityCorporateName;
    }

    public void setEntityCorporateName(String entityCorporateName) {
        this.entityCorporateName = entityCorporateName;
    }

    public Integer getEntityStatus() {
        return entityStatus;
    }

    public void setEntityStatus(Integer entityStatus) {
        this.entityStatus = entityStatus;
    }

    public Boolean getEntityUsesExternalManagement() {
        return entityUsesExternalManagement;
    }

    public void setEntityUsesExternalManagement(Boolean entityUsesExternalManagement) {
        this.entityUsesExternalManagement = entityUsesExternalManagement;
    }

    public String getEntityFiscalCode() {
        return entityFiscalCode;
    }

    public void setEntityFiscalCode(String entityFiscalCode) {
        this.entityFiscalCode = entityFiscalCode;
    }

    public String getEntityAddress() {
        return entityAddress;
    }

    public void setEntityAddress(String entityAddress) {
        this.entityAddress = entityAddress;
    }

    public String getEntityCity() {
        return entityCity;
    }

    public void setEntityCity(String entityCity) {
        this.entityCity = entityCity;
    }

    public String getEntityPostalCode() {
        return entityPostalCode;
    }

    public void setEntityPostalCode(String entityPostalCode) {
        this.entityPostalCode = entityPostalCode;
    }

    public Integer getEntityCountryId() {
        return entityCountryId;
    }

    public void setEntityCountryId(Integer entityCountryId) {
        this.entityCountryId = entityCountryId;
    }

    public String getEntityCountryName() {
        return entityCountryName;
    }

    public void setEntityCountryName(String entityCountryName) {
        this.entityCountryName = entityCountryName;
    }

    public String getEntityCountryCode() {
        return entityCountryCode;
    }

    public void setEntityCountryCode(String entityCountryCode) {
        this.entityCountryCode = entityCountryCode;
    }

    public Integer getEntityCountrySubdivisionId() {
        return entityCountrySubdivisionId;
    }

    public void setEntityCountrySubdivisionId(Integer entityCountrySubdivisionId) {
        this.entityCountrySubdivisionId = entityCountrySubdivisionId;
    }

    public String getEntityCountrySubdivisionName() {
        return entityCountrySubdivisionName;
    }

    public void setEntityCountrySubdivisionName(String entityCountrySubdivisionName) {
        this.entityCountrySubdivisionName = entityCountrySubdivisionName;
    }

    public String getEntityCountrySubdivisionCode() {
        return entityCountrySubdivisionCode;
    }

    public void setEntityCountrySubdivisionCode(String entityCountrySubdivisionCode) {
        this.entityCountrySubdivisionCode = entityCountrySubdivisionCode;
    }

    public Integer getTaxonomyId() {
        return taxonomyId;
    }

    public void setTaxonomyId(Integer taxonomyId) {
        this.taxonomyId = taxonomyId;
    }

    public String getTaxonomyCode() {
        return taxonomyCode;
    }

    public void setTaxonomyCode(String taxonomyCode) {
        this.taxonomyCode = taxonomyCode;
    }

    public String getTaxonomyDescription() {
        return taxonomyDescription;
    }

    public void setTaxonomyDescription(String taxonomyDescription) {
        this.taxonomyDescription = taxonomyDescription;
    }

    public Integer getTaxonomyParentId() {
        return taxonomyParentId;
    }

    public void setTaxonomyParentId(Integer taxonomyParentId) {
        this.taxonomyParentId = taxonomyParentId;
    }

    public String getTaxonomyParentDescription() {
        return taxonomyParentDescription;
    }

    public void setTaxonomyParentDescription(String taxonomyParentDescription) {
        this.taxonomyParentDescription = taxonomyParentDescription;
    }

    public String getTaxonomyParentCode() {
        return taxonomyParentCode;
    }

    public void setTaxonomyParentCode(String taxonomyParentCode) {
        this.taxonomyParentCode = taxonomyParentCode;
    }

    public Integer getCustomTaxonomyId() {
        return customTaxonomyId;
    }

    public void setCustomTaxonomyId(Integer customTaxonomyId) {
        this.customTaxonomyId = customTaxonomyId;
    }

    public String getCustomTaxonomyDescription() {
        return customTaxonomyDescription;
    }

    public void setCustomTaxonomyDescription(String customTaxonomyDescription) {
        this.customTaxonomyDescription = customTaxonomyDescription;
    }

    public String getCustomTaxonomyCode() {
        return customTaxonomyCode;
    }

    public void setCustomTaxonomyCode(String customTaxonomyCode) {
        this.customTaxonomyCode = customTaxonomyCode;
    }

    public Integer getOwnerUserId() {
        return ownerUserId;
    }

    public void setOwnerUserId(Integer ownerUserId) {
        this.ownerUserId = ownerUserId;
    }

    public String getOwnerUserName() {
        return ownerUserName;
    }

    public void setOwnerUserName(String ownerUserName) {
        this.ownerUserName = ownerUserName;
    }

    public Integer getModifyUserId() {
        return modifyUserId;
    }

    public void setModifyUserId(Integer modifyUserId) {
        this.modifyUserId = modifyUserId;
    }

    public String getModifyUserName() {
        return modifyUserName;
    }

    public void setModifyUserName(String modifyUserName) {
        this.modifyUserName = modifyUserName;
    }

    public Integer getTourId() {
        return tourId;
    }

    public void setTourId(Integer tourId) {
        this.tourId = tourId;
    }

    public String getTourName() {
        return tourName;
    }

    public void setTourName(String tourName) {
        this.tourName = tourName;
    }

    public String getTourPromoterRef() {
        return tourPromoterRef;
    }

    public void setTourPromoterRef(String tourPromoterRef) {
        this.tourPromoterRef = tourPromoterRef;
    }

    public Integer getTourEntityId() {
        return tourEntityId;
    }

    public void setTourEntityId(Integer tourEntityId) {
        this.tourEntityId = tourEntityId;
    }

    public Integer getTourOperatorId() {
        return tourOperatorId;
    }

    public void setTourOperatorId(Integer tourOperatorId) {
        this.tourOperatorId = tourOperatorId;
    }

    public List<EventCommunicationElement> getCommunicationElements() {
        return communicationElements;
    }

    public void setCommunicationElements(List<EventCommunicationElement> communicationElements) {
        this.communicationElements = communicationElements;
    }

    public List<Integer> getEventAttributesId() {
        return eventAttributesId;
    }

    public void setEventAttributesId(List<Integer> eventAttributesId) {
        this.eventAttributesId = eventAttributesId;
    }

    public List<Integer> getEventAttributesValueId() {
        return eventAttributesValueId;
    }

    public void setEventAttributesValueId(List<Integer> eventAttributesValueId) {
        this.eventAttributesValueId = eventAttributesValueId;
    }

    public List<EventCommunicationElement> getEmailCommunicationElements() {
        return emailCommunicationElements;
    }

    public void setEmailCommunicationElements(List<EventCommunicationElement> emailCommunicationElements) {
        this.emailCommunicationElements = emailCommunicationElements;
    }

    public List<Promotion> getPromotions() {
        return promotions;
    }

    public void setPromotions(List<Promotion> promotions) {
        this.promotions = promotions;
    }

    public List<EventRate> getRates() {
        return rates;
    }

    public void setRates(List<EventRate> rates) {
        this.rates = rates;
    }

    public List<RateGroup> getRateGroups() {
        return rateGroups;
    }

    public void setRateGroups(List<RateGroup> rateGroups) {
        this.rateGroups = rateGroups;
    }

    public List<Venue> getVenues() {
        return venues;
    }

    public void setVenues(List<Venue> venues) {
        this.venues = venues;
    }

    public List<VenueTemplatePrice> getPrices() {
        return prices;
    }

    public void setPrices(List<VenueTemplatePrice> prices) {
        this.prices = prices;
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public Entity getPromoter() {
        return promoter;
    }

    public void setPromoter(Entity promoter) {
        this.promoter = promoter;
    }

    public Boolean getUsePromoterFiscalData() {
        return usePromoterFiscalData;
    }

    public void setUsePromoterFiscalData(Boolean usePromoterFiscalData) {
        this.usePromoterFiscalData = usePromoterFiscalData;
    }

    public Boolean getUseTieredPricing() {
        return useTieredPricing;
    }

    public void setUseTieredPricing(Boolean useTieredPricing) {
        this.useTieredPricing = useTieredPricing;
    }

    public Boolean getMandatoryLogin() { return mandatoryLogin; }

    public void setMandatoryLogin(Boolean mandatoryLogin) { this.mandatoryLogin = mandatoryLogin; }

    public Integer getCustomerMaxSeats() { return customerMaxSeats; }

    public void setCustomerMaxSeats(Integer customerMaxSeats) { this.customerMaxSeats = customerMaxSeats; }

    public Long getInvoicePrefixId() {
        return invoicePrefixId;
    }

    public void setInvoicePrefixId(Long invoicePrefixId) {
        this.invoicePrefixId = invoicePrefixId;
    }

    public String getInvoicePrefix() {
        return invoicePrefix;
    }

    public void setInvoicePrefix(String invoicePrefix) {
        this.invoicePrefix = invoicePrefix;
    }

    public List<Long> getVenueTemplateIds() {
        return venueTemplateIds;
    }

    public void setVenueTemplateIds(List<Long> venueTemplateIds) {
        this.venueTemplateIds = venueTemplateIds;
    }

    public List<VenueQuota> getVenueQuotas() {
        return venueQuotas;
    }

    public void setVenueQuotas(List<VenueQuota> venueQuotas) {
        this.venueQuotas = venueQuotas;
    }

    public List<EventAttendantField> getAttendantFields() {
        return attendantFields;
    }

    public void setAttendantFields(List<EventAttendantField> attendantFields) {
        this.attendantFields = attendantFields;
    }

    public AttendantsConfig getAttendantsConfig() {
        return attendantsConfig;
    }

    public void setAttendantsConfig(AttendantsConfig attendantsConfig) {
        this.attendantsConfig = attendantsConfig;
    }

    public Boolean getAllowChannelUseAlternativeCharges() {
        return allowChannelUseAlternativeCharges;
    }

    public void setAllowChannelUseAlternativeCharges(Boolean allowChannelUseAlternativeCharges) {
        this.allowChannelUseAlternativeCharges = allowChannelUseAlternativeCharges;
    }

    public EventWhitelabelSettings getWhitelabelSettings() {
        return whitelabelSettings;
    }

    public void setWhitelabelSettings(EventWhitelabelSettings whitelabelSettings) {
        this.whitelabelSettings = whitelabelSettings;
    }

    public SeasonPackSettings getSeasonPackSettings() {
        return seasonPackSettings;
    }

    public void setSeasonPackSettings(SeasonPackSettings seasonPackSettings) {
        this.seasonPackSettings = seasonPackSettings;
    }

    public ChangeSeatsConfig getChangeSeatConfig() {
        return changeSeatConfig;
    }

    public void setChangeSeatConfig(ChangeSeatsConfig changeSeatConfig) {
        this.changeSeatConfig = changeSeatConfig;
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
