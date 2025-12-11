package es.onebox.mgmt.salerequests.converter;

import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.MsSessionSaleRequestDTO;
import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.MsSessionSaleRequestResponseDTO;
import es.onebox.mgmt.salerequests.dto.SessionDate;
import es.onebox.mgmt.salerequests.dto.SessionSaleRequestDTO;
import es.onebox.mgmt.salerequests.dto.SessionSaleRequestResponseDTO;
import es.onebox.mgmt.sessions.enums.SessionStatus;
import es.onebox.mgmt.sessions.enums.SessionType;

import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

public class SessionsSaleRequestConverter {

    public static SessionSaleRequestResponseDTO fromMsChannelsResponse(MsSessionSaleRequestResponseDTO msResponse) {
        SessionSaleRequestResponseDTO dto = new SessionSaleRequestResponseDTO();
        dto.setMetadata(msResponse.getMetadata());
        dto.setData(msResponse.getData()
                .stream()
                .map(SessionsSaleRequestConverter::fromMsChannelObject)
                .collect(Collectors.toList()));
        return dto;
    }

    private static SessionSaleRequestDTO fromMsChannelObject(MsSessionSaleRequestDTO msSessionSaleRequest) {
        SessionSaleRequestDTO result = new SessionSaleRequestDTO();

        result.setId(msSessionSaleRequest.getId());
        result.setName(msSessionSaleRequest.getName());
        if (nonNull(msSessionSaleRequest.getStatus())) {
            result.setStatus(SessionStatus.valueOf(msSessionSaleRequest.getStatus().name()));
        }
        if (nonNull(msSessionSaleRequest.getType())) {
            result.setType(SessionType.valueOf(msSessionSaleRequest.getType().name()));
        }
        if (nonNull(msSessionSaleRequest.getDate())){
            SessionDate date = new SessionDate();
            date.setStart(msSessionSaleRequest.getDate().getStart());
            date.setEnd(msSessionSaleRequest.getDate().getEnd());
            date.setPublication(msSessionSaleRequest.getDate().getPublication());
            date.setSalesStart(msSessionSaleRequest.getDate().getSalesStart());
            date.setSalesEnd(msSessionSaleRequest.getDate().getSalesEnd());
            date.setBookingStart(msSessionSaleRequest.getDate().getBookingStart());
            date.setBookingEnd(msSessionSaleRequest.getDate().getBookingEnd());
            result.setDate(date);
        }


        return result;
    }
}
