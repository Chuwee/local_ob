package es.onebox.mgmt.sessions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class SessionAdditionalConfigDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonProperty("avet_external_operative")
    private boolean avetExternalOperative;

    @JsonProperty("avet_match")
    private MatchDTO matchDTO;

    public SessionAdditionalConfigDTO() {
    }

    public SessionAdditionalConfigDTO(boolean avetExternalOperative, MatchDTO matchDTO) {
        this.avetExternalOperative = avetExternalOperative;
        this.matchDTO = matchDTO;
    }

    public boolean isAvetExternalOperative() {
        return avetExternalOperative;
    }

    public MatchDTO getMatchDTO() {
        return matchDTO;
    }
}
