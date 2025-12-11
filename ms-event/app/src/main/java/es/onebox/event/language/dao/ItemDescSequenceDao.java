package es.onebox.event.language.dao;

import es.onebox.jooq.cpanel.Tables;
import es.onebox.jooq.cpanel.tables.records.CpanelItemDescSequenceRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.springframework.stereotype.Repository;

@Repository
public class ItemDescSequenceDao extends DaoImpl<CpanelItemDescSequenceRecord, Integer> {

    public ItemDescSequenceDao() {
        super(Tables.CPANEL_ITEM_DESC_SEQUENCE);
    }

    public Integer insertNewRecord() {
        return this.dsl.insertInto(Tables.CPANEL_ITEM_DESC_SEQUENCE).defaultValues()
                .returningResult(Tables.CPANEL_ITEM_DESC_SEQUENCE.IDITEM).fetchOne()
                .map(r -> r.getValue(Tables.CPANEL_ITEM_DESC_SEQUENCE.IDITEM));
    }
}
