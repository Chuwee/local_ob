package es.onebox.event.events.dao;

import es.onebox.jooq.cpanel.tables.records.CpanelRangoRecargoCanalEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelRangoRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.jooq.Record1;
import org.jooq.Result;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import static es.onebox.jooq.cpanel.Tables.CPANEL_RANGO;
import static es.onebox.jooq.cpanel.Tables.CPANEL_RANGO_RECARGO_EVENTO;
import static es.onebox.jooq.cpanel.tables.CpanelRangoRecargoCanalEvento.CPANEL_RANGO_RECARGO_CANAL_EVENTO;

@Repository
public class ChannelEventSurchargeRangeDao extends DaoImpl<CpanelRangoRecargoCanalEventoRecord, CpanelRangoRecargoCanalEventoRecord> {

    protected ChannelEventSurchargeRangeDao() {
        super(CPANEL_RANGO_RECARGO_CANAL_EVENTO);
    }

    public void deleteByChannelEventId(Integer channelEventId) {
        dsl.delete(CPANEL_RANGO_RECARGO_CANAL_EVENTO)
                .where(CPANEL_RANGO_RECARGO_CANAL_EVENTO.IDCANALEVENTO.eq(channelEventId))
                .execute();
    }


    public void inheritChargesRangesFromEvent(Integer channelEventId, Integer eventId) {

        //first we clone the surcharge ranges belonging to the channel and recover the inserted values
        Result<Record1<Integer>> createdRangesIds = dsl.insertInto(CPANEL_RANGO, CPANEL_RANGO.NOMBRERANGO,
                CPANEL_RANGO.RANGOMAXIMO, CPANEL_RANGO.RANGOMINIMO, CPANEL_RANGO.VALOR, CPANEL_RANGO.PORCENTAJE,
                CPANEL_RANGO.VALORMAXIMO, CPANEL_RANGO.VALORMINIMO, CPANEL_RANGO.IDCURRENCY)
                .select(dsl.select(CPANEL_RANGO.NOMBRERANGO, CPANEL_RANGO.RANGOMAXIMO, CPANEL_RANGO.RANGOMINIMO,
                        CPANEL_RANGO.VALOR, CPANEL_RANGO.PORCENTAJE, CPANEL_RANGO.VALORMAXIMO, CPANEL_RANGO.VALORMINIMO, CPANEL_RANGO.IDCURRENCY)
                        .from(CPANEL_RANGO)
                        .join(CPANEL_RANGO_RECARGO_EVENTO).on(CPANEL_RANGO.IDRANGO.eq(CPANEL_RANGO_RECARGO_EVENTO.IDRANGO))
                        .and(CPANEL_RANGO_RECARGO_EVENTO.IDEVENTO.eq(eventId))
                )
                .returningResult(CPANEL_RANGO.IDRANGO).fetch();

        // Then we associate the cloned ranges to the channelEvent object
        Timestamp now = Timestamp.from(Instant.now());

        dsl.batchInsert(
                createdRangesIds.stream().map(r -> new CpanelRangoRecargoCanalEventoRecord(channelEventId, r.get(CPANEL_RANGO.IDRANGO), now, now))
                        .collect(Collectors.toList())
        ).execute();
    }

    public List<CpanelRangoRecord> getByChannelEventId(Integer channelEventId) {
        return dsl.select(CPANEL_RANGO.fields())
                .from(CPANEL_RANGO)
                .innerJoin(CPANEL_RANGO_RECARGO_CANAL_EVENTO).on(CPANEL_RANGO_RECARGO_CANAL_EVENTO.IDRANGO.eq(CPANEL_RANGO.IDRANGO))
                .where(CPANEL_RANGO_RECARGO_CANAL_EVENTO.IDCANALEVENTO.eq(channelEventId))
                .orderBy(CPANEL_RANGO.RANGOMINIMO)
                .fetch()
                .into(CpanelRangoRecord.class);
    }
}
