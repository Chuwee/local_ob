package es.onebox.mgmt.events.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.sessions.dto.MatchDTO;

import java.io.Serializable;
import java.util.List;

public class AdditionalConfigMatchesDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonProperty("avet_match_list")
    private List<MatchDTO> matchDTOList;

    public List<MatchDTO> getMatchDTOList() {
        return matchDTOList;
    }

    public void setMatchDTOList(List<MatchDTO> matchDTOList) {
        this.matchDTOList = matchDTOList;
    }
}
