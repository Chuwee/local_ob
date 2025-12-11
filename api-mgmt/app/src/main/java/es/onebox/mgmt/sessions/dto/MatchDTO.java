package es.onebox.mgmt.sessions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.time.ZonedDateTime;

public class MatchDTO implements Serializable {

    private static final long serialVersionUID = 2L;

    private Long id;
    private String name;
    @JsonProperty("match_date")
    private ZonedDateTime matchDate;
    @JsonProperty("start_sales_date")
    private ZonedDateTime startSalesDate;
    @JsonProperty("end_sales_date")
    private ZonedDateTime endSalesDate;
    @JsonProperty("match_date_confirmed")
    private Boolean matchDateConfirmed;
    @JsonProperty("smart_booking_related")
    private Boolean smartBookingRelated;

    public MatchDTO() {
    }

    public MatchDTO(Long id, String name, ZonedDateTime matchDate, ZonedDateTime startSalesDate, ZonedDateTime endSalesDate,
                    Boolean matchDateConfirmed, Boolean smartBookingRelated) {
        this.id = id;
        this.name = name;
        this.matchDate = matchDate;
        this.startSalesDate = startSalesDate;
        this.endSalesDate = endSalesDate;
        this.matchDateConfirmed = matchDateConfirmed == null
                ? Boolean.FALSE
                : matchDateConfirmed;
        this.smartBookingRelated = smartBookingRelated;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ZonedDateTime getMatchDate() {
        return matchDate;
    }

    public void setMatchDate(ZonedDateTime matchDate) {
        this.matchDate = matchDate;
    }

    public ZonedDateTime getStartSalesDate() {
        return startSalesDate;
    }

    public void setStartSalesDate(ZonedDateTime startSalesDate) {
        this.startSalesDate = startSalesDate;
    }

    public ZonedDateTime getEndSalesDate() {
        return endSalesDate;
    }

    public void setEndSalesDate(ZonedDateTime endSalesDate) {
        this.endSalesDate = endSalesDate;
    }

    public Boolean getMatchDateConfirmed() {
        return matchDateConfirmed;
    }

    public void setMatchDateConfirmed(Boolean matchDateConfirmed) {
        this.matchDateConfirmed = BooleanUtils.toBoolean(matchDateConfirmed);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getSmartBookingRelated() {
        return smartBookingRelated;
    }

    public void setSmartBookingRelated(Boolean smartBookingRelated) {
        this.smartBookingRelated = smartBookingRelated;
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
