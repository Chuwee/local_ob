package es.onebox.event.events.dao;

import es.onebox.jooq.cpanel.Tables;
import es.onebox.jooq.cpanel.tables.CpanelColectivo;
import es.onebox.jooq.cpanel.tables.records.CpanelColectivoRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public class CollectiveDao extends DaoImpl<CpanelColectivoRecord, Integer> {

    private static final CpanelColectivo collectiveTable = Tables.CPANEL_COLECTIVO;

    protected CollectiveDao() {
        super(collectiveTable);
    }

    public List<CpanelColectivoRecord> getBasicCollectiveInfoByIds(Set<Integer> ids) {
        return dsl.select(collectiveTable.IDCOLECTIVO, collectiveTable.NOMBRE, collectiveTable.IDSUBTIPOCOLECTIVO)
                .from(collectiveTable)
                .where(collectiveTable.IDCOLECTIVO.in(ids))
                .fetchInto(CpanelColectivoRecord.class);
    }

}
