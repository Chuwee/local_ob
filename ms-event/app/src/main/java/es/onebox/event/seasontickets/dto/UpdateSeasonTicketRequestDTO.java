package es.onebox.event.seasontickets.dto;

import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.event.events.dto.BookingDTO;
import es.onebox.event.events.dto.EventLanguageDTO;
import es.onebox.event.events.dto.EventVenueViewConfigDTO;
import es.onebox.event.events.enums.SessionPackType;
import es.onebox.event.seasontickets.dto.changeseat.UpdateSeasonTicketChangeSeat;
import es.onebox.event.seasontickets.dto.renewals.UpdateSeasonTicketRenewal;
import es.onebox.event.sessions.dto.PresalesRedirectionPolicyDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

public class UpdateSeasonTicketRequestDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 2L;

    private Long id;
    @Size(max = 50, message = "name cannot be above 50 characters")
    private String name;
    private String promoterReference;
    private String contactPersonName;
    private String contactPersonSurname;
    private String contactPersonEmail;
    private String contactPersonPhone;
    private Integer salesGoalTickets;
    private Double salesGoalRevenue;
    private IdDTO category;
    private IdDTO customCategory;
    private IdDTO tour;
    private List<EventLanguageDTO> languages;

    private ZonedDateTime salesStartingDate;
    private ZonedDateTime salesEndDate;
    private ZonedDateTime channelPublishingDate;
    private Boolean enableSales;
    private Boolean enableChannels;
    private ZonedDateTime bookingStartingDate;
    private ZonedDateTime bookingEndDate;
    private Boolean bookingEnabled;
    private Boolean enableSecondaryMarketSale;
    private ZonedDateTime secondaryMarketSaleStartingDate;
    private ZonedDateTime secondaryMarketSaleEndDate;

    private Boolean giftTicket;
    private SessionPackType sessionPackType;
    private Boolean allowGroups;
    private Boolean allowVenueReport;
    private Boolean useProducerFiscalData;

    private BookingDTO booking;

    private MaxBuyingLimitDTO maxBuyingLimit;

    private SeasonTicketTicketTemplatesDTO seasonTicketTicketTemplatesDTO;

    private Boolean memberMandatory;
    private Boolean allowRenewal;
    @Valid
    private UpdateSeasonTicketRenewal renewal;
    private Boolean allowChangeSeat;
    private UpdateSeasonTicketChangeSeat changeSeat;
    private Boolean allowTransferTicket;
    private Integer subscriptionListId;
    private Boolean enableSubscriptionList;
    private Boolean invitationUseTicketTemplate;
    private EventVenueViewConfigDTO eventVenueViewConfig;
    private Integer currencyId;
    private Boolean allowReleaseSeat;
    private Boolean registerMandatory;
    private Integer customerMaxSeats;
    private PresalesRedirectionPolicyDTO presalesRedirectionPolicy;
    private Long invoicePrefixId;

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

    public BookingDTO getBooking() {
        return booking;
    }

    public void setBooking(BookingDTO booking) {
        this.booking = booking;
    }

    public ZonedDateTime getSalesStartingDate() {
        return salesStartingDate;
    }

    public void setSalesStartingDate(ZonedDateTime salesStartingDate) {
        this.salesStartingDate = salesStartingDate;
    }

    public ZonedDateTime getSalesEndDate() {
        return salesEndDate;
    }

    public void setSalesEndDate(ZonedDateTime salesEndDate) {
        this.salesEndDate = salesEndDate;
    }

    public ZonedDateTime getChannelPublishingDate() {
        return channelPublishingDate;
    }

    public void setChannelPublishingDate(ZonedDateTime channelPublishingDate) {
        this.channelPublishingDate = channelPublishingDate;
    }

    public Boolean getEnableSales() {
        return enableSales;
    }

    public void setEnableSales(Boolean enableSales) {
        this.enableSales = enableSales;
    }

    public Boolean getEnableChannels() {
        return enableChannels;
    }

    public void setEnableChannels(Boolean enableChannels) {
        this.enableChannels = enableChannels;
    }

    public MaxBuyingLimitDTO getMaxBuyingLimit() {
        return maxBuyingLimit;
    }

    public void setMaxBuyingLimit(MaxBuyingLimitDTO maxBuyingLimit) {
        this.maxBuyingLimit = maxBuyingLimit;
    }

    public SeasonTicketTicketTemplatesDTO getSeasonTicketTicketTemplatesDTO() {
        return seasonTicketTicketTemplatesDTO;
    }

    public void setSeasonTicketTicketTemplatesDTO(SeasonTicketTicketTemplatesDTO seasonTicketTicketTemplatesDTO) {
        this.seasonTicketTicketTemplatesDTO = seasonTicketTicketTemplatesDTO;
    }

    public Boolean getMemberMandatory() {
        return memberMandatory;
    }

    public void setMemberMandatory(Boolean memberMandatory) {
        this.memberMandatory = memberMandatory;
    }

    public Boolean getAllowRenewal() {
        return allowRenewal;
    }

    public void setAllowRenewal(Boolean allowRenewal) {
        this.allowRenewal = allowRenewal;
    }

    public UpdateSeasonTicketRenewal getRenewal() {
        return renewal;
    }

    public void setRenewal(UpdateSeasonTicketRenewal renewal) {
        this.renewal = renewal;
    }

    public Boolean getAllowChangeSeat() {
        return allowChangeSeat;
    }

    public void setAllowChangeSeat(Boolean allowChangeSeat) {
        this.allowChangeSeat = allowChangeSeat;
    }

    public UpdateSeasonTicketChangeSeat getChangeSeat() {
        return changeSeat;
    }

    public void setChangeSeat(UpdateSeasonTicketChangeSeat changeSeat) {
        this.changeSeat = changeSeat;
    }

    public Boolean getAllowTransferTicket() {
        return allowTransferTicket;
    }

    public void setAllowTransferTicket(Boolean allowTransferTicket) {
        this.allowTransferTicket = allowTransferTicket;
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

    public IdDTO getTour() {
        return tour;
    }

    public void setTour(IdDTO tour) {
        this.tour = tour;
    }

    public Boolean getInvitationUseTicketTemplate() {
        return invitationUseTicketTemplate;
    }

    public void setInvitationUseTicketTemplate(Boolean invitationUseTicketTemplate) {
        this.invitationUseTicketTemplate = invitationUseTicketTemplate;
    }

    public ZonedDateTime getBookingStartingDate() {
        return bookingStartingDate;
    }

    public void setBookingStartingDate(ZonedDateTime bookingStartingDate) {
        this.bookingStartingDate = bookingStartingDate;
    }

    public ZonedDateTime getBookingEndDate() {
        return bookingEndDate;
    }

    public void setBookingEndDate(ZonedDateTime bookingEndDate) {
        this.bookingEndDate = bookingEndDate;
    }

    public Boolean getBookingEnabled() {
        return bookingEnabled;
    }

    public void setBookingEnabled(Boolean bookingEnabled) {
        this.bookingEnabled = bookingEnabled;
    }

    public Boolean getEnableSecondaryMarketSale() {
        return enableSecondaryMarketSale;
    }

    public void setEnableSecondaryMarketSale(Boolean enableSecondaryMarketSale) {
        this.enableSecondaryMarketSale = enableSecondaryMarketSale;
    }

    public ZonedDateTime getSecondaryMarketSaleStartingDate() {
        return secondaryMarketSaleStartingDate;
    }

    public void setSecondaryMarketSaleStartingDate(ZonedDateTime secondaryMarketSaleStartingDate) {
        this.secondaryMarketSaleStartingDate = secondaryMarketSaleStartingDate;
    }

    public ZonedDateTime getSecondaryMarketSaleEndDate() {
        return secondaryMarketSaleEndDate;
    }

    public void setSecondaryMarketSaleEndDate(ZonedDateTime secondaryMarketSaleEndDate) {
        this.secondaryMarketSaleEndDate = secondaryMarketSaleEndDate;
    }

    public EventVenueViewConfigDTO getEventVenueViewConfig() {
        return eventVenueViewConfig;
    }

    public void setEventVenueViewConfig(EventVenueViewConfigDTO eventVenueViewConfig) {
        this.eventVenueViewConfig = eventVenueViewConfig;
    }

    public Integer getCurrencyId() { return currencyId; }

    public void setCurrencyId(Integer currencyId) { this.currencyId = currencyId; }

    public Boolean getAllowReleaseSeat() {
        return allowReleaseSeat;
    }

    public void setAllowReleaseSeat(Boolean allowReleaseSeat) {
        this.allowReleaseSeat = allowReleaseSeat;
    }

    public Boolean getRegisterMandatory() {
        return registerMandatory;
    }

    public void setRegisterMandatory(Boolean registerMandatory) {
        this.registerMandatory = registerMandatory;
    }

    public Integer getCustomerMaxSeats() {
        return customerMaxSeats;
    }

    public void setCustomerMaxSeats(Integer customerMaxSeats) {
        this.customerMaxSeats = customerMaxSeats;
    }

    public PresalesRedirectionPolicyDTO getPresalesRedirectionPolicy() {return presalesRedirectionPolicy;}

    public void setPresalesRedirectionPolicy(PresalesRedirectionPolicyDTO presalesRedirectionPolicy) {
        this.presalesRedirectionPolicy = presalesRedirectionPolicy;
    }

    public Long getInvoicePrefixId() {
        return invoicePrefixId;
    }

    public void setInvoicePrefixId(Long invoicePrefixId) {
        this.invoicePrefixId = invoicePrefixId;
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
