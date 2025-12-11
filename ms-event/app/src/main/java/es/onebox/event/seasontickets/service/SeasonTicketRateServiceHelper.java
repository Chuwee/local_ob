package es.onebox.event.seasontickets.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.seasontickets.dao.SeasonTicketSessionDao;
import es.onebox.event.sessions.dao.SessionRateDao;
import es.onebox.event.sessions.dao.record.SessionRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelTarifaRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SeasonTicketRateServiceHelper {

    private final SeasonTicketSessionDao seasonTicketSessionDao;
    private final SessionRateDao sessionRateDao;

    @Autowired
    public SeasonTicketRateServiceHelper(SeasonTicketSessionDao seasonTicketSessionDao, SessionRateDao sessionRateDao) {
        this.seasonTicketSessionDao = seasonTicketSessionDao;
        this.sessionRateDao = sessionRateDao;
    }

    Integer getSessionIdFromSeasonTicketId(Integer seasonTicketId) {
        List<SessionRecord> sessionRecords = seasonTicketSessionDao.searchSessionInfoByEventId(seasonTicketId.longValue());
        SessionRecord sessionRecord = null;
        if (!CommonUtils.isEmpty(sessionRecords) && sessionRecords.size() == 1) {
            sessionRecord = sessionRecords.stream().findFirst().orElse(null);
        }
        if(sessionRecord == null) {
            throw OneboxRestException.builder(MsEventErrorCode.FIELD_NOT_UPGRADEABLE).
                    setMessage("Can't update rates while season ticket is in generation").build();
        }
        return sessionRecord.getIdsesion();
    }

    List<Integer> getVisibleIdRatesFromSession(Integer sessionId) {
        return sessionRateDao.getRatesBySessionId(sessionId).stream()
                .map(CpanelTarifaRecord::getIdtarifa)
                .collect(Collectors.toList());
    }
}
