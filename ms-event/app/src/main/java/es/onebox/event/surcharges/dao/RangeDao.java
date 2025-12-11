package es.onebox.event.surcharges.dao;

import es.onebox.jooq.cpanel.Tables;
import es.onebox.jooq.cpanel.tables.records.CpanelRangoRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RangeDao extends DaoImpl<CpanelRangoRecord, Integer> {

    protected RangeDao() {
        super(Tables.CPANEL_RANGO);
    }

    public int deleteByIds(List<Integer> rangeIds) {
        return this.dsl.deleteFrom(Tables.CPANEL_RANGO)
                .where(Tables.CPANEL_RANGO.IDRANGO.in(rangeIds))
                .execute();
    }

}
