package es.onebox.event.sessions.dao;

import es.onebox.jooq.cpanel.Tables;
import es.onebox.jooq.cpanel.tables.records.CpanelImpuestoRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

import static es.onebox.jooq.cpanel.Tables.CPANEL_ENTIDAD;
import static es.onebox.jooq.cpanel.Tables.CPANEL_EVENTO;
import static es.onebox.jooq.cpanel.Tables.CPANEL_IMPUESTO;
import static es.onebox.jooq.cpanel.Tables.CPANEL_PACK;
import static es.onebox.jooq.cpanel.tables.CpanelSesion.CPANEL_SESION;

@Repository
public class TaxDao extends DaoImpl<CpanelImpuestoRecord, Integer> {

    protected TaxDao() {
        super(CPANEL_IMPUESTO);
    }

    public List<Long> getEventTaxes(Long eventId) {
        return dsl.select(CPANEL_IMPUESTO.IDIMPUESTO)
                .from(CPANEL_IMPUESTO, CPANEL_EVENTO)
                .innerJoin(CPANEL_ENTIDAD).on(CPANEL_ENTIDAD.IDENTIDAD.eq(CPANEL_EVENTO.IDENTIDAD))
                .where(CPANEL_EVENTO.IDEVENTO.eq(eventId.intValue()).and(
                        CPANEL_IMPUESTO.IDOPERADORA.eq(CPANEL_ENTIDAD.IDOPERADORA)
                ))
                .fetch().into(Long.class);
    }

    public CpanelImpuestoRecord getTicketTaxBySession(Long eventId, Long sessionId) {
        return dsl.select(CPANEL_IMPUESTO.fields())
                .from(CPANEL_IMPUESTO)
                .innerJoin(CPANEL_SESION).on(CPANEL_SESION.IDIMPUESTO.eq(CPANEL_IMPUESTO.IDIMPUESTO))
                .innerJoin(CPANEL_EVENTO).on(CPANEL_SESION.IDEVENTO.eq(CPANEL_EVENTO.IDEVENTO))
                .where(CPANEL_SESION.IDSESION.eq(sessionId.intValue())
                        .and(CPANEL_EVENTO.IDEVENTO.eq(eventId.intValue())))
                .fetchOne().into(CpanelImpuestoRecord.class);
    }

    public CpanelImpuestoRecord getChargesTaxBySession(Long eventId, Long sessionId) {
        return dsl.select(CPANEL_IMPUESTO.fields())
                .from(CPANEL_IMPUESTO)
                .innerJoin(CPANEL_SESION).on(CPANEL_SESION.IDIMPUESTORECARGO.eq(CPANEL_IMPUESTO.IDIMPUESTO))
                .innerJoin(CPANEL_EVENTO).on(CPANEL_SESION.IDEVENTO.eq(CPANEL_EVENTO.IDEVENTO))
                .where(CPANEL_SESION.IDSESION.eq(sessionId.intValue())
                        .and(CPANEL_EVENTO.IDEVENTO.eq(eventId.intValue())))
                .fetchOne().into(CpanelImpuestoRecord.class);
    }

    public List<Long> getTaxesByEntity(Long entityId) {
        return dsl.select(CPANEL_IMPUESTO.IDIMPUESTO)
                .from(CPANEL_IMPUESTO, CPANEL_ENTIDAD)
                .where(CPANEL_ENTIDAD.IDENTIDAD.eq(entityId.intValue()).and(
                        CPANEL_IMPUESTO.IDOPERADORA.eq(CPANEL_ENTIDAD.IDOPERADORA)
                ))
                .fetch().into(Long.class);
    }

    public CpanelImpuestoRecord getPackTax(Long packId) {
        return dsl.select(CPANEL_IMPUESTO.fields())
                .from(CPANEL_IMPUESTO)
                .join(CPANEL_PACK).on(CPANEL_PACK.TAXID.eq(CPANEL_IMPUESTO.IDIMPUESTO))
                .where(CPANEL_PACK.IDPACK.eq(packId.intValue()))
                .fetchOneInto(CpanelImpuestoRecord.class);
    }
}
