package es.onebox.mgmt.datasources.ms.channel.salerequests;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.core.exception.ErrorCode;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.datasource.http.ClientRequestBody;
import es.onebox.datasource.http.HttpClient;
import es.onebox.datasource.http.QueryParameters;
import es.onebox.datasource.http.configuration.HttpClientFactoryBuilder;
import es.onebox.datasource.http.method.HttpMethod;
import es.onebox.datasource.http.response.ListType;
import es.onebox.mgmt.channels.commissions.dto.CommissionTypeDTO;
import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.common.IdNameListWithLimited;
import es.onebox.mgmt.datasources.common.dto.Surcharge;
import es.onebox.mgmt.datasources.common.enums.SurchargeType;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelCommission;
import es.onebox.mgmt.datasources.ms.channel.dto.SaleRequestAllowRefundResponse;
import es.onebox.mgmt.datasources.ms.channel.dto.taxes.SaleRequestSurchargesTaxes;
import es.onebox.mgmt.datasources.ms.channel.dto.taxes.SaleRequestSurchargesTaxesUpdate;
import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.MsSaleRequestDTO;
import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.MsSaleRequestPromotionsResponseDTO;
import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.MsSaleRequestSurchargesExtendedDTO;
import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.MsSaleRequestsFilter;
import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.MsSaleRequestsResponseDTO;
import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.MsSessionSaleRequestResponseDTO;
import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.MsSubscriptionListSalesRequestDTO;
import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.MsUpdateSaleRequestDTO;
import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.MsUpdateSaleRequestResponseDTO;
import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.SaleRequestAgreement;
import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.SaleRequestCommunicationElementDTO;
import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.SaleRequestDelivery;
import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.UpdateSaleRequestDelivery;
import es.onebox.mgmt.datasources.ms.event.dto.event.TicketCommunicationElement;
import es.onebox.mgmt.exception.ApiMgmtChannelsErrorCode;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.exception.ClientHttpExceptionBuilder;
import es.onebox.mgmt.salerequests.dto.CategoryIdRequestDTO;
import es.onebox.mgmt.salerequests.dto.FiltersSalesRequestExtended;
import es.onebox.mgmt.salerequests.dto.SearchSaleRequestSessionsFilter;
import es.onebox.mgmt.sessions.dto.DayOfWeekDTO;
import es.onebox.mgmt.sessions.enums.SessionField;
import es.onebox.mgmt.sessions.enums.SessionStatus;
import es.onebox.tracer.okhttp.TracingInterceptor;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MsSaleRequestsDatasource {

    private static final int TIMEOUT = 60000;

    private static final String API_VERSION = "v1";
    private static final String BASE_PATH = "/ms-channel-api/" + API_VERSION + "/sale-requests";
    private static final String SALE_REQUEST_DETAIL = "/{saleRequestId}";
    private static final String SALE_REQUEST_SESSIONS = "/{saleRequestId}/sessions";
    private static final String SALE_REQUEST_PROMOTIONS = "/{saleRequestId}/promotions";
    private static final String SALE_REQUEST_FILTERS_URL = "/filters/{filter}";
    private static final String SALE_REQUEST_SURCHARGES = "/{saleRequestId}/surcharges";
    private static final String SALE_REQUEST_COMMISSIONS = "/{saleRequestId}/commissions";
    private static final String SALE_REQUEST_COMM_ELEMENT = "{saleRequestId}/communication-elements";
    private static final String SALE_REQUEST_ALLOW_REFUND = "{saleRequestId}/allow-refund";
    private static final String SALE_REQUEST_DELIVERY = "/{saleRequestId}/delivery";
    private static final String SALE_REQUEST_STATUS = "{saleRequestId}/status";
    private static final String SALE_REQUEST_SUBSCRIPTION_LIST = "{saleRequestId}/subscription-list";
    private static final String SALE_REQUEST_EVENT_CATEGORY = "{saleRequestId}/event-category";
    private static final String TICKET_CONTENTS_PDF_IMAGES =  "/{saleRequestId}/ticket-contents/PDF/images";
    private static final String TICKET_CONTENTS_PRINTER_IMAGES =  "/{saleRequestId}/ticket-contents/PRINTER/images";
    private static final String AGREEMENTS = "/{saleRequestId}/agreements";
    private static final String AGREEMENT = "/{saleRequestId}/agreements/{agreementId}";
    private static final String SURCHARGES_TAXES = "/{saleRequestId}/surcharges-taxes";

    private final HttpClient httpClient;

    private static final Map<String, ErrorCode> ERROR_CODES = new HashMap<>();

    static {
        ERROR_CODES.put("FORBIDDEN", ApiMgmtErrorCode.FORBIDDEN);
        ERROR_CODES.put("BAD_PARAMETER", ApiMgmtErrorCode.BAD_REQUEST_PARAMETER);
        ERROR_CODES.put("SALE_REQUEST_NOT_FOUND", ApiMgmtErrorCode.SALE_REQUEST_NOT_FOUND);
        ERROR_CODES.put("REQUEST_RESOURCE_CONFLICT", ApiMgmtErrorCode.REQUEST_RESOURCE_CONFLICT);
        ERROR_CODES.put("404G0001", ApiMgmtErrorCode.SALE_REQUEST_NOT_FOUND);
        ERROR_CODES.put("AT_LEAST_ONE_RANGE", ApiMgmtErrorCode.AT_LEAST_ONE_RANGE);
        ERROR_CODES.put("SURCHARGE_FROM_RANGE_MANDATORY", ApiMgmtErrorCode.SURCHARGE_FROM_RANGE_MANDATORY);
        ERROR_CODES.put("SURCHARGE_DUPLICATED_FROM_RANGE", ApiMgmtErrorCode.SURCHARGE_FROM_RANGE_DUPLICATED);
        ERROR_CODES.put("FIXED_OR_PERCENTAGE_MANDATORY", ApiMgmtErrorCode.FIXED_OR_PERCENTAGE_MANDATORY);
        ERROR_CODES.put("FROM_RANGE_ZERO_MANDATORY", ApiMgmtErrorCode.SURCHARGE_FROM_RANGE_ZERO_MANDANTORY);
        ERROR_CODES.put("MIN_SURCHARGE_GREATER_THAN_MAX", ApiMgmtErrorCode.MIN_SURCHARGE_GREATER_THAN_MAX);
        ERROR_CODES.put("NEGATIVE_VALUE", ApiMgmtErrorCode.NEGATIVE_VALUE);
        ERROR_CODES.put("SURCHARGE_TYPE_NOT_SUPPORTED", ApiMgmtErrorCode.SURCHARGE_TYPE_NOT_SUPPORTED);
        ERROR_CODES.put("EVENT_CATEGORY_NOT_FOUND", ApiMgmtErrorCode.SALE_REQUESTS_EVENT_CATEGORY_NOT_FOUND);
        ERROR_CODES.put("INVALID_VALUE_FORMAT", ApiMgmtErrorCode.INVALID_PARAM_FORMAT);
        ERROR_CODES.put("CHANNEL_AGREEMENT_INVALID_STATE", ApiMgmtChannelsErrorCode.SALE_REQUEST_AGREEMENT_INVALID_STATE);
        ERROR_CODES.put("CHANNEL_AGREEMENT_NOT_FOUND", ApiMgmtChannelsErrorCode.SALE_REQUEST_AGREEMENT_NOT_FOUND);
        ERROR_CODES.put("INVALID_SALE_REQUEST_SURCHARGE_TAX", ApiMgmtChannelsErrorCode.INVALID_SALE_REQUEST_SURCHARGE_TAX);
        ERROR_CODES.put("INVALID_SALE_REQUEST_SURCHARGE_TAX_ORIGIN", ApiMgmtChannelsErrorCode.INVALID_SALE_REQUEST_SURCHARGE_TAX_ORIGIN);
    }

    @Autowired
    public MsSaleRequestsDatasource(@Value("${clients.services.ms-channel}") String baseUrl,
                                  ObjectMapper jacksonMapper,
                                  TracingInterceptor tracingInterceptor) {

        this.httpClient = HttpClientFactoryBuilder.builder()
                .baseUrl(baseUrl + BASE_PATH)
                .jacksonMapper(jacksonMapper)
                .interceptors(tracingInterceptor)
                .exceptionBuilder(new ClientHttpExceptionBuilder(ERROR_CODES, jacksonMapper))
                .readTimeout(TIMEOUT)
                .build();
    }

    public MsSaleRequestsResponseDTO searchSaleRequests(MsSaleRequestsFilter filter) {
        QueryParameters params = new QueryParameters.Builder().
                addQueryParameters(filter).
                build();

        return httpClient.buildRequest(HttpMethod.GET, StringUtils.EMPTY)
                .params(params)
                .execute(MsSaleRequestsResponseDTO.class);
    }

    public MsSaleRequestDTO getSaleRequestDetail(Long saleRequestId) {
        return  httpClient.buildRequest(HttpMethod.GET, SALE_REQUEST_DETAIL)
                .pathParams(saleRequestId)
                .execute(MsSaleRequestDTO.class);
    }

    public MsSessionSaleRequestResponseDTO getSessions(long operatorId, Long saleRequestId, SearchSaleRequestSessionsFilter filter) {
        QueryParameters.Builder params = fillGetSessionsFilter(operatorId, filter);

        return httpClient.buildRequest(HttpMethod.GET, SALE_REQUEST_SESSIONS)
                .pathParams(saleRequestId)
                .params(params.build())
                .execute(MsSessionSaleRequestResponseDTO.class);
    }

    public MsSaleRequestPromotionsResponseDTO getSaleRequestPromotions(Long saleRequestId){
        return httpClient.buildRequest(HttpMethod.GET, SALE_REQUEST_PROMOTIONS)
                .pathParams(saleRequestId)
                .execute(MsSaleRequestPromotionsResponseDTO.class);
    }

    public IdNameListWithLimited filtersSaleRequests(String filterType, FiltersSalesRequestExtended filter) {
        return httpClient.buildRequest(HttpMethod.GET, SALE_REQUEST_FILTERS_URL)
                .pathParams(filterType)
                .params(new QueryParameters.Builder().addQueryParameters(filter).build())
                .execute(IdNameListWithLimited.class);
    }

    public MsSaleRequestSurchargesExtendedDTO saleRequestSurcharges(Long saleRequestId, List<SurchargeType> type) {
        return httpClient.buildRequest(HttpMethod.GET, SALE_REQUEST_SURCHARGES)
                .pathParams(saleRequestId)
                .params(new QueryParameters.Builder().addQueryParameter("type", getParamType(type)).build())
                .execute(MsSaleRequestSurchargesExtendedDTO.class);
    }

    public void updateSaleRequestSurcharges(Long saleRequestId, List<Surcharge> surcharges) {
        httpClient.buildRequest(HttpMethod.POST, SALE_REQUEST_SURCHARGES)
                .pathParams(saleRequestId)
                .body(new ClientRequestBody(surcharges))
                .execute();
    }

    public List<ChannelCommission> getSaleRequestCommissions(Long saleRequestId, List<CommissionTypeDTO> types) {
        String type = null;
        if (CollectionUtils.isNotEmpty(types)) {
            type = types.stream().map(CommissionTypeDTO::name).collect(Collectors.joining(","));
        }

        QueryParameters params = new QueryParameters.Builder().
                addQueryParameter("type", type).
                build();

        return httpClient.buildRequest(HttpMethod.GET, SALE_REQUEST_COMMISSIONS)
                .pathParams(saleRequestId)
                .params(params)
                .execute(ListType.of(ChannelCommission.class));
    }

    public void updateSaleRequestCommissions(Long saleRequestId, List<ChannelCommission> commissionListDto) {
        httpClient.buildRequest(HttpMethod.POST, SALE_REQUEST_COMMISSIONS)
                .pathParams(saleRequestId)
                .body(new ClientRequestBody(commissionListDto))
                .execute();
    }

    public SaleRequestCommunicationElementDTO getCommunicationElements(Long saleRequestId) {
        return httpClient.buildRequest(HttpMethod.GET, SALE_REQUEST_COMM_ELEMENT)
                .pathParams(saleRequestId)
                .execute(SaleRequestCommunicationElementDTO.class);
    }

    public void updateCommunicationElementsBySaleRequest(Long saleRequestId, SaleRequestCommunicationElementDTO comPurchaseElement) {
        httpClient.buildRequest(HttpMethod.PUT, SALE_REQUEST_COMM_ELEMENT)
                .pathParams(saleRequestId)
                .body(new ClientRequestBody(comPurchaseElement))
                .execute();
    }

    public void deleteCommunicationElementsBySaleRequest(Long saleRequestId, SaleRequestCommunicationElementDTO comPurchaseElement) {
        httpClient.buildRequest(HttpMethod.DELETE, SALE_REQUEST_COMM_ELEMENT)
                .pathParams(saleRequestId)
                .body(new ClientRequestBody(comPurchaseElement))
                .execute();
    }

    public SaleRequestAllowRefundResponse getAllowRefund(Long saleRequestId) {
        return httpClient.buildRequest(HttpMethod.GET, SALE_REQUEST_ALLOW_REFUND)
                .pathParams(saleRequestId)
                .pathParams(saleRequestId)
                .execute(SaleRequestAllowRefundResponse.class);
    }

    public void updateAllowRefund(Long saleRequestId, SaleRequestAllowRefundResponse request) {
        httpClient.buildRequest(HttpMethod.POST, SALE_REQUEST_ALLOW_REFUND)
                .pathParams(saleRequestId)
                .body(new ClientRequestBody(request))
                .execute();
    }

    public MsUpdateSaleRequestResponseDTO updateSaleRequestStatus(Long saleRequestId, MsUpdateSaleRequestDTO msUpdateSaleRequestDTO){
        return httpClient.buildRequest(HttpMethod.PUT, SALE_REQUEST_STATUS)
                .pathParams(saleRequestId)
                .body(new ClientRequestBody(msUpdateSaleRequestDTO))
                .execute(MsUpdateSaleRequestResponseDTO.class);
    }

    public void updateSaleRequestSubscriptionList(Long saleRequestId, MsSubscriptionListSalesRequestDTO msSubscriptionListSalesRequestDTO){
        httpClient.buildRequest(HttpMethod.PUT, SALE_REQUEST_SUBSCRIPTION_LIST)
                .pathParams(saleRequestId)
                .body(new ClientRequestBody(msSubscriptionListSalesRequestDTO))
                .execute();
    }

    private String getParamType(List<SurchargeType> type) {
        if (CollectionUtils.isNotEmpty(type)) {
            return type.stream().map(SurchargeType::name).collect(Collectors.joining(","));
        }
        return null;
    }

    public void updateEventCategorySaleRequest(Long saleRequestId, CategoryIdRequestDTO categoryId) {
        httpClient.buildRequest(HttpMethod.PUT, SALE_REQUEST_EVENT_CATEGORY)
                .pathParams(saleRequestId)
                .body(new ClientRequestBody(categoryId, ClientRequestBody.Type.JSON))
                .execute();
    }

    public List<TicketCommunicationElement> getSaleRequestTicketPdfImages(Long saleRequestId, String language) {
        return httpClient.buildRequest(HttpMethod.GET, TICKET_CONTENTS_PDF_IMAGES)
                .pathParams(saleRequestId)
                .params(new QueryParameters.Builder()
                        .addQueryParameter("language", language)
                        .build())
                .execute(ListType.of(TicketCommunicationElement.class));
    }

    public void updateSaleRequestTicketPdfImages(Long saleRequestId, List<TicketCommunicationElement> comTicketElements) {
        httpClient.buildRequest(HttpMethod.PUT, TICKET_CONTENTS_PDF_IMAGES)
                .pathParams(saleRequestId)
                .body(new ClientRequestBody(comTicketElements))
                .execute();
    }

    public void deleteSaleRequestTicketPdfImages(Long saleRequestId, TicketCommunicationElement comTicketElement) {
        httpClient.buildRequest(HttpMethod.DELETE, TICKET_CONTENTS_PDF_IMAGES)
                .pathParams(saleRequestId)
                .body(new ClientRequestBody(comTicketElement))
                .execute();
    }

    public List<TicketCommunicationElement> getSaleRequestTicketPrinterImages(Long saleRequestId, String language) {
        return httpClient.buildRequest(HttpMethod.GET, TICKET_CONTENTS_PRINTER_IMAGES)
                .pathParams(saleRequestId)
                .params(new QueryParameters.Builder()
                        .addQueryParameter("language", language)
                        .build())
                .execute(ListType.of(TicketCommunicationElement.class));
    }

    public void updateSaleRequestTicketPrinterImages(Long saleRequestId, List<TicketCommunicationElement> comTicketElements) {
        httpClient.buildRequest(HttpMethod.PUT, TICKET_CONTENTS_PRINTER_IMAGES)
                .pathParams(saleRequestId)
                .body(new ClientRequestBody(comTicketElements))
                .execute();
    }

    public void deleteSaleRequestTicketPrinterImages(Long saleRequestId, TicketCommunicationElement comTicketElement) {
        httpClient.buildRequest(HttpMethod.DELETE, TICKET_CONTENTS_PRINTER_IMAGES)
                .pathParams(saleRequestId)
                .body(new ClientRequestBody(comTicketElement))
                .execute();
    }

    public SaleRequestDelivery getDelivery(Long saleRequestId) {
        return httpClient.buildRequest(HttpMethod.GET, SALE_REQUEST_DELIVERY)
                .pathParams(saleRequestId)
                .execute(SaleRequestDelivery.class);
    }

    public void updateDelivery(Long saleRequestId, UpdateSaleRequestDelivery request) {
        httpClient.buildRequest(HttpMethod.PUT, SALE_REQUEST_DELIVERY)
                .pathParams(saleRequestId)
                .body(new ClientRequestBody(request))
                .execute();
    }

    public List<SaleRequestAgreement> getAgreements(Long channelId) {
        return httpClient.buildRequest(HttpMethod.GET, AGREEMENTS).pathParams(channelId)
                .execute(ListType.of(SaleRequestAgreement.class));
    }

    public IdDTO createAgreement(Long channelId, SaleRequestAgreement body) {
        return httpClient.buildRequest(HttpMethod.POST, AGREEMENTS).pathParams(channelId).body(new ClientRequestBody(body))
                .execute(IdDTO.class);
    }

    public void updateAgreement(Long channelId, Long channelAgreementId, SaleRequestAgreement body) {
        httpClient.buildRequest(HttpMethod.PUT, AGREEMENT).pathParams(channelId, channelAgreementId).body(new ClientRequestBody(body))
                .execute();
    }

    public void deleteAgreement(Long channelId, Long agreementId) {
        httpClient.buildRequest(HttpMethod.DELETE, AGREEMENT).pathParams(channelId, agreementId).execute();
    }

    private QueryParameters.Builder fillGetSessionsFilter(long operatorId, SearchSaleRequestSessionsFilter filter) {
        QueryParameters.Builder params = new QueryParameters.Builder();

        fillOperatorAndEntity(params, operatorId, filter.getEntityId());

        if(CollectionUtils.isNotEmpty(filter.getStartDate())) {
            filter.getStartDate().forEach(op-> params.addQueryParameter("startDate", op));
        }

        if(filter.getDaysOfWeek() != null) {
            params.addQueryParameter("daysOfWeek", filter.getDaysOfWeek().stream()
                    .map(DayOfWeekDTO::name).collect(Collectors.joining(",")));
        }
        if(filter.getOlsonId() != null) {
            params.addQueryParameter("olsonId", filter.getOlsonId());
        }
        ConverterUtils.addFreeSearch(filter.getFreeSearch(), params);
        addLimitAndOffset(filter.getLimit(), filter.getOffset(), params);
        ConverterUtils.checkFilterFields(filter.getFields(), params, SessionField::byName);

        if(CollectionUtils.isNotEmpty(filter.getStatus())){
            params.addQueryParameter("status", filter.getStatus().stream()
                    .map(SessionStatus::name).collect(Collectors.joining(",")));
        }
        if(filter.getPublished()) {
            params.addQueryParameter("published", true);
        }

        ConverterUtils.checkSortFields(filter.getSort(), params, SessionField::byName);

        return params;
    }

    private void fillOperatorAndEntity(QueryParameters.Builder params, Long operatorId, Long entityId) {
        if (operatorId == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, "Operator is mandatory field", null);
        }
        params.addQueryParameter("operatorId", operatorId)
                .addQueryParameter("entityId", entityId);
    }

    private void addLimitAndOffset(Long limit, Long offset, QueryParameters.Builder params) {
        params.addQueryParameter("limit", limit)
                .addQueryParameter("offset", offset);
    }

    public SaleRequestSurchargesTaxes getSaleRequestSurchargesTaxes(Long saleRequestId) {
        return httpClient.buildRequest(HttpMethod.GET, SURCHARGES_TAXES)
                .pathParams(saleRequestId)
                .execute(SaleRequestSurchargesTaxes.class);
    }

    public void updateSaleRequestSurchargesTaxes(Long saleRequestId, SaleRequestSurchargesTaxesUpdate update) {
        httpClient.buildRequest(HttpMethod.PUT, SURCHARGES_TAXES)
                .pathParams(saleRequestId)
                .body(new ClientRequestBody(update))
                .execute();
    }
}
