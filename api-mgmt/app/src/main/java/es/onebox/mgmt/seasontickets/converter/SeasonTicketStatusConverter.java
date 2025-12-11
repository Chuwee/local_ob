package es.onebox.mgmt.seasontickets.converter;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicketInternalGenerationStatus;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicketStatus;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.seasontickets.enums.SeasonTicketGenerationStatus;
import es.onebox.mgmt.seasontickets.enums.SeasonTicketStatusDTO;

public class SeasonTicketStatusConverter {
    private SeasonTicketStatusConverter(){ throw new UnsupportedOperationException("Cannot instantiate utilities class");}

    public static SeasonTicketGenerationStatus convertInternalGenerationStatus(SeasonTicketInternalGenerationStatus seasonTicketInternalStatus) {
        SeasonTicketGenerationStatus finalStatus = null;
        if(seasonTicketInternalStatus != null) {

            switch (seasonTicketInternalStatus) {
                case CREATED:
                case VENUE_GENERATION_IN_PROGRESS:
                case SESSION_GENERATION_IN_PROGRESS:
                    finalStatus = SeasonTicketGenerationStatus.IN_PROGRESS;
                    break;
                case VENUE_ERROR:
                case SESSION_ERROR:
                    finalStatus = SeasonTicketGenerationStatus.ERROR;
                    break;
                case READY:
                default:
                    finalStatus = SeasonTicketGenerationStatus.READY;
                    break;
            }
        }
        return finalStatus;
    }

    public static SeasonTicketStatusDTO convertStatus(SeasonTicketStatus seasonTicketStatus) {
        SeasonTicketStatusDTO seasonTicketStatusDTO = null;

        if(seasonTicketStatus != null) {
            switch (seasonTicketStatus) {
                case DELETED:
                    throw new OneboxRestException(ApiMgmtErrorCode.SEASON_TICKET_NOT_FOUND, "No season ticket found ", null);
                case SET_UP:
                case PENDING_PUBLICATION:
                case READY:
                case CANCELLED:
                case FINISHED:
                default:
                    seasonTicketStatusDTO = SeasonTicketStatusDTO.valueOf(seasonTicketStatus.name());
                    break;
            }
        }
        return seasonTicketStatusDTO;
    }
}
