package es.onebox.mgmt.entities.externalconfiguration.dto.members;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;

public class ClubConfigMembersRequestDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -5566499002725626455L;
    /**
     * Enables/disables the effect of {@link #clubConfigMembersConnectionRequestDTO}
     */
    @JsonProperty("enabled")
    private Boolean membersEnabled;

    @JsonProperty("connection")
    private ClubConfigMembersConnectionRequestDTO clubConfigMembersConnectionRequestDTO;

    public Boolean getMembersEnabled() {
        return membersEnabled;
    }

    public void setMembersEnabled(Boolean membersEnabled) {
        this.membersEnabled = membersEnabled;
    }

    public ClubConfigMembersConnectionRequestDTO getClubConfigMembersConnectionRequestDTO() {
        return clubConfigMembersConnectionRequestDTO;
    }

    public void setClubConfigMembersConnectionRequestDTO(ClubConfigMembersConnectionRequestDTO clubConfigMembersConnectionRequestDTO) {
        this.clubConfigMembersConnectionRequestDTO = clubConfigMembersConnectionRequestDTO;
    }
}
