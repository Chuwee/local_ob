package es.onebox.event.events.dao;

import es.onebox.event.common.enums.EventTagType;
import es.onebox.event.events.dao.record.CommElementRecord;
import es.onebox.event.events.request.ChannelEventCommunicationElementFilter;
import es.onebox.jooq.cpanel.tables.records.CpanelElementosComEventoCanalRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.jooq.Condition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

import static es.onebox.jooq.cpanel.Tables.CPANEL_ELEMENTOS_COM_EVENTO_CANAL;
import static es.onebox.jooq.cpanel.Tables.CPANEL_IDIOMA;

@Repository
public class ChannelEventCommunicationElementDao extends DaoImpl<CpanelElementosComEventoCanalRecord, Integer> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelEventCommunicationElementDao.class);

    protected ChannelEventCommunicationElementDao() {
        super(CPANEL_ELEMENTOS_COM_EVENTO_CANAL);
    }

    public List<CpanelElementosComEventoCanalRecord> findCommunicationElements(Long channelEventId, Long sessionId, ChannelEventCommunicationElementFilter filter) {
        return dsl.select()
                .from(CPANEL_ELEMENTOS_COM_EVENTO_CANAL)
                .where(buildWhere(channelEventId, sessionId, filter))
                .orderBy(CPANEL_ELEMENTOS_COM_EVENTO_CANAL.POSITION)
                .fetch().into(CpanelElementosComEventoCanalRecord.class);
    }

    private Condition buildWhere(Long channelEventId, Long sessionId, ChannelEventCommunicationElementFilter filter) {
        Condition where = CPANEL_ELEMENTOS_COM_EVENTO_CANAL.IDCANALEEVENTO.eq(channelEventId.intValue());

        if (filter != null) {
            if (!filter.isIncludeAllSessions()) {
                if (sessionId != null) {
                    where = where.and(CPANEL_ELEMENTOS_COM_EVENTO_CANAL.IDSESION.eq(sessionId.intValue()));
                        } else {
                    where = where.and(CPANEL_ELEMENTOS_COM_EVENTO_CANAL.IDSESION.isNull());
                }
            }

            if (filter.getLanguageId() != null) {
                where = where.and(CPANEL_ELEMENTOS_COM_EVENTO_CANAL.IDIOMA.eq(filter.getLanguageId()));
            }

            if (filter.getTags() != null && !filter.getTags().isEmpty()) {
                where = where.and(CPANEL_ELEMENTOS_COM_EVENTO_CANAL.IDTAG.in(
                        filter.getTags()
                                .stream()
                                .map(EventTagType::getId)
                                .collect(Collectors.toList())
                ));
            }
        }
        return where;
    }

    public List<CommElementRecord> getCommElementByEventIdAndChannelId(Integer channelEventId) {
        return dsl.select(
                        CPANEL_ELEMENTOS_COM_EVENTO_CANAL.IDELEMENTO,
                        CPANEL_ELEMENTOS_COM_EVENTO_CANAL.IDIOMA,
                        CPANEL_ELEMENTOS_COM_EVENTO_CANAL.VALOR,
                        CPANEL_ELEMENTOS_COM_EVENTO_CANAL.POSITION,
                        CPANEL_ELEMENTOS_COM_EVENTO_CANAL.ALTTEXT,
                        CPANEL_IDIOMA.CODIGO
                )
                .from(CPANEL_ELEMENTOS_COM_EVENTO_CANAL)
                .innerJoin(CPANEL_IDIOMA).on(CPANEL_ELEMENTOS_COM_EVENTO_CANAL.IDIOMA.eq(CPANEL_IDIOMA.IDIDIOMA))
                .where(CPANEL_ELEMENTOS_COM_EVENTO_CANAL.IDCANALEEVENTO.eq(channelEventId)
                        .and(CPANEL_ELEMENTOS_COM_EVENTO_CANAL.IDTAG.eq(EventTagType.IMG_SQUARE_BANNER_WEB.getId()))
                        .and(CPANEL_ELEMENTOS_COM_EVENTO_CANAL.IDSESION.isNull())
                        .and(CPANEL_ELEMENTOS_COM_EVENTO_CANAL.VALOR.isNotNull()))
                .fetch()
                .map(record -> {
                    CommElementRecord commEl = new CommElementRecord();
                    commEl.setLanguageCode(record.get(CPANEL_IDIOMA.CODIGO));
                    commEl.setValue(record.get(CPANEL_ELEMENTOS_COM_EVENTO_CANAL.VALOR));
                    commEl.setIdIdioma(record.get(CPANEL_ELEMENTOS_COM_EVENTO_CANAL.IDIOMA));
                    commEl.setIdItem(record.get(CPANEL_ELEMENTOS_COM_EVENTO_CANAL.IDELEMENTO));
                    commEl.setPosition(record.get(CPANEL_ELEMENTOS_COM_EVENTO_CANAL.POSITION));
                    return commEl;
                });
    }

    public List<CommElementRecord> getCommElementByEventIdAndChannelIdAndSessionId(Integer channelEventId, Integer sessionId) {
        return dsl.select(
                        CPANEL_ELEMENTOS_COM_EVENTO_CANAL.IDELEMENTO,
                        CPANEL_ELEMENTOS_COM_EVENTO_CANAL.IDIOMA,
                        CPANEL_ELEMENTOS_COM_EVENTO_CANAL.VALOR,
                        CPANEL_ELEMENTOS_COM_EVENTO_CANAL.POSITION,
                        CPANEL_IDIOMA.CODIGO
                )
                .from(CPANEL_ELEMENTOS_COM_EVENTO_CANAL)
                .innerJoin(CPANEL_IDIOMA).on(CPANEL_ELEMENTOS_COM_EVENTO_CANAL.IDIOMA.eq(CPANEL_IDIOMA.IDIDIOMA))
                .where(CPANEL_ELEMENTOS_COM_EVENTO_CANAL.IDCANALEEVENTO.eq(channelEventId)
                        .and(CPANEL_ELEMENTOS_COM_EVENTO_CANAL.IDSESION.eq(sessionId))
                        .and(CPANEL_ELEMENTOS_COM_EVENTO_CANAL.IDTAG.eq(EventTagType.IMG_SQUARE_BANNER_WEB.getId()))
                        .and(CPANEL_ELEMENTOS_COM_EVENTO_CANAL.VALOR.isNotNull()))
                .fetch()
                .map(record -> {
                    CommElementRecord commEl = new CommElementRecord();
                    commEl.setLanguageCode(record.get(CPANEL_IDIOMA.CODIGO));
                    commEl.setValue(record.get(CPANEL_ELEMENTOS_COM_EVENTO_CANAL.VALOR));
                    commEl.setIdIdioma(record.get(CPANEL_ELEMENTOS_COM_EVENTO_CANAL.IDIOMA));
                    commEl.setIdItem(record.get(CPANEL_ELEMENTOS_COM_EVENTO_CANAL.IDELEMENTO));
                    commEl.setPosition(record.get(CPANEL_ELEMENTOS_COM_EVENTO_CANAL.POSITION));
                    return commEl;
                });
    }
}
