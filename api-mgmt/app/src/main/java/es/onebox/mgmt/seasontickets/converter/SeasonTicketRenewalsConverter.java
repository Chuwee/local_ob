package es.onebox.mgmt.seasontickets.converter;

import es.onebox.core.utils.common.NumberUtils;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.CountRenewalsPurgeResponse;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.DeleteRenewalsRequest;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.DeleteRenewalsResponse;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.DeleteRenewalsResponseItem;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.RelatedRate;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.RenewalCandidateReasonSeasonTicket;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.RenewalCandidatesSeasonTicket;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.RenewalEntity;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicketRenewal;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicketRenewalSeat;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicketRenewalSeatsSummary;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeatRenewal;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.UpdateRenewalRequest;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.UpdateRenewalRequestItem;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.UpdateRenewalResponse;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.UpdateRenewalResponseItem;
import es.onebox.mgmt.datasources.ms.ticket.dto.availability.NotNumberedZone;
import es.onebox.mgmt.datasources.ms.ticket.dto.availability.Row;
import es.onebox.mgmt.datasources.ms.ticket.dto.availability.Seat;
import es.onebox.mgmt.datasources.ms.ticket.dto.availability.Sector;
import es.onebox.mgmt.seasontickets.dto.SeasonTicketRenewalDTO;
import es.onebox.mgmt.seasontickets.dto.renewals.CountRenewalsPurgeResponseDTO;
import es.onebox.mgmt.seasontickets.dto.renewals.DeleteRenewalsRequestDTO;
import es.onebox.mgmt.seasontickets.dto.renewals.DeleteRenewalsResponseDTO;
import es.onebox.mgmt.seasontickets.dto.renewals.DeleteRenewalsResponseItemDTO;
import es.onebox.mgmt.seasontickets.dto.renewals.RelatedRateDTO;
import es.onebox.mgmt.seasontickets.dto.renewals.RenewalCandidateSeasonTicketDTO;
import es.onebox.mgmt.seasontickets.dto.renewals.RenewalEntityDTO;
import es.onebox.mgmt.seasontickets.dto.renewals.SeasonTicketRenewalSeatDTO;
import es.onebox.mgmt.seasontickets.dto.renewals.SeasonTicketRenewalSeatsSummaryDTO;
import es.onebox.mgmt.seasontickets.dto.renewals.SeasonTicketSeatType;
import es.onebox.mgmt.seasontickets.dto.renewals.SeatRenewalDTO;
import es.onebox.mgmt.seasontickets.dto.renewals.UpdateRenewalRequestDTO;
import es.onebox.mgmt.seasontickets.dto.renewals.UpdateRenewalRequestItemDTO;
import es.onebox.mgmt.seasontickets.dto.renewals.UpdateRenewalResponseDTO;
import es.onebox.mgmt.seasontickets.dto.renewals.UpdateRenewalResponseItemDTO;
import es.onebox.mgmt.seasontickets.dto.renewals.capacity.NotNumberedZoneDTO;
import es.onebox.mgmt.seasontickets.dto.renewals.capacity.RowDTO;
import es.onebox.mgmt.seasontickets.dto.renewals.capacity.SeatDTO;
import es.onebox.mgmt.seasontickets.dto.renewals.capacity.SectorDTO;
import es.onebox.mgmt.seasontickets.enums.RenewalCandidateReasonSeasonTicketDTO;
import es.onebox.mgmt.seasontickets.enums.SeatMappingStatus;
import es.onebox.mgmt.seasontickets.enums.SeatRenewalStatus;

import java.util.List;
import java.util.stream.Collectors;

public class SeasonTicketRenewalsConverter {

    private SeasonTicketRenewalsConverter() {
    }

    public static RenewalCandidateSeasonTicketDTO fromRenewalCandidatesSeasonTicketToRenewalCompatibleSeasonTicketDTO(RenewalCandidatesSeasonTicket renewalCandidatesSeasonTicket) {
        RenewalCandidateSeasonTicketDTO renewalCandidateSeasonTicketDTO = new RenewalCandidateSeasonTicketDTO();
        renewalCandidateSeasonTicketDTO.setId(renewalCandidatesSeasonTicket.getId());
        renewalCandidateSeasonTicketDTO.setName(renewalCandidatesSeasonTicket.getName());
        renewalCandidateSeasonTicketDTO.setCompatible(renewalCandidatesSeasonTicket.getCompatible());
        if(renewalCandidatesSeasonTicket.getReasons() != null) {
            renewalCandidateSeasonTicketDTO.setReasons(renewalCandidatesSeasonTicket.getReasons().stream()
                    .map(SeasonTicketRenewalsConverter::fromRepositoryReasonToDTOReason)
                    .collect(Collectors.toList()));
        }
        return renewalCandidateSeasonTicketDTO;
    }

