package es.onebox.event.seasontickets.converter;

import es.onebox.event.events.enums.VenueStatusDTO;
import es.onebox.event.seasontickets.dao.record.SessionCapacityGenerationStatusRecord;
import es.onebox.event.seasontickets.dao.record.VenueConfigStatusRecord;
import es.onebox.event.seasontickets.dto.SeasonTicketInternalGenerationStatus;
import es.onebox.event.sessions.dto.SessionGenerationStatus;

public class SeasonTicketGenerationStatusConverter {
    private SeasonTicketGenerationStatusConverter(){ throw new UnsupportedOperationException("Cannot instantiate utilities class");}

    public static SeasonTicketInternalGenerationStatus convert(VenueConfigStatusRecord venueStatus, SessionCapacityGenerationStatusRecord generationStatus) {
        SeasonTicketInternalGenerationStatus finalStatus = null;
        if(venueStatus == null) {
            finalStatus = SeasonTicketInternalGenerationStatus.CREATED;
        } else {
            VenueStatusDTO venueConfigStatus = VenueStatusDTO.byId(venueStatus.getEstado());
            switch (venueConfigStatus) {
                case ERROR:
                    finalStatus = SeasonTicketInternalGenerationStatus.VENUE_ERROR;
                    break;
                case PROCESSING:
                    finalStatus = SeasonTicketInternalGenerationStatus.VENUE_GENERATION_IN_PROGRESS;
                    break;
                case ACTIVE:
                default:
                    finalStatus = getInternalSessionGenerationStatus(generationStatus);
                    break;
            }
        }
        return finalStatus;
    }

    private static SeasonTicketInternalGenerationStatus getInternalSessionGenerationStatus(SessionCapacityGenerationStatusRecord generationStatus) {
        SeasonTicketInternalGenerationStatus finalStatus = null;
        if(generationStatus == null) {
            finalStatus = SeasonTicketInternalGenerationStatus.SESSION_GENERATION_IN_PROGRESS;
        } else {
            SessionGenerationStatus status = SessionGenerationStatus.byId(generationStatus.getEstadoGeneracionAforo());
            switch (status) {
                case PENDING:
                case IN_PROGRESS:
                    finalStatus = SeasonTicketInternalGenerationStatus.SESSION_GENERATION_IN_PROGRESS;
                    break;
                case ERROR:
                    finalStatus = SeasonTicketInternalGenerationStatus.SESSION_ERROR;
                    break;
                case ACTIVE:
                    finalStatus = SeasonTicketInternalGenerationStatus.READY;
                    break;
                default:
                    break;
            }
        }
        return finalStatus;
    }
}
