package es.onebox.mgmt.datasources.ms.order.dto;

import es.onebox.mgmt.export.dto.ExportFilter;
import es.onebox.mgmt.seasontickets.enums.ReleaseStatus;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serial;
import java.util.List;

public class SeasonTicketReleasesExportRequest extends ExportFilter<SeasonTicketReleasesFileField> {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long seasonTicketId;
    private Long entityId;
    private List<ReleaseStatus> releaseStatus;
    private Long sessionId;

    public Long getSeasonTicketId() {
        return seasonTicketId;
    }

    public void setSeasonTicketId(Long seasonTicketId) {
        this.seasonTicketId = seasonTicketId;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public List<ReleaseStatus> getReleaseStatus() {
        return releaseStatus;
    }

    public void setReleaseStatus(List<ReleaseStatus> releaseStatus) {
        this.releaseStatus = releaseStatus;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
