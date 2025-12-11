package es.onebox.mgmt.seasontickets.service;

import es.onebox.core.exception.CoreErrorCode;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicket;
import es.onebox.mgmt.datasources.ms.ticket.dto.SeasonTicketLinkResponse;
import es.onebox.mgmt.datasources.ms.ticket.dto.SeasonTicketNotNumberedZoneLinkResponse;
import es.onebox.mgmt.datasources.ms.ticket.dto.SeasonTicketNotNumberedZoneUnlinkResponse;
import es.onebox.mgmt.datasources.ms.ticket.repository.TicketsRepository;
import es.onebox.mgmt.datasources.ms.venue.dto.template.NotNumberedZoneCapacity;
import es.onebox.mgmt.datasources.ms.venue.repository.VenuesRepository;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.seasontickets.converter.SeasonTicketSeatLinkConverter;
import es.onebox.mgmt.seasontickets.dto.SeasonTicketLinkResponseDTO;
import es.onebox.mgmt.seasontickets.dto.SeasonTicketNotNumberedZoneLinkDTO;
import es.onebox.mgmt.seasontickets.dto.SeasonTicketNotNumberedZoneLinkResponseDTO;
import es.onebox.mgmt.seasontickets.dto.SeasonTicketNotNumberedZoneUnlinkResponseDTO;
import es.onebox.mgmt.seasontickets.dto.SeasonTicketSeatLinkDTO;
import es.onebox.mgmt.seasontickets.dto.SeasonTicketStatusResponseDTO;
import es.onebox.mgmt.seasontickets.enums.SeasonTicketGenerationStatus;
import es.onebox.mgmt.sessions.SessionUtils;
import es.onebox.mgmt.sessions.converters.SessionConverter;
import es.onebox.mgmt.sessions.dto.SessionVenueTagNotNumberedZoneRequestDTO;
import es.onebox.mgmt.sessions.dto.SessionVenueTagSeatRequestDTO;
import es.onebox.mgmt.venues.converter.VenueTagConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Optional;

import static es.onebox.mgmt.exception.ApiMgmtErrorCode.BAD_REQUEST_PARAMETER;
import static es.onebox.mgmt.exception.ApiMgmtErrorCode.SEASON_TICKET_NOT_READY;

@Service
public class SeasonTicketCapacityService {

    private final SeasonTicketService seasonTicketService;
    private final VenuesRepository venuesRepository;
    private final TicketsRepository ticketsRepository;

    @Autowired
    public SeasonTicketCapacityService(SeasonTicketService seasonTicketService,
                                       VenuesRepository venuesRepository,
                                       TicketsRepository ticketsRepository) {
        this.seasonTicketService = seasonTicketService;
        this.venuesRepository = venuesRepository;
        this.ticketsRepository = ticketsRepository;
    }

    public InputStream getCapacityMap(Long seasonTicketId) {
        if (seasonTicketId == null || seasonTicketId <= 0) {
            throw new OneboxRestException(BAD_REQUEST_PARAMETER, "venueTemplateId must be a positive integer", null);
        }

        SeasonTicket seasonTicket = seasonTicketService.getAndCheckSeasonTicket(seasonTicketId);
        SeasonTicketStatusResponseDTO seasonTicketStatusResponseDTO = seasonTicketService.getSeasonTicketStatus(seasonTicketId);

        if (!SeasonTicketGenerationStatus.READY.equals(seasonTicketStatusResponseDTO.getGenerationStatus())) {
            throw new OneboxRestException.Builder<>(SEASON_TICKET_NOT_READY)
                    .setMessage("Season ticket must be ready")
                    .setHttpStatus(HttpStatus.PRECONDITION_FAILED)
                    .build();
        }

        return ticketsRepository.getCapacityMap(seasonTicketId, seasonTicket.getSessionId());
    }

    public void updateSeatsCapacity(Long seasonTicketId, SessionVenueTagSeatRequestDTO[] seats) {
        SeasonTicket seasonTicket = checkEventSessionRelated(seasonTicketId);

        SessionUtils.validateVenueTags(seats);
        SessionUtils.validateVenueTagIds(seasonTicket.getVenues().get(0).getConfigId(), seats, venuesRepository);

        ticketsRepository.updateSeatsCapacity(seasonTicketId, seasonTicket.getSessionId(),
                SessionConverter.fromVenueTagRequest(seats));
    }

    public void updateNotNumberedZoneCapacity(Long seasonTicketId, SessionVenueTagNotNumberedZoneRequestDTO[] notNumberedZone) {
        SeasonTicket seasonTicket = checkEventSessionRelated(seasonTicketId);

        SessionUtils.validateVenueTags(notNumberedZone);
        SessionUtils.validateVenueTagIds(seasonTicket.getVenues().get(0).getConfigId(), notNumberedZone, venuesRepository);

        ticketsRepository.updateNNZonesCapacity(seasonTicketId, seasonTicket.getSessionId(),
                VenueTagConverter.fromVenueTagRequest(notNumberedZone));
    }

