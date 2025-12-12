package es.onebox.flc.events.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.common.utils.TimeZoneResolver;
import es.onebox.core.utils.dto.DateConvertible;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

public class Event implements DateConvertible, Serializable {

    @Serial
    private static final long serialVersionUID = -9078509707889824093L;

    private Integer id;
    private String name;
    private EventState state;
    @JsonProperty("start_date")
    private ZonedDateTime startDate;
    @JsonProperty("end_date")
    private ZonedDateTime endDate;
    @JsonProperty("sales_goal")
    private Double salesGoal;
    @JsonProperty("tickets_goal")
    private Integer ticketsGoal;
    @JsonProperty("external_reference_code")
    private String externalReferenceCode;
    @JsonProperty("attribute_values_map")
    private Map<Integer, List<Integer>> attributeValuesMap;

    @JsonIgnore
    private String timeZone;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public EventState getState() {
        return state;
    }

    public void setState(EventState state) {
        this.state = state;
    }

    public ZonedDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(ZonedDateTime startDate) {
        this.startDate = startDate;
    }

    public ZonedDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(ZonedDateTime endDate) {
        this.endDate = endDate;
    }

    public Double getSalesGoal() {
        return salesGoal;
    }

    public void setSalesGoal(Double salesGoal) {
        this.salesGoal = salesGoal;
    }

    public Integer getTicketsGoal() {
        return ticketsGoal;
    }

    public void setTicketsGoal(Integer ticketsGoal) {
        this.ticketsGoal = ticketsGoal;
    }

    public String getExternalReferenceCode() {
        return externalReferenceCode;
    }

    public void setExternalReferenceCode(String externalReferenceCode) {
        this.externalReferenceCode = externalReferenceCode;
    }

    public Map<Integer, List<Integer>> getAttributeValuesMap() {
        return attributeValuesMap;
    }

    public void setAttributeValuesMap(Map<Integer, List<Integer>> attributeValuesMap) {
        this.attributeValuesMap = attributeValuesMap;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    @Override
    public void convertDates() {
        if (startDate != null) {
            startDate = TimeZoneResolver.applyTimeZone(startDate, timeZone);
        }

        if (endDate != null) {
            endDate = TimeZoneResolver.applyTimeZone(endDate, timeZone);
        }
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
