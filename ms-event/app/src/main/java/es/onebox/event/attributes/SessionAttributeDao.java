package es.onebox.event.attributes;

import es.onebox.jooq.cpanel.tables.records.CpanelAtributosSesionRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

import static es.onebox.jooq.cpanel.Tables.CPANEL_ATRIBUTOS_SESION;

@Repository
public class SessionAttributeDao extends DaoImpl<CpanelAtributosSesionRecord, Integer> {

    protected SessionAttributeDao() {
        super(CPANEL_ATRIBUTOS_SESION);
    }

    public List<CpanelAtributosSesionRecord> getSessionAttributes(Integer sessionId) {
        return dsl.select()
                .from(CPANEL_ATRIBUTOS_SESION)
                .where(CPANEL_ATRIBUTOS_SESION.IDSESION.eq(sessionId))
                .fetch().into(CpanelAtributosSesionRecord.class);
    }

    public List<CpanelAtributosSesionRecord> getSessionAttribute(Integer sessionId, Integer attributeId) {
        return dsl.select()
                .from(CPANEL_ATRIBUTOS_SESION)
                .where(CPANEL_ATRIBUTOS_SESION.IDSESION.eq(sessionId).and(CPANEL_ATRIBUTOS_SESION.IDATRIBUTO.eq(attributeId)))
                .fetch().into(CpanelAtributosSesionRecord.class);
    }

}
