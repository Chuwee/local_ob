package es.onebox.event.seasontickets.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.events.dao.record.EventRecord;
import es.onebox.event.events.dao.record.VenueRecord;
import es.onebox.event.events.enums.EventStatus;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.seasontickets.dao.SeasonTicketEventDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static es.onebox.core.exception.CoreErrorCode.BAD_PARAMETER;

@Service
public class SeasonTicketHelper {

    private final SeasonTicketEventDao seasonTicketEventDao;

    @Autowired
    public SeasonTicketHelper(SeasonTicketEventDao seasonTicketEventDao) {
        this.seasonTicketEventDao = seasonTicketEventDao;
    }

    public EventRecord getAndCheckSeasonTicket(Long seasonTicketId) {
        if (seasonTicketId == null || seasonTicketId <= 0) {
            throw new OneboxRestException(BAD_PARAMETER, "seasonTicketId must have a value and be greater than 0", null);
        }
        Map.Entry<EventRecord, List<VenueRecord>> seasonTicket = seasonTicketEventDao.findSeasonTicket(seasonTicketId);
        if (seasonTicket == null || EventStatus.DELETED.getId().equals(seasonTicket.getKey().getEstado())) {
            throw OneboxRestException.builder(MsEventErrorCode.EVENT_NOT_FOUND).setMessage("Season ticket: " + seasonTicket + " not found")
                    .build();
        }
        return seasonTicket.getKey();
    }
}
