package es.onebox.mgmt.seasontickets.dto.releaseseat;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.datasources.ms.order.dto.SeasonTicketReleasesFileField;
import es.onebox.mgmt.export.dto.ExportRequest;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;

public class SeasonTicketReleasesExportRequestDTO extends ExportRequest<SeasonTicketReleasesFileField> {

    private static final long serialVersionUID = 1L;

    @JsonProperty("release_status")
    private List<String> releaseStatus;
    @JsonProperty("session_id")
    private Long sessionId;

    public List<String> getReleaseStatus() {
        return releaseStatus;
    }

    public void setReleaseStatus(List<String> releaseStatus) {
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
}
