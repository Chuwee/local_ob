package es.onebox.event.priceengine.surcharges.dao;

import es.onebox.jooq.cpanel.Tables;
import es.onebox.jooq.cpanel.tables.records.CpanelRangoRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.impl.TableImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

import static es.onebox.jooq.cpanel.Tables.CPANEL_RANGO;

@Repository
public class SurchargeRangeDao extends DaoImpl<CpanelRangoRecord, Integer> {

    protected SurchargeRangeDao() {
        super(CPANEL_RANGO);
    }

    public Integer insertInto(CpanelRangoRecord cpanelRangoRecord) {
        return dsl.insertInto(CPANEL_RANGO).set(cpanelRangoRecord).returning(CPANEL_RANGO.IDRANGO).fetchOne().getIdrango();
    }

    //Event Range Surcharges
    public List<CpanelRangoRecord> getEventSurchargeRangesByEventId(Integer eventId) {
        return this.getSurchargeRanges(Tables.CPANEL_RANGO_RECARGO_EVENTO, Tables.CPANEL_RANGO_RECARGO_EVENTO.IDEVENTO, eventId);
    }

    public List<CpanelRangoRecord> getEventPromotionSurchargeRangesByEventId(Integer eventId) {
        return this.getSurchargeRanges(Tables.CPANEL_RANGO_RECARGO_EVENTO_PROMOCION, Tables.CPANEL_RANGO_RECARGO_EVENTO_PROMOCION.IDEVENTO, eventId);
    }

    public List<CpanelRangoRecord> getEventInvitationSurchargeRangesByEventId(Integer eventId) {
        return this.getSurchargeRanges(Tables.CPANEL_RANGO_RECARGO_EVENTO_INV, Tables.CPANEL_RANGO_RECARGO_EVENTO_INV.IDEVENTO, eventId);
    }

    public List<CpanelRangoRecord> getEventSecondaryMarketSurchargeRangesByEventId(Integer eventId) {
        return this.getSurchargeRanges(Tables.CPANEL_RANGO_RECARGO_EVENTO_MERCADO_SECUNDARIO, Tables.CPANEL_RANGO_RECARGO_EVENTO_MERCADO_SECUNDARIO.IDEVENTO, eventId);
    }

    //Channel-Event Range Surcharges (event manager point of view)
    public List<CpanelRangoRecord> getChannelEventSurchargeRangesByChannelEventId(Integer channelEventId) {
        return this.getSurchargeRanges(Tables.CPANEL_RANGO_RECARGO_CANAL_EVENTO, Tables.CPANEL_RANGO_RECARGO_CANAL_EVENTO.IDCANALEVENTO, channelEventId);
    }

    public List<CpanelRangoRecord> getChannelEventPromotionSurchargeRangesByChannelEventId(Integer channelEventId) {
        return this.getSurchargeRanges(Tables.CPANEL_RANGO_RECARGO_CANAL_EVENTO_PROMOCION, Tables.CPANEL_RANGO_RECARGO_CANAL_EVENTO_PROMOCION.IDCANALEVENTO, channelEventId);
    }

    public List<CpanelRangoRecord> getChannelEventInvitationSurchargeRangesByChannelEventId(Integer channelEventId) {
        return this.getSurchargeRanges(Tables.CPANEL_RANGO_RECARGO_CANAL_EVENTO_INV, Tables.CPANEL_RANGO_RECARGO_CANAL_EVENTO_INV.IDCANALEVENTO, channelEventId);
    }

    //Channel Range Surcharges
    public List<CpanelRangoRecord> getChannelSurchargeRangesByChannelId(Integer channelId) {
        return this.getSurchargeRanges(Tables.CPANEL_RANGO_RECARGO_CANAL, Tables.CPANEL_RANGO_RECARGO_CANAL.IDCANAL, channelId);
    }

    public List<CpanelRangoRecord> getChannelPromotionSurchargeRangesByChannelId(Integer channelId) {
        return this.getSurchargeRanges(Tables.CPANEL_RANGO_RECARGO_CANAL_PROMOCION, Tables.CPANEL_RANGO_RECARGO_CANAL_PROMOCION.IDCANAL, channelId);
    }

    public List<CpanelRangoRecord> getChannelInvitationSurchargeRangesByChannelId(Integer channelId) {
        return this.getSurchargeRanges(Tables.CPANEL_RANGO_RECARGO_CANAL_INV, Tables.CPANEL_RANGO_RECARGO_CANAL_INV.IDCANAL, channelId);
    }

