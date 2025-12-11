package es.onebox.event.seasontickets.dao;

import es.onebox.event.events.dao.record.SeasonTicketChangeSeatPricesRecord;
import es.onebox.event.seasontickets.dto.changeseat.ChangeSeatSeasonTicketPriceFilter;
import es.onebox.jooq.cpanel.tables.CpanelSeasonTicketChangeSeatPrices;
import es.onebox.jooq.cpanel.tables.CpanelTarifa;
import es.onebox.jooq.cpanel.tables.CpanelZonaPreciosConfig;
import es.onebox.jooq.cpanel.tables.records.CpanelSeasonTicketChangeSeatPricesRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.apache.commons.lang3.ArrayUtils;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.SelectFieldOrAsterisk;
import org.jooq.SelectJoinStep;
import org.springframework.stereotype.Repository;

import java.util.List;

import static es.onebox.jooq.cpanel.Tables.CPANEL_SEASON_TICKET_CHANGE_SEAT_PRICES;
import static es.onebox.jooq.cpanel.Tables.CPANEL_TARIFA;
import static es.onebox.jooq.cpanel.Tables.CPANEL_ZONA_PRECIOS_CONFIG;

@Repository
public class SeasonTicketChangeSeatPricesDao extends DaoImpl<CpanelSeasonTicketChangeSeatPricesRecord, Integer> {

    private static final CpanelSeasonTicketChangeSeatPrices seasonTicketChangeSeatPrices = CPANEL_SEASON_TICKET_CHANGE_SEAT_PRICES.as("seasonTicketChangeSeatPrices");
    private static final CpanelZonaPreciosConfig zonaPreciosConfig = CPANEL_ZONA_PRECIOS_CONFIG.as("zonaPreciosConfig");
    private static final CpanelZonaPreciosConfig zonaPreciosConfigTarget = CPANEL_ZONA_PRECIOS_CONFIG.as("zonaPreciosConfigTarget");
    private static final CpanelTarifa tarifa = CPANEL_TARIFA.as("tarifa");

    private static final Field<String> JOIN_ZONA_PRECIOS_CONFIG_SOURCE_DESCRIPCION = zonaPreciosConfig.DESCRIPCION.as("sourcePriceTypeName");
    private static final Field<String> JOIN_ZONA_PRECIOS_CONFIG_TARGET_DESCRIPCION = zonaPreciosConfigTarget.DESCRIPCION.as("targetPriceTypeName");
    private static final Field<String> JOIN_TARIFA_NAME = tarifa.NOMBRE.as("rateName");

    private static final SelectFieldOrAsterisk[] JOIN_FIELDS = {
            JOIN_ZONA_PRECIOS_CONFIG_SOURCE_DESCRIPCION, JOIN_ZONA_PRECIOS_CONFIG_TARGET_DESCRIPCION, JOIN_TARIFA_NAME
    };

    protected SeasonTicketChangeSeatPricesDao() {
        super(CPANEL_SEASON_TICKET_CHANGE_SEAT_PRICES);
    }

    public void bulkInsertRecords(List<CpanelSeasonTicketChangeSeatPricesRecord> records) {
        dsl.batchInsert(records).execute();
    }

    public void bulkUpdateRecords(List<CpanelSeasonTicketChangeSeatPricesRecord> records) {
        dsl.batchUpdate(records).execute();
    }

    public Boolean existsChangeSeatPricesRelationId(Long seasonTicketId, ChangeSeatSeasonTicketPriceFilter seasonTicketPriceFilter) {
        SelectJoinStep<Record1<Integer>> query =
                dsl.select(seasonTicketChangeSeatPrices.IDPRICERELATION)
                        .from(seasonTicketChangeSeatPrices);

        query.where(buildWhere(seasonTicketId, seasonTicketPriceFilter));
        query.limit(1);

        return dsl.fetchExists(query);
    }

    public List<SeasonTicketChangeSeatPricesRecord> searchChangeSeatPricesTable(Long seasonTicketId,
                                                                                ChangeSeatSeasonTicketPriceFilter seasonTicketPriceFilter) {
        SelectFieldOrAsterisk[] fields = ArrayUtils.addAll(seasonTicketChangeSeatPrices.fields(), JOIN_FIELDS);

        SelectJoinStep<Record> query =
                dsl.select(fields)
                        .from(seasonTicketChangeSeatPrices)
                        .leftOuterJoin(zonaPreciosConfig).on(zonaPreciosConfig.IDZONA.eq(seasonTicketChangeSeatPrices.IDSOURCEPRICETYPE))
                        .leftOuterJoin(zonaPreciosConfigTarget).on(zonaPreciosConfigTarget.IDZONA.eq(seasonTicketChangeSeatPrices.IDTARGETPRICETYPE))
                        .leftOuterJoin(tarifa).on(tarifa.IDTARIFA.eq(seasonTicketChangeSeatPrices.IDRATE));

        query.where(buildWhere(seasonTicketId, seasonTicketPriceFilter));

        return query.fetch().map(this::buildSeasonTicketChangeSeatPriceRecord);
    }

    private Condition buildWhere(Long seasonTicketId, ChangeSeatSeasonTicketPriceFilter seasonTicketPriceFilter) {
        Condition conditions = seasonTicketChangeSeatPrices.IDSEASONTICKET.eq(seasonTicketId.intValue());
        if (seasonTicketPriceFilter != null) {
            if (seasonTicketPriceFilter.getSourcePriceTypeId() != null) {
                conditions = conditions.and(seasonTicketChangeSeatPrices.IDSOURCEPRICETYPE.eq(seasonTicketPriceFilter.getSourcePriceTypeId().intValue()));
            }
            if (seasonTicketPriceFilter.getTargetPriceTypeId() != null) {
                conditions = conditions.and(seasonTicketChangeSeatPrices.IDTARGETPRICETYPE.eq(seasonTicketPriceFilter.getTargetPriceTypeId().intValue()));
            }
            if (seasonTicketPriceFilter.getRateId() != null) {
                conditions = conditions.and(seasonTicketChangeSeatPrices.IDRATE.eq(seasonTicketPriceFilter.getRateId().intValue()));
            }
        }
        return conditions;
    }

    private SeasonTicketChangeSeatPricesRecord buildSeasonTicketChangeSeatPriceRecord(Record record) {
        SeasonTicketChangeSeatPricesRecord pricesRecord = record.into(SeasonTicketChangeSeatPricesRecord.class);
        pricesRecord.setSourcePriceTypeName(record.getValue(JOIN_ZONA_PRECIOS_CONFIG_SOURCE_DESCRIPCION));
        pricesRecord.setTargetPriceTypeName(record.getValue(JOIN_ZONA_PRECIOS_CONFIG_TARGET_DESCRIPCION));
        pricesRecord.setRateName(record.getValue(JOIN_TARIFA_NAME));
        return pricesRecord;
    }
}
