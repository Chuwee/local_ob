package es.onebox.event.seasontickets.dao;

import es.onebox.core.utils.common.CommonUtils;
import es.onebox.event.seasontickets.dao.record.VenueConfigStatusRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelConfigRecintoRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Result;
import org.springframework.stereotype.Repository;

import java.util.List;

import static es.onebox.jooq.cpanel.Tables.CPANEL_CONFIG_RECINTO;

@Repository
public class VenueConfigDao extends DaoImpl<CpanelConfigRecintoRecord, Integer> {

    protected VenueConfigDao() {
        super(CPANEL_CONFIG_RECINTO);
    }

    public VenueConfigStatusRecord getVenueConfigStatus(Integer venueConfigId) {
        Record1<Integer> result = dsl.select(CPANEL_CONFIG_RECINTO.ESTADO)
                .from(CPANEL_CONFIG_RECINTO)
                .where(CPANEL_CONFIG_RECINTO.IDCONFIGURACION.eq(venueConfigId))
                .fetchOne();
        return CommonUtils.ifNotNull(result, () -> result.into(VenueConfigStatusRecord.class));
    }

    public VenueConfigStatusRecord getVenueConfigStatusBySeasonTicketId(Integer seasonTicketId) {
        Record1<Integer> result = dsl.select(CPANEL_CONFIG_RECINTO.ESTADO)
                .from(CPANEL_CONFIG_RECINTO)
                .where(CPANEL_CONFIG_RECINTO.IDEVENTO.eq(seasonTicketId))
                .fetchOne();
        return CommonUtils.ifNotNull(result, () -> result.into(VenueConfigStatusRecord.class));
    }

    public List<CpanelConfigRecintoRecord> getVenueConfigListBySeasonTicketIdList(List<Long> seasonTicketIds) {
        Result<Record> result = dsl.select(CPANEL_CONFIG_RECINTO.fields())
                .from(CPANEL_CONFIG_RECINTO)
                .where(CPANEL_CONFIG_RECINTO.IDEVENTO.in(seasonTicketIds))
                .fetch();
        return CommonUtils.ifNotNull(result, () -> result.into(CpanelConfigRecintoRecord.class));
    }

    public CpanelConfigRecintoRecord getVenueConfigBySeasonTicketId(Integer seasonTicketId) {
        Record result = dsl.select(CPANEL_CONFIG_RECINTO.fields())
                .from(CPANEL_CONFIG_RECINTO)
                .where(CPANEL_CONFIG_RECINTO.IDEVENTO.eq(seasonTicketId))
                .fetchOne();
        return CommonUtils.ifNotNull(result, () -> result.into(CpanelConfigRecintoRecord.class));
    }

}
