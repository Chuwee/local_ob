package es.onebox.event.catalog.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ChannelEventDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long channelId;
    private Long eventId;
    private Long channelEventId;

    //Event
    private String eventName;
    private String eventDescription;
    private Integer eventType;
    private Integer eventStatus;
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
    private String promoterRef;
    private String chargePersonName;
    private String chargePersonSurname;
    private String chargePersonEmail;
    private String chargePersonPhone;
    private String chargePersonPosition;
    private Integer eventCapacity;
    private Boolean archived;
    private Integer eventSeasonType;
    private Boolean enabledSeasonEvent;
    private Boolean enabledBookingEvent;
    private Integer typeExpirationBookingEvent;
    private Integer unitsExpirationBookingEvent;
    private Integer typeUnitsExpirationBookingEvent;
    private Integer typeLimitDateBookingEvent;
    private Integer unitsLimitBookingEvent;
    private Integer typeUnitsLimitBookingEvent;
    private Integer typeLimitBookingEvent;
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

    //Event attributes
    private List<Integer> eventAttributesId;
    private List<Integer> eventAttributesValueId;
    private List<String> attributesValue;

    //Channel
    private Long channelEntityId;
    private String channelName;
    private Integer channelEventStatus;
    private Date publishChannelEventDate;
    private Date purchaseChannelEventDate;
    private Boolean publishChannelEvent;
    private Boolean purchaseChannelEvent;
    private Boolean purchaseSecondaryMarketChannelEvent;
    private Date endChannelEventDate;
    private Boolean eventDates;
    private Date beginBookingChannelEventDate;
    private Date endBookingChannelEventDate;
    private Boolean enabledBookingChannelEvent;
    private Integer customCategoryId;
    private String customCategoryName;
    private String customCategoryCode;
    private Boolean allowChannelPromotions;

    //Venue
    private List<Long> venueEntityId;
    private List<Long> venueId;
    private Boolean multiVenue;
    private Boolean multiLocation;
    private List<VenueTemplateDTO> venueTemplates;

    //Promoter Entity
    private Integer promoterId;
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

    private Boolean mandatoryLogin;
    private Integer customerMaxSeats;

    private Integer currency;

    //Channel-Event
    private Map<String, String> communicationElements;
    private Boolean ticketHandling;

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Long getChannelEventId() {
        return channelEventId;
    }

    public void setChannelEventId(Long channelEventId) {
        this.channelEventId = channelEventId;
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

    public Integer getEventType() {
        return eventType;
    }

    public void setEventType(Integer eventType) {
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

    public List<String> getEventLanguages() {
        return eventLanguages;
    }

    public void setEventLanguages(List<String> eventLanguages) {
        this.eventLanguages = eventLanguages;
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

    public Integer getEventSeasonType() {
        return eventSeasonType;
    }

    public void setEventSeasonType(Integer eventSeasonType) {
        this.eventSeasonType = eventSeasonType;
    }

    public Boolean getEnabledSeasonEvent() {
        return enabledSeasonEvent;
    }

    public void setEnabledSeasonEvent(Boolean enabledSeasonEvent) {
        this.enabledSeasonEvent = enabledSeasonEvent;
    }

    public Boolean getEnabledBookingEvent() {
        return enabledBookingEvent;
    }

    public void setEnabledBookingEvent(Boolean enabledBookingEvent) {
        this.enabledBookingEvent = enabledBookingEvent;
    }

    public Integer getTypeExpirationBookingEvent() {
        return typeExpirationBookingEvent;
    }

    public void setTypeExpirationBookingEvent(Integer typeExpirationBookingEvent) {
        this.typeExpirationBookingEvent = typeExpirationBookingEvent;
    }

    public Integer getUnitsExpirationBookingEvent() {
        return unitsExpirationBookingEvent;
    }

    public void setUnitsExpirationBookingEvent(Integer unitsExpirationBookingEvent) {
        this.unitsExpirationBookingEvent = unitsExpirationBookingEvent;
    }

    public Integer getTypeUnitsExpirationBookingEvent() {
        return typeUnitsExpirationBookingEvent;
    }

    public void setTypeUnitsExpirationBookingEvent(Integer typeUnitsExpirationBookingEvent) {
        this.typeUnitsExpirationBookingEvent = typeUnitsExpirationBookingEvent;
    }

    public Integer getTypeLimitDateBookingEvent() {
        return typeLimitDateBookingEvent;
    }

    public void setTypeLimitDateBookingEvent(Integer typeLimitDateBookingEvent) {
        this.typeLimitDateBookingEvent = typeLimitDateBookingEvent;
    }

    public Integer getUnitsLimitBookingEvent() {
        return unitsLimitBookingEvent;
    }

    public void setUnitsLimitBookingEvent(Integer unitsLimitBookingEvent) {
        this.unitsLimitBookingEvent = unitsLimitBookingEvent;
    }

    public Integer getTypeUnitsLimitBookingEvent() {
        return typeUnitsLimitBookingEvent;
    }

    public void setTypeUnitsLimitBookingEvent(Integer typeUnitsLimitBookingEvent) {
        this.typeUnitsLimitBookingEvent = typeUnitsLimitBookingEvent;
    }

    public Integer getTypeLimitBookingEvent() {
        return typeLimitBookingEvent;
    }

    public void setTypeLimitBookingEvent(Integer typeLimitBookingEvent) {
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

    public List<String> getAttributesValue() {
        return attributesValue;
    }

    public void setAttributesValue(List<String> attributesValue) {
        this.attributesValue = attributesValue;
    }

    public Long getChannelEntityId() {
        return channelEntityId;
    }

    public void setChannelEntityId(Long channelEntityId) {
        this.channelEntityId = channelEntityId;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public Integer getChannelEventStatus() {
        return channelEventStatus;
    }

    public void setChannelEventStatus(Integer channelEventStatus) {
        this.channelEventStatus = channelEventStatus;
    }

    public Date getPublishChannelEventDate() {
        return publishChannelEventDate;
    }

    public void setPublishChannelEventDate(Date publishChannelEventDate) {
        this.publishChannelEventDate = publishChannelEventDate;
    }

    public Date getPurchaseChannelEventDate() {
        return purchaseChannelEventDate;
    }

    public void setPurchaseChannelEventDate(Date purchaseChannelEventDate) {
        this.purchaseChannelEventDate = purchaseChannelEventDate;
    }

    public Boolean getPublishChannelEvent() {
        return publishChannelEvent;
    }

    public void setPublishChannelEvent(Boolean publishChannelEvent) {
        this.publishChannelEvent = publishChannelEvent;
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

    public Date getEndChannelEventDate() {
        return endChannelEventDate;
    }

    public void setEndChannelEventDate(Date endChannelEventDate) {
        this.endChannelEventDate = endChannelEventDate;
    }

    public Boolean getEventDates() {
        return eventDates;
    }

    public void setEventDates(Boolean eventDates) {
        this.eventDates = eventDates;
    }

    public Date getBeginBookingChannelEventDate() {
        return beginBookingChannelEventDate;
    }

    public void setBeginBookingChannelEventDate(Date beginBookingChannelEventDate) {
        this.beginBookingChannelEventDate = beginBookingChannelEventDate;
    }

    public Date getEndBookingChannelEventDate() {
        return endBookingChannelEventDate;
    }

    public void setEndBookingChannelEventDate(Date endBookingChannelEventDate) {
        this.endBookingChannelEventDate = endBookingChannelEventDate;
    }

    public Boolean getEnabledBookingChannelEvent() {
        return enabledBookingChannelEvent;
    }

    public void setEnabledBookingChannelEvent(Boolean enabledBookingChannelEvent) {
        this.enabledBookingChannelEvent = enabledBookingChannelEvent;
    }

    public Integer getCustomCategoryId() {
        return customCategoryId;
    }

    public void setCustomCategoryId(Integer customCategoryId) {
        this.customCategoryId = customCategoryId;
    }

    public String getCustomCategoryName() {
        return customCategoryName;
    }

    public void setCustomCategoryName(String customCategoryName) {
        this.customCategoryName = customCategoryName;
    }

    public String getCustomCategoryCode() {
        return customCategoryCode;
    }

    public void setCustomCategoryCode(String customCategoryCode) {
        this.customCategoryCode = customCategoryCode;
    }

    public Boolean getAllowChannelPromotions() {
        return allowChannelPromotions;
    }

    public void setAllowChannelPromotions(Boolean allowChannelPromotions) {
        this.allowChannelPromotions = allowChannelPromotions;
    }

    public List<Long> getVenueEntityId() {
        return venueEntityId;
    }

    public void setVenueEntityId(List<Long> venueEntityId) {
        this.venueEntityId = venueEntityId;
    }

    public List<Long> getVenueId() {
        return venueId;
    }

    public void setVenueId(List<Long> venueId) {
        this.venueId = venueId;
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

    public Map<String, String> getCommunicationElements() {
        return communicationElements;
    }

    public void setCommunicationElements(Map<String, String> communicationElements) {
        this.communicationElements = communicationElements;
    }

    public List<VenueTemplateDTO> getVenueTemplates() {
        return venueTemplates;
    }

    public void setVenueTemplates(List<VenueTemplateDTO> venueTemplates) {
        this.venueTemplates = venueTemplates;
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

    public Integer getPromoterId() {
        return promoterId;
    }

    public void setPromoterId(Integer promoterId) {
        this.promoterId = promoterId;
    }

    public Integer getCurrency() {
        return currency;
    }

    public void setCurrency(Integer currency) {
        this.currency = currency;
    }

    public Boolean getTicketHandling() {
        return ticketHandling;
    }

    public void setTicketHandling(Boolean ticketHandling) {
        this.ticketHandling = ticketHandling;
    }
}