    public static RenewalEntityDTO fromRenewalEntityToRenewalEntityDTO(RenewalEntity renewalEntity) {
        RenewalEntityDTO renewalEntityDTO = new RenewalEntityDTO();
        renewalEntityDTO.setId(renewalEntity.getId());
        renewalEntityDTO.setName(renewalEntity.getName());
        return renewalEntityDTO;
    }

    private static RenewalCandidateReasonSeasonTicketDTO fromRepositoryReasonToDTOReason(RenewalCandidateReasonSeasonTicket renewalCandidateReasonSeasonTicket) {
        return RenewalCandidateReasonSeasonTicketDTO.valueOf(renewalCandidateReasonSeasonTicket.name());
    }

    public static SeasonTicketRenewalSeatDTO convertSeasonTicketRenewalSeat(SeasonTicketRenewalSeat renewalSeat) {
        if(renewalSeat == null) {
            return null;
        }
        SeasonTicketRenewalSeatDTO renewalSeatDTO = new SeasonTicketRenewalSeatDTO();
        renewalSeatDTO.setId(renewalSeat.getId());
        renewalSeatDTO.setUserId(renewalSeat.getUserId());
        renewalSeatDTO.setSeasonTicketId(renewalSeat.getSeasonTicketId());
        renewalSeatDTO.setMemberId(renewalSeat.getMemberId());
        renewalSeatDTO.setProductClientId(renewalSeat.getProductClientId());
        renewalSeatDTO.setEmail(renewalSeat.getEmail());
        renewalSeatDTO.setName(renewalSeat.getName());
        renewalSeatDTO.setSurname(renewalSeat.getSurname());
        renewalSeatDTO.setBirthday(renewalSeat.getBirthday());
        renewalSeatDTO.setPhoneNumber(renewalSeat.getPhoneNumber());
        renewalSeatDTO.setSeasonTicketName(renewalSeat.getSeasonTicketName());
        renewalSeatDTO.setPostalCode(renewalSeat.getPostalCode());
        renewalSeatDTO.setAddress(renewalSeat.getAddress());
        renewalSeatDTO.setHistoricSeat(convertSeatRenewal(renewalSeat.getHistoricSeat()));
        renewalSeatDTO.setHistoricRate(renewalSeat.getHistoricRate());
        renewalSeatDTO.setActualSeat(convertSeatRenewal(renewalSeat.getActualSeat()));
        renewalSeatDTO.setActualRate(renewalSeat.getActualRate());
        renewalSeatDTO.setEntityId(renewalSeat.getEntityId());
        renewalSeatDTO.setEntityName(renewalSeat.getEntityName());
        renewalSeatDTO.setMappingStatus(convertSeatMappingStatus(renewalSeat.getMappingStatus()));
        renewalSeatDTO.setRenewalStatus(convertSeatRenewalStatus(renewalSeat.getRenewalStatus()));
        renewalSeatDTO.setRenewalSettings(convertSettings(renewalSeat.getRenewalSettings()));
        renewalSeatDTO.setActualRateId(renewalSeat.getActualRateId());
        renewalSeatDTO.setBalance(NumberUtils.zeroIfNull(renewalSeat.getBalance()));
        renewalSeatDTO.setOrderCode(renewalSeat.getOrderCode());
        renewalSeatDTO.setAutoRenewal(renewalSeat.getAutoRenewal());
        renewalSeatDTO.setRenewalSubstatus(renewalSeat.getRenewalSubstatus());

        return renewalSeatDTO;
    }

    private static SeatRenewalDTO convertSeatRenewal(SeatRenewal seatRenewal) {
        if(seatRenewal == null) {
            return null;
        }
        SeatRenewalDTO seatRenewalDTO = new SeatRenewalDTO();
        seatRenewalDTO.setNotNumberedZoneId(seatRenewal.getNotNumberedZoneId());
        seatRenewalDTO.setSectorId(seatRenewal.getSectorId());
        seatRenewalDTO.setRowId(seatRenewal.getRowId());
        seatRenewalDTO.setSeatId(seatRenewal.getSeatId());
        seatRenewalDTO.setSector(seatRenewal.getSector());
        seatRenewalDTO.setRow(seatRenewal.getRow());
        seatRenewalDTO.setSeat(seatRenewal.getSeat());
        seatRenewalDTO.setPrizeZone(seatRenewal.getPrizeZone());
        seatRenewalDTO.setNotNumberedZone(seatRenewal.getNotNumberedZone());
        if (seatRenewal.getSeatType() != null) {
            seatRenewalDTO.setSeatType(SeasonTicketSeatType.valueOf(seatRenewal.getSeatType().name()));
        } else {
            seatRenewalDTO.setSeatType(SeasonTicketSeatType.NUMBERED);
        }
        return seatRenewalDTO;
    }

