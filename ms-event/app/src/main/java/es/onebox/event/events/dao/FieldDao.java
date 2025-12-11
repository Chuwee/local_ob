package es.onebox.event.events.dao;

import es.onebox.jooq.cpanel.Tables;
import es.onebox.jooq.cpanel.tables.records.CpanelFieldRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

import static es.onebox.jooq.cpanel.tables.CpanelField.CPANEL_FIELD;

@Repository
public class FieldDao extends DaoImpl<CpanelFieldRecord, Integer> {

    protected FieldDao() {
        super(Tables.CPANEL_FIELD);
    }


    public Long count() {
        return dsl.selectCount()
                .from(CPANEL_FIELD)
                .fetchOne(0, Long.class);
    }

    public List<CpanelFieldRecord> getFields() {
        return dsl.select()
                .from(CPANEL_FIELD)
                .fetchInto(CpanelFieldRecord.class);
    }
}
