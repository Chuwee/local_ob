package es.onebox.event.events.dto;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.event.events.enums.EventAvetConfigType;
import es.onebox.event.events.enums.EventStatus;
import es.onebox.event.events.enums.EventType;
import es.onebox.event.events.enums.Provider;
import es.onebox.event.events.enums.SessionPackType;
import es.onebox.event.events.enums.TaxModeDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class EventDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 3333005914965150564L;

    private Long id;
    private String name;
    private String promoterReference;
    private EventStatus status;
    private EventType type;
    private Long entityId;
    private String entityName;
    private DatesDTO date;
    private Long externalId;
    private String contactPersonName;
    private String contactPersonSurname;
    private String contactPersonEmail;
    private String contactPersonPhone;
    private Integer salesGoalTickets;
    private Double salesGoalRevenue;
    private CategoryDTO category;
    private CategoryDTO customCategory;
    private IdNameDTO tour;
    private IdNameDTO producer;
    private List<VenueDTO> venues;
    private List<EventLanguageDTO> languages;
    private Boolean enableSubscriptionList;
    private Integer subscriptionListId;
    private Integer currencyId;
    private String externalReference;

    private EventVenueViewConfigDTO eventVenueViewConfig;
    private Boolean supraEvent;
    private Boolean giftTicket;
    private SessionPackType sessionPackType;
    private Boolean allowVenueReport;
    private Boolean useProducerFiscalData;
    private Boolean useTieredPricing;
    private Boolean invitationUseTicketTemplate;

    private Boolean allowGroups;
    private Integer groupPrice;
    private Boolean groupCompanionPayment;

    private BookingDTO booking;
    private EventTicketTemplatesDTO ticketTemplates;

    private EventAvetConfigType avetConfig;
    private Boolean archived;
    private Integer invoicePrefixId;
    private AccommodationsConfigDTO accommodationsConfig;
    private Provider inventoryProvider;
    private EventWhitelabelSettingsDTO whitelabelSettings;

    private Map<String, Object> externalData;
    private EventExternalConfigDTO eventExternalConfig;
    private TaxModeDTO taxMode;
    private Boolean allowChangeSeat;
    private EventChangeSeatDTO changeSeat;
    private Boolean allowTransferTicket;
    private EventTransferTicketDTO transfer;
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

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public DatesDTO getDate() {
        return date;
    }

    public void setDate(DatesDTO date) {
        this.date = date;
    }

    public Long getExternalId() {
        return externalId;
    }

    public void setExternalId(Long externalId) {
        this.externalId = externalId;
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

    public CategoryDTO getCategory() {
        return category;
    }

    public void setCategory(CategoryDTO category) {
        this.category = category;
    }

    public CategoryDTO getCustomCategory() {
        return customCategory;
    }

    public void setCustomCategory(CategoryDTO customCategory) {
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

    public List<VenueDTO> getVenues() {
        return venues;
    }

    public void setVenues(List<VenueDTO> venues) {
        this.venues = venues;
    }

    public List<EventLanguageDTO> getLanguages() {
        return languages;
    }

    public void setLanguages(List<EventLanguageDTO> languages) {
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

    public EventVenueViewConfigDTO getEventVenueViewConfig() {
        return eventVenueViewConfig;
    }

    public void setEventVenueViewConfig(EventVenueViewConfigDTO eventVenueViewConfig) {
        this.eventVenueViewConfig = eventVenueViewConfig;
    }

    public Boolean getSupraEvent() {
        return supraEvent;
    }

    public void setSupraEvent(Boolean supraEvent) {
        this.supraEvent = supraEvent;
    }

    public Boolean getGiftTicket() {
        return giftTicket;
    }

    public void setGiftTicket(Boolean giftTicket) {
        this.giftTicket = giftTicket;
    }

    public SessionPackType getSessionPackType() {
        return sessionPackType;
    }

    public void setSessionPackType(SessionPackType sessionPackType) {
        this.sessionPackType = sessionPackType;
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

    public Integer getCurrencyId() { return currencyId; }

    public void setCurrencyId(Integer currencyId) { this.currencyId = currencyId; }

    public Boolean getAllowVenueReport() {
        return allowVenueReport;
    }

    public void setAllowVenueReport(Boolean allowVenueReport) {
        this.allowVenueReport = allowVenueReport;
    }

    public Boolean getUseProducerFiscalData() {
        return useProducerFiscalData;
    }

    public void setUseProducerFiscalData(Boolean useProducerFiscalData) {
        this.useProducerFiscalData = useProducerFiscalData;
    }

    public Boolean getUseTieredPricing() {
        return useTieredPricing;
    }

    public void setUseTieredPricing(Boolean useTieredPricing) {
        this.useTieredPricing = useTieredPricing;
    }

    public BookingDTO getBooking() {
        return booking;
    }

    public void setBooking(BookingDTO booking) {
        this.booking = booking;
    }

    public EventTicketTemplatesDTO getTicketTemplates() {
        return ticketTemplates;
    }

    public void setTicketTemplates(EventTicketTemplatesDTO ticketTemplates) {
        this.ticketTemplates = ticketTemplates;
    }

    public Boolean getInvitationUseTicketTemplate() {
        return invitationUseTicketTemplate;
    }

    public void setInvitationUseTicketTemplate(Boolean invitationUseTicketTemplate) {
        this.invitationUseTicketTemplate = invitationUseTicketTemplate;
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

    public Integer getInvoicePrefixId() {
        return invoicePrefixId;
    }

    public void setInvoicePrefixId(Integer invoicePrefixId) {
        this.invoicePrefixId = invoicePrefixId;
    }

    public AccommodationsConfigDTO getAccommodationsConfig() {
        return accommodationsConfig;
    }

    public void setAccommodationsConfig(AccommodationsConfigDTO accommodationsConfig) {
        this.accommodationsConfig = accommodationsConfig;
    }
    
    public Provider getInventoryProvider() {
        return inventoryProvider;
    }
    
    public void setInventoryProvider(Provider inventoryProvider) {
        this.inventoryProvider = inventoryProvider;
    }

    public Map<String, Object> getExternalData() {
        return externalData;
    }

    public void setExternalData(Map<String, Object> externalData) {
        this.externalData = externalData;
    }

    public EventWhitelabelSettingsDTO getWhitelabelSettings() {
        return whitelabelSettings;
    }

    public String getExternalReference() {
        return externalReference;
    }

    public void setExternalReference(String externalReference) {
        this.externalReference = externalReference;
    }

    public void setWhitelabelSettings(EventWhitelabelSettingsDTO whitelabelSettings) {
        this.whitelabelSettings = whitelabelSettings;
    }

    public EventExternalConfigDTO getEventExternalConfig() {
        return eventExternalConfig;
    }

    public void setEventExternalConfig(EventExternalConfigDTO eventExternalConfig) {
        this.eventExternalConfig = eventExternalConfig;
    }

    public Boolean getAllowChangeSeat() {
        return allowChangeSeat;
    }

    public void setAllowChangeSeat(Boolean allowChangeSeat) {
        this.allowChangeSeat = allowChangeSeat;
    }

    public EventChangeSeatDTO getChangeSeat() {
        return changeSeat;
    }

    public void setChangeSeat(EventChangeSeatDTO changeSeat) {
        this.changeSeat = changeSeat;
    }

    public TaxModeDTO getTaxMode() {
        return taxMode;
    }

    public void setTaxMode(TaxModeDTO taxMode) {
        this.taxMode = taxMode;
    }

    public Boolean getAllowTransferTicket() {
        return allowTransferTicket;
    }

    public void setAllowTransferTicket(Boolean allowTransferTicket) {
        this.allowTransferTicket = allowTransferTicket;
    }

    public EventTransferTicketDTO getTransfer() {
        return transfer;
    }

    public void setTransfer(EventTransferTicketDTO transfer) {
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
