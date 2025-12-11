package es.onebox.mgmt.sessions.converters;

import es.onebox.mgmt.sessions.enums.SessionStatus;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

public class SessionStatusConverter {

    private SessionStatusConverter() {
    }

    public static List<es.onebox.mgmt.datasources.ms.event.dto.session.SessionStatus> toMs(List<SessionStatus> sessionStatus) {
        if (CollectionUtils.isEmpty(sessionStatus)) {
            return null;
        } else {
            return sessionStatus.stream().map(status -> switch (status) {
                case PLANNED -> es.onebox.mgmt.datasources.ms.event.dto.session.SessionStatus.PLANNED;
                case PREVIEW -> es.onebox.mgmt.datasources.ms.event.dto.session.SessionStatus.PREVIEW;
                case SCHEDULED -> es.onebox.mgmt.datasources.ms.event.dto.session.SessionStatus.SCHEDULED;
                case READY -> es.onebox.mgmt.datasources.ms.event.dto.session.SessionStatus.READY;
                case CANCELLED -> es.onebox.mgmt.datasources.ms.event.dto.session.SessionStatus.CANCELLED;
                case NOT_ACCOMPLISHED -> es.onebox.mgmt.datasources.ms.event.dto.session.SessionStatus.NOT_ACCOMPLISHED;
                case IN_PROGRESS -> es.onebox.mgmt.datasources.ms.event.dto.session.SessionStatus.IN_PROGRESS;
                case FINALIZED -> es.onebox.mgmt.datasources.ms.event.dto.session.SessionStatus.FINALIZED;
                case CANCELLED_EXTERNAL -> es.onebox.mgmt.datasources.ms.event.dto.session.SessionStatus.CANCELLED_EXTERNAL;
            }).collect(Collectors.toList());
        }
    }
}
