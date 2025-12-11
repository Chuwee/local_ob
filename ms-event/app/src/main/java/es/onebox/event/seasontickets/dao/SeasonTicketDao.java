package es.onebox.event.seasontickets.dao;

import es.onebox.jooq.cpanel.tables.records.CpanelSeasonTicketRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.springframework.stereotype.Repository;

import static es.onebox.jooq.cpanel.Tables.CPANEL_SEASON_TICKET;

@Repository
public class SeasonTicketDao extends DaoImpl<CpanelSeasonTicketRecord, Integer> {

    protected SeasonTicketDao() {
        super(CPANEL_SEASON_TICKET);
    }

}
