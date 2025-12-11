package es.onebox.mgmt.entities.externalconfiguration.dto.members;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.entities.externalconfiguration.dto.BaseClubConfigConnectionDTO;
import es.onebox.mgmt.entities.externalconfiguration.enums.Status;

import java.io.Serializable;

public class ClubConfigMembersConnectionResponseDTO extends BaseClubConfigConnectionDTO implements Serializable {

    @JsonProperty("status")
    private Status status;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
