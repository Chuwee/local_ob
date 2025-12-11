package es.onebox.event.venues.dao;

import es.onebox.jooq.cpanel.tables.records.CpanelRazonBloqueoRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

import static es.onebox.jooq.cpanel.Tables.CPANEL_RAZON_BLOQUEO;

@Repository
public class BlockingReasonDao extends DaoImpl<CpanelRazonBloqueoRecord, Integer> {

    protected BlockingReasonDao() {
        super(CPANEL_RAZON_BLOQUEO);
    }

    public List<Long> findByVenueTemplate(final Integer venueTemplateId) {
        return dsl.select(CPANEL_RAZON_BLOQUEO.IDRAZONBLOQUEO)
                .from(CPANEL_RAZON_BLOQUEO)
                .where(CPANEL_RAZON_BLOQUEO.IDCONFIGURACION.eq(venueTemplateId.intValue()))
                .fetchInto(Long.class);
    }

}
