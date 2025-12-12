package es.onebox.common.datasources.ms.ticket;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.common.datasources.DatasourceUtils;
import es.onebox.common.datasources.common.dto.SeatStatus;
import es.onebox.common.datasources.ms.ticket.dto.ExternalMode;
import es.onebox.common.datasources.ms.ticket.dto.OrderItemPrint;
import es.onebox.common.datasources.ms.ticket.dto.PdfTicketDetails;
import es.onebox.common.datasources.ms.ticket.dto.SessionTicketSearchFilter;
import es.onebox.common.datasources.ms.ticket.dto.SessionTicketSearchResponse;
import es.onebox.common.datasources.ms.ticket.dto.TicketStatus;
import es.onebox.common.datasources.ms.ticket.dto.TicketsUpdateRequestDTO;
import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.common.exception.ClientHttpExceptionBuilder;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.datasource.http.ClientRequestBody;
import es.onebox.datasource.http.HttpClient;
import es.onebox.datasource.http.QueryParameters;
import es.onebox.datasource.http.configuration.HttpClientFactoryBuilder;
import es.onebox.datasource.http.method.HttpMethod;
import es.onebox.venue.venuetemplates.VenueMapProto;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component()
public class MsTicketDatasource {
    private static final String API_VERSION = "v1";
    private static final String BASE_PATH = "/tickets-api/" + API_VERSION;

    private static final String SESSIONS = "/sessions";
    private static final String EVENTS = "/events";
    private static final String CAPACITY = "/capacity";
    private static final String TICKETS = "/tickets";
    private static final String TICKET_GENERATION = "/ticket-generation/PDF";
    private static final String ORDER = "/order/{orderCode}";

    public static final String SESSION = SESSIONS + "/{sessionId}";
    public static final String ITEM_PASSBOOK = "/passbook" + ORDER + "/{itemId}";

    private static final Map<String, ApiExternalErrorCode> ERROR_CODES = new HashMap<>();

    static {
        ERROR_CODES.put("ORDER_NOT_FOUND", ApiExternalErrorCode.ORDER_NOT_FOUND);
        ERROR_CODES.put("S3_FILE_NOT_FOUND", ApiExternalErrorCode.S3_FILE_NOT_FOUND);
        ERROR_CODES.put("ORDER_TOTALLY_REFUNDED", ApiExternalErrorCode.ORDER_TOTALLY_REFUNDED);
        ERROR_CODES.put("ORDER_NOT_PAID", ApiExternalErrorCode.ORDER_NOT_PAID);
        ERROR_CODES.put("TICKET_PDF_URL_NOT_GENERATED", ApiExternalErrorCode.TICKET_PDF_URL_NOT_GENERATED);
        ERROR_CODES.put("EXTERNAL_WALLET_ERROR", ApiExternalErrorCode.EXTERNAL_WALLET_ERROR);
        ERROR_CODES.put("EXTERNAL_WALLET_LAYOUT_NOT_FOUND", ApiExternalErrorCode.EXTERNAL_WALLET_ERROR);
    }

    private final CloseableHttpClient basicHttpClient;
    private final String baseUrl;

    private final HttpClient httpClient;


    @Autowired
    public MsTicketDatasource(@Value("${clients.services.ms-ticket}") String baseUrl,
                              ObjectMapper jacksonMapper) {
        this.basicHttpClient = HttpClients.createDefault();
        this.baseUrl = baseUrl + BASE_PATH;
        this.httpClient = HttpClientFactoryBuilder.builder()
                .baseUrl(baseUrl + BASE_PATH)
                .jacksonMapper(jacksonMapper)
                .exceptionBuilder(new ClientHttpExceptionBuilder(ERROR_CODES, jacksonMapper))
                .build();
    }

    public VenueMapProto.VenueMap getCapacityMap(Long eventId, Long sessionId, List<Long> sectorIds) {

        String url = baseUrl + EVENTS + "/" + eventId + SESSIONS + "/" + sessionId + CAPACITY;

        try {
            RequestBuilder requestBuilder = RequestBuilder.get()
                    .setUri(url)
                    .setHeader(HttpHeaders.ACCEPT, "application/x-protobuf");

            DatasourceUtils.addParameters(requestBuilder, "sectorIds", sectorIds);
            CloseableHttpResponse response = basicHttpClient.execute(requestBuilder.build());
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                return VenueMapProto.VenueMap.parseFrom(response.getEntity().getContent());
            } else {
                throw new OneboxRestException(ApiExternalErrorCode.GENERIC_ERROR,
                        "Error querying capacity map. HTTP status "
                                + response.getStatusLine().getStatusCode()
                                + ", " + response.getStatusLine().getReasonPhrase(), null);
            }
        } catch (IOException e) {
            throw new OneboxRestException(ApiExternalErrorCode.GENERIC_ERROR, "Request problem session capacity with id: " + sessionId, e);
        }
    }

    public void updateTicket(Long sessionId, Long id, SeatStatus status, Long blockingReasonId) {
        TicketsUpdateRequestDTO ticketsUpdateRequestDTO = new TicketsUpdateRequestDTO();
        ticketsUpdateRequestDTO.setSessionId(sessionId);
        if (status.equals(SeatStatus.PROMOTOR_LOCKED)) {
            ticketsUpdateRequestDTO.setStatus(TicketStatus.BLOCKED_PROMOTER);
            ticketsUpdateRequestDTO.setBlockingReasonId(blockingReasonId);
        } else if (status.equals(SeatStatus.FREE)) {
            ticketsUpdateRequestDTO.setStatus(TicketStatus.AVAILABLE);
            ticketsUpdateRequestDTO.setBlockingReasonId(null);
        }

        QueryParameters.Builder params = new QueryParameters.Builder();
        params.addQueryParameter("sessionId", sessionId);
        params.addQueryParameter("id", id);

        httpClient.buildRequest(HttpMethod.PUT, TICKETS)
                .params(params.build())
                .body(new ClientRequestBody(ticketsUpdateRequestDTO))
                .execute();
    }

    public SessionTicketSearchResponse getTicketsBySession(Long sessionId, SessionTicketSearchFilter filter) {
        QueryParameters.Builder parameters = new QueryParameters.Builder();
        parameters.addQueryParameters(filter);

        return httpClient.buildRequest(HttpMethod.GET, SESSION + TICKETS)
                .pathParams(sessionId)
                .params(parameters.build())
                .execute(SessionTicketSearchResponse.class);
    }

    public PdfTicketDetails getOrderMergedTickets(String orderCode) {
        return httpClient.buildRequest(HttpMethod.GET, TICKET_GENERATION + ORDER)
                .pathParams(orderCode)
                .execute(PdfTicketDetails.class);
    }

    public OrderItemPrint getItemPassbook(String orderCode, Long itemId, ExternalMode externalMode) {
        QueryParameters.Builder params = new QueryParameters.Builder();
        params.addQueryParameter("externalMode", externalMode);
        return httpClient.buildRequest(HttpMethod.GET, ITEM_PASSBOOK)
                .pathParams(orderCode, itemId)
                .params(params.build())
                .execute(OrderItemPrint.class);
    }

}
