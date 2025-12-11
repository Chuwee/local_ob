package es.onebox.event.seasontickets.service.releaseseat;

import es.onebox.event.seasontickets.dao.couch.EarningsLimit;
import es.onebox.event.seasontickets.dao.couch.SeasonTicketReleaseSeat;
import es.onebox.event.seasontickets.dto.releaseseat.EarningsLimitDTO;
import es.onebox.event.seasontickets.dto.releaseseat.SeasonTicketReleaseSeatDTO;

public class SeasonTicketReleaseSeatConverter {
    public static SeasonTicketReleaseSeatDTO toDto(SeasonTicketReleaseSeat document) {
        SeasonTicketReleaseSeatDTO dto = new SeasonTicketReleaseSeatDTO();
        if (document != null) {
            dto.setReleaseMaxDelayTime(document.getReleaseSeatMaxDelayTime());
            dto.setRecoverMaxDelayTime(document.getRecoverReleasedSeatMaxDelayTime());
            dto.setCustomerPercentage(document.getCustomerPercentage());
            dto.setExcludedSessions(document.getExcludedSessions());
            dto.setMaxReleases(document.getMaxReleases());
            dto.setMaxReleasesEnabled(document.getMaxReleasesEnabled());
            dto.setEarningsLimit(toEarningsLimitDto(document.getEarningsLimit()));
            dto.setReleaseMinDelayTime(document.getReleaseSeatMinDelayTime());
            dto.setSkipNotifications(document.getSkipNotifications());
        }
        return dto;
    }

    public static void updateReleaseSeatDocument(SeasonTicketReleaseSeat document, SeasonTicketReleaseSeatDTO dto) {
        document.setReleaseSeatMaxDelayTime(dto.getReleaseMaxDelayTime());
        document.setRecoverReleasedSeatMaxDelayTime(dto.getRecoverMaxDelayTime());
        document.setCustomerPercentage(dto.getCustomerPercentage());
        document.setExcludedSessions(dto.getExcludedSessions());
        document.setMaxReleases(dto.getMaxReleases());
        document.setMaxReleasesEnabled(dto.getMaxReleasesEnabled());
        document.setEarningsLimit(fromEarningsLimitDto(dto.getEarningsLimit()));
        document.setReleaseSeatMinDelayTime(dto.getReleaseMinDelayTime());
    }

    private static EarningsLimit fromEarningsLimitDto(EarningsLimitDTO earningsLimitDTO) {
        if (earningsLimitDTO == null) {
            return null;
        }
        EarningsLimit earningsLimit = new EarningsLimit();
        earningsLimit.setEnabled(earningsLimitDTO.getEnabled());
        earningsLimit.setPercentage(earningsLimitDTO.getPercentage());
        return earningsLimit;
    }

    private static EarningsLimitDTO toEarningsLimitDto(EarningsLimit earningsLimit) {
        if (earningsLimit == null) {
            return null;
        }
        EarningsLimitDTO earningsLimitDTO = new EarningsLimitDTO();
        earningsLimitDTO.setEnabled(earningsLimit.getEnabled());
        earningsLimitDTO.setPercentage(earningsLimit.getPercentage());
        return earningsLimitDTO;
    }
}