package es.onebox.event.sessions.dao;

import es.onebox.event.sessions.dto.SessionStatus;
import es.onebox.jooq.cpanel.tables.records.CpanelSesionesAbonoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelTarifaRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.jooq.InsertValuesStep2;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

import static es.onebox.jooq.cpanel.Tables.CPANEL_SESION;
import static es.onebox.jooq.cpanel.Tables.CPANEL_SESIONES_ABONO;
import static es.onebox.jooq.cpanel.tables.CpanelTarifa.CPANEL_TARIFA;

@Repository
public class SeasonSessionDao extends DaoImpl<CpanelTarifaRecord, Integer> {

    protected SeasonSessionDao() {
        super(CPANEL_TARIFA);
    }

    public void asociateSessionstoSeason(Long seasonId, List<Long> seasonSessionIds) {
        InsertValuesStep2<CpanelSesionesAbonoRecord, Integer, Integer> inserts = dsl.insertInto(CPANEL_SESIONES_ABONO,
                CPANEL_SESIONES_ABONO.IDABONO,
                CPANEL_SESIONES_ABONO.IDSESION);
        for (Long seasonSessionId : seasonSessionIds) {
            inserts.values(seasonId.intValue(), seasonSessionId.intValue());
        }
        inserts.execute();
    }

    public List<Long> findSessionPacksBySessionId(Long sessionId) {
        return dsl.select(CPANEL_SESIONES_ABONO.IDABONO)
                .from(CPANEL_SESIONES_ABONO).innerJoin(CPANEL_SESION).on(CPANEL_SESIONES_ABONO.IDABONO.eq(CPANEL_SESION.IDSESION))
                .where(CPANEL_SESIONES_ABONO.IDSESION.eq(sessionId.intValue()))
                .and(CPANEL_SESION.ESTADO.ne(SessionStatus.DELETED.getId()))
                .fetch(CPANEL_SESIONES_ABONO.IDABONO, Long.class);
    }

    /**
     * @return map where the keys are session pack IDs and the values are lists of session pack IDs
     */
    public Map<Integer, List<Integer>> findAllSessionPacksBySessionIds(List<Integer> sessionIds) {
        return dsl.select(CPANEL_SESIONES_ABONO.IDABONO, CPANEL_SESIONES_ABONO.IDSESION)
                .from(CPANEL_SESIONES_ABONO).innerJoin(CPANEL_SESION).on(CPANEL_SESIONES_ABONO.IDABONO.eq(CPANEL_SESION.IDSESION))
                .where(CPANEL_SESIONES_ABONO.IDSESION.in(sessionIds))
                .and(CPANEL_SESION.ESTADO.ne(SessionStatus.DELETED.getId()))
                .fetchGroups(r -> r.get(CPANEL_SESIONES_ABONO.IDABONO),
                        r -> r.get(CPANEL_SESIONES_ABONO.IDSESION));
    }

    /**
     * @return map where the keys are session IDs and the values are lists of season pack IDs
     */
    public Map<Integer, List<Integer>> findSessionPacksBySessionForSessionIds(List<Long> sessionIds) {
        return dsl.select(CPANEL_SESIONES_ABONO.IDABONO, CPANEL_SESIONES_ABONO.IDSESION)
                .from(CPANEL_SESIONES_ABONO).innerJoin(CPANEL_SESION).on(CPANEL_SESIONES_ABONO.IDABONO.eq(CPANEL_SESION.IDSESION))
                .where(CPANEL_SESIONES_ABONO.IDSESION.in(sessionIds))
                .and(CPANEL_SESION.ESTADO.ne(SessionStatus.DELETED.getId()))
                .fetchGroups(r -> r.get(CPANEL_SESIONES_ABONO.IDSESION),
                        r -> r.get(CPANEL_SESIONES_ABONO.IDABONO));
    }

    public List<Long> findSessionsBySessionPackId(Long seasonId) {
        return dsl.select(CPANEL_SESIONES_ABONO.IDSESION)
                .from(CPANEL_SESIONES_ABONO).innerJoin(CPANEL_SESION).on(CPANEL_SESIONES_ABONO.IDSESION.eq(CPANEL_SESION.IDSESION))
                .where(CPANEL_SESIONES_ABONO.IDABONO.eq(seasonId.intValue()))
                .and(CPANEL_SESION.ESTADO.ne(SessionStatus.DELETED.getId()))
                .fetch(CPANEL_SESIONES_ABONO.IDSESION, Long.class);
    }

    public Map<Integer, List<Integer>> findSessionsBySessionPackIds(List<Long> seasonId) {
        return dsl.select(CPANEL_SESIONES_ABONO.IDABONO, CPANEL_SESIONES_ABONO.IDSESION)
                .from(CPANEL_SESIONES_ABONO).innerJoin(CPANEL_SESION).on(CPANEL_SESIONES_ABONO.IDSESION.eq(CPANEL_SESION.IDSESION))
                .where(CPANEL_SESIONES_ABONO.IDABONO.in(seasonId)
                .and(CPANEL_SESION.ESTADO.ne(SessionStatus.DELETED.getId())))
                .fetchGroups(CPANEL_SESIONES_ABONO.IDABONO,CPANEL_SESIONES_ABONO.IDSESION);
    }

    /**
     * @return map where the keys are season pack IDs and the values are lists of session pack IDs
     */
    public Map<Integer, List<Integer>> findAllSessionsBySessionPackIds(List<Integer> seasonIds) {
        return dsl.select(CPANEL_SESIONES_ABONO.IDSESION, CPANEL_SESIONES_ABONO.IDABONO)
                .from(CPANEL_SESIONES_ABONO).innerJoin(CPANEL_SESION).on(CPANEL_SESIONES_ABONO.IDSESION.eq(CPANEL_SESION.IDSESION))
                .where(CPANEL_SESIONES_ABONO.IDABONO.in(seasonIds))
                .and(CPANEL_SESION.ESTADO.ne(SessionStatus.DELETED.getId()))
                .fetchGroups(r -> r.get(CPANEL_SESIONES_ABONO.IDSESION),
                        r -> r.get(CPANEL_SESIONES_ABONO.IDABONO));
    }

    public List<Long> findAllSessionsBySessionPackId(Long seasonId) {
        return dsl.select(CPANEL_SESIONES_ABONO.IDSESION)
                .from(CPANEL_SESIONES_ABONO)
                .where(CPANEL_SESIONES_ABONO.IDABONO.eq(seasonId.intValue()))
                .fetch(CPANEL_SESIONES_ABONO.IDSESION, Long.class);
    }

    public void unlinkAllSessionsOfPack(Long seasonId) {
        dsl.delete(CPANEL_SESIONES_ABONO).where(CPANEL_SESIONES_ABONO.IDABONO.eq(seasonId.intValue())).execute();
    }

    public void disasociateSessionOfSeason(Long seasonId, Long sessionId) {
        dsl.delete(CPANEL_SESIONES_ABONO)
                .where(CPANEL_SESIONES_ABONO.IDABONO.eq(seasonId.intValue()))
                .and(CPANEL_SESIONES_ABONO.IDSESION.eq(sessionId.intValue()))
                .execute();
    }

}
