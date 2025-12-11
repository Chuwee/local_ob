package es.onebox.event.catalog.elasticsearch.utils;

import es.onebox.event.catalog.dao.record.SessionForCatalogRecord;
import es.onebox.event.events.enums.ChannelEventStatus;
import es.onebox.event.events.enums.EventStatus;
import es.onebox.event.events.enums.SessionState;
import es.onebox.event.sessions.dto.SessionStatus;
import es.onebox.jooq.cpanel.tables.records.CpanelCanalEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelSesionRecord;

import static es.onebox.event.common.utils.ConverterUtils.isByteAsATrue;

public class CatalogPublishableUtils {

    private CatalogPublishableUtils() {
        throw new UnsupportedOperationException("Cannot instantiate utilities class");
    }

    public static boolean isNotDeleted(CpanelSesionRecord s) {
        return !s.getEstado().equals(SessionState.DELETED.value());
    }

    public static boolean isPublishable(CpanelEventoRecord event) {
        return EventStatus.READY.getId().equals(event.getEstado());
    }

    public static boolean isPublishable(CpanelCanalEventoRecord channelEvent) {
        return ChannelEventStatus.ACCEPTED.getId().equals(channelEvent.getEstadorelacion()) && channelEvent.getPublicado() == 1;
    }

    public static boolean isPublishable(CpanelSesionRecord session) {
        return SessionStatus.READY.getId().equals(session.getEstado()) && session.getPublicado() == 1 && !session.getIspreview();
    }

    public static boolean isSessionPublishable(CpanelSesionRecord session, CpanelEventoRecord event, CpanelCanalEventoRecord channelEvent) {
        return isPublishable(channelEvent) && isPublishable(session) && isPublishable(event);
    }

    public static boolean isSessionPreviewable(CpanelSesionRecord session) {
        return SessionStatus.READY.getId().equals(session.getEstado()) && session.getPublicado() == 1 && session.getIspreview();
    }

    public static boolean isEventChannelSessionIndexable(CpanelSesionRecord session, CpanelEventoRecord event, CpanelCanalEventoRecord channelEvent) {
        //TODO-RAUL check isPublishable(session) redundancy
        return isPublishable(channelEvent) && isPublishable(session) && isPublishable(event) && (isPublishable(session) || isSessionPreviewable(session));
    }

    public static Boolean isSessionPublished(CpanelSesionRecord session) {
        if (SessionStatus.READY.getId().equals(session.getEstado()) && Boolean.TRUE.equals(isByteAsATrue(session.getPublicado()))) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public static boolean isSessionToIndex(SessionForCatalogRecord session) {
        return isNotDeleted(session) && (isPublishable(session) || isSessionPreviewable(session));
    }

}
