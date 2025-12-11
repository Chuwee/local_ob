package es.onebox.event.sessions;

import es.onebox.core.exception.ErrorCode;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.exception.MsEventSessionErrorCode;
import es.onebox.event.sessions.dao.SessionDao;
import es.onebox.event.sessions.dao.record.SessionRecord;
import es.onebox.event.sessions.dto.SessionStatus;
import es.onebox.event.sessions.request.SessionSearchFilter;
import es.onebox.event.sorting.SessionField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static es.onebox.event.exception.MsEventSessionErrorCode.SESSION_NOT_MATCH_EVENT;

@Component
public class SessionValidationHelper {

    private static final List<String> SESSION_FIELDS = new ArrayList<>();

    static {
        SESSION_FIELDS.add(SessionField.ID.getRequestField());
        SESSION_FIELDS.add(SessionField.EVENT_ID.getRequestField());
        SESSION_FIELDS.add(SessionField.STATUS.getRequestField());
        SESSION_FIELDS.add(SessionField.ENTITY_ID.getRequestField());
        SESSION_FIELDS.add(SessionField.OPERATOR_ID.getRequestField());
        SESSION_FIELDS.add(SessionField.TICKET_COMMUNICATION_ELEMENT_TICKET_OFFICE.getRequestField());
        SESSION_FIELDS.add(SessionField.TICKET_COMMUNICATION_ELEMENT_PDF.getRequestField());
    }

    @Autowired
    private SessionDao sessionDao;

    public SessionRecord getSessionAndValidateWithEvent(Long eventId, Long sessionId) {
        SessionRecord sessionRecord = getSessionAndValidate(sessionId);
        if (eventId != null && !eventId.equals(sessionRecord.getIdevento().longValue())) {
            throw OneboxRestException.builder(SESSION_NOT_MATCH_EVENT).
                    setMessage("Session: " + sessionId + " not matches with event: " + eventId).build();
        }
        return sessionRecord;
    }

    public SessionRecord getSessionAndValidate(Long sessionId) {
        validateId(sessionId, MsEventSessionErrorCode.SESSION_ID_INVALID);
        SessionRecord sessionRecord = sessionDao.findSession(sessionId);
        if (sessionRecord == null || SessionStatus.DELETED.equals(SessionStatus.byId(sessionRecord.getEstado()))) {
            throw OneboxRestException.builder(MsEventSessionErrorCode.SESSION_NOT_FOUND).
                    setMessage("Session: " + sessionId + " not found").build();
        }
        return sessionRecord;
    }

    public SessionRecord getSessionAndValidate(Long eventId, Long sessionId) {
        validateId(sessionId, MsEventSessionErrorCode.SESSION_ID_INVALID);
        SessionRecord sessionRecord = sessionDao.findSession(eventId, sessionId);
        if (sessionRecord == null || SessionStatus.DELETED.equals(SessionStatus.byId(sessionRecord.getEstado()))) {
            throw OneboxRestException.builder(MsEventSessionErrorCode.SESSION_NOT_FOUND).
                    setMessage("Session: " + sessionId + " not found").build();
        }
        return sessionRecord;
    }



    public Map<Integer, SessionRecord> getSessionsAndValidateWithEvent(Long eventId, List<Long> sessionIds) {
        validateId(eventId, MsEventErrorCode.EVENT_ID_INVALID);
        sessionIds.forEach(id -> validateId(id, MsEventSessionErrorCode.SESSION_ID_INVALID));
        SessionSearchFilter sessionSearchFilter = new SessionSearchFilter();
        sessionSearchFilter.setIds(sessionIds);
        sessionSearchFilter.setFields(SESSION_FIELDS);
        List<SessionRecord> sessionRecords = sessionDao.findSessions(sessionSearchFilter, null);
        if (sessionRecords.isEmpty() || sessionRecords.size() < sessionIds.size()) {
            throw OneboxRestException.builder(MsEventSessionErrorCode.SESSION_NOT_FOUND).build();
        }
        for (SessionRecord record: sessionRecords) {
            if (SessionStatus.DELETED.equals(SessionStatus.byId(record.getEstado()))) {
                throw OneboxRestException.builder(MsEventSessionErrorCode.SESSION_NOT_FOUND).
                        setMessage("Session: " + record.getIdsesion() + " not found").build();
            }
            if (!eventId.equals(record.getIdevento().longValue())) {
                throw OneboxRestException.builder(SESSION_NOT_MATCH_EVENT).
                        setMessage("Session: " + record.getIdsesion() + " not matches with event: " + eventId).build();
            }
        }
        return sessionRecords.stream().collect(Collectors.toMap(SessionRecord::getIdsesion, Function.identity()));
    }

    private void validateId(Long id, ErrorCode errorCode) {
        if (id == null || id <= 0) {
            throw OneboxRestException.builder(errorCode).build();
        }
    }

}
