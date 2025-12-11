package es.onebox.event.packs.dao;

import es.onebox.jooq.cpanel.Tables;
import es.onebox.jooq.cpanel.tables.CpanelPackItem;
import es.onebox.jooq.cpanel.tables.records.CpanelPackItemRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PackItemsDao extends DaoImpl<CpanelPackItemRecord, Integer> {

    private static final CpanelPackItem packItemTable = Tables.CPANEL_PACK_ITEM;

    protected PackItemsDao() {
        super(packItemTable);
    }

    public List<CpanelPackItemRecord> getPackItemRecordsById(Integer packId) {
        return dsl.select(packItemTable.fields())
                .from(packItemTable)
                .where(packItemTable.IDPACK.eq(packId))
                .fetchInto(CpanelPackItemRecord.class);
    }

    public CpanelPackItemRecord getPackItemRecordById(Integer packItemId) {
        return dsl.select(packItemTable.fields())
                .from(packItemTable)
                .where(packItemTable.IDPACKITEM.eq(packItemId))
                .fetchOneInto(CpanelPackItemRecord.class);
    }

    public CpanelPackItemRecord getPackMainItemRecordById(Integer packId) {
        return dsl.select(packItemTable.fields())
                .from(packItemTable)
                .where(packItemTable.IDPACK.eq(packId))
                .and(packItemTable.PRINCIPAL.eq(Boolean.TRUE))
                .fetchOneInto(CpanelPackItemRecord.class);
    }

    public void deletePackItemRecordById(Integer packItemId) {
        dsl.delete(packItemTable)
                .where(packItemTable.IDPACKITEM.eq(packItemId))
                .execute();
    }

    public void deleteAllPackItems(Integer packId) {
        dsl.delete(packItemTable)
                .where(packItemTable.IDPACK.eq(packId))
                .execute();
    }
}
