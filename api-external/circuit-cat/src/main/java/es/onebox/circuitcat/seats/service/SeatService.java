package es.onebox.circuitcat.seats.service;

import es.onebox.common.datasources.common.dto.SeatStatus;
import es.onebox.circuitcat.seats.dto.UpdateSeatDTO;
import es.onebox.common.datasources.ms.event.dto.SessionDTO;
import es.onebox.common.datasources.ms.event.repository.MsEventRepository;
import es.onebox.common.datasources.ms.ticket.repository.MsTicketRepository;
import es.onebox.common.datasources.ms.venue.dto.BlockingReasonDTO;
import es.onebox.common.datasources.ms.venue.dto.VenueTemplate;
import es.onebox.common.datasources.ms.venue.repository.VenueTemplateRepository;
import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.venue.venuetemplates.VenueMapProto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class SeatService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SeatService.class);
    private MsTicketRepository ticketRepository;
    private MsEventRepository eventRepository;
    private VenueTemplateRepository venueTemplateRepository;
    private static final String MEMBER_BLOCKING_REASON_PATTERN = "soci";

    @Autowired
    public SeatService(MsTicketRepository ticketRepository, MsEventRepository eventRepository, VenueTemplateRepository venueTemplateRepository) {
        this.ticketRepository = ticketRepository;
        this.eventRepository = eventRepository;
        this.venueTemplateRepository = venueTemplateRepository;
    }

    public void block(String sector, String row, String seat, List<Long> sessionIds) {
        List<UpdateSeatDTO> seatList = new ArrayList<>();
        for (Long sessionId : sessionIds) {
            SessionDTO sessionDTO;
            try {
                sessionDTO = eventRepository.getSession(sessionId);
            } catch (OneboxRestException e) {
                LOGGER.error("[CIRCUIT CAT SECTOR] Session {} not found", sessionId);
                throw new OneboxRestException(ApiExternalErrorCode.SESSION_NOT_FOUND);
            }

            VenueTemplate venueTemplate = venueTemplateRepository.getVenueTemplate(sessionDTO.getVenueConfigId());
            Long sectorId = getSectorId(venueTemplate, sector);

            List<BlockingReasonDTO> blockingReasons = venueTemplateRepository.getBlockingReasons(venueTemplate.getId());
            BlockingReasonDTO blockingReason = blockingReasons.stream().filter(blockingReasonDTO -> blockingReasonDTO.getName().toLowerCase().contains(MEMBER_BLOCKING_REASON_PATTERN)).findFirst().orElse(null);

            VenueMapProto.VenueMap venueMap = ticketRepository.getCapacityMap(sessionDTO.getEventId(), sessionId, Arrays.asList(sectorId));

            Long locationId = getFreeLocationId(venueMap.getSectorMapList(), sector, row, seat);

            if (locationId != null && blockingReason != null) {
                UpdateSeatDTO updateSeatDTO = new UpdateSeatDTO();
                updateSeatDTO.setId(locationId);
                updateSeatDTO.setSessionId(sessionId);
                updateSeatDTO.setBlockingReasonId(blockingReason.getId());
                updateSeatDTO.setStatus(SeatStatus.PROMOTOR_LOCKED);
                seatList.add(updateSeatDTO);
            } else {
                LOGGER.error("[CIRCUIT CAT BLOCK] Seat {} - {} - {} with not valid state (free) for session {} or without Soci blocking reason", sector, row, seat, sessionId);
                throw new OneboxRestException(ApiExternalErrorCode.SEAT_NOT_AVAILABLE);
            }
        }

        // Update blocking reason
        seatList.stream().forEach(updateSeatDTO -> {
                try {
                    ticketRepository.updateTicket(updateSeatDTO.getSessionId(), updateSeatDTO.getId(), updateSeatDTO.getStatus(), updateSeatDTO.getBlockingReasonId());
                } catch (Exception e) {
                    LOGGER.error("[CIRCUIT CAT BLOCK] Seat {} - session {} can not update with blocking reason {}", updateSeatDTO.getId(), updateSeatDTO.getSessionId(), updateSeatDTO.getBlockingReasonId());
                    throw new OneboxRestException(ApiExternalErrorCode.SEAT_NOT_UPDATED);
                }
        });
    }

    public void unblock(String sector, String row, String seat, List<Long> sessionIds) {
        List<UpdateSeatDTO> seatList = new ArrayList<>();
        for (Long sessionId : sessionIds) {
            SessionDTO sessionDTO;
            try {
                sessionDTO = eventRepository.getSession(sessionId);
            } catch (OneboxRestException e) {
                LOGGER.error("[CIRCUIT CAT UNBLOCK] Session {} not found", sessionId);
                throw new OneboxRestException(ApiExternalErrorCode.SESSION_NOT_FOUND);
            }

            VenueTemplate venueTemplate = venueTemplateRepository.getVenueTemplate(sessionDTO.getVenueConfigId());
            Long sectorId = getSectorId(venueTemplate, sector);

            List<BlockingReasonDTO> blockingReasons = venueTemplateRepository.getBlockingReasons(venueTemplate.getId());
            BlockingReasonDTO blockingReason = blockingReasons.stream().filter(blockingReasonDTO -> blockingReasonDTO.getName().toLowerCase().contains(MEMBER_BLOCKING_REASON_PATTERN)).findFirst().orElse(null);

            VenueMapProto.VenueMap venueMap = ticketRepository.getCapacityMap(sessionDTO.getEventId(), sessionId, Arrays.asList(sectorId));

            Long locationId = getBlockedLocationId(venueMap.getSectorMapList(), sector, row, seat, blockingReason.getId());

            if (locationId != null && blockingReason != null) {
                UpdateSeatDTO updateSeatDTO = new UpdateSeatDTO();
                updateSeatDTO.setId(locationId);
                updateSeatDTO.setSessionId(sessionId);
                updateSeatDTO.setBlockingReasonId(null);
                updateSeatDTO.setStatus(SeatStatus.FREE);
                seatList.add(updateSeatDTO);
            } else {
                LOGGER.error("[CIRCUIT CAT UNBLOCK] Seat {} - {} - {} with not valid state (soci blocking reason) for session {}", sector, row, seat, sessionId);
                throw new OneboxRestException(ApiExternalErrorCode.SEAT_NOT_AVAILABLE);
            }
        }

        // move to available and set null the blocking reason
        seatList.stream().forEach(updateSeatDTO -> {
            try {
                ticketRepository.updateTicket(updateSeatDTO.getSessionId(), updateSeatDTO.getId(), updateSeatDTO.getStatus(), updateSeatDTO.getBlockingReasonId());
            } catch (Exception e) {
                LOGGER.error("[CIRCUIT CAT UNBLOCK] Seat {} - session {} can not update to available state", updateSeatDTO.getId(), updateSeatDTO.getSessionId());
                throw new OneboxRestException(ApiExternalErrorCode.SEAT_NOT_UPDATED);
            }
        });
    }

    private Long getSectorId(VenueTemplate venueTemplate, String sector) {
        return venueTemplate.getSectors().stream()
                .filter(s -> s.getCode().equals(sector))
                .findFirst().orElse(null)
                .getId();
    }

    private Long getFreeLocationId(List<VenueMapProto.SectorMap>  sectorsMap, String sector, String row, String seat) {
        for (VenueMapProto.SectorMap sectorMap : sectorsMap) {
            if (sectorMap.getCode().equals(sector)) {
                for (VenueMapProto.RowMap rowMap : sectorMap.getRowMapList()) {
                    if (rowMap.getName().equals(row)) {
                        for (VenueMapProto.SeatMap seatMap : rowMap.getSeatMapList()) {
                            if (seatMap.getName().equals(seat) && seatMap.getStatus().equals(VenueMapProto.Enums.SeatStatus.FREE)) {
                                return seatMap.getTicketId();
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    private Long getBlockedLocationId(List<VenueMapProto.SectorMap>  sectorsMap, String sector, String row, String seat, Long blockingReason) {
        for (VenueMapProto.SectorMap sectorMap : sectorsMap) {
            if (sectorMap.getCode().equals(sector)) {
                for (VenueMapProto.RowMap rowMap : sectorMap.getRowMapList()) {
                    if (rowMap.getName().equals(row)) {
                        for (VenueMapProto.SeatMap seatMap : rowMap.getSeatMapList()) {
                            if (seatMap.getName().equals(seat) && seatMap.getStatus().equals(VenueMapProto.Enums.SeatStatus.PROMOTOR_LOCKED) && blockingReason.longValue() == seatMap.getBlockingReason()) {
                                return seatMap.getTicketId();
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
}
