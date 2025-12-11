package es.onebox.mgmt.seasontickets.converter;

import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicketLinkSeatReason;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicketLinkSeatResult;
import es.onebox.mgmt.datasources.ms.ticket.SeasonTicketNotNumberedZoneLink;
import es.onebox.mgmt.datasources.ms.ticket.dto.SeasonTicketLinkResponse;
import es.onebox.mgmt.datasources.ms.ticket.dto.SeasonTicketNotNumberedZoneLinkResponse;
import es.onebox.mgmt.datasources.ms.ticket.dto.SeasonTicketNotNumberedZoneUnlinkResponse;
import es.onebox.mgmt.datasources.ms.ticket.dto.SeasonTicketSeatLink;
import es.onebox.mgmt.seasontickets.dto.SeasonTicketLinkResponseDTO;
import es.onebox.mgmt.seasontickets.dto.SeasonTicketLinkSeatResultDTO;
import es.onebox.mgmt.seasontickets.dto.SeasonTicketNotNumberedZoneLinkDTO;
import es.onebox.mgmt.seasontickets.dto.SeasonTicketNotNumberedZoneLinkResponseDTO;
import es.onebox.mgmt.seasontickets.dto.SeasonTicketNotNumberedZoneUnlinkResponseDTO;
import es.onebox.mgmt.seasontickets.dto.SeasonTicketSeatLinkDTO;
import es.onebox.mgmt.seasontickets.enums.SeasonTicketLinkSeatReasonDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SeasonTicketSeatLinkConverter {

    private SeasonTicketSeatLinkConverter() {
        throw new UnsupportedOperationException("Cannot instantiate utilities class");
    }

    public static SeasonTicketSeatLink fromSeasonTicketSeatLinkDTOToSeasonTicketSeatLink(SeasonTicketSeatLinkDTO seats) {
        SeasonTicketSeatLink seasonTicketSeatLink = new SeasonTicketSeatLink();
        seasonTicketSeatLink.setIds(new ArrayList<>(seats.getIds()));

        return seasonTicketSeatLink;
    }
    
    public static SeasonTicketLinkResponseDTO fromSeasonTicketLinkResponseToSeasonTicketLinkResponseDTO(SeasonTicketLinkResponse seasonTicketLinkResponse) {
        if(seasonTicketLinkResponse == null) {
            return null;
        }

        SeasonTicketLinkResponseDTO seasonTicketLinkResponseDTO = new SeasonTicketLinkResponseDTO();
        List<SeasonTicketLinkSeatResultDTO> seasonTicketLinkSeatResultDTOList = seasonTicketLinkResponse.getResults()
                .stream()
                .map(SeasonTicketSeatLinkConverter::fromSeasonTicketLinkSeatResultToSeasonTicketLinkSeatResultDTO)
                .collect(Collectors.toList());
        seasonTicketLinkResponseDTO.setResults(seasonTicketLinkSeatResultDTOList);

        return seasonTicketLinkResponseDTO;
    }

    private static SeasonTicketLinkSeatResultDTO fromSeasonTicketLinkSeatResultToSeasonTicketLinkSeatResultDTO(SeasonTicketLinkSeatResult seasonTicketLinkSeatResult) {
        if(seasonTicketLinkSeatResult == null) {
            return null;
        }
        return new SeasonTicketLinkSeatResultDTO(
                seasonTicketLinkSeatResult.getId(),
                seasonTicketLinkSeatResult.getResult(),
                fromSeasonTicketLinkSeatReasonToSeasonTicketLinkSeatReasonDTO(seasonTicketLinkSeatResult.getReason()));
    }

    private static SeasonTicketLinkSeatReasonDTO fromSeasonTicketLinkSeatReasonToSeasonTicketLinkSeatReasonDTO(SeasonTicketLinkSeatReason reason) {
        if(reason == null) {
            return null;
        } else {
            return SeasonTicketLinkSeatReasonDTO.valueOf(reason.name());
        }
    }

    public static SeasonTicketNotNumberedZoneLink convertSeasonTicketNotNumberedZoneLinkDTO(SeasonTicketNotNumberedZoneLinkDTO notNumberedZoneDTO) {
        if(notNumberedZoneDTO == null) {
            return null;
        }
        return new SeasonTicketNotNumberedZoneLink(notNumberedZoneDTO.getId(), notNumberedZoneDTO.getCount());
    }

    public static SeasonTicketNotNumberedZoneLinkResponseDTO convertSeasonTicketNotNumberedZoneLinkResponse(SeasonTicketNotNumberedZoneLinkResponse response) {
        if(response == null) {
            return null;
        }
        SeasonTicketNotNumberedZoneLinkResponseDTO responseDTO = new SeasonTicketNotNumberedZoneLinkResponseDTO();
        responseDTO.setLinkedSeats(response.getLinkedSeats());
        responseDTO.setNotLinkedSeats(response.getNotLinkedSeats());
        return responseDTO;
    }

    public static SeasonTicketNotNumberedZoneUnlinkResponseDTO convertSeasonTicketNotNumberedZoneUnlinkResponse(SeasonTicketNotNumberedZoneUnlinkResponse response) {
        if(response == null) {
            return null;
        }
        SeasonTicketNotNumberedZoneUnlinkResponseDTO responseDTO = new SeasonTicketNotNumberedZoneUnlinkResponseDTO();
        responseDTO.setUnlinkedSeats(response.getUnlinkedSeats());
        return responseDTO;
    }
}
