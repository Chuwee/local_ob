package es.onebox.event.seasontickets.dto;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.event.events.dto.BookingDTO;
import es.onebox.event.events.dto.CategoryDTO;
import es.onebox.event.events.dto.DatesDTO;
import es.onebox.event.events.dto.EventLanguageDTO;
import es.onebox.event.events.dto.EventVenueViewConfigDTO;
import es.onebox.event.events.dto.TimeZoneDTO;
import es.onebox.event.events.dto.VenueDTO;
import es.onebox.event.sessions.dto.PresalesRedirectionPolicyDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

public class BaseSeasonTicketDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String promoterReference;
    private SeasonTicketStatusDTO status;
    private DatesDTO date;
    private Long entityId;
    private String entityName;
    private IdNameDTO producer;
    private List<VenueDTO> venues;
    private String contactPersonName;
    private String contactPersonSurname;
    private String contactPersonEmail;
    private String contactPersonPhone;
    private List<EventLanguageDTO> languages;
    private Integer currencyId;

    private ZonedDateTime salesStartingDate;
    private ZonedDateTime salesEndDate;
    private ZonedDateTime bookingStartingDate;
    private ZonedDateTime bookingEndDate;
    private ZonedDateTime channelPublishingDate;
    private ZonedDateTime secondaryMarketSaleStartingDate;
    private ZonedDateTime secondaryMarketSaleEndDate;
    private TimeZoneDTO venueTimeZone;
    private Boolean enableSales;
    private Boolean enableChannels;
    private Boolean bookingEnabled;
    private Boolean enableSecondaryMarketSale;
    private MaxBuyingLimitDTO maxBuyingLimit;
    private BookingDTO booking;
    private Boolean useProducerFiscalData;
    private Boolean invitationUseTicketTemplate;
    private Integer subscriptionListId;
    private Boolean enableSubscriptionList;
    private Integer salesGoalTickets;
    private BigDecimal salesGoalRevenue;
    private CategoryDTO category;
    private CategoryDTO customCategory;
    private IdNameDTO tour;
    private EventVenueViewConfigDTO eventVenueViewConfig;

    private Boolean isMemberMandatory;
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

    public SeasonTicketStatusDTO getStatus() {
        return status;
    }

    public void setStatus(SeasonTicketStatusDTO status) {
        this.status = status;
    }

    public DatesDTO getDate() {
        return date;
    }

    public void setDate(DatesDTO date) {
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

    public List<VenueDTO> getVenues() {
        return venues;
    }

    public void setVenues(List<VenueDTO> venues) {
        this.venues = venues;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
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

    public List<EventLanguageDTO> getLanguages() {
        return languages;
    }

    public void setLanguages(List<EventLanguageDTO> languages) {
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

    public ZonedDateTime getChannelPublishingDate() {
        return channelPublishingDate;
    }

    public void setChannelPublishingDate(ZonedDateTime channelPublishingDate) {
        this.channelPublishingDate = channelPublishingDate;
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

    public Boolean getEnableSecondaryMarketSale() {
        return enableSecondaryMarketSale;
    }

    public void setEnableSecondaryMarketSale(Boolean enableSecondaryMarketSale) {
        this.enableSecondaryMarketSale = enableSecondaryMarketSale;
    }

    public TimeZoneDTO getVenueTimeZone() {
        return venueTimeZone;
    }

    public void setVenueTimeZone(TimeZoneDTO venueTimeZone) {
        this.venueTimeZone = venueTimeZone;
    }

    public MaxBuyingLimitDTO getMaxBuyingLimit() {
        return maxBuyingLimit;
    }

    public void setMaxBuyingLimit(MaxBuyingLimitDTO maxBuyingLimit) {
        this.maxBuyingLimit = maxBuyingLimit;
    }

    public Boolean getMemberMandatory() {
        return isMemberMandatory;
    }

    public void setMemberMandatory(Boolean memberMandatory) {
        isMemberMandatory = memberMandatory;
    }

    public BookingDTO getBooking() {
        return booking;
    }

    public void setBooking(BookingDTO booking) {
        this.booking = booking;
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

    public Boolean getUseProducerFiscalData() {
        return useProducerFiscalData;
    }

    public void setUseProducerFiscalData(Boolean useProducerFiscalData) {
        this.useProducerFiscalData = useProducerFiscalData;
    }

    public Integer getSubscriptionListId() {return subscriptionListId;}

    public void setSubscriptionListId(Integer subscriptionListId) {this.subscriptionListId = subscriptionListId;}

    public Boolean getEnableSubscriptionList() {return enableSubscriptionList;}

    public void setEnableSubscriptionList(Boolean enableSubscriptionList) {this.enableSubscriptionList = enableSubscriptionList;}

    public Integer getSalesGoalTickets() {
        return salesGoalTickets;
    }

    public void setSalesGoalTickets(Integer salesGoalTickets) {
        this.salesGoalTickets = salesGoalTickets;
    }

    public BigDecimal getSalesGoalRevenue() {
        return salesGoalRevenue;
    }

    public void setSalesGoalRevenue(BigDecimal salesGoalRevenue) {
        this.salesGoalRevenue = salesGoalRevenue;
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

    public Boolean getInvitationUseTicketTemplate() {
        return invitationUseTicketTemplate;
    }

    public void setInvitationUseTicketTemplate(Boolean invitationUseTicketTemplate) {
        this.invitationUseTicketTemplate = invitationUseTicketTemplate;
    }

    public EventVenueViewConfigDTO getEventVenueViewConfig() {
        return eventVenueViewConfig;
    }

    public void setEventVenueViewConfig(EventVenueViewConfigDTO eventVenueViewConfig) {
        this.eventVenueViewConfig = eventVenueViewConfig;
    }

    public Integer getCurrencyId() { return currencyId; }

    public void setCurrencyId(Integer currencyId) { this.currencyId = currencyId; }

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
}
