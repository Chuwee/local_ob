package es.onebox.event.sessions.request;

import es.onebox.core.serializer.dto.request.BaseRequestFilter;
import es.onebox.core.serializer.dto.request.FilterWithOperator;
import es.onebox.core.serializer.dto.request.SortOperator;
import es.onebox.core.serializer.validation.MaxLimit;
import es.onebox.event.events.enums.EventStatus;
import es.onebox.event.events.enums.EventType;
import es.onebox.event.sessions.dto.SessionGenerationStatus;
import es.onebox.event.sessions.dto.SessionSalesType;
import es.onebox.event.sessions.dto.SessionStatus;
import es.onebox.event.sessions.enums.SessionType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serial;
import java.time.DayOfWeek;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@MaxLimit(1000)
public class SessionSearchFilter extends BaseRequestFilter {

    @Serial
    private static final long serialVersionUID = 1591189390579123891L;

    private Long id;
    private List<Long> ids;
    private Long entityId;
    private Long operatorId;
    private Long venueConfigId;
    private List<Long> venueId;
    private List<Long> accessValidationSpaceId;
    private Long venueEntityId;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private List<FilterWithOperator<ZonedDateTime>> startDate;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private FilterWithOperator<ZonedDateTime> endDate;
    private Set<DayOfWeek> daysOfWeek = new HashSet<>();
    private List<HourPeriod> hourPeriods = new ArrayList<>();
    private List<SessionStatus> status = new ArrayList<>();
    private List<SessionGenerationStatus> generationStatus = new ArrayList<>();
    private ZonedDateTime startDateFrom;
    private ZonedDateTime startDateTo;
    private ZonedDateTime rangeDateFrom;
    private ZonedDateTime rangeDateTo;
    private String olsonId;
    private List<Long> eventId;
    private List<SessionType> type;
    private List<SessionSalesType> saleType;
    private Boolean isSessionPack;
    private Boolean allowPartialRefund;
    private Boolean includeDeleted;
    private List<EventStatus> eventStatus;
    private List<EventType> eventType;
    private String freeSearch;
    private List<String> fields;
    private List<FilterWithOperator<ZonedDateTime>> admissionDate;
    private List<FilterWithOperator<ZonedDateTime>> inValidationDate;
    private SortOperator<String> sort;
    private boolean getQueueitInfo;
    private Boolean includeDynamicPriceConfig;
    private String inventoryProvider;

    public SessionSearchFilter() {
    }

