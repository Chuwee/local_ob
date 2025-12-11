package es.onebox.event.venues.dao;

import es.onebox.jooq.cpanel.tables.records.CpanelEntidadRecintoConfigRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.springframework.stereotype.Repository;

import static es.onebox.jooq.cpanel.Tables.CPANEL_ENTIDAD_RECINTO_CONFIG;
import static es.onebox.jooq.cpanel.Tables.CPANEL_SESION;

@Repository
public class EntityVenueTemplateDao extends DaoImpl<CpanelEntidadRecintoConfigRecord, Integer> {

    protected EntityVenueTemplateDao() {
        super(CPANEL_ENTIDAD_RECINTO_CONFIG);
    }

    public CpanelEntidadRecintoConfigRecord getByVenueTemplateId(Long venueTemplateId) {
        return dsl.select().from(CPANEL_ENTIDAD_RECINTO_CONFIG).
                where(CPANEL_ENTIDAD_RECINTO_CONFIG.IDCONFIGURACION.eq(venueTemplateId.intValue())).
                fetchOneInto(CpanelEntidadRecintoConfigRecord.class);
    }

    public CpanelEntidadRecintoConfigRecord getBySessionId(Integer sessionId) {
        return dsl.select(CPANEL_ENTIDAD_RECINTO_CONFIG.fields()).
                from(CPANEL_ENTIDAD_RECINTO_CONFIG).
                join(CPANEL_SESION).on(CPANEL_ENTIDAD_RECINTO_CONFIG.IDRELACIONENTRECINTO.eq(CPANEL_SESION.IDRELACIONENTIDADRECINTO)).
                where(CPANEL_SESION.IDSESION.eq(sessionId)).
                fetchOneInto(CpanelEntidadRecintoConfigRecord.class);
    }

}
