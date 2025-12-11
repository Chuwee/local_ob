package es.onebox.mgmt.seasontickets.dto.releaseseat;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.request.BaseRequestFilter;
import es.onebox.core.serializer.validation.MaxLimit;
import es.onebox.mgmt.seasontickets.enums.ReleaseStatus;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.util.List;
@MaxLimit(20)
public class SeasonTicketReleasesFilterDTO extends BaseRequestFilter {

    @Serial
    private static final long serialVersionUID = -6328895195835112420L;

    @JsonProperty("release_status")
    private List<ReleaseStatus> releaseStatus;
    @JsonProperty("session_id")
    private Long sessionId;

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
}
