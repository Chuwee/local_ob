package es.onebox.circuitcat.conciliation.service;

import es.onebox.circuitcat.conciliation.dto.ConciliationDTO;
import es.onebox.circuitcat.common.dto.Seat;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ConciliationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConciliationService.class);

    @Autowired
    private MsTicketRepository ticketRepository;
    @Autowired
    private MsEventRepository eventRepository;
    @Autowired
    private VenueTemplateRepository venueTemplateRepository;

    private static final String MEMBER_BLOCKING_REASON_PATTERN = "Soci";

    public ConciliationDTO getConciliation(List<Long> sessionIds, List<Seat> seats) {
        ConciliationDTO conciliationDTO = new ConciliationDTO();
        Map<Long, VenueMapProto.VenueMap> venueMaps = new HashMap<>();
        Map<Long, Long> blockingReasonBySessionId = new HashMap<>();
        Map<Seat, List<Long>> promoterLockedSeatsOB;

        for (Long sessionId : sessionIds) {
            SessionDTO sessionDTO;
            try {
                sessionDTO = eventRepository.getSession(sessionId);
                VenueMapProto.VenueMap venueMap = ticketRepository.getCapacityMap(sessionDTO.getEventId(), sessionId, null);
                venueMaps.put(sessionId, venueMap);

                VenueTemplate venueTemplate = venueTemplateRepository.getVenueTemplate(sessionDTO.getVenueConfigId());
                List<BlockingReasonDTO> blockingReasons = venueTemplateRepository.getBlockingReasons(venueTemplate.getId());
                BlockingReasonDTO blockingReason = blockingReasons.stream().filter(blockingReasonDTO -> blockingReasonDTO.getName().contains(MEMBER_BLOCKING_REASON_PATTERN)).findFirst().orElse(null);
                blockingReasonBySessionId.put(sessionId, blockingReason.getId());
            } catch (OneboxRestException e) {
                LOGGER.error("[CIRCUIT CAT CONCILIATION] Session {} not found", sessionId);
                throw new OneboxRestException(ApiExternalErrorCode.SESSION_NOT_FOUND);
            }
        }

        promoterLockedSeatsOB = getPromoterLockedSeatsOB(venueMaps, blockingReasonBySessionId);

        // Internal inconsistencies (member not blocked in all sessions)
        conciliationDTO.setInconsistencies(validateInternalInconsistencies(promoterLockedSeatsOB, sessionIds.size()));

        // member in circuit and not in all ob sessions
        conciliationDTO.setExternalMembers(validateExternaltMembers(promoterLockedSeatsOB, seats));

        // member in onebox and not in circuit (list by parameter)
        conciliationDTO.setInternalMembers(validateInternalMembers(promoterLockedSeatsOB, seats, sessionIds.size()));

        return conciliationDTO;
    }

    private List<Seat> validateInternalMembers(Map<Seat, List<Long>> promoterLockedSeatsOB, List<Seat> seats, int size) {
        List<Seat> result = new ArrayList<>();
        List<Seat> filteredSeats = filterInconsistencies(promoterLockedSeatsOB, size);

        for (Seat filteredSeat : filteredSeats) {
            if (!seats.contains(filteredSeat)) {
                result.add(filteredSeat);
            }
        }

        return result;
    }

    private List<Seat> filterInconsistencies(Map<Seat, List<Long>> promoterLockedSeatsOB, int size) {
        List<Seat> result = new ArrayList<>();

        for (Map.Entry<Seat, List<Long>> seatListEntry : promoterLockedSeatsOB.entrySet()) {
            Seat seat = seatListEntry.getKey();
            List<Long> promoterLockedSeats = seatListEntry.getValue();
            if(promoterLockedSeats.size() == size) {
                result.add(seat);
            }
        }

        return result;
    }

    private List<Seat> validateExternaltMembers(Map<Seat, List<Long>> promoterLockedSeatsOB, List<Seat> seats) {
        List<Seat> result = new ArrayList<>();

        for (Seat seat : seats) {
            if (!promoterLockedSeatsOB.containsKey(seat)) {
                result.add(seat);
            }
        }

        return result;
    }

    private List<Seat> validateInternalInconsistencies(Map<Seat, List<Long>> promoterLockeSeatsOB, int size) {
        List<Seat> seats = new ArrayList<>();
        for (Map.Entry<Seat, List<Long>> promoterSeat : promoterLockeSeatsOB.entrySet()) {
            Seat seat = promoterSeat.getKey();
            List<Long> sessionIdsList = promoterSeat.getValue();
            if (sessionIdsList.size() != size) {
                seats.add(seat);
            }
        }

        return seats;
    }

    private Map<Seat, List<Long>> getPromoterLockedSeatsOB(Map<Long, VenueMapProto.VenueMap> venueMaps, Map<Long, Long> blockingReasonBySessionId) {
        Map<Seat, List<Long>> result = new HashMap<>();

        for (Map.Entry<Long, VenueMapProto.VenueMap> entry : venueMaps.entrySet()) {
            Long sessionId = entry.getKey();
            VenueMapProto.VenueMap venueMap = entry.getValue();
            Long blockingReasonId = blockingReasonBySessionId.get(sessionId);
            for (VenueMapProto.SectorMap sectorMap : venueMap.getSectorMapList()) {
                for (VenueMapProto.RowMap rowMap : sectorMap.getRowMapList()) {
                    for (VenueMapProto.SeatMap seatMap : rowMap.getSeatMapList()) {
                        if (seatMap.getStatus().equals(VenueMapProto.Enums.SeatStatus.PROMOTOR_LOCKED) &&
                                blockingReasonId != null && seatMap.getBlockingReason() == blockingReasonId.intValue()) {
                            Seat seat = new Seat();
                            seat.setSector(sectorMap.getCode());
                            seat.setRow(rowMap.getName());
                            seat.setSeat(seatMap.getName());
                            if (result.containsKey(seat)) {
                                result.get(seat).add(sessionId);
                            } else {
                                result.put(seat, new ArrayList<>(Arrays.asList(sessionId)));
                            }
                        }
                    }
                }
            }
        }
        return result;
    }
}