    private static SeasonTicketRenewalDTO convertSettings(SeasonTicketRenewal seasonTicketRenewal){
        if(seasonTicketRenewal == null) {
            return null;
        }
        SeasonTicketRenewalDTO seasonTicketRenewalDTO = new SeasonTicketRenewalDTO();
        seasonTicketRenewalDTO.setEnable(seasonTicketRenewal.getRenewalEnabled());
        seasonTicketRenewalDTO.setEndDate(seasonTicketRenewal.getRenewalEndDate());
        seasonTicketRenewalDTO.setInProcess(seasonTicketRenewal.getRenewalInProcess());
        seasonTicketRenewalDTO.setStartDate(seasonTicketRenewal.getRenewalStartingDate());
        seasonTicketRenewalDTO.setAutomatic(seasonTicketRenewal.getAutoRenewal());
        return seasonTicketRenewalDTO;
    }

    private static SeatRenewalStatus convertSeatRenewalStatus(es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeatRenewalStatus renewalStatus) {
        if(renewalStatus != null) {
            return SeatRenewalStatus.valueOf(renewalStatus.name());
        } else {
            return null;
        }
    }

    public static SeasonTicketRenewalSeatsSummaryDTO convertSeasonTicketRenewalSeatsSummary(SeasonTicketRenewalSeatsSummary summary) {
        if(summary == null) {
            return null;
        }
        SeasonTicketRenewalSeatsSummaryDTO summaryDTO = new SeasonTicketRenewalSeatsSummaryDTO();
        summaryDTO.setOriginSeasonTicketId(summary.getOriginSeasonTicketId());
        summaryDTO.setOriginSeasonTicketName(summary.getOriginSeasonTicketName());
        summaryDTO.setRenewalImportDate(summary.getRenewalImportDate());
        summaryDTO.setMappedImports(summary.getMappedImports());
        summaryDTO.setNotMappedImports(summary.getNotMappedImports());
        summaryDTO.setTotalRenewals(summary.getTotalRenewals());
        summaryDTO.setGenerationStatus(summary.getGenerationStatus());
        summaryDTO.setAutomaticRenewalStatus(summary.getAutomaticRenewalStatus());
        return summaryDTO;
    }

    private static SeatMappingStatus convertSeatMappingStatus(es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeatMappingStatus mappingStatus) {
        if(mappingStatus != null) {
            return SeatMappingStatus.valueOf(mappingStatus.name());
        } else {
            return null;
        }
    }

    public static SectorDTO convertSector(Sector sector) {
        if(sector == null) {
            return null;
        }
        SectorDTO sectorDTO = new SectorDTO();
        sectorDTO.setSectorId(sector.getSectorId());
        sectorDTO.setSectorName(sector.getSectorName());
        sectorDTO.setAvailableSeats(sector.getAvailableSeats());

        if(sector.getRowList() != null) {
            List<RowDTO> rowDTOList = sector.getRowList().stream()
                    .map(SeasonTicketRenewalsConverter::convertRow)
                    .toList();
            sectorDTO.setRowList(rowDTOList);
        }

        if(sector.getNotNumberedZones() != null) {
            List<NotNumberedZoneDTO> notNumberedZones = sector.getNotNumberedZones().stream()
                    .map(SeasonTicketRenewalsConverter::convertNotNumberedZone)
                    .toList();
            sectorDTO.setNotNumberedZones(notNumberedZones);
        }

        return sectorDTO;
    }

    public static RowDTO convertRow(Row row) {
        if(row == null) {
            return null;
        }
        RowDTO rowDTO = new RowDTO();
        rowDTO.setRowId(row.getRowId());
        rowDTO.setRowName(row.getRowName());
        rowDTO.setAvailableSeats(row.getAvailableSeats());
        return rowDTO;
    }

    public static NotNumberedZoneDTO convertNotNumberedZone(NotNumberedZone nnz) {
        if(nnz == null) {
            return null;
        }
        NotNumberedZoneDTO notNumberedZoneDTO = new NotNumberedZoneDTO();
        notNumberedZoneDTO.setNotNumberedZoneId(nnz.getNotNumberedZoneId());
        notNumberedZoneDTO.setNotNumberedZoneName(nnz.getNotNumberedZoneName());
        notNumberedZoneDTO.setAvailableSeats(nnz.getAvailableSeats());
        return notNumberedZoneDTO;
    }

