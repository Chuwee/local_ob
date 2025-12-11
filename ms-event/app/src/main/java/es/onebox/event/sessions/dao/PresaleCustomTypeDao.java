package es.onebox.event.sessions.dao;

import es.onebox.jooq.cpanel.tables.records.CpanelPreventaCustomTypeRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

import static es.onebox.jooq.cpanel.tables.CpanelPreventaCustomType.CPANEL_PREVENTA_CUSTOM_TYPE;

@Repository
public class PresaleCustomTypeDao extends DaoImpl<CpanelPreventaCustomTypeRecord, Integer> {

    protected PresaleCustomTypeDao() {
        super(CPANEL_PREVENTA_CUSTOM_TYPE);
    }

    public List<Integer> findPresaleCustomTypeIds(Long presaleId) {
        return dsl.select(CPANEL_PREVENTA_CUSTOM_TYPE.CUSTOMTYPEID)
                .from(CPANEL_PREVENTA_CUSTOM_TYPE)
                .where(CPANEL_PREVENTA_CUSTOM_TYPE.PRESALESID.eq(presaleId.intValue()))
                .fetch().into(Integer.class);
    }

    public void deleteByPresaleId(Integer presaleId) {
        dsl.delete(CPANEL_PREVENTA_CUSTOM_TYPE)
                .where(CPANEL_PREVENTA_CUSTOM_TYPE.PRESALESID.eq(presaleId))
                .execute();
    }
}
