package es.onebox.event.seasontickets.dto.renewals;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.time.ZonedDateTime;

public class SeasonTicketChangeSeat implements Serializable {
    private static final long serialVersionUID = -5083810816453509198L;

    private Boolean changeSeatEnabled;
    private ZonedDateTime changeSeatStartingDate;
    private ZonedDateTime changeSeatEndDate;
    private Boolean maxChangeSeatValueEnabled;
    private Integer maxChangeSeatValue;

    public Boolean getChangeSeatEnabled() {
        return changeSeatEnabled;
    }

    public void setChangeSeatEnabled(Boolean changeSeatEnabled) {
        this.changeSeatEnabled = changeSeatEnabled;
    }

    public ZonedDateTime getChangeSeatStartingDate() {
        return changeSeatStartingDate;
    }

    public void setChangeSeatStartingDate(ZonedDateTime changeSeatStartingDate) {
        this.changeSeatStartingDate = changeSeatStartingDate;
    }

    public ZonedDateTime getChangeSeatEndDate() {
        return changeSeatEndDate;
    }

    public void setChangeSeatEndDate(ZonedDateTime changeSeatEndDate) {
        this.changeSeatEndDate = changeSeatEndDate;
    }

    public Boolean getMaxChangeSeatValueEnabled() {
        return maxChangeSeatValueEnabled;
    }

    public void setMaxChangeSeatValueEnabled(Boolean maxChangeSeatValueEnabled) {
        this.maxChangeSeatValueEnabled = maxChangeSeatValueEnabled;
    }

    public Integer getMaxChangeSeatValue() {
        return maxChangeSeatValue;
    }

    public void setMaxChangeSeatValue(Integer maxChangeSeatValue) {
        this.maxChangeSeatValue = maxChangeSeatValue;
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
