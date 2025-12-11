package es.onebox.event.seasontickets.dao;

import es.onebox.jooq.cpanel.tables.records.CpanelSeasonTicketChangeSeatQuotasRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

import static es.onebox.jooq.cpanel.Tables.CPANEL_SEASON_TICKET_CHANGE_SEAT_QUOTAS;


@Repository
public class SeasonTicketChangedSeatQuotasDao extends DaoImpl<CpanelSeasonTicketChangeSeatQuotasRecord, Integer> {

    protected SeasonTicketChangedSeatQuotasDao() {
        super(CPANEL_SEASON_TICKET_CHANGE_SEAT_QUOTAS);
    }

    public List<CpanelSeasonTicketChangeSeatQuotasRecord> getBySeasonTicketId(Integer seasonTicketId) {
        return dsl.select(CPANEL_SEASON_TICKET_CHANGE_SEAT_QUOTAS.fields())
                .from(CPANEL_SEASON_TICKET_CHANGE_SEAT_QUOTAS)
                .where(CPANEL_SEASON_TICKET_CHANGE_SEAT_QUOTAS.IDSEASONTICKET.eq(seasonTicketId))
                .fetchInto(CpanelSeasonTicketChangeSeatQuotasRecord.class);
    }

    public void deleteBySeasonTicketId(Integer seasonTicketId) {
        dsl.deleteFrom(CPANEL_SEASON_TICKET_CHANGE_SEAT_QUOTAS)
                .where(CPANEL_SEASON_TICKET_CHANGE_SEAT_QUOTAS.IDSEASONTICKET.eq(seasonTicketId))
                .execute();
    }
    public void insert(Integer seasonTicketId, List<Integer> quotaIds) {
        dsl.batchInsert(
                quotaIds.stream().map(quotaId -> new CpanelSeasonTicketChangeSeatQuotasRecord(seasonTicketId, quotaId)).toList()
            ).execute();
    }
}
