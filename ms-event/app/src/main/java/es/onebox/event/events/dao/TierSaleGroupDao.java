package es.onebox.event.events.dao;

import es.onebox.event.events.dao.record.TierSaleGroupRecord;
import es.onebox.jooq.cpanel.Tables;
import es.onebox.jooq.cpanel.tables.records.CpanelTierCupoRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.jooq.Record;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

import static es.onebox.jooq.cpanel.Tables.CPANEL_TIER;
import static es.onebox.jooq.cpanel.Tables.CPANEL_TIER_CUPO;

@Repository
public class TierSaleGroupDao extends DaoImpl<CpanelTierCupoRecord, CpanelTierCupoRecord> {

    protected TierSaleGroupDao() {
        super(CPANEL_TIER_CUPO);
    }

    public CpanelTierCupoRecord getByTierAndSaleGroup(int tierId, int saleGroupId) {
        Record result = dsl.select()
                .from(CPANEL_TIER_CUPO)
                .where(CPANEL_TIER_CUPO.IDCUPO.eq(saleGroupId))
                .and(CPANEL_TIER_CUPO.IDTIER.eq(tierId))
                .fetchOne();
        if (result == null) {
            return null;
        }
        return result.into(CpanelTierCupoRecord.class);
    }

    public int delete(int tierId, int saleGroupId) {
        return dsl.deleteFrom(Tables.CPANEL_TIER_CUPO)
                .where(Tables.CPANEL_TIER_CUPO.IDCUPO.eq(saleGroupId))
                .and(Tables.CPANEL_TIER_CUPO.IDTIER.eq(tierId))
                .execute();
    }

    public int deleteByTierId(int tierId) {
        return dsl.deleteFrom(Tables.CPANEL_TIER_CUPO)
                .where(Tables.CPANEL_TIER_CUPO.IDTIER.eq(tierId))
                .execute();
    }

    private TierSaleGroupRecord buildRecord(Record record) {
        TierSaleGroupRecord tierSaleGroupRecord = new TierSaleGroupRecord();
        tierSaleGroupRecord.setIdcupo(record.get(CPANEL_TIER_CUPO.IDCUPO));
        tierSaleGroupRecord.setIdtier(record.get(CPANEL_TIER_CUPO.IDTIER));
        tierSaleGroupRecord.setLimite(record.get(CPANEL_TIER_CUPO.LIMITE));
        tierSaleGroupRecord.setPriceTypeId(record.get(CPANEL_TIER.IDZONA));
        tierSaleGroupRecord.setTierLimit(record.get(CPANEL_TIER.LIMITE));
        return tierSaleGroupRecord;
    }

    public List<TierSaleGroupRecord> getByTierIds(Collection<Long> activeTierIds) {
        return dsl.select(CPANEL_TIER_CUPO.fields())
                .select(CPANEL_TIER.LIMITE, CPANEL_TIER.IDZONA)
                .from(CPANEL_TIER_CUPO)
                .join(CPANEL_TIER).on(CPANEL_TIER.IDTIER.eq(CPANEL_TIER_CUPO.IDTIER))
                .where(CPANEL_TIER_CUPO.IDTIER.in(activeTierIds))
                .fetch()
                .map(this::buildRecord);
    }

    public List<CpanelTierCupoRecord> getBySaleGroupId(int saleGroupId) {
        return dsl.select()
                .from(CPANEL_TIER_CUPO)
                .where(CPANEL_TIER_CUPO.IDCUPO.eq(saleGroupId))
                .fetchInto(CpanelTierCupoRecord.class);
    }

    public int deleteBySaleGroupId(int saleGroupId) {
        return dsl.deleteFrom(CPANEL_TIER_CUPO)
                .where(CPANEL_TIER_CUPO.IDCUPO.eq(saleGroupId))
                .execute();
    }
}
