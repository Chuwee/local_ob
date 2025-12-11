package es.onebox.mgmt.datasources.ms.event.dto.event;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class Event implements Serializable {

    @Serial
    private static final long serialVersionUID = 6319980049276485794L;

    private Long id;
    private String name;
    private String promoterReference;
    private EventStatus status;
    private EventType type;
    private Dates date;
    private Long entityId;
    private Long externalId;
    private String entityName;
    private String contactPersonName;
    private String contactPersonSurname;
    private String contactPersonEmail;
    private String contactPersonPhone;
    private Integer salesGoalTickets;
    private Double salesGoalRevenue;
    private Category category;
    private Category customCategory;
    private IdNameDTO tour;
    private IdNameDTO producer;
    private List<Venue> venues;
    private List<EventLanguage> languages;
    private Boolean enableSubscriptionList;
    private Integer subscriptionListId;

    private EventVenueViewConfig eventVenueViewConfig;
    private Boolean supraEvent;
    private SessionPackType sessionPackType;
    private Boolean allowVenueReport;
    private Boolean useProducerFiscalData;
    private Booking booking;
    private Boolean useTieredPricing;
    private Boolean invitationUseTicketTemplate;
    private EventTicketTemplates ticketTemplates;
    private Boolean allowGroups;
    private Integer groupPrice;
    private Boolean groupCompanionPayment;

    private EventAvetConfigType avetConfig;
    private Boolean archived;
    private Long invoicePrefixId;
    private Long currencyId;
    private EventAccommodationsConfig accommodationsConfig;
    private Provider inventoryProvider;

    private EventWhitelabelSettings whitelabelSettings;
    private Map<String, Object> externalData;
    private String externalReference;
    private EventExternalConfig eventExternalConfig;
    private TaxMode taxMode;
    private Boolean allowChangeSeat;
    private EventChangeSeat changeSeat;
    private Boolean allowTransferTicket;
    private EventTransferTicket transfer;
    private Boolean phoneVerificationRequired;
    private Boolean attendantVerificationRequired;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPromoterReference() {
        return promoterReference;
    }

    public void setPromoterReference(String promoterReference) {
        this.promoterReference = promoterReference;
    }

    public EventStatus getStatus() {
        return status;
    }

    public void setStatus(EventStatus status) {
        this.status = status;
    }

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public Dates getDate() {
        return date;
    }

    public void setDate(Dates date) {
        this.date = date;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public Long getExternalId() {
        return externalId;
    }

    public void setExternalId(Long externalId) {
        this.externalId = externalId;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getContactPersonName() {
        return contactPersonName;
    }

    public void setContactPersonName(String contactPersonName) {
        this.contactPersonName = contactPersonName;
    }

    public String getContactPersonSurname() {
        return contactPersonSurname;
    }

    public void setContactPersonSurname(String contactPersonSurname) {
        this.contactPersonSurname = contactPersonSurname;
    }

    public String getContactPersonEmail() {
        return contactPersonEmail;
    }

    public void setContactPersonEmail(String contactPersonEmail) {
        this.contactPersonEmail = contactPersonEmail;
    }

    public String getContactPersonPhone() {
        return contactPersonPhone;
    }

    public void setContactPersonPhone(String contactPersonPhone) {
        this.contactPersonPhone = contactPersonPhone;
    }

    public Integer getSalesGoalTickets() {
        return salesGoalTickets;
    }

    public void setSalesGoalTickets(Integer salesGoalTickets) {
        this.salesGoalTickets = salesGoalTickets;
    }

    public Double getSalesGoalRevenue() {
        return salesGoalRevenue;
    }

    public void setSalesGoalRevenue(Double salesGoalRevenue) {
        this.salesGoalRevenue = salesGoalRevenue;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Category getCustomCategory() {
        return customCategory;
    }

    public void setCustomCategory(Category customCategory) {
        this.customCategory = customCategory;
    }

    public IdNameDTO getTour() {
        return tour;
    }

    public void setTour(IdNameDTO tour) {
        this.tour = tour;
    }

    public IdNameDTO getProducer() {
        return producer;
    }

    public void setProducer(IdNameDTO producer) {
        this.producer = producer;
    }

    public List<Venue> getVenues() {
        return venues;
    }

    public void setVenues(List<Venue> venues) {
        this.venues = venues;
    }

    public List<EventLanguage> getLanguages() {
        return languages;
    }

    public void setLanguages(List<EventLanguage> languages) {
        this.languages = languages;
    }

    public Boolean getEnableSubscriptionList() {
        return enableSubscriptionList;
    }

    public void setEnableSubscriptionList(Boolean enableSubscriptionList) {
        this.enableSubscriptionList = enableSubscriptionList;
    }

    public Integer getSubscriptionListId() {
        return subscriptionListId;
    }

    public void setSubscriptionListId(Integer subscriptionListId) {
        this.subscriptionListId = subscriptionListId;
    }

    public EventVenueViewConfig getEventVenueViewConfig() {
        return eventVenueViewConfig;
    }

    public void setEventVenueViewConfig(EventVenueViewConfig eventVenueViewConfig) {
        this.eventVenueViewConfig = eventVenueViewConfig;
    }

    public Boolean getSupraEvent() {
        return supraEvent;
    }

    public void setSupraEvent(Boolean supraEvent) {
        this.supraEvent = supraEvent;
    }

    public SessionPackType getSessionPackType() {
        return sessionPackType;
    }

    public void setSessionPackType(SessionPackType sessionPackType) {
        this.sessionPackType = sessionPackType;
    }

    public Boolean getAllowVenueReport() {
        return allowVenueReport;
    }

    public void setAllowVenueReport(Boolean allowVenueReport) {
        this.allowVenueReport = allowVenueReport;
    }

    public Boolean getUseProducerFiscalData() {
        return useProducerFiscalData;
    }

    public Long getCurrencyId() { return currencyId; }

    public void setCurrencyId(Long currencyId) { this.currencyId = currencyId; }

    public void setUseProducerFiscalData(Boolean useProducerFiscalData) {
        this.useProducerFiscalData = useProducerFiscalData;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public Boolean getUseTieredPricing() {
        return useTieredPricing;
    }

    public void setUseTieredPricing(Boolean useTieredPricing) {
        this.useTieredPricing = useTieredPricing;
    }

    public Boolean getInvitationUseTicketTemplate() {
        return invitationUseTicketTemplate;
    }

    public void setInvitationUseTicketTemplate(Boolean invitationUseTicketTemplate) {
        this.invitationUseTicketTemplate = invitationUseTicketTemplate;
    }

    public EventTicketTemplates getTicketTemplates() {
        return ticketTemplates;
    }

    public void setTicketTemplates(EventTicketTemplates ticketTemplates) {
        this.ticketTemplates = ticketTemplates;
    }

    public Boolean getAllowGroups() {
        return allowGroups;
    }

    public void setAllowGroups(Boolean allowGroups) {
        this.allowGroups = allowGroups;
    }

    public Integer getGroupPrice() {
        return groupPrice;
    }

    public void setGroupPrice(Integer groupPrice) {
        this.groupPrice = groupPrice;
    }

    public Boolean getGroupCompanionPayment() {
        return groupCompanionPayment;
    }

    public void setGroupCompanionPayment(Boolean groupCompanionPayment) {
        this.groupCompanionPayment = groupCompanionPayment;
    }

    public EventAvetConfigType getAvetConfig() {
        return avetConfig;
    }

    public void setAvetConfig(EventAvetConfigType avetConfig) {
        this.avetConfig = avetConfig;
    }

    public Boolean getArchived() {
        return archived;
    }

    public void setArchived(Boolean archived) {
        this.archived = archived;
    }

    public Long getInvoicePrefixId() {
        return invoicePrefixId;
    }

    public void setInvoicePrefixId(Long invoicePrefixId) {
        this.invoicePrefixId = invoicePrefixId;
    }

    public Map<String, Object> getExternalData() {
        return externalData;
    }

    public void setExternalData(Map<String, Object> externalData) {
        this.externalData = externalData;
    }

    public EventAccommodationsConfig getAccommodationsConfig() {
        return accommodationsConfig;
    }

    public void setAccommodationsConfig(EventAccommodationsConfig accommodationsConfig) {
        this.accommodationsConfig = accommodationsConfig;
    }

    public Provider getInventoryProvider() {
        return inventoryProvider;
    }
    
    public void setInventoryProvider(Provider inventoryProvider) {
        this.inventoryProvider = inventoryProvider;
    }

    public EventWhitelabelSettings getWhitelabelSettings() {
        return whitelabelSettings;
    }

    public void setWhitelabelSettings(EventWhitelabelSettings whitelabelSettings) {
        this.whitelabelSettings = whitelabelSettings;
    }

    public String getExternalReference() {
        return externalReference;
    }

    public void setExternalReference(String externalReference) {
        this.externalReference = externalReference;
    }

    public EventExternalConfig getEventExternalConfig() {
        return eventExternalConfig;
    }

    public void setEventExternalConfig(EventExternalConfig eventExternalConfig) {
        this.eventExternalConfig = eventExternalConfig;
    }

    public Boolean getAllowChangeSeat() {
        return allowChangeSeat;
    }

    public void setAllowChangeSeat(Boolean allowChangeSeat) {
        this.allowChangeSeat = allowChangeSeat;
    }

    public EventChangeSeat getChangeSeat() {
        return changeSeat;
    }

    public void setChangeSeat(EventChangeSeat changeSeat) {
        this.changeSeat = changeSeat;
    }

    public TaxMode getTaxMode() {
        return taxMode;
    }

    public void setTaxMode(TaxMode taxMode) {
        this.taxMode = taxMode;
    }

    public Boolean getAllowTransferTicket() {
        return allowTransferTicket;
    }

    public void setAllowTransferTicket(Boolean allowTransferTicket) {
        this.allowTransferTicket = allowTransferTicket;
    }

    public EventTransferTicket getTransfer() {
        return transfer;
    }

    public void setTransfer(EventTransferTicket transfer) {
        this.transfer = transfer;
    }

    public Boolean getPhoneVerificationRequired() {
        return phoneVerificationRequired;
    }

    public void setPhoneVerificationRequired(Boolean phoneVerificationRequired) {
        this.phoneVerificationRequired = phoneVerificationRequired;
    }

    public Boolean getAttendantVerificationRequired() {
        return attendantVerificationRequired;
    }

    public void setAttendantVerificationRequired(Boolean attendantVerificationRequired) {
        this.attendantVerificationRequired = attendantVerificationRequired;
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
