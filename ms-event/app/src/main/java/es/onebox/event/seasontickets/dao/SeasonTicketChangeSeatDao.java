package es.onebox.event.seasontickets.dao;

import es.onebox.jooq.cpanel.tables.records.CpanelSeasonTicketChangeSeatRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.springframework.stereotype.Repository;

import static es.onebox.jooq.cpanel.Tables.CPANEL_SEASON_TICKET_CHANGE_SEAT;


@Repository
public class SeasonTicketChangeSeatDao extends DaoImpl<CpanelSeasonTicketChangeSeatRecord, Integer> {

    protected SeasonTicketChangeSeatDao() {
        super(CPANEL_SEASON_TICKET_CHANGE_SEAT);
    }

    public void insertOrUpdate(Integer seasonTicketId, Boolean enableLimit) {
        dsl.insertInto(CPANEL_SEASON_TICKET_CHANGE_SEAT, CPANEL_SEASON_TICKET_CHANGE_SEAT.IDSEASONTICKET, CPANEL_SEASON_TICKET_CHANGE_SEAT.LIMITCHANGESEATQUOTAS)
                .values(seasonTicketId, enableLimit)
                .onDuplicateKeyUpdate()
                .set(CPANEL_SEASON_TICKET_CHANGE_SEAT.LIMITCHANGESEATQUOTAS, enableLimit)
                .execute();
    }
}
