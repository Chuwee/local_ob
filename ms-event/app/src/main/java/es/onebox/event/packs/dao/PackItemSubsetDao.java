package es.onebox.event.packs.dao;

import es.onebox.event.packs.dto.PackItemSubsetsFilter;
import es.onebox.jooq.cpanel.Tables;
import es.onebox.jooq.cpanel.tables.CpanelPackItem;
import es.onebox.jooq.cpanel.tables.CpanelPackItemSubset;
import es.onebox.jooq.cpanel.tables.records.CpanelPackItemSubsetRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.jooq.InsertSetStep;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PackItemSubsetDao extends DaoImpl<CpanelPackItemSubsetRecord, Integer> {

    private static final CpanelPackItemSubset packItemSubsetTable = Tables.CPANEL_PACK_ITEM_SUBSET;

    protected PackItemSubsetDao() {
        super(packItemSubsetTable);
    }

    public List<CpanelPackItemSubsetRecord> getSubsetsByPackItemId(Integer packItemId) {
        return dsl.select(packItemSubsetTable.fields())
                .from(packItemSubsetTable)
                .where(packItemSubsetTable.IDPACKITEM.eq(packItemId))
                .fetchInto(CpanelPackItemSubsetRecord.class);
    }

    public List<CpanelPackItemSubsetRecord> getSubsetsByPackItemId(Integer packItemId, PackItemSubsetsFilter filter) {
        return dsl.select(packItemSubsetTable.fields())
                .from(packItemSubsetTable)
                .where(packItemSubsetTable.IDPACKITEM.eq(packItemId))
                .limit(filter.getLimit().intValue())
                .offset(filter.getOffset().intValue())
                .fetchInto(CpanelPackItemSubsetRecord.class);
    }

    public Long countSubsetsByPackItemId(Integer packItemId) {
        return dsl.selectCount()
                .from(packItemSubsetTable)
                .where(packItemSubsetTable.IDPACKITEM.eq(packItemId))
                .fetchOne(0, Long.class);
    }

    public void bulkInsert(List<CpanelPackItemSubsetRecord> subsetRecords) {
        if (subsetRecords.isEmpty()) {
            return;
        }

        InsertSetStep<CpanelPackItemSubsetRecord> insertSetStep = dsl.insertInto(packItemSubsetTable);
        for (int i = 0; i < subsetRecords.size() - 1; i++) {
            insertSetStep.set(subsetRecords.get(i)).newRecord();
        }
        insertSetStep.set(subsetRecords.get(subsetRecords.size() - 1)).execute();
    }

    public void deleteAllSubsetsByPackItemId(Integer packItemId) {
        dsl.delete(packItemSubsetTable)
                .where(packItemSubsetTable.IDPACKITEM.eq(packItemId))
                .execute();
    }

    public void deleteAllSubsetsByPackId(Integer packId) {
        CpanelPackItem packItemTable = Tables.CPANEL_PACK_ITEM;
        dsl.delete(packItemSubsetTable)
                .where(packItemSubsetTable.IDPACKITEM.in(
                        dsl.select(packItemTable.IDPACKITEM)
                                .from(packItemTable)
                                .where(packItemTable.IDPACK.eq(packId))
                                .and(packItemTable.PRINCIPAL.eq(Boolean.TRUE))
                ))
                .execute();
    }
}
