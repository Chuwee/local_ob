package es.onebox.event.datasources.ms.ticket;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.core.exception.ErrorCode;
import es.onebox.datasource.http.ClientRequestBody;
import es.onebox.datasource.http.HttpClient;
import es.onebox.datasource.http.QueryParameters;
import es.onebox.datasource.http.configuration.HttpClientFactoryBuilder;
import es.onebox.datasource.http.method.HttpMethod;
import es.onebox.datasource.http.response.ListType;
import es.onebox.event.datasources.ms.ticket.dto.CloneSessionDTO;
import es.onebox.event.datasources.ms.ticket.dto.LinkSessionCapacityResponse;
import es.onebox.event.datasources.ms.ticket.dto.PassbookTemplate;
import es.onebox.event.datasources.ms.ticket.dto.SessionCompatibilityValidationResponse;
import es.onebox.event.datasources.ms.ticket.dto.SessionDTO;
import es.onebox.event.datasources.ms.ticket.dto.SessionPriceZonesFilter;
import es.onebox.event.datasources.ms.ticket.dto.SessionUnlinkResponse;
import es.onebox.event.datasources.ms.ticket.dto.TicketFilter;
import es.onebox.event.datasources.ms.ticket.dto.TicketsSearchResponse;
import es.onebox.event.datasources.ms.ticket.dto.occupation.SessionOccupationByPriceZoneDTO;
import es.onebox.event.datasources.ms.ticket.dto.occupation.SessionOccupationVenueContainer;
import es.onebox.event.datasources.ms.ticket.dto.occupation.SessionOccupationsSearchRequest;
import es.onebox.event.datasources.ms.ticket.dto.occupation.SessionVenueContainerSearchRequest;
import es.onebox.event.datasources.ms.ticket.dto.seasontickets.UpdateRelatedSeatsRequest;
import es.onebox.event.datasources.ms.ticket.dto.seasontickets.UpdateRelatedSeatsRequestItem;
import es.onebox.event.datasources.ms.ticket.dto.seasontickets.UpdateRelatedSeatsResponse;
import es.onebox.event.datasources.ms.ticket.dto.seasontickets.renewals.RenewalSeasonTicketOriginSeat;
import es.onebox.event.datasources.ms.ticket.dto.seasontickets.renewals.SeasonTicketRenewalRequest;
import es.onebox.event.datasources.ms.ticket.dto.seasontickets.renewals.SeasonTicketRenewalResponse;
import es.onebox.event.datasources.ms.ticket.dto.secondarymarket.SecondaryMarketSearchFilter;
import es.onebox.event.datasources.ms.ticket.dto.secondarymarket.SecondaryMarketSearchResponse;
import es.onebox.event.datasources.ms.ticket.enums.CapacityType;
import es.onebox.event.datasources.ms.ticket.enums.TicketStatus;
import es.onebox.event.datasources.utils.ClientHttpExceptionBuilder;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.exception.MsEventSeasonTicketErrorCode;
import es.onebox.event.seasontickets.amqp.renewals.relatedseats.RenewalsUpdateRelatedSeatsRequestItem;
import okhttp3.Interceptor;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class MsTicketDatasource {

    private static final String API_VERSION = "v1";
    private static final String BASE_PATH = "/tickets-api/" + API_VERSION;
    private static final String SESSION = "/sessions/{sessionId}";
    private static final String VALIDATIONS = "/validations";
    private static final String VALIDATIONS_PATH = SESSION + VALIDATIONS;
    private static final String LINK = "/link";
    private static final String LINK_PATH = SESSION + LINK;
    private static final String UNLINK = SESSION + "/unlink";
    private static final String PASSBOOK_TEMPLATES = "/passbook-templates";
    private static final String TICKETS_PATH = "/tickets/search";
    private static final int READ_TIMEOUT = 60000;
    private static final Map<String, ErrorCode> ERROR_CODES = new HashMap<>();

    static {
        ERROR_CODES.put("MTK0002", null);
        ERROR_CODES.put("MTK0003", null);
        ERROR_CODES.put("PASSBOOK_TEMPLATE_NOT_FOUND", MsEventErrorCode.PASSBOOK_TEMPLATE_NOT_FOUND);
        ERROR_CODES.put("RENEWALS_SEATS_NOT_AVAILABLE", MsEventSeasonTicketErrorCode.RENEWALS_SEATS_NOT_AVAILABLE);
    }

    private final HttpClient httpClient;

    @Autowired
    public MsTicketDatasource(@Value("${clients.services.ms-ticket}") String baseUrl,
                              ObjectMapper jacksonMapper,
                              Interceptor tracingInterceptor) {
        this.httpClient = HttpClientFactoryBuilder.builder()
                .baseUrl(baseUrl + BASE_PATH)
                .jacksonMapper(jacksonMapper)
                .interceptors(tracingInterceptor)
                .exceptionBuilder(new ClientHttpExceptionBuilder(ERROR_CODES, jacksonMapper))
                .readTimeout(READ_TIMEOUT)
                .build();
    }

    public void createSession(SessionDTO session) {
        httpClient.buildRequest(HttpMethod.POST, "/sessions")
                .body(new ClientRequestBody(session))
                .execute();
    }

    public void cloneSession(Long fromSessionId, CloneSessionDTO session) {
        httpClient.buildRequest(HttpMethod.POST, "/sessions/{sessionId}/clone")
                .pathParams(fromSessionId)
                .body(new ClientRequestBody(session))
                .execute();
    }

    public List<Long> getSessionQuotas(Long sessionId, CapacityType type, Boolean asSeasonSession) {
        QueryParameters.Builder builder = new QueryParameters.Builder();
        builder.addQueryParameter("type", type.name());
        if (BooleanUtils.isTrue(asSeasonSession)) {
            builder.addQueryParameter("session-pack-linked", asSeasonSession);
        }
        return httpClient.buildRequest(HttpMethod.GET, "/sessions/{sessionId}/quotas")
                .pathParams(sessionId)
                .params(builder.build())
                .execute(ListType.of(Long.class));
    }

    public List<Long> getSessionPriceZones(Long sessionId, List<Long> quotaIds, CapacityType type) {
        QueryParameters.Builder builder = new QueryParameters.Builder();
        SessionPriceZonesFilter filter = new SessionPriceZonesFilter();
        filter.setQuota(quotaIds);
        filter.setType(type);
        builder.addQueryParameters(filter);
        return httpClient.buildRequest(HttpMethod.GET, "/sessions/{sessionId}/price-zones")
                .pathParams(sessionId)
                .params(builder.build())
                .execute(ListType.of(Long.class));
    }

    public List<SessionOccupationByPriceZoneDTO> searchSessionOccupationsByPriceZones(SessionOccupationsSearchRequest request) {
        return httpClient.buildRequest(HttpMethod.POST, "/session-occupations/price-zones/search")
                .body(new ClientRequestBody(request))
                .execute(ListType.of(SessionOccupationByPriceZoneDTO.class));
    }

    public Long countSessionOccupationsByPriceZones(Long sessionId, Long priceZoneId) {
        return httpClient.buildRequest(HttpMethod.GET, "/session-occupations/{sessionId}/price-zone/{priceZoneId}")
                .pathParams(sessionId, priceZoneId)
                .execute(Long.class);
    }

    public List<SessionOccupationVenueContainer> searchOccupationsByContainer(Long sessionId, SessionVenueContainerSearchRequest request) {
        return httpClient.buildRequest(HttpMethod.POST, "/session-occupations/{sessionId}/venue-containers/search")
                .pathParams(sessionId)
                .body(new ClientRequestBody(request))
                .execute(ListType.of(SessionOccupationVenueContainer.class));
    }

    public SessionCompatibilityValidationResponse validateSessionCompatibility(Long sessionId, Boolean restrictive, Long targetSessionId) {
        QueryParameters.Builder params = new QueryParameters.Builder();

        params.addQueryParameter("restrictive", restrictive);
        params.addQueryParameter("targetSessionId", targetSessionId);

        return httpClient.buildRequest(HttpMethod.GET, VALIDATIONS_PATH)
                .pathParams(sessionId)
                .params(params.build())
                .execute(SessionCompatibilityValidationResponse.class);
    }

    public LinkSessionCapacityResponse linkSessionCapacity(Long sessionId, Long targetSessionId, Boolean updateBarcodes) {
        QueryParameters.Builder params = new QueryParameters.Builder();
        params.addQueryParameter("targetSessionId", targetSessionId);
        params.addQueryParameter("updateBarcodes", updateBarcodes);

        return httpClient.buildRequest(HttpMethod.POST, LINK_PATH)
                .pathParams(sessionId)
                .params(params.build())
                .execute(LinkSessionCapacityResponse.class);
    }

    public SessionUnlinkResponse unLinkSessionCapacity(Long sessionId, Long targetSessionId, Boolean updateBarcodes) {
        QueryParameters.Builder params = new QueryParameters.Builder();
        params.addQueryParameter("targetSessionId", targetSessionId);
        params.addQueryParameter("updateBarcodes", updateBarcodes);

        return httpClient.buildRequest(HttpMethod.DELETE, UNLINK)
                .pathParams(sessionId)
                .params(params.build())
                .execute(SessionUnlinkResponse.class);
    }

    public PassbookTemplate getPassbookTemplate(String code, Long entityId) {
        return httpClient.buildRequest(HttpMethod.GET, PASSBOOK_TEMPLATES + "/{code}")
                .pathParams(code)
                .params(new QueryParameters.Builder().addQueryParameter("entityId", entityId).build())
                .execute(PassbookTemplate.class);
    }

    public SeasonTicketRenewalResponse renewalSeasonTicket(Long renewalSeasonTicketSessionId, Long originSeasonTicketSessionId,
                                                           List<RenewalSeasonTicketOriginSeat> originSeats) {
        SeasonTicketRenewalRequest request = new SeasonTicketRenewalRequest();
        request.setOriginSeasonTicketSessionId(originSeasonTicketSessionId);
        request.setOriginSeats(originSeats);
        return httpClient.buildRequest(HttpMethod.POST, "/season-tickets/{sessionId}/renewal")
                .pathParams(renewalSeasonTicketSessionId)
                .body(new ClientRequestBody(request))
                .execute(SeasonTicketRenewalResponse.class);
    }

    public TicketsSearchResponse getTickets(Long sessionId, List<Long> seatIds, List<TicketStatus> ticketStatus) {
        TicketFilter request = new TicketFilter();
        request.setSessionId(sessionId);
        request.setId(seatIds);
        request.setStatus(ticketStatus);
        return httpClient.buildRequest(HttpMethod.POST, TICKETS_PATH)
                .body(new ClientRequestBody(request))
                .execute(TicketsSearchResponse.class);
    }

    public UpdateRelatedSeatsResponse updateRelatedSeasonTicketSeatsStatus(Long renewalSeasonTicketSessionId, List<RenewalsUpdateRelatedSeatsRequestItem> blockSeats, List<RenewalsUpdateRelatedSeatsRequestItem> unblockSeats) {
        UpdateRelatedSeatsRequest request = new UpdateRelatedSeatsRequest();
        if (blockSeats != null) {
            request.setBlockSeats(blockSeats.stream().map(this::convert).collect(Collectors.toList()));
        }
        if (unblockSeats != null) {
            request.setUnblockSeats(unblockSeats.stream().map(this::convert).collect(Collectors.toList()));
        }
        return httpClient.buildRequest(HttpMethod.PUT, "/season-tickets/{sessionId}/renewal/seats")
                .pathParams(renewalSeasonTicketSessionId)
                .body(new ClientRequestBody(request))
                .execute(UpdateRelatedSeatsResponse.class);
    }

    private UpdateRelatedSeatsRequestItem convert(RenewalsUpdateRelatedSeatsRequestItem source) {
        if (source == null) {
            return null;
        }
        UpdateRelatedSeatsRequestItem target = new UpdateRelatedSeatsRequestItem();
        target.setUserId(source.getUserId());
        target.setSeasonTicketId(source.getSeasonTicketId());
        target.setRenewalId(source.getRenewalId());
        target.setSeatId(source.getSeatId());
        return target;
    }

    public void updateBarcodes(Long seasonTicketSessionId) {
        httpClient.buildRequest(HttpMethod.POST, SESSION + "/update-barcodes")
                .pathParams(seasonTicketSessionId)
                .execute();
    }

    public SecondaryMarketSearchResponse getSecondaryMarketLocations(SecondaryMarketSearchFilter filter) {
        return httpClient.buildRequest(HttpMethod.POST, "/secondary-market/locations")
                .body(new ClientRequestBody(filter))
                .execute(SecondaryMarketSearchResponse.class);
    }

}