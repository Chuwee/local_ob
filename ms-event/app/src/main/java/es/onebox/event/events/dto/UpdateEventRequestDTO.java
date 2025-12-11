package es.onebox.event.events.dto;

import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.event.events.enums.EventStatus;
import es.onebox.event.events.enums.SessionPackType;
import es.onebox.event.events.enums.TaxModeDTO;
import jakarta.validation.Valid;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class UpdateEventRequestDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String promoterReference;
    private EventStatus status;
    private String contactPersonName;
    private String contactPersonSurname;
    private String contactPersonEmail;
    private String contactPersonPhone;
    private Integer salesGoalTickets;
    private Double salesGoalRevenue;
    private IdDTO category;
    private IdDTO customCategory;
    private Long currencyId;
    private String externalReference;

    private List<EventLanguageDTO> languages;
    private Boolean enableSubscriptionList;
    private Integer subscriptionListId;
    private IdDTO tour;
    private SessionPackType sessionPackType;
    private String customSelectTemplate;

    private EventVenueViewConfigDTO eventVenueViewConfig;
    private Boolean supraEvent;
    private Boolean giftTicket;
    private Boolean allowVenueReport;
    private Boolean useProducerFiscalData;
    private Boolean useTieredPricing;
    private Boolean invitationUseTicketTemplate;

    private Boolean allowGroups;
    private Integer groupPrice;
    private Boolean groupCompanionPayment;

    private BookingDTO booking;
    private EventTicketTemplatesDTO ticketTemplates;
    private Boolean archived;
    private Integer invoicePrefixId;
    private AccommodationsConfigDTO accommodationsConfig;
    private EventWhitelabelSettingsDTO whitelabelSettings;
    private EventExternalConfigDTO eventExternalConfig;
    private TaxModeDTO taxMode;
    private Boolean allowChangeSeat;
    @Valid
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

    public IdDTO getCategory() {
        return category;
    }

    public void setCategory(IdDTO category) {
        this.category = category;
    }

    public IdDTO getCustomCategory() {
        return customCategory;
    }

    public void setCustomCategory(IdDTO customCategory) {
        this.customCategory = customCategory;
    }

    public List<EventLanguageDTO> getLanguages() {
        return languages;
    }

    public void setLanguages(List<EventLanguageDTO> languages) {
        this.languages = languages;
    }

    public IdDTO getTour() {
        return tour;
    }

    public void setTour(IdDTO tour) {
        this.tour = tour;
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

    public Integer getSubscriptionListId() {
        return subscriptionListId;
    }

    public void setSubscriptionListId(Integer subscriptionListId) {
        this.subscriptionListId = subscriptionListId;
    }

    public Boolean getEnableSubscriptionList() {
        return enableSubscriptionList;
    }

    public void setEnableSubscriptionList(Boolean enableSubscriptionList) {
        this.enableSubscriptionList = enableSubscriptionList;
    }

    public Boolean getArchived() {
        return archived;
    }

    public void setArchived(Boolean archived) {
        this.archived = archived;
    }

    public String getCustomSelectTemplate() {
        return customSelectTemplate;
    }

    public void setCustomSelectTemplate(String customSelectTemplate) {
        this.customSelectTemplate = customSelectTemplate;
    }

    public Integer getInvoicePrefixId() {
        return invoicePrefixId;
    }

    public void setInvoicePrefixId(Integer invoicePrefixId) {
        this.invoicePrefixId = invoicePrefixId;
    }

    public Long getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(Long currencyId) {
        this.currencyId = currencyId;
    }

    public AccommodationsConfigDTO getAccommodationsConfig() {
        return accommodationsConfig;
    }

    public void setAccommodationsConfig(AccommodationsConfigDTO accommodationsConfig) {
        this.accommodationsConfig = accommodationsConfig;
    }

    public EventWhitelabelSettingsDTO getWhitelabelSettings() {
        return whitelabelSettings;
    }

    public void setWhitelabelSettings(EventWhitelabelSettingsDTO whitelabelSettings) {
        this.whitelabelSettings = whitelabelSettings;
    }

    public String getExternalReference() {
        return externalReference;
    }

    public void setExternalReference(String externalReference) {
        this.externalReference = externalReference;
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
