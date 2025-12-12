package es.onebox.circuitcat.sectors.service;

import es.onebox.circuitcat.sectors.converter.SectorConverter;
import es.onebox.circuitcat.common.dto.Seat;
import es.onebox.common.datasources.common.dto.SeatStatus;
import es.onebox.circuitcat.sectors.dto.Sector;
import es.onebox.circuitcat.sectors.dto.SectorDTO;
import es.onebox.common.datasources.ms.event.dto.EventChannelDTO;
import es.onebox.common.datasources.ms.event.dto.SaleGroupDTO;
import es.onebox.common.datasources.ms.event.dto.SessionDTO;
import es.onebox.common.datasources.ms.event.repository.MsEventRepository;
import es.onebox.common.datasources.ms.ticket.repository.MsTicketRepository;
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
import java.util.stream.Collectors;

@Service
public class SectorService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SectorService.class);
    private MsTicketRepository ticketRepository;
    private MsEventRepository eventRepository;
    private VenueTemplateRepository venueTemplateRepository;
    // Pending to be deleted
    private Long defaultChannelId = 183L;

    @Autowired
    public SectorService(MsTicketRepository ticketRepository, MsEventRepository eventRepository, VenueTemplateRepository venueTemplateRepository) {
        this.ticketRepository = ticketRepository;
        this.eventRepository = eventRepository;
        this.venueTemplateRepository = venueTemplateRepository;
    }

    public List<SectorDTO> getSectorStatus(String sectorCode, List<Long> sessionIds, Long channelId) {
        List<Sector> sectors = new ArrayList<>();
        Map<Long, SessionDTO> sessions = getSessions(sessionIds);
        List<Long> eventIds = getEventIds(sessions);

        // when circuit has developed the change, channelId will be required and this code will be deleted
        if (channelId == null) {
            channelId = defaultChannelId;
        }

        Sector sector = new Sector();
        sector.setCode(sectorCode);

        List<Long> validQuotas = getValidQuotas(eventIds, channelId);

        for (Long sessionId : sessionIds) {
            SessionDTO sessionDTO = sessions.get(sessionId);

            VenueTemplate venueTemplate = venueTemplateRepository.getVenueTemplate(sessionDTO.getVenueConfigId());

            es.onebox.common.datasources.ms.venue.dto.Sector venueSector = venueTemplate.getSectors().stream()
                    .filter(s -> s.getCode().equals(sectorCode))
                    .findFirst().orElse(null);

            if (venueSector == null) {
                LOGGER.error("[CIRCUIT CAT SECTOR] Sector code {} not found", sectorCode);
                throw new OneboxRestException(ApiExternalErrorCode.VENUE_SECTOR_NOT_FOUND);
            }

            Long sectorId = venueSector.getId();

            VenueMapProto.VenueMap venueMap = ticketRepository.getCapacityMap(sessionDTO.getEventId(), sessionId, Arrays.asList(sectorId));

            //Sectors
            for (VenueMapProto.SectorMap sectorMap : venueMap.getSectorMapList()) {
                if (sectorMap.getCode().equals(sectorCode)) {
                    for (VenueMapProto.RowMap rowMap : sectorMap.getRowMapList()) {
                        for (VenueMapProto.SeatMap seatMap : rowMap.getSeatMapList()) {
                            String seatKey = seatKey(sectorMap, rowMap, seatMap);

                            if (sector.getSeats() == null) {
                                sector.setSeats(new HashMap<>());
                            }

                            if (sector.getSeats().get(seatKey) == null) {
                                Seat seat = createSeat(sectorMap, rowMap, seatMap, validQuotas);
                                sector.getSeats().put(seatKey, seat);
                            } else {
                                Seat seat = sector.getSeats().get(seatKey);
                                SeatStatus seatStatus = from(seatMap.getStatus(), validQuotas, seatMap.getQuota());

                                if (!seat.getStatus().equals(SeatStatus.FREE) || !seatStatus.equals(SeatStatus.FREE)) {
                                    seat.setStatus(SeatStatus.PROMOTOR_LOCKED);
                                }
                            }
                        }
                    }
                }
            }
        }
        sectors.add(sector);
        return SectorConverter.from(sectors);
    }

    private SeatStatus from(VenueMapProto.Enums.SeatStatus seatStatus, List validQuotas, int quota) {
        if (!validQuotas.contains(Long.valueOf(quota))) {
            return SeatStatus.PROMOTOR_LOCKED;
        } else if (seatStatus.equals(VenueMapProto.Enums.SeatStatus.FREE)) {
            return SeatStatus.FREE;
        } else if (seatStatus.equals(VenueMapProto.Enums.SeatStatus.SOLD)) {
            return SeatStatus.SOLD;
        } else if (seatStatus.equals(VenueMapProto.Enums.SeatStatus.PROMOTOR_LOCKED)) {
            return SeatStatus.PROMOTOR_LOCKED;
        } else {
            return SeatStatus.KILL;
        }
    }

    private String seatKey(VenueMapProto.SectorMap sectorMap, VenueMapProto.RowMap rowMap, VenueMapProto.SeatMap seatMap) {
        return sectorMap.getCode() + "_" + rowMap.getName() + "_" + seatMap.getName();
    }

    private Seat createSeat(VenueMapProto.SectorMap sectorMap, VenueMapProto.RowMap rowMap, VenueMapProto.SeatMap seatMap, List<Long> validQuotas) {
        Seat seat = new Seat();
        seat.setSector(sectorMap.getCode());
        seat.setRow(rowMap.getName());
        seat.setSeat(seatMap.getName());
        seat.setStatus(from(seatMap.getStatus(), validQuotas, seatMap.getQuota()));
        return seat;
    }

    private Map<Long, SessionDTO> getSessions(List<Long> sessionIds) {
        Map<Long, SessionDTO> sessions = new HashMap<>();
        for (Long sessionId : sessionIds) {
            try {
                sessions.put(sessionId, eventRepository.getSession(sessionId));
            } catch (OneboxRestException e) {
                LOGGER.error("[CIRCUIT CAT SECTOR] Session {} not found", sessionId);
                throw new OneboxRestException(ApiExternalErrorCode.SESSION_NOT_FOUND);
            }
        }
        return sessions;
    }

    private List<Long> getEventIds(Map<Long, SessionDTO> sessions) {
        List<Long> eventIds = new ArrayList<>();
        for (SessionDTO sessionDTO : sessions.values()) {
            if (!eventIds.contains(sessionDTO.getEventId())) {
                eventIds.add(sessionDTO.getEventId());
            }
        }
        return eventIds;
    }

    private List<Long> getValidQuotas(List<Long> eventIds, Long channelId) {
        List<Long> quotas = new ArrayList<>();
        for (Long eventId : eventIds) {
            EventChannelDTO eventChannel = eventRepository.getEventChannel(eventId, channelId);

            if (eventChannel.getUseAllSaleGroups().booleanValue()) {
                quotas.addAll(eventChannel.getSaleGroups().stream().map(SaleGroupDTO::getId).collect(Collectors.toList()));
            } else {
                quotas.addAll(eventChannel.getSaleGroups().stream()
                        .filter(saleGroupDTO -> saleGroupDTO.getSelected())
                        .map(SaleGroupDTO::getId).collect(Collectors.toList()));
            }
        }
        return quotas;
    }
}
