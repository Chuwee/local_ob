package es.onebox.event.packs.dao;

import es.onebox.event.packs.dao.domain.PackRateRecord;
import es.onebox.jooq.cpanel.Tables;
import es.onebox.jooq.cpanel.tables.CpanelTarifa;
import es.onebox.jooq.cpanel.tables.CpanelTarifaPack;
import es.onebox.jooq.cpanel.tables.records.CpanelTarifaPackRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PackRateDao extends DaoImpl<CpanelTarifaPackRecord, Integer> {

    protected PackRateDao() {
        super(Tables.CPANEL_TARIFA_PACK);
    }

    public List<CpanelTarifaPackRecord> getRatesByPackId(Integer packId) {
        return dsl.select()
                .from(Tables.CPANEL_TARIFA_PACK)
                .where(Tables.CPANEL_TARIFA_PACK.IDPACK.eq(packId))
                .fetchInto(CpanelTarifaPackRecord.class);
    }

    public CpanelTarifaPackRecord findPackRateById(Integer rateId, Integer packId) {
        return dsl.select()
                .from(Tables.CPANEL_TARIFA_PACK)
                .where(Tables.CPANEL_TARIFA_PACK.IDPACK.eq(packId))
                .and(Tables.CPANEL_TARIFA_PACK.IDTARIFA.eq(rateId))
                .fetchOneInto(CpanelTarifaPackRecord.class);
    }

    public void resetDefaultsByPackId(Integer packId) {
        dsl.update(Tables.CPANEL_TARIFA_PACK).set(Tables.CPANEL_TARIFA_PACK.DEFECTO, Boolean.FALSE)
                .where(Tables.CPANEL_TARIFA_PACK.IDPACK.eq(packId)).execute();
    }

    public List<PackRateRecord> getDetailedRatesByPackId(Integer packId) {
        CpanelTarifaPack packRate = Tables.CPANEL_TARIFA_PACK;
        CpanelTarifa rate = Tables.CPANEL_TARIFA;
        return dsl.select(packRate.fields())
                .select(rate.NOMBRE.as("name"))
                .from(packRate)
                .join(rate).on(rate.IDTARIFA.eq(packRate.IDTARIFAEVENTO))
                .where(packRate.IDPACK.eq(packId))
                .fetch(record -> {
                    PackRateRecord packRateRecord = new PackRateRecord();
                    packRateRecord.from(record.into(Tables.CPANEL_TARIFA_PACK));
                    packRateRecord.setName(record.get("name", String.class));
                    return packRateRecord;
                });
    }

}
