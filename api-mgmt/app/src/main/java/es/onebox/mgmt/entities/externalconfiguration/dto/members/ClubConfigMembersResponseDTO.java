package es.onebox.mgmt.entities.externalconfiguration.dto.members;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;

public class ClubConfigMembersResponseDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 3463549348196950861L;

    /**
     * Enables/disables the effect of {@link #clubConfigMembersConnectionResponseDTO}
     */
    @JsonProperty("enabled")
    private Boolean membersEnabled;

    @JsonProperty("connection")
    private ClubConfigMembersConnectionResponseDTO clubConfigMembersConnectionResponseDTO;

    public ClubConfigMembersConnectionResponseDTO getClubConfigMembersConnectionResponseDTO() {
        return clubConfigMembersConnectionResponseDTO;
    }

    public void setClubConfigMembersConnectionResponseDTO(ClubConfigMembersConnectionResponseDTO clubConfigMembersConnectionResponseDTO) {
        this.clubConfigMembersConnectionResponseDTO = clubConfigMembersConnectionResponseDTO;
    }

    public Boolean getMembersEnabled() {
        return membersEnabled;
    }

    public void setMembersEnabled(Boolean membersEnabled) {
        this.membersEnabled = membersEnabled;
    }
}
