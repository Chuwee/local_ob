package es.onebox.event.events.dao;

import es.onebox.jooq.cpanel.tables.records.CpanelElementosComCanalEventoRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.stream.Collectors;

import static es.onebox.jooq.cpanel.Tables.CPANEL_DESC_POR_IDIOMA;
import static es.onebox.jooq.cpanel.Tables.CPANEL_ELEMENTOS_COM_CANAL_EVENTO;
import static es.onebox.jooq.cpanel.Tables.CPANEL_IDIOMA;

@Repository
public class EventChannelCommElemDao extends DaoImpl<CpanelElementosComCanalEventoRecord, Integer> {


    protected EventChannelCommElemDao() {
        super(CPANEL_ELEMENTOS_COM_CANAL_EVENTO);
    }

    public Map<String, String> getEventChannelCommElem(int commElemRelationId) {
        return dsl.select(
                        CPANEL_DESC_POR_IDIOMA.DESCRIPCION,
                        CPANEL_DESC_POR_IDIOMA.IDITEM,
                        CPANEL_IDIOMA.CODIGO)
                .from(CPANEL_ELEMENTOS_COM_CANAL_EVENTO)
                .innerJoin(CPANEL_DESC_POR_IDIOMA)
                .on(CPANEL_ELEMENTOS_COM_CANAL_EVENTO.SEATSELECTIONDISCLAIMER.eq(CPANEL_DESC_POR_IDIOMA.IDITEM))
                .innerJoin(CPANEL_IDIOMA)
                .on(CPANEL_DESC_POR_IDIOMA.IDIDIOMA.eq(CPANEL_IDIOMA.IDIDIOMA))
                .where(CPANEL_ELEMENTOS_COM_CANAL_EVENTO.IDINSTANCIA.eq(commElemRelationId))
                .fetch()
                .intoGroups(CPANEL_IDIOMA.CODIGO)
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().get(0).get(CPANEL_DESC_POR_IDIOMA.DESCRIPCION)
                ));
    }
}
