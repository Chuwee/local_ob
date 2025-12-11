package es.onebox.event.catalog.elasticsearch.indexer;

import es.onebox.event.events.dao.record.SeasonTicketChangeSeatPricesRecord;
import es.onebox.event.seasontickets.dao.SeasonTicketChangeSeatPricesDao;
import es.onebox.event.seasontickets.dao.SeasonTicketDao;
import es.onebox.event.seasontickets.dao.SeasonTicketSessionDao;
import es.onebox.event.seasontickets.dao.couch.SeasonTicketReleaseSeat;
import es.onebox.event.seasontickets.dao.couch.SeasonTicketReleaseSeatCouchDao;
import es.onebox.event.seasontickets.dao.couch.SeasonTicketRenewalConfig;
import es.onebox.event.seasontickets.dao.couch.SeasonTicketRenewalConfigCouchDao;
import es.onebox.event.sessions.dao.record.SessionRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelSeasonTicketRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelSesionRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SeasonTicketRelatedDataSupplier {

    private final SeasonTicketDao seasonTicketDao;
    private final SeasonTicketRenewalConfigCouchDao seasonTicketRenewalConfigCouchDao;
    private final SeasonTicketChangeSeatPricesDao seasonTicketChangeSeatPricesDao;
    private final SeasonTicketReleaseSeatCouchDao seasonTicketReleaseSeatCouchDao;
    private final SeasonTicketSessionDao seasonTicketSessionDao;

    @Autowired
    public SeasonTicketRelatedDataSupplier(SeasonTicketDao seasonTicketDao,
                                           SeasonTicketRenewalConfigCouchDao seasonTicketRenewalConfigCouchDao,
                                           SeasonTicketChangeSeatPricesDao seasonTicketChangeSeatPricesDao,
                                           SeasonTicketReleaseSeatCouchDao seasonTicketReleaseSeatCouchDao, SeasonTicketSessionDao seasonTicketSessionDao) {
        this.seasonTicketDao = seasonTicketDao;
        this.seasonTicketRenewalConfigCouchDao = seasonTicketRenewalConfigCouchDao;
        this.seasonTicketChangeSeatPricesDao = seasonTicketChangeSeatPricesDao;
        this.seasonTicketReleaseSeatCouchDao = seasonTicketReleaseSeatCouchDao;
        this.seasonTicketSessionDao = seasonTicketSessionDao;
    }

    public CpanelSeasonTicketRecord getSeasonTicket(Integer seasonTicketId) {
        return seasonTicketDao.getById(seasonTicketId);
    }

    public CpanelSesionRecord getSeasonTicketSession(Integer seasonTicketId) {
        List<SessionRecord> sessionRecords = seasonTicketSessionDao.searchSessionInfoByEventId(seasonTicketId.longValue());
        return sessionRecords.stream().findFirst().orElse(null);
    }

    public SeasonTicketRenewalConfig getRenewalConfig(Integer seasonTicketId) {
        return seasonTicketRenewalConfigCouchDao.get(String.valueOf(seasonTicketId));
    }

    public List<SeasonTicketChangeSeatPricesRecord> getSeatReallocationPrices(Integer seasonTicketId) {
        return seasonTicketChangeSeatPricesDao.searchChangeSeatPricesTable(seasonTicketId.longValue(), null);
    }

    public SeasonTicketReleaseSeat getSeasonTicketReleaseSeat(Integer seasonTicketId) {
        return seasonTicketReleaseSeatCouchDao.get(String.valueOf(seasonTicketId));
    }
}