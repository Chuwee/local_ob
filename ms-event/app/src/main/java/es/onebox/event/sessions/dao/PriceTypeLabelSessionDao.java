package es.onebox.event.sessions.dao;

import es.onebox.jooq.cpanel.tables.records.CpanelZonaPrecioEtiquetaSesionRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.util.Set;

import static es.onebox.jooq.cpanel.tables.CpanelZonaPrecioEtiquetaSesion.CPANEL_ZONA_PRECIO_ETIQUETA_SESION;


@Repository
public class PriceTypeLabelSessionDao extends DaoImpl<CpanelZonaPrecioEtiquetaSesionRecord, Integer> {

    protected PriceTypeLabelSessionDao() {
        super(CPANEL_ZONA_PRECIO_ETIQUETA_SESION);
    }

    public void clone(Long fromSessionId, Long toSessionId) {
        dsl.insertInto(CPANEL_ZONA_PRECIO_ETIQUETA_SESION,
                CPANEL_ZONA_PRECIO_ETIQUETA_SESION.IDSESION,
                CPANEL_ZONA_PRECIO_ETIQUETA_SESION.IDZONAPRECIO,
                CPANEL_ZONA_PRECIO_ETIQUETA_SESION.IDETIQUETA)
                .select(dsl.select(DSL.inline(toSessionId.intValue()),
                        CPANEL_ZONA_PRECIO_ETIQUETA_SESION.IDZONAPRECIO,
                        CPANEL_ZONA_PRECIO_ETIQUETA_SESION.IDETIQUETA)
                        .from(CPANEL_ZONA_PRECIO_ETIQUETA_SESION)
                        .where(CPANEL_ZONA_PRECIO_ETIQUETA_SESION.IDSESION.eq(fromSessionId.intValue())))
                .execute();
    }

    public void delete(Long priceTypeId, Long sessionId) {
        dsl.delete(CPANEL_ZONA_PRECIO_ETIQUETA_SESION)
                .where(CPANEL_ZONA_PRECIO_ETIQUETA_SESION.IDZONAPRECIO.eq(priceTypeId.intValue())
                        .and(CPANEL_ZONA_PRECIO_ETIQUETA_SESION.IDSESION.eq(sessionId.intValue()))).execute();
    }

    public void deleteBySessionIds(Set<Integer> sessionIds) {
        dsl.delete(CPANEL_ZONA_PRECIO_ETIQUETA_SESION)
                .where(CPANEL_ZONA_PRECIO_ETIQUETA_SESION.IDSESION.in(sessionIds))
                .execute();
    }
}
