package es.onebox.flc.sessions.service;

import es.onebox.common.datasources.ms.event.dto.SessionDTO;
import es.onebox.common.datasources.ms.event.dto.SessionsDTO;
import es.onebox.common.datasources.ms.event.enums.SessionStatus;
import es.onebox.common.datasources.ms.event.repository.MsEventRepository;
import es.onebox.common.datasources.ms.event.request.SessionSearchFilter;
import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.common.utils.DateUtils;
import es.onebox.core.exception.ExceptionBuilder;
import es.onebox.core.exception.OneRequiredParameterException;
import es.onebox.core.serializer.dto.request.Operator;
import es.onebox.flc.events.dto.SessionState;
import es.onebox.flc.sessions.converter.SessionConverter;
import es.onebox.flc.sessions.dto.Session;
import es.onebox.flc.utils.AuthenticationUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service(value = "flcSessionService")
public class SessionService {

    @Autowired
    private MsEventRepository msEventRepository;

    public List<Session> getSessions(List<Long> sessionIds, List<SessionState> sessionStates, ZonedDateTime sessionStart,
                                     ZonedDateTime sessionEnd, List<Long> eventIds, List<Long> venueIds, List<Long> accessValidationSpaceIds, Long limit, Long offset) {

        List<Object> params = new ArrayList<>(Arrays.asList(sessionIds, sessionStates, sessionStart, sessionEnd, eventIds, venueIds, accessValidationSpaceIds));
        if (params.stream().noneMatch(Objects::nonNull)) {
            throw new OneRequiredParameterException("Bad Parameters, you need at least one parameter", params);
        } else if (sessionStart != null && sessionEnd != null && sessionStart.isAfter(sessionEnd)) {
            throw ExceptionBuilder.build(ApiExternalErrorCode.START_DATE_AFTER_END_DATE);
        } else if (!validateSessionStates(sessionStates)) {
            throw ExceptionBuilder.build(ApiExternalErrorCode.INVALID_SESSION_STATE);
        } else {
            SessionSearchFilter filter = new SessionSearchFilter();

            filter.setEntityId(Long.valueOf((Integer) AuthenticationUtils.getAttribute("entityId")));
            filter.setOperatorId(Long.valueOf((Integer) AuthenticationUtils.getAttribute("operatorId")));

            filter.setId(sessionIds);
            if(CollectionUtils.isNotEmpty(sessionStates)) {
                filter.setStatus(getStatus(sessionStates));
            }
            if (sessionStart != null) {
                filter.setStartDate(DateUtils.getDate(sessionStart, Operator.LESS_THAN_OR_EQUALS));
            }
            if (sessionEnd != null) {
                filter.setEndDate(DateUtils.getDate(sessionEnd, Operator.GREATER_THAN_OR_EQUALS));
            }

            filter.setEventId(eventIds);
            filter.setVenueId(venueIds);
            filter.setAccessValidationSpaceIds(accessValidationSpaceIds);
            filter.setLimit(limit);
            filter.setOffset(offset);

            SessionsDTO sessions = msEventRepository.getSessions(filter);

            if (sessions != null && sessions.getData() != null && !sessions.getData().isEmpty()) {
                return SessionConverter.convert(sessions);
            } else {
                throw ExceptionBuilder.build(ApiExternalErrorCode.NO_CONTENT);
            }
        }
    }

    private List<SessionStatus> getStatus(List<SessionState> sessionStates) {
        List<SessionStatus> status = new ArrayList<>();
        for (SessionState sessionState : sessionStates) {
            status.add(SessionStatus.byId(sessionState.getId()));
        }
        return status;
    }

    private boolean validateSessionStates(List<SessionState> sessionStates) {
        if (sessionStates != null) {
            for (SessionState sessionState : sessionStates) {
                if (SessionState.get(sessionState.getId()) != null) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    public Session getSession(Long sessionId) {
        Long entityId = Long.valueOf((Integer) AuthenticationUtils.getAttribute("entityId"));

        SessionDTO sessionDTO = msEventRepository.getSession(sessionId);
        if (sessionDTO != null) {
            if (sessionDTO.getEntityId().equals(entityId)) {
                return SessionConverter.convert(sessionDTO);
            } else {
                throw ExceptionBuilder.build(ApiExternalErrorCode.ACCESS_DENIED);
            }
        } else {
            throw ExceptionBuilder.build(ApiExternalErrorCode.NO_CONTENT);
        }
    }
}
