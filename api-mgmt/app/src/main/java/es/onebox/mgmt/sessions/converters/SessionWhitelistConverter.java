package es.onebox.mgmt.sessions.converters;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.datasources.ms.event.dto.event.Event;
import es.onebox.mgmt.datasources.ms.event.dto.session.Session;
import es.onebox.mgmt.datasources.ms.ticket.dto.Ticket;
import es.onebox.mgmt.datasources.ms.ticket.dto.WhitelistSearchResponse;
import es.onebox.mgmt.datasources.ms.venue.dto.template.PriceType;
import es.onebox.mgmt.datasources.ms.venue.dto.template.Row;
import es.onebox.mgmt.datasources.ms.venue.dto.template.Sector;
import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTemplate;
import es.onebox.mgmt.sessions.dto.BarcodeDTO;
import es.onebox.mgmt.sessions.dto.BarcodeSessionDataDTO;
import es.onebox.mgmt.sessions.dto.SeatDataDTO;
import es.onebox.mgmt.sessions.dto.SessionWhitelistDTO;
import es.onebox.mgmt.sessions.dto.WhitelistFilter;
import es.onebox.mgmt.sessions.dto.WhitelistFilterDTO;
import es.onebox.mgmt.sessions.enums.ValidationStatus;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SessionWhitelistConverter {

    private SessionWhitelistConverter() {}

    public static SessionWhitelistDTO toDTO(WhitelistSearchResponse source, Event event, Session session,
                                            VenueTemplate venueTemplate, List<PriceType> inPriceTypes) {
        Map<Long, String> sectors = venueTemplate.getSectors().stream().collect(Collectors.toMap(Sector::getId, Sector::getName));
        Map<Long, String> rows = venueTemplate.getRows().stream().collect(Collectors.toMap(Row::getRowId, Row::getName));
        Map<Long, String> priceTypes = inPriceTypes.stream().collect(Collectors.toMap(PriceType::getId, PriceType::getName));

        SessionWhitelistDTO target = new SessionWhitelistDTO();
        target.setMetadata(source.getMetadata());
        target.setData(source.getData().stream().map(elem -> toDTO(elem, event, session, sectors, rows, priceTypes)).collect(Collectors.toList()));
        return target;
    }

    private static BarcodeDTO<ValidationStatus> toDTO(Ticket source, Event event, Session session, Map<Long, String> sectors,
                                                      Map<Long, String> rows, Map<Long, String> priceTypes) {
        BarcodeDTO<ValidationStatus> target = new BarcodeDTO<>();
        target.setBarcode(source.getBarcode());
        target.setStatus(ValidationStatus.valueOf(source.getStatus().name()));
        target.setSession(new BarcodeSessionDataDTO(session.getId(), session.getName(), session.getDate().getStart()));
        target.setEvent(new IdNameDTO(event.getId(), event.getName()));
        target.setPriceZone(getIdName(priceTypes, source.getPriceTypeId()));
        SeatDataDTO seatData = new SeatDataDTO();
        if (source.getSeatId() != null || source.getSeat() != null) {
            seatData.setSeat(new IdNameDTO(source.getSeatId(), source.getSeat()));
        }
        if (source.getNotNumberedAreaId() != null) {
            seatData.setNotNumberedArea(new IdNameDTO(source.getNotNumberedAreaId()));
        }
        seatData.setSector(getIdName(sectors, source.getSectorId()));
        seatData.setRow(getIdName(rows, source.getRow()));
        target.setSeatData(seatData);
        return target;
    }

    private static IdNameDTO getIdName(Map<Long, String> source, Long id) {
        if (id != null) {
            return new IdNameDTO(id, source.getOrDefault(id, null));
        }
        return null;
    }

    public static WhitelistFilter toMs(WhitelistFilterDTO source) {
        WhitelistFilter target = new WhitelistFilter();
        target.setLimit(source.getLimit());
        target.setOffset(source.getOffset());
        target.setBarcode(source.getBarcode());
        return target;
    }
}
