package es.onebox.mgmt.seasontickets.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.response.Metadata;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.CountRenewalsPurgeResponse;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.DeleteRenewalsRequest;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.DeleteRenewalsResponse;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.RelatedRate;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.RenewalCandidatesSeasonTicketsRepositoryResponse;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.RenewalEntitiesRepositoryResponse;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.RenewalSeasonTicket;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicket;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicketRenewal;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicketRenewalsRepositoryResponse;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.UpdateRenewalRequest;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.UpdateRenewalResponse;
import es.onebox.mgmt.datasources.ms.event.repository.SeasonTicketRepository;
import es.onebox.mgmt.datasources.ms.ticket.dto.availability.Seat;
import es.onebox.mgmt.datasources.ms.ticket.dto.availability.Sector;
import es.onebox.mgmt.datasources.ms.ticket.repository.TicketsRepository;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.seasontickets.converter.SeasonTicketRenewalsConverter;
import es.onebox.mgmt.seasontickets.dto.UpdateSeasonTicketOperativeDTO;
import es.onebox.mgmt.seasontickets.dto.UpdateSeasonTicketRenewalDTO;
import es.onebox.mgmt.seasontickets.dto.renewals.CountRenewalsPurgeResponseDTO;
import es.onebox.mgmt.seasontickets.dto.renewals.DeleteRenewalsRequestDTO;
import es.onebox.mgmt.seasontickets.dto.renewals.DeleteRenewalsResponseDTO;
import es.onebox.mgmt.seasontickets.dto.renewals.RenewalCandidateSeasonTicketDTO;
import es.onebox.mgmt.seasontickets.dto.renewals.RenewalCandidatesSeasonTicketsResponse;
import es.onebox.mgmt.seasontickets.dto.renewals.RenewalEntitiesResponse;
import es.onebox.mgmt.seasontickets.dto.renewals.RenewalEntityDTO;
import es.onebox.mgmt.seasontickets.dto.renewals.RenewalSeasonTicketDTO;
import es.onebox.mgmt.seasontickets.dto.renewals.SeasonTicketRenewalFilter;
import es.onebox.mgmt.seasontickets.dto.renewals.SeasonTicketRenewalPurgeFilter;
import es.onebox.mgmt.seasontickets.dto.renewals.SeasonTicketRenewalSeatDTO;
import es.onebox.mgmt.seasontickets.dto.renewals.SeasonTicketRenewalsFilter;
import es.onebox.mgmt.seasontickets.dto.renewals.SeasonTicketRenewalsResponse;
import es.onebox.mgmt.seasontickets.dto.renewals.UpdateRenewalRequestDTO;
import es.onebox.mgmt.seasontickets.dto.renewals.UpdateRenewalResponseDTO;
import es.onebox.mgmt.seasontickets.dto.renewals.capacity.SeasonTicketRenewalAvailableSeatsDTO;
import es.onebox.mgmt.seasontickets.dto.renewals.capacity.SeasonTicketRenewalCapacityTreeDTO;
import es.onebox.mgmt.seasontickets.dto.renewals.capacity.SeatDTO;
import es.onebox.mgmt.seasontickets.dto.renewals.capacity.SectorDTO;
import es.onebox.mgmt.security.SecurityManager;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SeasonTicketRenewalsService {

    private static final String UTC = "UTC";

    private final SeasonTicketService seasonTicketService;
    private final SeasonTicketRepository seasonTicketRepository;
    private final TicketsRepository ticketsRepository;
    private final SecurityManager securityManager;

    @Autowired
    public SeasonTicketRenewalsService(@Lazy SeasonTicketService seasonTicketService,
                                       SeasonTicketRepository seasonTicketRepository,
                                       TicketsRepository ticketsRepository,
                                       SecurityManager securityManager) {
        this.seasonTicketService = seasonTicketService;
        this.seasonTicketRepository = seasonTicketRepository;
        this.ticketsRepository = ticketsRepository;
        this.securityManager = securityManager;
    }

    public void setRenewalData(SeasonTicket seasonTicketDTO, UpdateSeasonTicketOperativeDTO operative) {
        seasonTicketDTO.setAllowRenewal(operative.getAllowRenewal());
        if(operative.getRenewal() != null) {
            UpdateSeasonTicketRenewalDTO renewalDTO = operative.getRenewal();
            SeasonTicketRenewal renewal = new SeasonTicketRenewal();

            renewalDTO.setZoneId(UTC);
            renewalDTO.convertDates();

            renewal.setRenewalEnabled(renewalDTO.getEnable());
            renewal.setRenewalStartingDate(renewalDTO.getStartDate());
            renewal.setRenewalEndDate(renewalDTO.getEndDate());
            renewal.setAutoRenewal(renewalDTO.getAutomatic());
            renewal.setAutoRenewalMandatory(renewalDTO.getAutomaticMandatory());
            if (renewalDTO.getRenewalType() != null) {
                renewal.setRenewalType(renewalDTO.getRenewalType().name());
            }
            renewal.setBankAccountId(renewalDTO.getBankAccountId());
            renewal.setGroupByReference(renewalDTO.getGroupByReference());
            seasonTicketDTO.setRenewal(renewal);
        }
    }

    public RenewalCandidatesSeasonTicketsResponse searchRenewalCandidatesSeasonTickets(Long seasonTicketId) {
        seasonTicketService.getAndCheckSeasonTicket(seasonTicketId);
        RenewalCandidatesSeasonTicketsRepositoryResponse repositoryResponse = seasonTicketRepository.searchRenewalCandidatesSeasonTickets(seasonTicketId);
        if(repositoryResponse.getSeasonTicketRenewalCandidatesList() != null && !repositoryResponse.getSeasonTicketRenewalCandidatesList().isEmpty()) {
            List<RenewalCandidateSeasonTicketDTO> list = repositoryResponse.getSeasonTicketRenewalCandidatesList().stream()
                    .map(SeasonTicketRenewalsConverter::fromRenewalCandidatesSeasonTicketToRenewalCompatibleSeasonTicketDTO)
                    .collect(Collectors.toList());
            return new RenewalCandidatesSeasonTicketsResponse(list);
        } else {
            return new RenewalCandidatesSeasonTicketsResponse(Collections.emptyList());
        }
    }

    public RenewalEntitiesResponse searchRenewalEntities(Long seasonTicketId, SeasonTicketRenewalFilter filter) {
        seasonTicketService.getAndCheckSeasonTicket(seasonTicketId);
        RenewalEntitiesRepositoryResponse repositoryResponse = seasonTicketRepository.getRenewalEntities(seasonTicketId, filter);

        List<RenewalEntityDTO> renewalDTOList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(repositoryResponse.getRenewalEntities())) {
            renewalDTOList.addAll(repositoryResponse.getRenewalEntities()
                    .stream()
                    .map(SeasonTicketRenewalsConverter::fromRenewalEntityToRenewalEntityDTO)
                    .collect(Collectors.toList()));
        }


        RenewalEntitiesResponse response = new RenewalEntitiesResponse();
        Metadata metadata = new Metadata();
        response.setData(renewalDTOList);
        metadata.setTotal((long) renewalDTOList.size());
        metadata.setLimit(filter.getLimit());
        metadata.setOffset(filter.getOffset());
        response.setMetadata(metadata);

        return response;
    }

    public void setRenewalSeasonTicketDTO(Long seasonTicketId, RenewalSeasonTicketDTO renewalSeasonTicketDTO) {
        seasonTicketService.getAndCheckSeasonTicket(seasonTicketId);
        RenewalSeasonTicket renewalSeasonTicket = new RenewalSeasonTicket();
        renewalSeasonTicket.setRenewalSeasonTicket(renewalSeasonTicketDTO.getRenewalSeasonTicket());
        renewalSeasonTicket.setExternalEvent(renewalSeasonTicketDTO.getExternalEvent());
        renewalSeasonTicket.setRenewalExternalEvent(renewalSeasonTicketDTO.getRenewalExternalEvent());
        renewalSeasonTicket.setIncludeAllEntities(renewalSeasonTicketDTO.getIncludeAllEntities());
        renewalSeasonTicket.setIncludeBalance(renewalSeasonTicketDTO.getIncludeBalance());

        List<RelatedRate> relatedRates = renewalSeasonTicketDTO.getRates().stream()
                .map(SeasonTicketRenewalsConverter::convertRelatedRateDTO)
                .collect(Collectors.toList());
        renewalSeasonTicket.setRates(relatedRates);

        seasonTicketRepository.setRenewalSeasonTicketDTO(seasonTicketId, renewalSeasonTicket);
    }

    public SeasonTicketRenewalsResponse getRenewalsSeasonTicket(Long seasonTicketId, SeasonTicketRenewalFilter filter) {
        seasonTicketService.getAndCheckSeasonTicket(seasonTicketId);
        SeasonTicketRenewalsRepositoryResponse repositoryResponse = seasonTicketRepository.getRenewalsSeasonTicket(seasonTicketId, filter);

        List<SeasonTicketRenewalSeatDTO> renewalDTOList = repositoryResponse.getData()
                .stream()
                .map(SeasonTicketRenewalsConverter::convertSeasonTicketRenewalSeat)
                .collect(Collectors.toList());

        SeasonTicketRenewalsResponse response = new SeasonTicketRenewalsResponse();
        response.setData(renewalDTOList);
        response.setMetadata(repositoryResponse.getMetadata());
        response.setSummary(SeasonTicketRenewalsConverter.convertSeasonTicketRenewalSeatsSummary(repositoryResponse.getSummary()));

        return response;
    }

    public SeasonTicketRenewalsResponse getRenewals(SeasonTicketRenewalsFilter filter) {
        checkRenewalsFilter(filter);
        SeasonTicketRenewalsRepositoryResponse repositoryResponse = seasonTicketRepository.getRenewals(filter);

        List<SeasonTicketRenewalSeatDTO> renewalDTOList = repositoryResponse.getData()
                .stream()
                .map(SeasonTicketRenewalsConverter::convertSeasonTicketRenewalSeat)
                .collect(Collectors.toList());

        SeasonTicketRenewalsResponse response = new SeasonTicketRenewalsResponse();
        response.setData(renewalDTOList);
        response.setMetadata(repositoryResponse.getMetadata());
        response.setSummary(SeasonTicketRenewalsConverter.convertSeasonTicketRenewalSeatsSummary(repositoryResponse.getSummary()));

        return response;
    }

    public SeasonTicketRenewalCapacityTreeDTO getCapacityTree(Long seasonTicketId) {
        Long sessionId = validateSeasonTicketAndGetSessionId(seasonTicketId);

        List<Sector> sectorList = ticketsRepository.getAvailableSectorsAndRows(sessionId);
        List<SectorDTO> sectorDTOList = sectorList.stream()
                .map(SeasonTicketRenewalsConverter::convertSector)
                .collect(Collectors.toList());

        SeasonTicketRenewalCapacityTreeDTO response = new SeasonTicketRenewalCapacityTreeDTO();
        response.addAll(sectorDTOList);
        return response;
    }

    private Long validateSeasonTicketAndGetSessionId(Long seasonTicketId) {
        SeasonTicket seasonTicket = seasonTicketService.getAndCheckSeasonTicket(seasonTicketId);

        Long sessionId = seasonTicket.getSessionId();
        if(sessionId == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.SEASON_TICKET_IN_CREATION);
        }
        return sessionId;
    }

    public SeasonTicketRenewalAvailableSeatsDTO getAvailableSeats(Long seasonTicketId, Long rowId) {
        Long sessionId = validateSeasonTicketAndGetSessionId(seasonTicketId);

        List<Seat> seatList = ticketsRepository.getAvailableSeatsByRow(sessionId, rowId);
        List<SeatDTO> seatDTOList = seatList.stream()
                .map(SeasonTicketRenewalsConverter::convertSeat)
                .toList();

        SeasonTicketRenewalAvailableSeatsDTO response = new SeasonTicketRenewalAvailableSeatsDTO();
        response.addAll(seatDTOList);
        return response;
    }

    public SeasonTicketRenewalAvailableSeatsDTO getAvailableSeatsByNotNumberedZone(Long seasonTicketId, Long notNumberedZoneId) {
        Long sessionId = validateSeasonTicketAndGetSessionId(seasonTicketId);

        List<Seat> seatList = ticketsRepository.getAvailableSeatsByNotNumberedZone(sessionId, notNumberedZoneId);
        List<SeatDTO> seatDTOList = seatList.stream()
                .map(SeasonTicketRenewalsConverter::convertSeat)
                .toList();

        SeasonTicketRenewalAvailableSeatsDTO response = new SeasonTicketRenewalAvailableSeatsDTO();
        response.addAll(seatDTOList);
        return response;
    }

    public UpdateRenewalResponseDTO updateRenewalSeats(Long seasonTicketId, UpdateRenewalRequestDTO requestDTO) {
        seasonTicketService.getAndCheckSeasonTicket(seasonTicketId);
        UpdateRenewalRequest request = SeasonTicketRenewalsConverter.convertUpdateRenewalRequest(requestDTO);
        UpdateRenewalResponse updateRenewalResponse = seasonTicketRepository.updateRenewalSeats(seasonTicketId, request);
        return SeasonTicketRenewalsConverter.convertUpdateRenewalResponse(updateRenewalResponse);
    }

    public void deleteRenewalSeat(Long seasonTicketId, String renewalId) {
        seasonTicketService.getAndCheckSeasonTicket(seasonTicketId);
        seasonTicketRepository.deleteRenewalSeat(seasonTicketId, renewalId);
    }

    public DeleteRenewalsResponseDTO deleteRenewalSeats(Long seasonTicketId, DeleteRenewalsRequestDTO requestDTO) {
        seasonTicketService.getAndCheckSeasonTicket(seasonTicketId);
        DeleteRenewalsRequest request = SeasonTicketRenewalsConverter.convertDeleteRenewalsRequest(requestDTO);
        DeleteRenewalsResponse deleteRenewalsResponse = seasonTicketRepository.deleteRenewalSeats(seasonTicketId, request);
        return SeasonTicketRenewalsConverter.convertDeleteRenewalsResponse(deleteRenewalsResponse);
    }

    public void purgeSeasonTicketRenewalSeats(Long seasonTicketId, SeasonTicketRenewalPurgeFilter filter) {
        seasonTicketService.getAndCheckSeasonTicket(seasonTicketId);
        seasonTicketRepository.purgeSeasonTicketRenewalSeats(seasonTicketId, filter);
    }

    public CountRenewalsPurgeResponseDTO countRenewalsPurge(Long seasonTicketId, SeasonTicketRenewalPurgeFilter filter) {
        seasonTicketService.getAndCheckSeasonTicket(seasonTicketId);
        CountRenewalsPurgeResponse response = seasonTicketRepository.countRenewalsPurge(seasonTicketId, filter);
        return SeasonTicketRenewalsConverter.convertCountRenewalsPurgeResponse(response);
    }

    private void checkRenewalsFilter(SeasonTicketRenewalsFilter filter) {
        if (filter.getSeasonTicketId() != null) {
            SeasonTicket seasonTicket = seasonTicketRepository.getSeasonTicket(filter.getSeasonTicketId().longValue());
            if (seasonTicket != null) {
                securityManager.checkEntityAccessible(seasonTicket.getEntityId());
            }
        }
        if (filter.getEntityId() != null) {
            securityManager.checkEntityAccessible(filter.getEntityId());
        }
    }
}
