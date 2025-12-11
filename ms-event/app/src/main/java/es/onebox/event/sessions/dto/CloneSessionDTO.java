package es.onebox.event.sessions.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.time.ZonedDateTime;

public class CloneSessionDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private ZonedDateTime startDate;
    private ZonedDateTime endDate;
    private String reference;
    private Boolean targetFreeStatus;
    private Long targetBlockingReasonId;
    private Long sourceSessionId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ZonedDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(ZonedDateTime startDate) {
        this.startDate = startDate;
    }

    public Boolean getTargetFreeStatus() {
        return targetFreeStatus;
    }

    public void setTargetFreeStatus(Boolean targetFreeStatus) {
        this.targetFreeStatus = targetFreeStatus;
    }

    public Long getTargetBlockingReasonId() {
        return targetBlockingReasonId;
    }

    public void setTargetBlockingReasonId(Long targetBlockingReasonId) {
        this.targetBlockingReasonId = targetBlockingReasonId;
    }

    public Long getSourceSessionId() {
        return sourceSessionId;
    }

    public void setSourceSessionId(Long sourceSessionId) {
        this.sourceSessionId = sourceSessionId;
    }


    public ZonedDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(ZonedDateTime endDate) {
        this.endDate = endDate;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
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
