package es.onebox.common.datasources.ms.event.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.common.datasources.common.enums.SessionType;
import es.onebox.common.datasources.ms.event.enums.SessionStatus;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class SessionDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private SessionStatus status;
    private Long externalId;
    private String name;
    private Integer saleType;
    @JsonProperty("type")
    private SessionType sessionType;
    private IdNameDTO ticketTax;
    private IdNameDTO chargesTax;
    private IdNameDTO space;

    private Boolean enableMembersLoginsLimit;
    private Integer membersLoginsLimit;

    private List<Long> sessionIds;
    private List<Long> seasonIds;

    private Long eventId;
    private String eventName;

    private Long entityId;
    private String entityName;

    @JsonProperty("timezone")
    private TimeZoneDTO timeZone;
    private SessionDateDTO date;
    private AccessScheduleType accessScheduleType;

    private Long capacity;
    private Long venueConfigId;
    private String venueConfigName;
    private Long venueConfigSpaceId;
    private String venueConfigSpaceName;
    private Boolean useVenueConfigCapacity;
    private boolean venueConfigGraphic;
    private Boolean useTemplateAccess;
    private Long venueId;
    private String venueName;
    private String city;
    private Long countryId;

    private Boolean enableChannels;
    private Boolean enableBookings;
    private Boolean enableSales;
    private Boolean enableCaptcha;
    private Boolean enableShowDateInChannels;
    private Boolean enableShowTimeInChannels;
    private Boolean enableOrphanSeats;

    private Boolean enableProducerTaxData;
    private Integer producerId;
    private Integer invoicePrefixId;

    private Boolean enableSessionTicketLimit;
    private Integer sessionTicketLimit;

    private Boolean enableQueue;
    private String queueAlias;
    private String skipQueueToken;

    private Boolean enableCountryFilter;
    private List<String> countries;

    private Boolean enableSubscriptionList;
    private Integer subscriptionListId;


    private String color;
    private Boolean allowPartialRefund;

    private String reference;

    private Boolean presaleEnabled;

    private List<SessionPreSaleConfigDTO> preSales;
    private Map<String,Object> externalData;

    @JsonProperty("isSmartBooking")
    private Boolean isSmartBooking;

    private Boolean showDate;
    private Boolean showDatetime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SessionStatus getStatus() {
        return status;
    }

    public void setStatus(SessionStatus status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSaleType() {
        return saleType;
    }

    public void setSaleType(Integer saleType) {
        this.saleType = saleType;
    }

    public SessionType getSessionType() {
        return sessionType;
    }

    public void setSessionType(SessionType sessionType) {
        this.sessionType = sessionType;
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

    public Long getVenueConfigSpaceId() {
        return venueConfigSpaceId;
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

    public void setUseVenueConfigCapacity(Boolean useVenueConfigCapacity) { this.useVenueConfigCapacity = useVenueConfigCapacity; }

    public boolean isVenueConfigGraphic() { return venueConfigGraphic; }

    public void setVenueConfigGraphic(boolean venueConfigGraphic) {
        this.venueConfigGraphic = venueConfigGraphic;
    }

    public Boolean getUseTemplateAccess() {
        return useTemplateAccess;
    }

    public void setUseTemplateAccess(Boolean useTemplateAccess) {
        this.useTemplateAccess = useTemplateAccess;
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


    public Long getExternalId() {
        return externalId;
    }

    public void setExternalId(Long externalId) {
        this.externalId = externalId;
    }


    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
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

    public Boolean getEnableSessionTicketLimit() {
        return enableSessionTicketLimit;
    }

    public void setEnableSessionTicketLimit(Boolean enableSessionTicketLimit) {
        this.enableSessionTicketLimit = enableSessionTicketLimit;
    }

    public Boolean getEnableOrphanSeats() {
        return enableOrphanSeats;
    }

    public void setEnableOrphanSeats(Boolean enableOrphanSeats) {
        this.enableOrphanSeats = enableOrphanSeats;
    }

    public Integer getSessionTicketLimit() {
        return sessionTicketLimit;
    }

    public void setSessionTicketLimit(Integer sessionTicketLimit) {
        this.sessionTicketLimit = sessionTicketLimit;
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

    public TimeZoneDTO getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(TimeZoneDTO timeZone) {
        this.timeZone = timeZone;
    }

    public SessionDateDTO getDate() {
        return date;
    }

    public void setDate(SessionDateDTO date) {
        this.date = date;
    }

    public AccessScheduleType getAccessScheduleType() {
        return accessScheduleType;
    }

    public void setAccessScheduleType(AccessScheduleType accessScheduleType) {
        this.accessScheduleType = accessScheduleType;
    }

    public Boolean getPresaleEnabled() {
        return presaleEnabled;
    }

    public void setPresaleEnabled(Boolean presaleEnabled) {
        this.presaleEnabled = presaleEnabled;
    }

    public List<SessionPreSaleConfigDTO> getPreSales() {
        return preSales;
    }

    public void setPreSales(List<SessionPreSaleConfigDTO> preSales) {
        this.preSales = preSales;
    }

    public Map<String, Object> getExternalData() {
        return externalData;
    }

    public void setExternalData(Map<String, Object> externalData) {
        this.externalData = externalData;
    }

    public Boolean getSmartBooking() {
        return isSmartBooking;
    }

    public void setSmartBooking(Boolean smartBooking) {
        isSmartBooking = smartBooking;
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

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
