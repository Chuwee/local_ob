package es.onebox.mgmt.sessions;

import es.onebox.core.exception.CoreErrorCode;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.mgmt.datasources.ms.event.dto.session.Session;
import es.onebox.mgmt.datasources.ms.event.dto.session.Sessions;
import es.onebox.mgmt.datasources.ms.event.repository.EventsRepository;
import es.onebox.mgmt.datasources.ms.ticket.repository.TicketsRepository;
import es.onebox.mgmt.datasources.ms.venue.dto.template.NotNumberedZone;
import es.onebox.mgmt.datasources.ms.venue.dto.template.NotNumberedZoneCapacity;
import es.onebox.mgmt.datasources.ms.venue.repository.VenuesRepository;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.exception.ApiMgmtSessionErrorCode;
import es.onebox.mgmt.seasontickets.service.ReleasedSeatsQuotaHelper;
import es.onebox.mgmt.security.SecurityUtils;
import es.onebox.mgmt.sessions.converters.SessionConverter;
import es.onebox.mgmt.sessions.dto.CapacityRelocationRequestDTO;
import es.onebox.mgmt.sessions.dto.SessionCapacityUpdateBulkNnzDTO;
import es.onebox.mgmt.sessions.dto.SessionCapacityUpdateBulkSeatsDTO;
import es.onebox.mgmt.sessions.dto.SessionPackNotNumberedZoneLinkDTO;
import es.onebox.mgmt.sessions.dto.SessionPackSeatLinkDTO;
import es.onebox.mgmt.sessions.dto.SessionSearchFilter;
import es.onebox.mgmt.sessions.dto.SessionVenueTagNotNumberedZoneRequestDTO;
import es.onebox.mgmt.sessions.dto.SessionVenueTagSeatRequestDTO;
import es.onebox.mgmt.validation.ValidationService;
import es.onebox.mgmt.venues.converter.VenueTagConverter;
import es.onebox.mgmt.venues.dto.BaseVenueTagDTO;
import es.onebox.mgmt.venues.dto.VenueTagNotNumberedZoneBulkRequestDTO;
import es.onebox.mgmt.venues.dto.VenueTagNotNumberedZoneRequestDTO;
import es.onebox.mgmt.venues.dto.VenueTagSeatRequestDTO;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SessionsCapacityService {

    public static final int SEAT_LINK_MAXIMUM = 2000;

    private final EventsRepository eventsRepository;
    private final VenuesRepository venuesRepository;
    private final ValidationService validationService;
    private final TicketsRepository ticketsRepository;
    private final ReleasedSeatsQuotaHelper releasedSeatsQuotaHelper;

    @Autowired
    public SessionsCapacityService(
            EventsRepository eventsRepository,
            VenuesRepository venuesRepository,
            ValidationService validationService,
            TicketsRepository ticketsRepository,
            ReleasedSeatsQuotaHelper releasedSeatsQuotaHelper) {
        this.eventsRepository = eventsRepository;
        this.venuesRepository = venuesRepository;
        this.validationService = validationService;
        this.ticketsRepository = ticketsRepository;
        this.releasedSeatsQuotaHelper = releasedSeatsQuotaHelper;
    }

    // CAPACITY MAP

    public InputStream getCapacityMap(Long eventId, Long sessionId) {
        Session session = validationService.getAndCheckSession(eventId, sessionId);
        if (CommonUtils.isTrue(session.getArchived())) {
            throw new OneboxRestException(ApiMgmtSessionErrorCode.NO_SEAT_OR_OCCUPATION_INFO_AVAILABLE);
        }
        return ticketsRepository.getCapacityMap(eventId, sessionId);
    }

    // UPDATE CAPACITY

    public void updateSeatsCapacity(Long eventId, Long sessionId, SessionVenueTagSeatRequestDTO[] seats) {
        validateCapacityUpdate(eventId, Collections.singletonList(sessionId), seats);
        ticketsRepository.updateSeatsCapacity(eventId, sessionId, SessionConverter.fromVenueTagRequest(seats));
    }

    public void updateNotNumberedZoneCapacity(Long eventId, Long sessionId, SessionVenueTagNotNumberedZoneRequestDTO[] notNumberedZones) {
        validateCapacityUpdate(eventId, Collections.singletonList(sessionId), notNumberedZones);
        ticketsRepository.updateNNZonesCapacity(eventId, sessionId, SessionConverter.fromVenueTagRequest(notNumberedZones));
    }

    public void updateSeatsCapacityBulk(Long eventId, SessionCapacityUpdateBulkSeatsDTO request) {
        validateCapacityUpdate(eventId, request);
        ticketsRepository.updateSeatsCapacityBulk(eventId, request.getIds(),
                VenueTagConverter.fromVenueTagRequest(request.getValues().toArray(new VenueTagSeatRequestDTO[0])));
    }

    public void updateNotNumberedZonesCapacityBulk(Long eventId, SessionCapacityUpdateBulkNnzDTO request) {
        validateCapacityUpdate(eventId, request);
        ticketsRepository.updateNNZonesCapacityBulk(eventId, request.getIds(),
                VenueTagConverter.fromVenueTagRequest(request.getValues().toArray(new VenueTagNotNumberedZoneBulkRequestDTO[0])));
    }

    // UPDATE CAPACITY VALIDATIONS

    private void validateCapacityUpdate(Long eventId, SessionCapacityUpdateBulkSeatsDTO request) {
        validateCapacityUpdate(eventId, request.getIds(), request.getValues().toArray(new VenueTagSeatRequestDTO[0]));
    }

    private void validateCapacityUpdate(Long eventId, List<Long> sessionIds, VenueTagSeatRequestDTO[] values) {
        Long venueTemplateId = validateTemplateDataAndGetId(eventId, sessionIds, values);
        SessionUtils.validateVenueTagIds(venueTemplateId, values, venuesRepository);
        validateNoReleasedSeatsQuotasExist(values, venueTemplateId);
    }

    private void validateNoReleasedSeatsQuotasExist(VenueTagSeatRequestDTO[] values, Long venueTemplateId) {
        boolean hasReleasedSeatQuota = releasedSeatsQuotaHelper.hasReleasedSeatQuota(
                Arrays.stream(values).map(VenueTagSeatRequestDTO::getQuota).filter(Objects::nonNull).collect(Collectors.toList()), venueTemplateId
        );
        if (hasReleasedSeatQuota) {
            throw new OneboxRestException(ApiMgmtErrorCode.INVALID_RELEASED_SEAT_QUOTA);
        }
    }

    private void validateCapacityUpdate(Long eventId, SessionCapacityUpdateBulkNnzDTO request) {
        validateCapacityUpdate(eventId, request.getIds(), request.getValues().toArray(new VenueTagNotNumberedZoneRequestDTO[0]));
    }

    private void validateCapacityUpdate(Long eventId, List<Long> sessionIds, VenueTagNotNumberedZoneRequestDTO[] values) {
        Long venueTemplateId = validateTemplateDataAndGetId(eventId, sessionIds, values);
        SessionUtils.validateVenueTagIds(venueTemplateId, values, venuesRepository);

        Set<Long> nnzIds = Arrays.stream(values).map(VenueTagNotNumberedZoneRequestDTO::getId).collect(Collectors.toSet());

        List<NotNumberedZone> templateNNZones = venuesRepository.getNotNumberedZones(venueTemplateId, null);
        List<Long> templateNNZoneIDs = templateNNZones.stream().map(NotNumberedZone::getId).toList();
        if (!new HashSet<>(templateNNZoneIDs).containsAll(nnzIds)) {
            throw new OneboxRestException(ApiMgmtSessionErrorCode.INVALID_NNZ_TEMPLATE_BULK);
        }
    }

    private Long validateTemplateDataAndGetId(Long eventId, List<Long> sessionIds, BaseVenueTagDTO[] values) {
        if (values.length == 0) {
            throw new OneboxRestException(CoreErrorCode.BAD_PARAMETER, "values is mandatory for bulk update", null);
        }
        if ((eventId == null || eventId < 0)
                || (CommonUtils.isEmpty(sessionIds) || sessionIds.stream().anyMatch(id -> id < 0))) {
            throw new OneboxRestException(CoreErrorCode.BAD_PARAMETER, "eventId and sessionId must be a positive integer", null);
        }
        validationService.getAndCheckEventExternal(eventId);

        SessionSearchFilter filter = new SessionSearchFilter();
        filter.setId(sessionIds);
        Sessions sessions = eventsRepository.getSessions(SecurityUtils.getUserOperatorId(), eventId, filter);
        if (sessions.getMetadata().getTotal().intValue() != sessionIds.size()) {
            throw new OneboxRestException(ApiMgmtSessionErrorCode.SESSION_NOT_FOUND,
                    "Any session not found for event: " + eventId, null);
        }
        Long venueTemplateId = null;
        List<Session> sessionsList = sessions.getData();
        for (Session session : sessionsList) {
            ValidationService.validateSession(eventId, session.getId(), session);
            if (venueTemplateId == null) {
                venueTemplateId = session.getVenueConfigId();
            } else if (!venueTemplateId.equals(session.getVenueConfigId())) {
                throw new OneboxRestException(ApiMgmtSessionErrorCode.INVALID_SESSIONS_TEMPLATE_BULK,
                        "All sessions must belong to the same template - event: " + eventId + " - sessions: "
                                + sessionsList.stream().map(Session::getId).map(String::valueOf).collect(Collectors.joining()), null);
            }
        }
        SessionUtils.validateVenueTags(values);
        return venueTemplateId;
    }

    // SESSION PACK LINK / UNLINK

    public void linkSessionPackSeats(Long eventId, Long sessionId, SessionPackSeatLinkDTO seats) {
        if (seats.getIds().size() > SEAT_LINK_MAXIMUM) {
            OneboxRestException e = new OneboxRestException(ApiMgmtErrorCode.SEAT_LINK_LIMIT_EXCEEDED);
            e.setMessage("The number of seats to link is greater than " + SEAT_LINK_MAXIMUM);
            throw e;
        }
        Session session = validationService.getAndCheckSession(eventId, sessionId);
        SessionUtils.checkSessionPack(session);
        Long targetId = SessionUtils.validateSessionPackChange(session, seats.getTarget(), seats.getQuota(), venuesRepository);

        List<Long> failed = ticketsRepository.linkSeats(eventId, sessionId, SessionConverter.buildSeatLinks(seats, targetId));
        if (CollectionUtils.isNotEmpty(failed)) {
            throw new OneboxRestException(ApiMgmtSessionErrorCode.SESSION_PACK_LINK_SEATS,
                    "Unexpected error while linking seats to session " + sessionId + " pack", null);
        }
    }

    public void unlinkSessionPackSeats(Long eventId, Long sessionId, SessionPackSeatLinkDTO seats) {
        if (seats.getIds().size() > SEAT_LINK_MAXIMUM) {
            OneboxRestException e = new OneboxRestException(ApiMgmtErrorCode.SEAT_LINK_LIMIT_EXCEEDED);
            e.setMessage("The number of seats to unlink is greater than " + SEAT_LINK_MAXIMUM);
            throw e;
        }
        Session session = validationService.getAndCheckSession(eventId, sessionId);
        SessionUtils.checkSessionPack(session);
        Long targetId = SessionUtils.validateSessionPackChange(session, seats.getTarget(), seats.getQuota(), venuesRepository);

        List<Long> failed = ticketsRepository.unlinkSeats(eventId, sessionId, SessionConverter.buildSeatLinks(seats, targetId));
        if (CollectionUtils.isNotEmpty(failed)) {
            throw new OneboxRestException(ApiMgmtSessionErrorCode.SESSION_PACK_UNLINK_SEATS,
                    "Unexpected error while unlinking seats to session " + sessionId + " pack", null);
        }
    }

    public void linkSessionPackNNZ(Long eventId, Long sessionId, SessionPackNotNumberedZoneLinkDTO notNumberedZone) {
        if (notNumberedZone.getCount() > SEAT_LINK_MAXIMUM) {
            OneboxRestException e = new OneboxRestException(ApiMgmtErrorCode.SEAT_LINK_LIMIT_EXCEEDED);
            e.setMessage("The nnz count to link is greater than " + SEAT_LINK_MAXIMUM);
            throw e;
        }
        Session session = validationService.getAndCheckSession(eventId, sessionId);
        SessionUtils.checkSessionPack(session);
        checkNotNumberedZone(notNumberedZone.getId(), session.getVenueConfigId());
        Long sourceId = SessionUtils.validateSessionPackChange(session, notNumberedZone.getSource(), null, venuesRepository);
        Long targetId = SessionUtils.validateSessionPackChange(session, notNumberedZone.getTarget(), null, venuesRepository);

        ticketsRepository.linkNNZ(eventId, sessionId, SessionConverter.buildNNZLinks(notNumberedZone, sourceId, targetId));
    }

    public void unlinkSessionPackNNZ(Long eventId, Long sessionId, SessionPackNotNumberedZoneLinkDTO notNumberedZone) {
        if (notNumberedZone.getCount() > SEAT_LINK_MAXIMUM) {
            OneboxRestException e = new OneboxRestException(ApiMgmtErrorCode.SEAT_LINK_LIMIT_EXCEEDED);
            e.setMessage("The nnz count to link is greater than " + SEAT_LINK_MAXIMUM);
            throw e;
        }
        Session session = validationService.getAndCheckSession(eventId, sessionId);
        SessionUtils.checkSessionPack(session);
        checkNotNumberedZone(notNumberedZone.getId(), session.getVenueConfigId());
        Long sourceId = SessionUtils.validateSessionPackChange(session, notNumberedZone.getSource(), null, venuesRepository);
        Long targetId = SessionUtils.validateSessionPackChange(session, notNumberedZone.getTarget(), null, venuesRepository);

        ticketsRepository.unlinkNNZ(eventId, sessionId, SessionConverter.buildNNZLinks(notNumberedZone, sourceId, targetId));
    }

    private void checkNotNumberedZone(Long zoneId, Long venueConfigId) {
        NotNumberedZoneCapacity nnZone = venuesRepository.getNotNumberedZone(venueConfigId, zoneId);
        if (nnZone == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.VENUE_TEMPLATE_NNZONE_NOT_FOUND);
        }
    }

    public void relocateSeats(Long eventId, Long sessionId, CapacityRelocationRequestDTO relocationRequest) {
        validationService.getAndCheckSession(eventId, sessionId);
        ticketsRepository.relocateSeats(eventId, sessionId, SessionConverter.toMs(relocationRequest));
    }
}
