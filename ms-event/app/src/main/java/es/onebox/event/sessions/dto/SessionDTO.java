package es.onebox.event.sessions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.event.events.dto.TimeZoneDTO;
import es.onebox.event.events.dto.statusflag.SessionReleaseFlagStatus;
import es.onebox.event.events.dto.statusflag.SessionSaleFlagStatus;
import es.onebox.event.events.enums.EventType;
import es.onebox.event.sessions.enums.AccessScheduleType;
import es.onebox.event.sessions.enums.SessionType;
import es.onebox.event.sessions.enums.SessionVirtualQueueVersion;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class SessionDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 5836856831951393005L;

    private Long id;
    private String name;
    private SessionStatus status;
    private SessionGenerationStatus generationStatus;
    private SessionSaleFlagStatus sale;
    private SessionReleaseFlagStatus release;

    @JsonProperty("type")
    private SessionType sessionType;

    private SessionDateDTO date;

    private List<RateDTO> rates;
    private IdNameDTO ticketTax;
    private IdNameDTO chargesTax;

    private Integer saleType;
    private AccessScheduleType accessScheduleType;
    private IdNameDTO space;

    private Long eventId;
    private String eventName;
    private EventType eventType;

    private Long entityId;
    private String entityName;

    private Long venueConfigId;
    private String venueConfigName;
    private Integer venueConfigTemplateType;
    private Boolean venueConfigGraphic;
    private Long venueConfigSpaceId;
    private String venueConfigSpaceName;
    private Boolean useVenueConfigCapacity;
    private Boolean useTemplateAccess;
    private Long venueId;
    private String venueName;
    private Long capacity;
    private String city;
    private Long countryId;
    @JsonProperty("timezone")
    private TimeZoneDTO timeZone;

    private Boolean enableChannels;
    private Boolean enableBookings;
    private Boolean enableSales;
    private Boolean enableCaptcha;
    private Boolean enableShowDateInChannels;
    private Boolean enableShowTimeInChannels;
    private Boolean enableShowUnconfirmedDateInChannels;
    private Boolean enableSessionTicketLimit;
    private Integer sessionTicketLimit;
    private Boolean enableOrphanSeats;
    private Boolean enableSecondaryMarket;

    private List<Long> sessionIds;
    private List<Long> seasonIds;
    private Long externalId;
    private Boolean external;

    private Boolean enableMembersLoginsLimit;
    private Integer membersLoginsLimit;
    private List<SessionPreSaleConfigDTO> preSales;

    private SessionStreamingDTO streaming;

    private Boolean showDate;
    private Boolean showDatetime;
    private Boolean showUnconfirmedDate;
    private Boolean unconfirmedDate;

    private Boolean enableCountryFilter;
    private List<String> countries;

    private Boolean enableProducerTaxData;
    private Integer producerId;
    private Integer invoicePrefixId;
    private Boolean enableQueue;
    private String queueAlias;
    private String skipQueueToken;
    private SessionVirtualQueueVersion queueVersion;

    private Boolean enableSubscriptionList;
    private Integer subscriptionListId;

    private String color;
    private Boolean allowPartialRefund;

    private String reference;

    private Boolean presaleEnabled;
    private String publicationCancelledReason;
    private Boolean archived;
    private Map<String,Object> externalData;

    private Boolean isSmartBooking;
    private Long smartBookingRelatedId;

    private String externalReference;

    private PresalesRedirectionPolicyDTO presalesRedirectionPolicy;

    private Boolean highDemand;

    private SessionExternalConfigDTO sessionExternalConfig;

    private Boolean useDynamicPrices;

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

    public SessionStatus getStatus() {
        return status;
    }

    public void setStatus(SessionStatus status) {
        this.status = status;
    }

    public SessionType getSessionType() {
        return sessionType;
    }

    public void setSessionType(SessionType sessionType) {
        this.sessionType = sessionType;
    }

    public SessionDateDTO getDate() {
        return date;
    }

    public void setDate(SessionDateDTO date) {
        this.date = date;
    }

    public List<RateDTO> getRates() {
        return rates;
    }

    public void setRates(List<RateDTO> rates) {
        this.rates = rates;
    }

    public IdNameDTO getTicketTax() {
        return ticketTax;
    }

    public void setTicketTax(IdNameDTO ticketTax) {
        this.ticketTax = ticketTax;
    }

    public IdNameDTO getChargesTax() {
        return chargesTax;
    }

    public void setChargesTax(IdNameDTO chargesTax) {
        this.chargesTax = chargesTax;
    }

    public Integer getSaleType() {
        return saleType;
    }

    public void setSaleType(Integer saleType) {
        this.saleType = saleType;
    }

    public AccessScheduleType getAccessScheduleType() {
        return accessScheduleType;
    }

    public void setAccessScheduleType(AccessScheduleType accessScheduleType) {
        this.accessScheduleType = accessScheduleType;
    }

    public List<Long> getSessionIds() {
        return sessionIds;
    }

    public void setSessionIds(List<Long> sessionIds) {
        this.sessionIds = sessionIds;
    }

    public List<Long> getSeasonIds() {
        return seasonIds;
    }

    public void setSeasonIds(List<Long> seasonIds) {
        this.seasonIds = seasonIds;
    }

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

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
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

    public Long getVenueConfigId() {
        return venueConfigId;
    }

    public void setVenueConfigId(Long venueConfigId) {
        this.venueConfigId = venueConfigId;
    }

    public String getVenueConfigName() {
        return venueConfigName;
    }

    public void setVenueConfigName(String venueConfigName) {
        this.venueConfigName = venueConfigName;
    }

    public Integer getVenueConfigTemplateType() {
        return venueConfigTemplateType;
    }

    public void setVenueConfigTemplateType(Integer venueConfigTemplateType) {
        this.venueConfigTemplateType = venueConfigTemplateType;
    }

    public Long getVenueConfigSpaceId() {
        return venueConfigSpaceId;
    }

    public Boolean getVenueConfigGraphic() {
        return venueConfigGraphic;
    }

    public void setVenueConfigGraphic(Boolean venueConfigGraphic) {
        this.venueConfigGraphic = venueConfigGraphic;
    }

    public void setVenueConfigSpaceId(Long venueConfigSpaceId) {
        this.venueConfigSpaceId = venueConfigSpaceId;
    }

    public String getVenueConfigSpaceName() {
        return venueConfigSpaceName;
    }

    public void setVenueConfigSpaceName(String venueConfigSpaceName) {
        this.venueConfigSpaceName = venueConfigSpaceName;
    }

    public Boolean getUseVenueConfigCapacity() {
        return useVenueConfigCapacity;
    }

    public void setUseVenueConfigCapacity(Boolean useVenueConfigCapacity) {
        this.useVenueConfigCapacity = useVenueConfigCapacity;
    }

    public Long getVenueId() {
        return venueId;
    }

    public void setVenueId(Long venueId) {
        this.venueId = venueId;
    }

    public String getVenueName() {
        return venueName;
    }

    public void setVenueName(String venueName) {
        this.venueName = venueName;
    }

    public Long getCapacity() {
        return capacity;
    }

    public void setCapacity(Long capacity) {
        this.capacity = capacity;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public TimeZoneDTO getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(TimeZoneDTO timeZone) {
        this.timeZone = timeZone;
    }

    public Boolean getEnableChannels() {
        return enableChannels;
    }

    public void setEnableChannels(Boolean enableChannels) {
        this.enableChannels = enableChannels;
    }

    public Boolean getEnableBookings() {
        return enableBookings;
    }

    public void setEnableBookings(Boolean enableBookings) {
        this.enableBookings = enableBookings;
    }

    public Boolean getEnableSales() {
        return enableSales;
    }

    public void setEnableSales(Boolean enableSales) {
        this.enableSales = enableSales;
    }

    public Boolean getEnableCaptcha() {
        return enableCaptcha;
    }

    public void setEnableCaptcha(Boolean enableCaptcha) {
        this.enableCaptcha = enableCaptcha;
    }

    public IdNameDTO getSpace() {
        return space;
    }

    public void setSpace(IdNameDTO space) {
        this.space = space;
    }

    public SessionSaleFlagStatus getSale() {
        return sale;
    }

    public void setSale(SessionSaleFlagStatus sale) {
        this.sale = sale;
    }

    public SessionReleaseFlagStatus getRelease() {
        return release;
    }

    public void setRelease(SessionReleaseFlagStatus release) {
        this.release = release;
    }

    public void setExternalId(Long externalId) {
        this.externalId = externalId;
    }

    public Long getExternalId() {
        return externalId;
    }

    public SessionGenerationStatus getGenerationStatus() {
        return generationStatus;
    }

    public void setGenerationStatus(SessionGenerationStatus generationStatus) {
        this.generationStatus = generationStatus;
    }

    public Boolean getEnableMembersLoginsLimit() {
        return enableMembersLoginsLimit;
    }

    public void setEnableMembersLoginsLimit(Boolean enableMembersLoginsLimit) {
        this.enableMembersLoginsLimit = enableMembersLoginsLimit;
    }

    public Integer getMembersLoginsLimit() {
        return membersLoginsLimit;
    }

    public void setMembersLoginsLimit(Integer membersLoginsLimit) {
        this.membersLoginsLimit = membersLoginsLimit;
    }

    public List<SessionPreSaleConfigDTO> getPreSales() {
        return preSales;
    }

    public void setPreSales(List<SessionPreSaleConfigDTO> preSales) {
        this.preSales = preSales;
    }

    public SessionStreamingDTO getStreaming() {
        return streaming;
    }

    public void setStreaming(SessionStreamingDTO streaming) {
        this.streaming = streaming;
    }

    public Boolean getShowDate() {
        return showDate;
    }

    public void setShowDate(Boolean showDate) {
        this.showDate = showDate;
    }

    public Boolean getShowDatetime() {
        return showDatetime;
    }

    public void setShowDatetime(Boolean showDatetime) {
        this.showDatetime = showDatetime;
    }

    public Boolean getShowUnconfirmedDate() {
        return showUnconfirmedDate;
    }

    public void setShowUnconfirmedDate(Boolean showUnconfirmedDate) {
        this.showUnconfirmedDate = showUnconfirmedDate;
    }

    public Boolean getUnconfirmedDate() {
        return unconfirmedDate;
    }

    public void setUnconfirmedDate(Boolean unconfirmedDate) {
        this.unconfirmedDate = unconfirmedDate;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Boolean getUseTemplateAccess() {
        return useTemplateAccess;
    }

    public void setUseTemplateAccess(Boolean useTemplateAccess) {
        this.useTemplateAccess = useTemplateAccess;
    }

    public Boolean getEnableShowDateInChannels() {
        return enableShowDateInChannels;
    }

    public void setEnableShowDateInChannels(Boolean enableShowDateInChannels) {
        this.enableShowDateInChannels = enableShowDateInChannels;
    }

    public Boolean getEnableShowTimeInChannels() {
        return enableShowTimeInChannels;
    }

    public void setEnableShowTimeInChannels(Boolean enableShowTimeInChannels) {
        this.enableShowTimeInChannels = enableShowTimeInChannels;
    }

    public Boolean getEnableShowUnconfirmedDateInChannels() {
        return enableShowUnconfirmedDateInChannels;
    }

    public void setEnableShowUnconfirmedDateInChannels(Boolean enableShowUnconfirmedDateInChannels) {
        this.enableShowUnconfirmedDateInChannels = enableShowUnconfirmedDateInChannels;
    }

    public Boolean getEnableSessionTicketLimit() {
        return enableSessionTicketLimit;
    }

    public void setEnableSessionTicketLimit(Boolean enableSessionTicketLimit) {
        this.enableSessionTicketLimit = enableSessionTicketLimit;
    }

    public Integer getSessionTicketLimit() {
        return sessionTicketLimit;
    }

    public void setSessionTicketLimit(Integer sessionTicketLimit) {
        this.sessionTicketLimit = sessionTicketLimit;
    }

    public Boolean getEnableOrphanSeats() {
        return enableOrphanSeats;
    }

    public void setEnableOrphanSeats(Boolean enableOrphanSeats) {
        this.enableOrphanSeats = enableOrphanSeats;
    }

    public Boolean getEnableSecondaryMarket() {
        return enableSecondaryMarket;
    }

    public void setEnableSecondaryMarket(Boolean enableSecondaryMarket) {
        this.enableSecondaryMarket = enableSecondaryMarket;
    }

    public Boolean getEnableCountryFilter() {
        return enableCountryFilter;
    }

    public void setEnableCountryFilter(Boolean enableCountryFilter) {
        this.enableCountryFilter = enableCountryFilter;
    }

    public List<String> getCountries() {
        return countries;
    }

    public void setCountries(List<String> countries) {
        this.countries = countries;
    }

    public Boolean getEnableProducerTaxData() {
        return enableProducerTaxData;
    }

    public void setEnableProducerTaxData(Boolean enableProducerTaxData) {
        this.enableProducerTaxData = enableProducerTaxData;
    }

    public Integer getProducerId() {
        return producerId;
    }

    public void setProducerId(Integer producerId) {
        this.producerId = producerId;
    }

    public Integer getInvoicePrefixId() {
        return invoicePrefixId;
    }

    public void setInvoicePrefixId(Integer invoicePrefixId) {
        this.invoicePrefixId = invoicePrefixId;
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

    public Boolean getEnableQueue() {
        return enableQueue;
    }

    public void setEnableQueue(Boolean enableQueue) {
        this.enableQueue = enableQueue;
    }

    public String getQueueAlias() {
        return queueAlias;
    }

    public void setQueueAlias(String queueAlias) {
        this.queueAlias = queueAlias;
    }

    public String getSkipQueueToken() {
        return skipQueueToken;
    }

    public void setSkipQueueToken(String skipQueueToken) {
        this.skipQueueToken = skipQueueToken;
    }

    public SessionVirtualQueueVersion getQueueVersion() {
        return queueVersion;
    }

    public void setQueueVersion(SessionVirtualQueueVersion queueVersion) {
        this.queueVersion = queueVersion;
    }

    public Boolean getExternal() {
        return external;
    }

    public void setExternal(Boolean external) {
        this.external = external;
    }

    public Boolean getAllowPartialRefund() {
        return allowPartialRefund;
    }

    public void setAllowPartialRefund(Boolean allowPartialRefund) {
        this.allowPartialRefund = allowPartialRefund;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getPublicationCancelledReason() {
        return publicationCancelledReason;
    }

    public void setPublicationCancelledReason(String publicationCancelledReason) {
        this.publicationCancelledReason = publicationCancelledReason;
    }

    public Boolean getArchived() {
        return archived;
    }

    public void setArchived(Boolean archived) {
        this.archived = archived;
    }

    public Boolean getPresaleEnabled() {
        return presaleEnabled;
    }

    public void setPresaleEnabled(Boolean presaleEnabled) {
        this.presaleEnabled = presaleEnabled;
    }

    public Map<String,Object> getExternalData() {
        return externalData;
    }

    public void setExternalData(Map<String,Object> externalData) {
        this.externalData = externalData;
    }

    public Boolean getIsSmartBooking() {
        return isSmartBooking;
    }

    public void setIsSmartBooking(Boolean isSmartBooking) {
        this.isSmartBooking = isSmartBooking;
    }

    public Long getSmartBookingRelatedId() {
        return smartBookingRelatedId;
    }

    public void setSmartBookingRelatedId(Long smartBookingRelatedId) {
        this.smartBookingRelatedId = smartBookingRelatedId;
    }

    public String getExternalReference() {
        return externalReference;
    }

    public void setExternalReference(String externalReference) {
        this.externalReference = externalReference;
    }

    public PresalesRedirectionPolicyDTO getPresalesRedirectionPolicy() {return presalesRedirectionPolicy;}

    public void setPresalesRedirectionPolicy(PresalesRedirectionPolicyDTO presalesRedirectionPolicy) {this.presalesRedirectionPolicy = presalesRedirectionPolicy;}

    public Boolean getHighDemand() {
        return highDemand;
    }

    public void setHighDemand(Boolean highDemand) {
        this.highDemand = highDemand;
    }

    public SessionExternalConfigDTO getSessionExternalConfig() {
        return sessionExternalConfig;
    }

    public void setSessionExternalConfig(SessionExternalConfigDTO sessionExternalConfig) {
        this.sessionExternalConfig = sessionExternalConfig;
    }

    public Boolean getUseDynamicPrices() {
        return useDynamicPrices;
    }

    public void setUseDynamicPrices(Boolean useDynamicPrices) {
        this.useDynamicPrices = useDynamicPrices;
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