    public List<CpanelRangoRecord> getChannelSecondaryMarketSurchargeRangesByChannelId(Integer channelId) {
        // TODO
        //return this.getSurchargeRanges(Tables.CPANEL_RANGO_RECARGO_CANAL_MERCADO_SECUNDARIO, Tables.CPANEL_RANGO_RECARGO_CANAL_MERCADO_SECUNDARIO.IDCANAL, channelId);
        return null;
    }

    //Event-Channel Range Surcharges (channel manager point of view)
    public List<CpanelRangoRecord> getEventChannelSurchargeRangesByEventChannelId(Integer eventChannelId) {
        return this.getSurchargeRanges(Tables.CPANEL_RANGO_RECARGO_EVENTO_CANAL, Tables.CPANEL_RANGO_RECARGO_EVENTO_CANAL.IDEVENTOCANAL, eventChannelId);
    }

    public List<CpanelRangoRecord> getEventChannelPromotionSurchargeRangesByEventChannelId(Integer eventChannelId) {
        return this.getSurchargeRanges(Tables.CPANEL_RANGO_RECARGO_EVENTO_CANAL_PROMOCION, Tables.CPANEL_RANGO_RECARGO_EVENTO_CANAL_PROMOCION.IDEVENTOCANAL, eventChannelId);
    }

    public List<CpanelRangoRecord> getEventChannelInvitationSurchargeRangesByEventChannelId(Integer eventChannelId) {
        return this.getSurchargeRanges(Tables.CPANEL_RANGO_RECARGO_EVENTO_CANAL_INV, Tables.CPANEL_RANGO_RECARGO_EVENTO_CANAL_INV.IDEVENTOCANAL, eventChannelId);
    }

    public List<CpanelRangoRecord> getEntitySurchargeRangesByEventId(Integer eventId) {
        return dsl.select(CPANEL_RANGO.fields()).from(CPANEL_RANGO)
                .innerJoin(Tables.CPANEL_RANGO_RECARGO_ENTIDAD).on(Tables.CPANEL_RANGO_RECARGO_ENTIDAD.IDRANGO.eq(CPANEL_RANGO.IDRANGO))
                .innerJoin(Tables.CPANEL_EVENTO).on(Tables.CPANEL_EVENTO.IDENTIDAD.eq(Tables.CPANEL_RANGO_RECARGO_ENTIDAD.IDENTIDAD))
                .where(Tables.CPANEL_EVENTO.IDEVENTO.eq(eventId))
                .orderBy(CPANEL_RANGO.RANGOMINIMO.asc())
                .fetchInto(CpanelRangoRecord.class);
    }

    public List<CpanelRangoRecord> getEntitySecondaryMarketSurchargeRangesByEventId(Integer eventId) {
        return dsl.select(CPANEL_RANGO.fields()).from(CPANEL_RANGO)
                .innerJoin(Tables.CPANEL_RANGO_RECARGO_ENTIDAD_MERCADO_SECUNDARIO).on(Tables.CPANEL_RANGO_RECARGO_ENTIDAD_MERCADO_SECUNDARIO.IDRANGO.eq(CPANEL_RANGO.IDRANGO))
                .innerJoin(Tables.CPANEL_EVENTO).on(Tables.CPANEL_EVENTO.IDENTIDAD.eq(Tables.CPANEL_RANGO_RECARGO_ENTIDAD_MERCADO_SECUNDARIO.IDENTIDAD))
                .where(Tables.CPANEL_EVENTO.IDEVENTO.eq(eventId))
                .orderBy(CPANEL_RANGO.RANGOMINIMO.asc())
                .fetchInto(CpanelRangoRecord.class);
    }

    private <T extends Record> List<CpanelRangoRecord> getSurchargeRanges(TableImpl<T> table, Field<Integer> filterField, Integer filterValue) {
        return dsl.select(CPANEL_RANGO.fields()).from(CPANEL_RANGO)
                .innerJoin(table).on(table.field("idRango", Integer.class).eq(CPANEL_RANGO.IDRANGO))
                .where(table.field(filterField).eq(filterValue))
                .orderBy(CPANEL_RANGO.RANGOMINIMO.asc())
                .fetchInto(CpanelRangoRecord.class);
    }
}
