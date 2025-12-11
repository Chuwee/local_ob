package es.onebox.mgmt.seasontickets.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicketReleaseSeat;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.seasontickets.dto.releaseseat.SeasonTicketReleaseSeatConfigDTO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class SeasonTicketReleaseSeatValidationService {

    private SeasonTicketReleaseSeatValidationService() {}

    public static void validateReleaseSeatUpdate(SeasonTicketReleaseSeatConfigDTO dto, Set<Long> assignedSessions, SeasonTicketReleaseSeat seasonTicketReleaseSeat) {
        if (dto.getCustomerPercentage() != null && !isPercentageValid(dto.getCustomerPercentage())) {
            throw new OneboxRestException(ApiMgmtErrorCode.SEASON_TICKET_RELEASE_SEAT_INVALID_PERCENTAGES);
        }

        if ((dto.getReleaseSeatMaxDelayTime() != null && dto.getReleaseSeatMaxDelayTime() < 0)
                || (dto.getReleaseSeatMinDelayTime() != null && dto.getReleaseSeatMinDelayTime() < 0)
                || (dto.getRecoverReleasedSeatMaxDelayTime() != null && dto.getRecoverReleasedSeatMaxDelayTime() < 0)) {
            throw new OneboxRestException(ApiMgmtErrorCode.SEASON_TICKET_RELEASE_SEAT_INVALID_DELAY_TIMES);
        }

        if (CollectionUtils.isNotEmpty(dto.getExcludedSessions()) &&
                (CollectionUtils.isEmpty(assignedSessions) || !assignedSessions.containsAll(dto.getExcludedSessions()))) {
            throw new OneboxRestException(ApiMgmtErrorCode.SEASON_TICKET_RELEASE_SEAT_INVALID_EXCLUDED_SESSION);
        }

        if (BooleanUtils.isTrue(dto.getMaxReleasesEnabled()) && dto.getMaxReleases() != null && dto.getMaxReleases() < 1) {
            throw new OneboxRestException(ApiMgmtErrorCode.SEASON_TICKET_RELEASE_SEAT_INVALID_MAX_RELEASES);
        }

        if (dto.getReleaseSeatMaxDelayTime() != null && dto.getReleaseSeatMinDelayTime() != null) {
            if (dto.getReleaseSeatMinDelayTime() <= dto.getReleaseSeatMaxDelayTime()) {
                throw new OneboxRestException(ApiMgmtErrorCode.SEASON_TICKET_RELEASE_SEAT_INVALID_MIN_DELAY_TIMES);
            }
        }
    }

    private static boolean isPercentageValid(Double percentage) {
        return percentage >= 0.0 && percentage <= 100.0;
    }

}
