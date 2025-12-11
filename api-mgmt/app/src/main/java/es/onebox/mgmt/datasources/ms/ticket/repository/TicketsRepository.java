package es.onebox.mgmt.datasources.ms.ticket.repository;

import es.onebox.core.file.exporter.status.model.ExportProcess;
import es.onebox.mgmt.datasources.common.dto.QuotaCapacity;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventType;
import es.onebox.mgmt.datasources.ms.ticket.MsTicketDatasource;
import es.onebox.mgmt.datasources.ms.ticket.SeasonTicketNotNumberedZoneLink;
import es.onebox.mgmt.datasources.ms.ticket.dto.CapacityExportFilter;
import es.onebox.mgmt.datasources.ms.ticket.dto.CapacityRelocationRequest;
import es.onebox.mgmt.datasources.ms.ticket.dto.NotNumberedZoneLinkDTO;
import es.onebox.mgmt.datasources.ms.ticket.dto.PassbookPreviewRequest;
import es.onebox.mgmt.datasources.ms.ticket.dto.SeasonTicketLinkResponse;
import es.onebox.mgmt.datasources.ms.ticket.dto.SeasonTicketNotNumberedZoneLinkResponse;
import es.onebox.mgmt.datasources.ms.ticket.dto.SeasonTicketNotNumberedZoneUnlinkResponse;
import es.onebox.mgmt.datasources.ms.ticket.dto.SeasonTicketSeatLink;
import es.onebox.mgmt.datasources.ms.ticket.dto.SeasonTicketSeatsSummary;
import es.onebox.mgmt.datasources.ms.ticket.dto.SeatLinkDTO;
import es.onebox.mgmt.datasources.ms.ticket.dto.SessionOccupationDTO;
import es.onebox.mgmt.datasources.ms.ticket.dto.SessionOccupationsSearchRequest;
import es.onebox.mgmt.datasources.ms.ticket.dto.SessionPriceZoneOccupationResponseDTO;
import es.onebox.mgmt.datasources.ms.ticket.dto.SessionWithQuotasDTO;
import es.onebox.mgmt.datasources.ms.ticket.dto.TicketPrintResult;
import es.onebox.mgmt.datasources.ms.ticket.dto.WhitelistSearchResponse;
import es.onebox.mgmt.datasources.ms.ticket.dto.availability.Seat;
import es.onebox.mgmt.datasources.ms.ticket.dto.availability.Sector;
import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTagNotNumberedZoneDTO;
import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTagSeatDTO;
import es.onebox.mgmt.export.dto.ExportFilter;
import es.onebox.mgmt.sessions.dto.WhiteListExportFileField;
import es.onebox.mgmt.sessions.dto.WhitelistFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Repository
public class TicketsRepository {

    private final MsTicketDatasource msTicketDatasource;

    @Autowired
    public TicketsRepository(MsTicketDatasource msTicketDatasource) {
        this.msTicketDatasource = msTicketDatasource;
    }

    public SessionOccupationDTO getSessionOccupation(Long sessionId) {
        return msTicketDatasource.getSessionOccupation(sessionId);
    }

    public List<SessionPriceZoneOccupationResponseDTO> getSessionOccupationsByPriceZones(EventType eventType, Long sessionId, List<Long> quotas) {
        SessionOccupationsSearchRequest request = new SessionOccupationsSearchRequest();
        request.setEventType(eventType);
        List<SessionWithQuotasDTO> sessionData = new ArrayList<>();
        sessionData.add(new SessionWithQuotasDTO(sessionId, null));
        for (Long quota : quotas) {
            sessionData.add(new SessionWithQuotasDTO(sessionId, quota));
        }
        request.setSessions(sessionData);
        return msTicketDatasource.searchSessionOccupationsByPriceZones(request);
    }

    public InputStream getCapacityMap(Long eventId, Long sessionId) {
        return msTicketDatasource.getCapacityMap(eventId, sessionId);
    }

    public void updateSeatsCapacity(Long eventId, Long sessionId, VenueTagSeatDTO[] fromTagRequest) {
        msTicketDatasource.updateSeatsCapacity(eventId, sessionId, fromTagRequest);
    }

    public void updateSeatsCapacityBulk(Long eventId, List<Long> sessionIds, VenueTagSeatDTO[] fromTagRequest) {
        msTicketDatasource.updateSeatsCapacityBulk(eventId, sessionIds, List.of(fromTagRequest));
    }

    public void updateNNZonesCapacity(Long eventId, Long sessionId, VenueTagNotNumberedZoneDTO[] fromTagRequest) {
        msTicketDatasource.updateNNZonesCapacity(eventId, sessionId, fromTagRequest);
    }

    public void updateNNZonesCapacityBulk(Long eventId, List<Long> sessionIds, VenueTagNotNumberedZoneDTO[] fromTagRequest) {
        msTicketDatasource.updateNNZonesCapacityBulk(eventId, sessionIds, List.of(fromTagRequest));
    }

    public Boolean getSessionCapacityUpdating(Long eventId, Long sessionId) {
        return msTicketDatasource.getSessionCapacityUpdating(eventId, sessionId);
    }

    public List<Long> getEventSessionsCapacityUpdating(Long eventId) {
        return msTicketDatasource.getEventSessionsCapacityUpdating(eventId);
    }

    public Boolean getSessionCapacityGenerating(Long eventId, Long sessionId) {
        return msTicketDatasource.getSessionCapacityGenerating(eventId, sessionId);
    }

