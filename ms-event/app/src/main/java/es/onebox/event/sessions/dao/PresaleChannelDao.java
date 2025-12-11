package es.onebox.event.sessions.dao;

import es.onebox.jooq.cpanel.tables.records.CpanelPreventaCanalRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

import static es.onebox.jooq.cpanel.tables.CpanelPreventaCanal.CPANEL_PREVENTA_CANAL;

@Repository
public class PresaleChannelDao extends DaoImpl<CpanelPreventaCanalRecord, Integer> {

    protected PresaleChannelDao() {
        super(CPANEL_PREVENTA_CANAL);
    }

    public List<Integer> findPresaleChannelIds(Long presaleId) {
        return dsl.select(CPANEL_PREVENTA_CANAL.IDCANAL)
                .from(CPANEL_PREVENTA_CANAL)
                .where(CPANEL_PREVENTA_CANAL.IDPREVENTA.eq(presaleId.intValue()))
                .fetch().into(Integer.class);
    }

    public void deleteByPresaleId(Integer presaleId) {
        dsl.delete(CPANEL_PREVENTA_CANAL)
                .where(CPANEL_PREVENTA_CANAL.IDPREVENTA.eq(presaleId))
                .execute();
    }
}
