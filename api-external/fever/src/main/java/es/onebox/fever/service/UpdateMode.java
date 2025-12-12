package es.onebox.fever.service;

import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.fever.dto.UpdateExtReferenceRequest;

public enum UpdateMode {
    EVENT,
    SESSION,
    ENTITY,
    USER;

    public static UpdateMode resolve(UpdateExtReferenceRequest request) {
        boolean hasEvent = request.getEventId() != null;
        boolean hasSession = request.getSessionId() != null;
        boolean hasEntity = request.getEntityId() != null;
        boolean hasUser = request.getUserId() != null;

        if (hasEntity && !hasEvent && !hasSession && !hasUser) return ENTITY;
        if (hasEvent && !hasSession && !hasEntity && !hasUser) return EVENT;
        if (hasEvent && hasSession && !hasEntity && !hasUser) return SESSION;
        if (hasUser && !hasEvent && !hasSession && !hasEntity) return USER;

        throw new OneboxRestException(ApiExternalErrorCode.BAD_REQUEST_PARAMETER,  "Invalid combination of parameters. Provide only one of: entityId, eventId, userId or (eventId + sessionId)", null);
    }
}
