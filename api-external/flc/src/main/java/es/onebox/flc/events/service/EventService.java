package es.onebox.flc.events.service;

import es.onebox.common.datasources.ms.event.dto.EventDTO;
import es.onebox.common.datasources.ms.event.dto.EventsDTO;
import es.onebox.common.datasources.ms.event.enums.EventStatus;
import es.onebox.common.datasources.ms.event.enums.SessionStatus;
import es.onebox.common.datasources.ms.event.repository.MsEventRepository;
import es.onebox.common.datasources.ms.event.request.EventSearchFilter;
import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.common.utils.DateUtils;
import es.onebox.core.exception.ExceptionBuilder;
import es.onebox.core.exception.OneRequiredParameterException;
import es.onebox.core.serializer.dto.request.Operator;
import es.onebox.flc.events.converter.EventConverter;
import es.onebox.flc.events.dto.Event;
import es.onebox.flc.events.dto.EventState;
import es.onebox.flc.events.dto.SessionState;
import es.onebox.flc.utils.AuthenticationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service(value = "flcEventService")
public class EventService {

    @Autowired
    MsEventRepository msEventRepository;

    public List<Event> getEvents(List<Long> eventIds, ZonedDateTime gte, ZonedDateTime lte,
                                 ZonedDateTime sessionStartDate, ZonedDateTime sessionEndDate,
                                 List<EventState> eventStates, List<SessionState> sessionStates, List<Long> venueIds,
                                 String externalReferenceCode, Long limit, Long offset) {

        List<Object> params = new ArrayList<>(Arrays.asList(eventIds, gte, lte, sessionStartDate, sessionEndDate,
                eventStates, sessionStates, venueIds, externalReferenceCode));
        if (!params.stream().anyMatch(o -> o != null)) {
            throw new OneRequiredParameterException("Bad Parameters, you need at least one parameter", params);
        } else if (gte != null && lte != null && gte.isAfter(lte)) {
            throw ExceptionBuilder.build(ApiExternalErrorCode.START_DATE_AFTER_END_DATE);
        } else {

            // Prepare paramenters
            EventSearchFilter eventSearchFilter = new EventSearchFilter();
            eventSearchFilter.setLimit(limit);
            eventSearchFilter.setOffset(offset);
            eventSearchFilter.setId(eventIds);
            if (gte != null) {
                eventSearchFilter.setStartDate(DateUtils.getDate(gte, Operator.GREATER_THAN_OR_EQUALS));
            }
            if (lte != null) {
                eventSearchFilter.setEndDate(DateUtils.getDate(lte, Operator.LESS_THAN_OR_EQUALS));
            }
            if (sessionStartDate != null) {
                eventSearchFilter.setSessionStartDate(sessionStartDate);
            }
            if (sessionEndDate != null) {
                eventSearchFilter.setSessionEndDate(sessionEndDate);
            }
            if (eventStates != null) {
                eventSearchFilter.setStatus(getStatus(eventStates));
            }
            if (sessionStates != null) {
                eventSearchFilter.setSessionStatus(getSessionStatus(sessionStates));
            }
            eventSearchFilter.setVenueId(venueIds);
            eventSearchFilter.setExternalReference(externalReferenceCode);
            eventSearchFilter.setEntityId(Long.valueOf((Integer) AuthenticationUtils.getAttribute("entityId")));
            eventSearchFilter.setOperatorId(Long.valueOf((Integer) AuthenticationUtils.getAttribute("operatorId")));

            EventsDTO events = msEventRepository.search(eventSearchFilter);
            if (events != null && events.getData() != null && !events.getData().isEmpty()) {
                List<Long> eventsIds = events.getData().stream().map(EventDTO::getId).collect(Collectors.toList());
                final Map<Integer, Map<Integer, List<Integer>>> attributes = msEventRepository.getAttributes(eventsIds);

                return EventConverter.convert(events, attributes);
            } else {
                throw ExceptionBuilder.build(ApiExternalErrorCode.NO_CONTENT);
            }
        }
    }

    private List<SessionStatus> getSessionStatus(List<SessionState> sessionStates) {
        List<SessionStatus> status = new ArrayList<>();
        for (SessionState sessionState : sessionStates) {
            status.add(SessionStatus.byId(sessionState.getId()));
        }
        return status;
    }

    private List<EventStatus> getStatus(List<EventState> eventStates) {
        List<EventStatus> status = new ArrayList<>();
        for (EventState eventState : eventStates) {
            status.add(EventStatus.byId(eventState.getId()));
        }
        return status;
    }
}