    private SeasonTicket checkEventSessionRelated(Long seasonTicketId) {
        if ((seasonTicketId == null || seasonTicketId < 0)) {
            throw new OneboxRestException(CoreErrorCode.BAD_PARAMETER, "seasonTicketId must be a positive integer", null);
        }

        SeasonTicket seasonTicket = seasonTicketService.getAndCheckSeasonTicket(seasonTicketId);

        if (seasonTicketService.getSeasonTicketStatus(seasonTicketId).getGenerationStatus() !=
                SeasonTicketGenerationStatus.READY) {
            throw new OneboxRestException(SEASON_TICKET_NOT_READY, "The season ticket is not ready: " + seasonTicket, null);
        }

        return seasonTicket;
    }

    public SeasonTicketLinkResponseDTO linkSeasonTicketSeats(Long seasonTicketId, SeasonTicketSeatLinkDTO seats) {
        SeasonTicket seasonTicket = checkEventSessionRelated(seasonTicketId);
        SeasonTicketLinkResponse response = ticketsRepository.linkSeasonTicketSeats(seasonTicket.getSessionId(),
                SeasonTicketSeatLinkConverter.fromSeasonTicketSeatLinkDTOToSeasonTicketSeatLink(seats));
        return SeasonTicketSeatLinkConverter.fromSeasonTicketLinkResponseToSeasonTicketLinkResponseDTO(response);
    }

    public SeasonTicketLinkResponseDTO unLinkSeasonTicketSeats(Long seasonTicketId, SeasonTicketSeatLinkDTO seats) {
        SeasonTicket seasonTicket = checkEventSessionRelated(seasonTicketId);
        SeasonTicketLinkResponse response = ticketsRepository.unLinkSeasonTicketSeats(seasonTicket.getSessionId(),
                SeasonTicketSeatLinkConverter.fromSeasonTicketSeatLinkDTOToSeasonTicketSeatLink(seats));
        return SeasonTicketSeatLinkConverter.fromSeasonTicketLinkResponseToSeasonTicketLinkResponseDTO(response);
    }

    public SeasonTicketNotNumberedZoneLinkResponseDTO linkSeasonTicketNNZ(Long seasonTicketId, SeasonTicketNotNumberedZoneLinkDTO notNumberedZone) {
        SeasonTicket seasonTicket = seasonTicketService.getAndCheckSeasonTicket(seasonTicketId);
        validateSeasonTicketStatus(seasonTicketId);

        Long venueConfigID = seasonTicket.getVenues().get(0).getConfigId();
        checkNotNumberedZone(notNumberedZone.getId(), venueConfigID);

        SeasonTicketNotNumberedZoneLinkResponse response = ticketsRepository.linkSeasonTicketNNZ(seasonTicket.getSessionId(),
                SeasonTicketSeatLinkConverter.convertSeasonTicketNotNumberedZoneLinkDTO(notNumberedZone));
        return SeasonTicketSeatLinkConverter.convertSeasonTicketNotNumberedZoneLinkResponse(response);
    }

    public SeasonTicketNotNumberedZoneUnlinkResponseDTO unlinkSeasonTicketNNZ(Long seasonTicketId, SeasonTicketNotNumberedZoneLinkDTO notNumberedZone) {
        SeasonTicket seasonTicket = seasonTicketService.getAndCheckSeasonTicket(seasonTicketId);
        validateSeasonTicketStatus(seasonTicketId);

        Long venueConfigID = seasonTicket.getVenues().get(0).getConfigId();
        checkNotNumberedZone(notNumberedZone.getId(), venueConfigID);

        SeasonTicketNotNumberedZoneUnlinkResponse response = ticketsRepository.unlinkSeasonTicketNNZ(seasonTicket.getSessionId(),
                SeasonTicketSeatLinkConverter.convertSeasonTicketNotNumberedZoneLinkDTO(notNumberedZone));
        return SeasonTicketSeatLinkConverter.convertSeasonTicketNotNumberedZoneUnlinkResponse(response);
    }

    private void validateSeasonTicketStatus(Long seasonTicketId) {
        SeasonTicketGenerationStatus status = Optional.ofNullable(seasonTicketService.getSeasonTicketStatus(seasonTicketId))
                .orElseThrow(() -> new OneboxRestException(SEASON_TICKET_NOT_READY))
                .getGenerationStatus();
        if (!SeasonTicketGenerationStatus.READY.equals(status)) {
            throw new OneboxRestException(ApiMgmtErrorCode.SEASON_TICKET_NOT_READY);
        }
    }

    private void checkNotNumberedZone(Long zoneId, Long venueConfigId) {
        NotNumberedZoneCapacity nnZone = venuesRepository.getNotNumberedZone(venueConfigId, zoneId);
        if (nnZone == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.VENUE_TEMPLATE_NNZONE_NOT_FOUND);
        }
    }
}
