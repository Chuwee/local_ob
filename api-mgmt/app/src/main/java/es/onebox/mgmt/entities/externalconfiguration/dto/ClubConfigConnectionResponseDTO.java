package es.onebox.mgmt.entities.externalconfiguration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.entities.externalconfiguration.enums.Status;

import java.io.Serial;

public class ClubConfigConnectionResponseDTO extends BaseClubConfigConnectionDTO {

    @Serial
    private static final long serialVersionUID = -4147937923290233761L;

    @JsonProperty("name")
    private String name;

    @JsonProperty("ping_requests_blocked")
    private Boolean pingRequestsBlocked;

    @JsonProperty("status")
    private Status status;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getPingRequestsBlocked() {
        return pingRequestsBlocked;
    }

    public void setPingRequestsBlocked(Boolean pingRequestsBlocked) {
        this.pingRequestsBlocked = pingRequestsBlocked;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
