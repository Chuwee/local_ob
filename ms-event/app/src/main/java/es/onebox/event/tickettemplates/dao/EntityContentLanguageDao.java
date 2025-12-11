package es.onebox.event.tickettemplates.dao;

import es.onebox.jooq.cpanel.tables.CpanelIdiomaContenidoEntidad;
import es.onebox.jooq.cpanel.tables.records.CpanelIdiomaContenidoEntidadRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

import static es.onebox.jooq.cpanel.Tables.CPANEL_IDIOMA_CONTENIDO_ENTIDAD;

@Repository
public class EntityContentLanguageDao extends DaoImpl<CpanelIdiomaContenidoEntidadRecord, Integer>{

    protected EntityContentLanguageDao() {
        super(CpanelIdiomaContenidoEntidad.CPANEL_IDIOMA_CONTENIDO_ENTIDAD);
    }

    public List<Integer> getEntityContentLanguageIds(Integer entityId) {
        return dsl.select(CPANEL_IDIOMA_CONTENIDO_ENTIDAD.IDIDIOMA)
                    .from(CPANEL_IDIOMA_CONTENIDO_ENTIDAD)
                    .where(CPANEL_IDIOMA_CONTENIDO_ENTIDAD.IDENTIDAD.eq(entityId))
                    .fetchInto(Integer.class);
    }
}