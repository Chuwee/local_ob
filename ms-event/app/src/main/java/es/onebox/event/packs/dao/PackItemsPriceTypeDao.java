package es.onebox.event.packs.dao;

import es.onebox.jooq.cpanel.Tables;
import es.onebox.jooq.cpanel.tables.CpanelPackItemZonaPrecio;
import es.onebox.jooq.cpanel.tables.records.CpanelPackItemZonaPrecioRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.jooq.InsertSetStep;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PackItemsPriceTypeDao extends DaoImpl<CpanelPackItemZonaPrecioRecord, Integer> {

    private static final CpanelPackItemZonaPrecio packItemPriceTypeTable = Tables.CPANEL_PACK_ITEM_ZONA_PRECIO;

    protected PackItemsPriceTypeDao() {
        super(packItemPriceTypeTable);
    }

    public List<CpanelPackItemZonaPrecioRecord> getPackItemPriceTypesById(Integer packItemId) {
        return dsl.select(packItemPriceTypeTable.fields())
                .from(packItemPriceTypeTable)
                .where(packItemPriceTypeTable.IDPACKITEM.eq(packItemId))
                .fetchInto(CpanelPackItemZonaPrecioRecord.class);
    }

    public void bulkInsert(List<CpanelPackItemZonaPrecioRecord> packItemPriceTypes) {
        InsertSetStep<CpanelPackItemZonaPrecioRecord> insertSetStep = dsl.insertInto(packItemPriceTypeTable);
        for (int i = 0; i < packItemPriceTypes.size() - 1; i++) {
            insertSetStep.set(packItemPriceTypes.get(i)).newRecord();
        }
        insertSetStep.set(packItemPriceTypes.get(packItemPriceTypes.size() - 1)).execute();
    }

    public void deletePackItemPriceTypesByConfigIdAndPackId(Integer packItemId) {
        dsl.delete(packItemPriceTypeTable)
                .where(packItemPriceTypeTable.IDPACKITEM.eq(packItemId))
                .execute();
    }
}
