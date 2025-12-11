package es.onebox.mgmt.entities.externalconfiguration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;

public class ClubConfigTicketingConnectionRequestDTO extends BaseClubConfigConnectionDTO {

    @Serial
    private static final long serialVersionUID = -640803117434435319L;

    @JsonProperty("name")
    private String name;

    @JsonProperty("ping_requests_blocked")
    private Boolean pingRequestsBlocked;

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
}