    public List<Long> getEventSessionsCapacityGenerating(Long eventId) {
        return msTicketDatasource.getEventSessionsCapacityGenerating(eventId);
    }

    public List<Long> linkSeats(Long eventId, Long sessionId, SeatLinkDTO seatLink) {
        return msTicketDatasource.linkSeats(eventId, sessionId, seatLink);
    }

    public List<Long> unlinkSeats(Long eventId, Long sessionId, SeatLinkDTO seatLink) {
        return msTicketDatasource.unlinkSeats(eventId, sessionId, seatLink);
    }

    public void linkNNZ(Long eventId, Long sessionId, NotNumberedZoneLinkDTO nnzLink) {
        msTicketDatasource.linkNNZ(eventId, sessionId, nnzLink);
    }

    public void unlinkNNZ(Long eventId, Long sessionId, NotNumberedZoneLinkDTO nnzLink) {
        msTicketDatasource.unlinkNNZ(eventId, sessionId, nnzLink);
    }

    public TicketPrintResult getPassbookPreview(PassbookPreviewRequest request, String passbookCode) {
        return msTicketDatasource.getPassbookPreview(request, passbookCode);
    }

    public SeasonTicketLinkResponse linkSeasonTicketSeats(Long sessionId, SeasonTicketSeatLink seasonTicketSeatLink) {
        return msTicketDatasource.seasonTicketLinkSeats(sessionId, seasonTicketSeatLink);
    }

    public SeasonTicketLinkResponse unLinkSeasonTicketSeats(Long sessionId, SeasonTicketSeatLink seasonTicketSeatLink) {
        return msTicketDatasource.seasonTicketUnLinkSeats(sessionId, seasonTicketSeatLink);
    }

    public List<QuotaCapacity> getQuotasCapacity(Long eventId, Long sessionId) {
        return msTicketDatasource.getQuotasCapacity(eventId, sessionId);
    }

    public void updateQuotasCapacity(Long eventId, Long sessionId, List<QuotaCapacity> requestDTO) {
        msTicketDatasource.updateQuotasCapacity(eventId, sessionId, requestDTO, false);
    }

    public void updateQuotasCapacitySkipRefreshSession(Long eventId, Long sessionId, List<QuotaCapacity> requestDTO) {
        msTicketDatasource.updateQuotasCapacity(eventId, sessionId, requestDTO, true);
    }

    public SessionOccupationDTO getSessionGroupsOccupation(Long sessionId) {
        return msTicketDatasource.getSessionGroupOccupation(sessionId);
    }

    public WhitelistSearchResponse getWhitelist(Long sessionId, WhitelistFilter filter) {
        return msTicketDatasource.getWhitelist(sessionId, filter);
    }

    public ExportProcess generateWhiteListReport(Long sessionId, ExportFilter<WhiteListExportFileField> filter) {
        return msTicketDatasource.generateWhiteListReport(sessionId, filter);
    }

    public ExportProcess getWhiteListReportStatus(Long sessionId, String exportId, Long userId) {
        return msTicketDatasource.getWhitelistReportStatus(sessionId, exportId, userId);
    }

    public List<Sector> getAvailableSectorsAndRows(Long sessionId) {
        return msTicketDatasource.getAvailableSectorsAndRows(sessionId);
    }

    public List<Seat> getAvailableSeatsByRow(Long sessionId, Long rowId) {
        return msTicketDatasource.getAvailableSeatsByRow(sessionId, rowId);
    }

    public List<Seat> getAvailableSeatsByNotNumberedZone(Long sessionId, Long notNumberedZoneId) {
        return msTicketDatasource.getAvailableSeatsByNotNumberedZone(sessionId, notNumberedZoneId);
    }

    public Long getSessionSalesAmount(Long sessionId) {
        return msTicketDatasource.getSessionSalesAmount(sessionId);
    }

    public SeasonTicketNotNumberedZoneLinkResponse linkSeasonTicketNNZ(Long sessionId, SeasonTicketNotNumberedZoneLink seasonTicketNotNumberedZoneLink) {
        return msTicketDatasource.linkSeasonTicketNNZ(sessionId, seasonTicketNotNumberedZoneLink);
    }

    public SeasonTicketNotNumberedZoneUnlinkResponse unlinkSeasonTicketNNZ(Long sessionId, SeasonTicketNotNumberedZoneLink seasonTicketNotNumberedZoneLink) {
        return msTicketDatasource.unlinkSeasonTicketNNZ(sessionId, seasonTicketNotNumberedZoneLink);
    }

    public SeasonTicketSeatsSummary getSeasonTicketSeatsSummary(Long sessionId) {
        return msTicketDatasource.getSeasonTicketSeatsSummary(sessionId);
    }

    public ExportProcess generateSessionCapacityReport(Long eventId, Long sessionId, CapacityExportFilter filter) {
        return msTicketDatasource.generateSessionCapacityReport(eventId, sessionId, filter);
    }

    public ExportProcess getSessionCapacityReportStatus(Long eventId, Long sessionId, String exportId, Long userId) {
        return msTicketDatasource.getSessionCapacityReportStatus(eventId, sessionId, exportId, userId);
    }

    public void relocateSeats(Long eventId, Long sessionId, CapacityRelocationRequest capacityRelocationRequest) {
        msTicketDatasource.relocateSeats(eventId, sessionId, capacityRelocationRequest);
    }
}
