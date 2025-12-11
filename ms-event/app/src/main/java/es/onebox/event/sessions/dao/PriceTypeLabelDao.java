package es.onebox.event.sessions.dao;

import es.onebox.jooq.cpanel.tables.records.CpanelZonaPrecioEtiquetaRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static es.onebox.jooq.cpanel.Tables.CPANEL_ETIQUETA;
import static es.onebox.jooq.cpanel.Tables.CPANEL_GRUPO_ETIQUETA;
import static es.onebox.jooq.cpanel.Tables.CPANEL_ZONA_PRECIO_ETIQUETA;


@Repository
public class PriceTypeLabelDao extends DaoImpl<CpanelZonaPrecioEtiquetaRecord, Integer> {

    protected PriceTypeLabelDao() {
        super(CPANEL_ZONA_PRECIO_ETIQUETA);
    }

    public List<CpanelZonaPrecioEtiquetaRecord> getByVenueTemplateId(Integer venueTemplateId) {
        return dsl.select(CPANEL_ZONA_PRECIO_ETIQUETA.fields())
                .from(CPANEL_ZONA_PRECIO_ETIQUETA)
                .join(CPANEL_ETIQUETA).on(CPANEL_ETIQUETA.IDETIQUETA.eq(CPANEL_ZONA_PRECIO_ETIQUETA.IDETIQUETA))
                .join(CPANEL_GRUPO_ETIQUETA).on(CPANEL_ETIQUETA.IDGRUPO.eq(CPANEL_GRUPO_ETIQUETA.IDGRUPO))
                .and(CPANEL_GRUPO_ETIQUETA.IDCONFIGURACION.eq(venueTemplateId))
                .fetchInto(CpanelZonaPrecioEtiquetaRecord.class);
    }

    public Map<Integer, List<CpanelZonaPrecioEtiquetaRecord>> getByVenueTemplateIds(Set<Integer> venueTemplateIds) {
        return dsl.select(CPANEL_GRUPO_ETIQUETA.IDCONFIGURACION).select(CPANEL_ZONA_PRECIO_ETIQUETA.fields())
                .from(CPANEL_ZONA_PRECIO_ETIQUETA)
                .join(CPANEL_ETIQUETA).on(CPANEL_ETIQUETA.IDETIQUETA.eq(CPANEL_ZONA_PRECIO_ETIQUETA.IDETIQUETA))
                .join(CPANEL_GRUPO_ETIQUETA).on(CPANEL_ETIQUETA.IDGRUPO.eq(CPANEL_GRUPO_ETIQUETA.IDGRUPO))
                .and(CPANEL_GRUPO_ETIQUETA.IDCONFIGURACION.in(venueTemplateIds))
                .fetchGroups(r -> r.get(CPANEL_GRUPO_ETIQUETA.IDCONFIGURACION),
                        r -> r.into(CPANEL_ZONA_PRECIO_ETIQUETA));
    }
}
