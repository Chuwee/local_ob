package es.onebox.mgmt.datasources.ms.event.dto.seasonticket;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.datasources.common.dto.TimeZone;
import es.onebox.mgmt.datasources.ms.event.dto.event.Booking;
import es.onebox.mgmt.datasources.ms.event.dto.event.Category;
import es.onebox.mgmt.datasources.ms.event.dto.event.Dates;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventLanguage;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventVenueViewConfig;
import es.onebox.mgmt.datasources.ms.event.dto.event.Provider;
import es.onebox.mgmt.datasources.ms.event.dto.event.Venue;
import es.onebox.mgmt.datasources.ms.event.dto.session.PresalesRedirectionPolicy;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

public class SeasonTicket implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long sessionId;
    private String name;
    private String promoterReference;
    private SeasonTicketStatus status;
    private SeasonTicketInternalGenerationStatus generationStatus;
    private Dates date;
    private Long entityId;
    private String entityName;
    private IdNameDTO producer;
    private List<Venue> venues;
    private Category category;
    private Category customCategory;
    private IdNameDTO tour;
    private String contactPersonName;
    private String contactPersonSurname;
    private String contactPersonEmail;
    private String contactPersonPhone;
    private List<EventLanguage> languages;
    private ZonedDateTime bookingStartingDate;
    private ZonedDateTime bookingEndDate;
    private Boolean bookingEnabled;
    private ZonedDateTime salesStartingDate;
    private ZonedDateTime salesEndDate;
    private Boolean enableSales;
    private ZonedDateTime channelPublishingDate;
    private Boolean enableChannels;
    private TimeZone venueTimeZone;
    private MaxBuyingLimit maxBuyingLimit;
    private SeasonTicketTicketTemplates seasonTicketTicketTemplatesDTO;
    private Boolean memberMandatory;
    private Boolean allowRenewal;
    private SeasonTicketRenewal renewal;
    private Boolean allowChangeSeat;
    private SeasonTicketChangeSeat changeSeat;
    private Boolean allowTransferTicket;
    private SeasonTicketTransferTicket transfer;
    private Booking booking;
    private Integer subscriptionListId;
    private Boolean enableSubscriptionList;
    private Integer salesGoalTickets;
    private BigDecimal salesGoalRevenue;
    private Boolean useProducerFiscalData;
    private Boolean invitationUseTicketTemplate;
    private EventVenueViewConfig eventVenueViewConfig;
    private Long currencyId;
    private Boolean allowReleaseSeat;
    private Boolean enableSecondaryMarketSale;
    private ZonedDateTime secondaryMarketSaleStartingDate;
    private ZonedDateTime secondaryMarketSaleEndDate;
    private Boolean registerMandatory;
    private Integer customerMaxSeats;
    private PresalesRedirectionPolicy presalesRedirectionPolicy;
    private Long invoicePrefixId;
    private Provider inventoryProvider;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
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

    public SeasonTicketStatus getStatus() {
        return status;
    }

    public void setStatus(SeasonTicketStatus status) {
        this.status = status;
    }

    public SeasonTicketInternalGenerationStatus getGenerationStatus() {
        return generationStatus;
    }

    public void setGenerationStatus(SeasonTicketInternalGenerationStatus generationStatus) {
        this.generationStatus = generationStatus;
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

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
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

    public List<EventLanguage> getLanguages() {
        return languages;
    }

    public void setLanguages(List<EventLanguage> languages) {
        this.languages = languages;
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

    public Boolean getEnableSales() {
        return enableSales;
    }

    public void setEnableSales(Boolean enableSales) {
        this.enableSales = enableSales;
    }

    public ZonedDateTime getChannelPublishingDate() {
        return channelPublishingDate;
    }

    public void setChannelPublishingDate(ZonedDateTime channelPublishingDate) {
        this.channelPublishingDate = channelPublishingDate;
    }

    public Boolean getEnableChannels() {
        return enableChannels;
    }

    public void setEnableChannels(Boolean enableChannels) {
        this.enableChannels = enableChannels;
    }

    public TimeZone getVenueTimeZone() {
        return venueTimeZone;
    }

    public void setVenueTimeZone(TimeZone venueTimeZone) {
        this.venueTimeZone = venueTimeZone;
    }

    public MaxBuyingLimit getMaxBuyingLimit() {
        return maxBuyingLimit;
    }

    public void setMaxBuyingLimit(MaxBuyingLimit maxBuyingLimit) {
        this.maxBuyingLimit = maxBuyingLimit;
    }

    public SeasonTicketTicketTemplates getSeasonTicketTicketTemplatesDTO() {
        return seasonTicketTicketTemplatesDTO;
    }

    public void setSeasonTicketTicketTemplatesDTO(SeasonTicketTicketTemplates seasonTicketTicketTemplatesDTO) {
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

    public SeasonTicketRenewal getRenewal() {
        return renewal;
    }

    public void setRenewal(SeasonTicketRenewal renewal) {
        this.renewal = renewal;
    }

    public Boolean getAllowChangeSeat() {
        return allowChangeSeat;
    }

    public void setAllowChangeSeat(Boolean allowChangeSeat) {
        this.allowChangeSeat = allowChangeSeat;
    }

    public SeasonTicketChangeSeat getChangeSeat() {
        return changeSeat;
    }

    public void setChangeSeat(SeasonTicketChangeSeat changeSeat) {
        this.changeSeat = changeSeat;
    }

    public Boolean getAllowTransferTicket() {
        return allowTransferTicket;
    }

    public void setAllowTransferTicket(Boolean allowTransferTicket) {
        this.allowTransferTicket = allowTransferTicket;
    }

    public SeasonTicketTransferTicket getTransfer() {
        return transfer;
    }

    public void setTransfer(SeasonTicketTransferTicket transfer) {
        this.transfer = transfer;
    }

    public Integer getSubscriptionListId() {return subscriptionListId;}

    public void setSubscriptionListId(Integer subscriptionListId) {this.subscriptionListId = subscriptionListId;}

    public Boolean getEnableSubscriptionList() {
        return enableSubscriptionList;
    }

    public void setEnableSubscriptionList(Boolean enableSubscriptionList) {
        this.enableSubscriptionList = enableSubscriptionList;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public Boolean getUseProducerFiscalData() {
        return useProducerFiscalData;
    }

    public void setUseProducerFiscalData(Boolean useProducerFiscalData) {
        this.useProducerFiscalData = useProducerFiscalData;
    }

    public BigDecimal getSalesGoalRevenue() {
        return salesGoalRevenue;
    }

    public void setSalesGoalRevenue(BigDecimal salesGoalRevenue) {
        this.salesGoalRevenue = salesGoalRevenue;
    }

    public Integer getSalesGoalTickets() {
        return salesGoalTickets;
    }

    public void setSalesGoalTickets(Integer salesGoalTickets) {
        this.salesGoalTickets = salesGoalTickets;
    }

    public IdNameDTO getTour() {return tour;}

    public void setTour(IdNameDTO tour) {this.tour = tour;}

    public ZonedDateTime getBookingEndDate() {
        return bookingEndDate;
    }

    public void setBookingEndDate(ZonedDateTime bookingEndDate) {
        this.bookingEndDate = bookingEndDate;
    }

    public ZonedDateTime getBookingStartingDate() {
        return bookingStartingDate;
    }

    public void setBookingStartingDate(ZonedDateTime bookingStartingDate) {
        this.bookingStartingDate = bookingStartingDate;
    }

    public Boolean getBookingEnabled() {
        return bookingEnabled;
    }

    public void setBookingEnabled(Boolean bookingEnabled) {
        this.bookingEnabled = bookingEnabled;
    }

    public Boolean getInvitationUseTicketTemplate() {
        return invitationUseTicketTemplate;
    }

    public void setInvitationUseTicketTemplate(Boolean invitationUseTicketTemplate) {
        this.invitationUseTicketTemplate = invitationUseTicketTemplate;
    }

    public EventVenueViewConfig getEventVenueViewConfig() {
        return eventVenueViewConfig;
    }

    public void setEventVenueViewConfig(EventVenueViewConfig eventVenueViewConfig) {
        this.eventVenueViewConfig = eventVenueViewConfig;
    }

    public Long getCurrencyId() { return currencyId; }

    public void setCurrencyId(Long currencyId) { this.currencyId = currencyId; }
    public Boolean getAllowReleaseSeat() {
        return allowReleaseSeat;
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

    public Provider getInventoryProvider() {
        return inventoryProvider;
    }

    public void setInventoryProvider(Provider inventoryProvider) {
        this.inventoryProvider = inventoryProvider;
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

    public PresalesRedirectionPolicy getPresalesRedirectionPolicy() {return presalesRedirectionPolicy;}

    public void setPresalesRedirectionPolicy(PresalesRedirectionPolicy presalesRedirectionPolicy) {
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
