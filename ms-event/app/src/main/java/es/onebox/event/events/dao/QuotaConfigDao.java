package es.onebox.event.events.dao;

import es.onebox.jooq.cpanel.Tables;
import es.onebox.jooq.cpanel.tables.records.CpanelCuposConfigRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class QuotaConfigDao extends DaoImpl<CpanelCuposConfigRecord, Integer> {

    protected QuotaConfigDao() {
        super(Tables.CPANEL_CUPOS_CONFIG);
    }


    public List<Integer> getQuotasConfigByVenueTemplateId(Integer venueTemplateId) {
        return dsl.selectDistinct(Tables.CPANEL_CUPOS_CONFIG.IDCUPO)
                .from(Tables.CPANEL_CUPOS_CONFIG)
                .where(Tables.CPANEL_CUPOS_CONFIG.IDCONFIGURACION.eq(venueTemplateId))
                .fetchInto(Integer.class);
    }

}