    public SessionSearchFilter(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public Long getVenueConfigId() {
        return venueConfigId;
    }

    public void setVenueConfigId(Long venueConfigId) {
        this.venueConfigId = venueConfigId;
    }

    public List<Long> getVenueId() {
        return venueId;
    }

    public void setVenueId(List<Long> venueId) {
        this.venueId = venueId;
    }

    public List<Long> getAccessValidationSpaceId() {
        return accessValidationSpaceId;
    }

    public void setAccessValidationSpaceId(List<Long> accessValidationSpaceId) {
        this.accessValidationSpaceId = accessValidationSpaceId;
    }

    public Long getVenueEntityId() {
        return venueEntityId;
    }

    public void setVenueEntityId(Long venueEntityId) {
        this.venueEntityId = venueEntityId;
    }

    public List<FilterWithOperator<ZonedDateTime>> getStartDate() {
        return startDate;
    }

    public void setStartDate(List<FilterWithOperator<ZonedDateTime>> startDate) {
        this.startDate = startDate;
    }

    public FilterWithOperator<ZonedDateTime> getEndDate() {
        return endDate;
    }

    public void setEndDate(FilterWithOperator<ZonedDateTime> endDate) {
        this.endDate = endDate;
    }

    public Set<DayOfWeek> getDaysOfWeek() {
        return daysOfWeek;
    }

    public void setDaysOfWeek(Set<DayOfWeek> daysOfWeek) {
        this.daysOfWeek = daysOfWeek;
    }

    public List<HourPeriod> getHourPeriods() {
        return hourPeriods;
    }

    public void setHourPeriods(List<HourPeriod> hourPeriods) {
        this.hourPeriods = hourPeriods;
    }

    public List<SessionStatus> getStatus() {
        return status;
    }

    public void setStatus(List<SessionStatus> status) {
        this.status = status;
    }

    public List<SessionGenerationStatus> getGenerationStatus() {
        return generationStatus;
    }

    public void setGenerationStatus(List<SessionGenerationStatus> generationStatus) {
        this.generationStatus = generationStatus;
    }

    public ZonedDateTime getStartDateFrom() {
        return startDateFrom;
    }

    public void setStartDateFrom(ZonedDateTime startDateFrom) {
        this.startDateFrom = startDateFrom;
    }

    public ZonedDateTime getStartDateTo() {
        return startDateTo;
    }

    public void setStartDateTo(ZonedDateTime startDateTo) {
        this.startDateTo = startDateTo;
    }

    public ZonedDateTime getRangeDateFrom() {
        return rangeDateFrom;
    }

    public void setRangeDateFrom(ZonedDateTime rangeDateFrom) {
        this.rangeDateFrom = rangeDateFrom;
    }

    public ZonedDateTime getRangeDateTo() {
        return rangeDateTo;
    }

    public void setRangeDateTo(ZonedDateTime rangeDateTo) {
        this.rangeDateTo = rangeDateTo;
    }

    public String getOlsonId() {
        return olsonId;
    }

    public void setOlsonId(String olsonId) {
        this.olsonId = olsonId;
    }

    public List<Long> getEventId() {
        return eventId;
    }

    public void setEventId(List<Long> eventId) {
        this.eventId = eventId;
    }

    public List<SessionType> getType() {
        return type;
    }

    public void setType(List<SessionType> type) {
        this.type = type;
    }

    public List<SessionSalesType> getSaleType() {
        return saleType;
    }

    public void setSaleType(List<SessionSalesType> saleType) {
        this.saleType = saleType;
    }

    public Boolean getSessionPack() {
        return isSessionPack;
    }

    public void setSessionPack(Boolean sessionPack) {
        isSessionPack = sessionPack;
    }

    public Boolean getAllowPartialRefund() {
        return allowPartialRefund;
    }

    public void setAllowPartialRefund(Boolean allowPartialRefund) {
        this.allowPartialRefund = allowPartialRefund;
    }

    public Boolean getIncludeDeleted() {
        return includeDeleted;
    }

    public void setIncludeDeleted(Boolean includeDeleted) {
        this.includeDeleted = includeDeleted;
    }

    public List<EventStatus> getEventStatus() {
        return eventStatus;
    }

    public void setEventStatus(List<EventStatus> eventStatus) {
        this.eventStatus = eventStatus;
    }

    public List<EventType> getEventType() {
        return eventType;
    }

    public void setEventType(List<EventType> eventType) {
        this.eventType = eventType;
    }

    public String getFreeSearch() {
        return freeSearch;
    }

    public void setFreeSearch(String freeSearch) {
        this.freeSearch = freeSearch;
    }

    public List<String> getFields() {
        return fields;
    }

    public void setFields(List<String> fields) {
        this.fields = fields;
    }

    public SortOperator<String> getSort() {
        return sort;
    }

    public void setSort(SortOperator<String> sort) {
        this.sort = sort;
    }

    public List<FilterWithOperator<ZonedDateTime>> getAdmissionDate() {
        return admissionDate;
    }

    public void setAdmissionDate(List<FilterWithOperator<ZonedDateTime>> admissionDate) {
        this.admissionDate = admissionDate;
    }

    public List<FilterWithOperator<ZonedDateTime>> getInValidationDate() {
        return inValidationDate;
    }

    public void setInValidationDate(List<FilterWithOperator<ZonedDateTime>> inValidationDate) {
        this.inValidationDate = inValidationDate;
    }

    public boolean isGetQueueitInfo() {
        return getQueueitInfo;
    }

    public void setGetQueueitInfo(boolean getQueueitInfo) {
        this.getQueueitInfo = getQueueitInfo;
    }

    public Boolean getIncludeDynamicPriceConfig() {
        return includeDynamicPriceConfig;
    }

    public void setIncludeDynamicPriceConfig(Boolean includeDynamicPriceConfig) {
        this.includeDynamicPriceConfig = includeDynamicPriceConfig;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    public String getInventoryProvider() {
        return inventoryProvider;
    }

    public void setInventoryProvider(String inventoryProvider) {
        this.inventoryProvider = inventoryProvider;
    }
}
