package es.onebox.event.sessions.dao;

import es.onebox.event.events.enums.SessionState;
import es.onebox.event.sessions.domain.SessionRate;
import es.onebox.jooq.cpanel.tables.records.CpanelSesionTarifaRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelTarifaRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.jooq.InsertValuesStep3;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

import static es.onebox.jooq.cpanel.Tables.CPANEL_SESION;
import static es.onebox.jooq.cpanel.tables.CpanelSesionTarifa.CPANEL_SESION_TARIFA;
import static es.onebox.jooq.cpanel.tables.CpanelTarifa.CPANEL_TARIFA;

@Repository
public class SessionRateDao extends DaoImpl<CpanelTarifaRecord, Integer> {

    protected SessionRateDao() {
        super(CPANEL_TARIFA);
    }

    public List<CpanelTarifaRecord> getRatesBySessionId(Integer sessionId) {
        return dsl.select(
                        CPANEL_TARIFA.IDTARIFA,
                        CPANEL_TARIFA.NOMBRE,
                        CPANEL_SESION_TARIFA.DEFECTO,
                        CPANEL_TARIFA.POSITION)
                .from(CPANEL_TARIFA)
                .innerJoin(CPANEL_SESION_TARIFA).using(CPANEL_TARIFA.IDTARIFA)
                .where(CPANEL_SESION_TARIFA.IDSESION.eq(sessionId))
                .and(CPANEL_SESION_TARIFA.VISIBILIDAD.isTrue())
                .fetchInto(CpanelTarifaRecord.class);
    }

    public List<CpanelTarifaRecord> getRatesBySessionIds(List<Integer> sessionId) {
        return dsl.select(
                        CPANEL_TARIFA.IDTARIFA,
                        CPANEL_TARIFA.NOMBRE,
                        CPANEL_SESION_TARIFA.DEFECTO,
                        CPANEL_TARIFA.POSITION)
                .from(CPANEL_TARIFA)
                .innerJoin(CPANEL_SESION_TARIFA).using(CPANEL_TARIFA.IDTARIFA)
                .where(CPANEL_SESION_TARIFA.IDSESION.in(sessionId))
                .and(CPANEL_SESION_TARIFA.VISIBILIDAD.isTrue())
                .fetchInto(CpanelTarifaRecord.class);
    }

    public List<CpanelSesionTarifaRecord> getSessionRatesBySessionId(Integer sessionId) {
        return dsl.select(
                        CPANEL_SESION_TARIFA.IDSESION,
                        CPANEL_SESION_TARIFA.IDTARIFA,
                        CPANEL_SESION_TARIFA.DEFECTO,
                        CPANEL_SESION_TARIFA.VISIBILIDAD)
                .from(CPANEL_SESION_TARIFA)
                .where(CPANEL_SESION_TARIFA.IDSESION.eq(sessionId))
                .fetchInto(CpanelSesionTarifaRecord.class);
    }

    public int bulkInsertSessionRates(List<SessionRate> sessionRates) {
        InsertValuesStep3<CpanelSesionTarifaRecord, Integer, Integer, Boolean> inserts = dsl.insertInto(CPANEL_SESION_TARIFA,
                CPANEL_SESION_TARIFA.IDSESION,
                CPANEL_SESION_TARIFA.IDTARIFA,
                CPANEL_SESION_TARIFA.DEFECTO);
        for (SessionRate sessionRate : sessionRates) {
            inserts.values(sessionRate.getSessionId().intValue(), sessionRate.getRateId(), sessionRate.getDefaultRate());
        }
        return inserts.execute();
    }

    public void cleanRatesForSessionId(Integer sessionId) {
        dsl.delete(CPANEL_SESION_TARIFA).where(CPANEL_SESION_TARIFA.IDSESION.eq(sessionId)).execute();
    }

    public void cleanRatesForSessionIds(List<Long> sessionIds) {
        dsl.delete(CPANEL_SESION_TARIFA).where(CPANEL_SESION_TARIFA.IDSESION.in(sessionIds)).execute();
    }

    public Long countByRateId(Integer rateId) {
        return dsl.selectCount()
                .from(CPANEL_SESION_TARIFA)
                .where(CPANEL_SESION_TARIFA.IDTARIFA.eq(rateId))
                .fetchOne(0, Long.class);
    }

    public void deleteRateForSessionId(Integer sessionId, Integer rateId) {
        dsl.delete(CPANEL_SESION_TARIFA)
                .where(CPANEL_SESION_TARIFA.IDSESION.eq(sessionId))
                .and(CPANEL_SESION_TARIFA.IDTARIFA.eq(rateId))
                .execute();
    }

    public void createSessionRateRelationship(Integer sessionId, Integer rateId) {
        dsl.insertInto(CPANEL_SESION_TARIFA, CPANEL_SESION_TARIFA.IDSESION, CPANEL_SESION_TARIFA.IDTARIFA, CPANEL_SESION_TARIFA.DEFECTO)
                .values(sessionId, rateId, true)
                .execute();
    }

    public Map<Long, List<SessionRate>> getSessionRatesByEventId(Long eventId) {
        return dsl.select(CPANEL_SESION_TARIFA.IDSESION, CPANEL_SESION_TARIFA.IDTARIFA, CPANEL_SESION_TARIFA.DEFECTO,
                        CPANEL_TARIFA.NOMBRE, CPANEL_TARIFA.ACCESORESTRICTIVO, CPANEL_TARIFA.POSITION)
                .from(CPANEL_SESION_TARIFA)
                .join(CPANEL_SESION).on(CPANEL_SESION_TARIFA.IDSESION.eq(CPANEL_SESION.IDSESION)).and(CPANEL_SESION.ESTADO.ne(SessionState.DELETED.value()))
                .join(CPANEL_TARIFA).on(CPANEL_SESION_TARIFA.IDTARIFA.eq(CPANEL_TARIFA.IDTARIFA))
                .where(CPANEL_SESION.IDEVENTO.eq(eventId.intValue()))
                .fetchGroups(
                        r -> r.getValue(CPANEL_SESION_TARIFA.IDSESION).longValue(),
                        r -> {
                            SessionRate response = new SessionRate();
                            response.setRateId(r.getValue(CPANEL_SESION_TARIFA.IDTARIFA));
                            response.setRateName(r.getValue(CPANEL_TARIFA.NOMBRE));
                            response.setDefaultRate(r.getValue(CPANEL_SESION_TARIFA.DEFECTO));
                            response.setRestrictiveAccess(r.getValue(CPANEL_TARIFA.ACCESORESTRICTIVO));
                            response.setPosition(r.getValue(CPANEL_TARIFA.POSITION));
                            return response;
                        }
                );
    }

    public void updateSesionTarifaVisibilities(CpanelSesionTarifaRecord cpanelSesionTarifaRecord) {
        dsl.update(CPANEL_SESION_TARIFA)
                .set(CPANEL_SESION_TARIFA.VISIBILIDAD, cpanelSesionTarifaRecord.getVisibilidad())
                .where(CPANEL_SESION_TARIFA.IDTARIFA.eq(cpanelSesionTarifaRecord.getIdtarifa()))
                .execute();
    }

    public void updateSessionRateToNewRateId(Integer sessionId, Integer currenRateId, Integer newRateId) {
        dsl.update(CPANEL_SESION_TARIFA)
                .set(CPANEL_SESION_TARIFA.IDTARIFA, newRateId)
                .where(CPANEL_SESION_TARIFA.IDSESION.eq(sessionId).and(CPANEL_SESION_TARIFA.IDTARIFA.eq(currenRateId)))
                .execute();
    }
}
