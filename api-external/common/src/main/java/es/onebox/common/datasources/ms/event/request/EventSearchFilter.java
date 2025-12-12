package es.onebox.common.datasources.ms.event.request;

import es.onebox.common.datasources.ms.event.enums.EventStatus;
import es.onebox.common.datasources.ms.event.enums.EventType;
import es.onebox.common.datasources.ms.event.enums.SessionStatus;
import es.onebox.core.serializer.dto.request.BaseRequestFilter;
import es.onebox.core.serializer.dto.request.FilterWithOperator;
import es.onebox.core.serializer.dto.request.SortOperator;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

public class EventSearchFilter extends BaseRequestFilter implements Serializable {

    @Serial
    private static final long serialVersionUID = -9198303607094589052L;

    private List<Long> id;
    private Long operatorId;
    private Long entityId;
    private String name;
    private List<EventStatus> status;
    private List<EventType> type;
    private List<Integer> avetCompetitions;
    private Long producerId;
    private List<Long> venueId;
    private Long venueConfigId;
    private Long venueEntityId;
    private Long countryId;
    private String city;
    private Boolean includeArchived;
    private String freeSearch;

    private Boolean includeSeasonTickets;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private List<FilterWithOperator<ZonedDateTime>> startDate;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private List<FilterWithOperator<ZonedDateTime>> endDate;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime sessionStartDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime sessionEndDate;
    private SortOperator<String> sort;
    private List<String> fields;
    private List<SessionStatus> sessionStatus;
    private String externalReference;

    public List<Long> getId() {
        return id;
    }

    public void setId(List<Long> id) {
        this.id = id;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<EventStatus> getStatus() {
        return status;
    }

    public void setStatus(List<EventStatus> status) {
        this.status = status;
    }

    public List<EventType> getType() {
        return type;
    }

    public void setType(List<EventType> type) {
        this.type = type;
    }

    public List<Integer> getAvetCompetitions() {
        return avetCompetitions;
    }

    public void setAvetCompetitions(List<Integer> avetCompetitions) {
        this.avetCompetitions = avetCompetitions;
    }

    public Long getProducerId() {
        return producerId;
    }

    public void setProducerId(Long producerId) {
        this.producerId = producerId;
    }

    public List<Long> getVenueId() {
        return venueId;
    }

    public void setVenueId(List<Long> venueId) {
        this.venueId = venueId;
    }

    public Long getVenueConfigId() {
        return venueConfigId;
    }

    public void setVenueConfigId(Long venueConfigId) {
        this.venueConfigId = venueConfigId;
    }

    public Long getVenueEntityId() {
        return venueEntityId;
    }

    public void setVenueEntityId(Long venueEntityId) {
        this.venueEntityId = venueEntityId;
    }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Boolean getIncludeArchived() {
        return includeArchived;
    }

    public void setIncludeArchived(Boolean includeArchived) {
        this.includeArchived = includeArchived;
    }

    public String getFreeSearch() {
        return freeSearch;
    }

    public void setFreeSearch(String freeSearch) {
        this.freeSearch = freeSearch;
    }

    public List<FilterWithOperator<ZonedDateTime>> getStartDate() {
        return startDate;
    }

    public void setStartDate(List<FilterWithOperator<ZonedDateTime>> startDate) {
        this.startDate = startDate;
    }

    public List<FilterWithOperator<ZonedDateTime>> getEndDate() {
        return endDate;
    }

    public void setEndDate(List<FilterWithOperator<ZonedDateTime>> endDate) {
        this.endDate = endDate;
    }

    public SortOperator<String> getSort() {
        return sort;
    }

    public void setSort(SortOperator<String> sort) {
        this.sort = sort;
    }

    public List<String> getFields() {
        return fields;
    }

    public void setFields(List<String> fields) {
        this.fields = fields;
    }

    public List<SessionStatus> getSessionStatus() {
        return sessionStatus;
    }

    public void setSessionStatus(List<SessionStatus> sessionStatus) {
        this.sessionStatus = sessionStatus;
    }

    public String getExternalReference() {
        return externalReference;
    }

    public void setExternalReference(String externalReference) {
        this.externalReference = externalReference;
    }

    public ZonedDateTime getSessionStartDate() {
        return sessionStartDate;
    }

    public void setSessionStartDate(ZonedDateTime sessionStartDate) {
        this.sessionStartDate = sessionStartDate;
    }

    public ZonedDateTime getSessionEndDate() {
        return sessionEndDate;
    }

    public void setSessionEndDate(ZonedDateTime sessionEndDate) {
        this.sessionEndDate = sessionEndDate;
    }

    public Boolean getIncludeSeasonTickets() {
        return includeSeasonTickets;
    }

    public void setIncludeSeasonTickets(Boolean includeSeasonTickets) {
        this.includeSeasonTickets = includeSeasonTickets;
    }

    @Override
    protected Long getDefaultLimit() {
        return 50L;
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
