package es.onebox.mgmt.datasources.ms.event.dto.session;

import java.time.ZonedDateTime;

public class CloneSessionData {

    private String name;
    private ZonedDateTime startDate;
    private ZonedDateTime endDate;
    private String reference;
    private Boolean targetFreeStatus;
    private Long targetBlockingReasonId;

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
}
