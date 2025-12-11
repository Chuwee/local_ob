package es.onebox.mgmt.sessions.converters;

import es.onebox.mgmt.datasources.integration.avetconfig.dto.Match;
import es.onebox.mgmt.events.dto.AdditionalConfigMatchesDTO;
import es.onebox.mgmt.sessions.dto.MatchDTO;

import java.util.List;
import java.util.stream.Collectors;

public class MatchConverter {

    private MatchConverter() {
    }

    public static MatchDTO fromMs(Match source) {

        if(source == null) {
            return null;
        }

        return new MatchDTO(
                source.getMatchId(),
                source.getName(),
                source.getMatchDate(),
                source.getStartSalesDate(),
                source.getEndSalesDate(),
                source.isMatchDateConfirmed(),
                source.getSmartBookingRelated()
        );
    }

    public static AdditionalConfigMatchesDTO listFromMs(List<Match> source) {
        AdditionalConfigMatchesDTO additionalConfigMatchesDTO = new AdditionalConfigMatchesDTO();
        List<MatchDTO> matchDTOList = source.stream()
                .map(MatchConverter::fromMs)
                .collect(Collectors.toList());
        additionalConfigMatchesDTO.setMatchDTOList(matchDTOList);
        return additionalConfigMatchesDTO;
    }
}