    public static SeatDTO convertSeat(Seat seat) {
        if(seat == null) {
            return null;
        }
        SeatDTO seatDTO = new SeatDTO();
        seatDTO.setSeatId(seat.getSeatId());
        seatDTO.setSeatName(seat.getSeatName());
        return seatDTO;
    }

    public static UpdateRenewalRequest convertUpdateRenewalRequest(UpdateRenewalRequestDTO requestDTO) {
        if(requestDTO == null) {
            return null;
        }
        UpdateRenewalRequest request = new UpdateRenewalRequest();
        if(requestDTO.getItems() != null) {
            List<UpdateRenewalRequestItem> requestItemList = requestDTO.getItems().stream()
                    .map(SeasonTicketRenewalsConverter::convertUpdateRenewalRequestItem)
                    .collect(Collectors.toList());
            request.setItems(requestItemList);
        }
        return request;
    }

    public static UpdateRenewalRequestItem convertUpdateRenewalRequestItem(UpdateRenewalRequestItemDTO item) {
        if(item == null) {
            return null;
        }
        UpdateRenewalRequestItem requestItem = new UpdateRenewalRequestItem();
        requestItem.setUserId(item.getUserId());
        requestItem.setId(item.getId());
        requestItem.setSeatId(item.getSeatId());
        requestItem.setRateId(item.getRateId());
        requestItem.setRenewalSubstatus(item.getRenewalSubstatus());
        requestItem.setAutoRenewal(item.getAutoRenewal());
        return requestItem;
    }

    public static UpdateRenewalResponseDTO convertUpdateRenewalResponse(UpdateRenewalResponse response) {
        if(response == null) {
            return null;
        }
        UpdateRenewalResponseDTO responseDTO = new UpdateRenewalResponseDTO();
        if(response.getItems() != null) {
            List<UpdateRenewalResponseItemDTO> responseItemDTOList = response.getItems().stream()
                    .map(SeasonTicketRenewalsConverter::convertUpdateRenewalResponseItem)
                    .collect(Collectors.toList());
            responseDTO.setItems(responseItemDTOList);
        }
        return responseDTO;
    }

    public static UpdateRenewalResponseItemDTO convertUpdateRenewalResponseItem(UpdateRenewalResponseItem item) {
        if(item == null) {
            return null;
        }
        UpdateRenewalResponseItemDTO responseItemDTO = new UpdateRenewalResponseItemDTO();
        responseItemDTO.setId(item.getId());
        responseItemDTO.setResult(item.getResult());
        responseItemDTO.setReason(item.getReason());
        return responseItemDTO;
    }

    public static DeleteRenewalsRequest convertDeleteRenewalsRequest(DeleteRenewalsRequestDTO requestDTO) {
        if(requestDTO == null) {
            return null;
        }
        DeleteRenewalsRequest request = new DeleteRenewalsRequest();
        if(requestDTO.getRenewalIds() != null) {
            request.setRenewalIds(requestDTO.getRenewalIds());
        }
        return request;
    }

    public static DeleteRenewalsResponseDTO convertDeleteRenewalsResponse(DeleteRenewalsResponse response) {
        if(response == null) {
            return null;
        }
        DeleteRenewalsResponseDTO responseDTO = new DeleteRenewalsResponseDTO();
        if(response.getItems() != null) {
            List<DeleteRenewalsResponseItemDTO> responseItemDTOList = response.getItems().stream()
                    .map(SeasonTicketRenewalsConverter::convertDeleteRenewalsResponseItem)
                    .collect(Collectors.toList());
            responseDTO.setItems(responseItemDTOList);
        }
        return responseDTO;
    }

    public static DeleteRenewalsResponseItemDTO convertDeleteRenewalsResponseItem(DeleteRenewalsResponseItem item) {
        if(item == null) {
            return null;
        }
        DeleteRenewalsResponseItemDTO responseItemDTO = new DeleteRenewalsResponseItemDTO();
        responseItemDTO.setId(item.getId());
        responseItemDTO.setResult(item.getResult());
        return responseItemDTO;
    }

    public static RelatedRate convertRelatedRateDTO(RelatedRateDTO rateDTO) {
        if(rateDTO == null) {
            return null;
        }
        RelatedRate rate = new RelatedRate();
        rate.setOldRateId(rateDTO.getOldRateId());
        rate.setNewRateId(rateDTO.getNewRateId());
        return rate;
    }

    public static CountRenewalsPurgeResponseDTO convertCountRenewalsPurgeResponse(CountRenewalsPurgeResponse response) {
        if(response == null) {
            return null;
        }
        CountRenewalsPurgeResponseDTO responseDTO = new CountRenewalsPurgeResponseDTO();
        responseDTO.setDeletableRenewals(response.getDeletableRenewals());
        return responseDTO;
    }
}
