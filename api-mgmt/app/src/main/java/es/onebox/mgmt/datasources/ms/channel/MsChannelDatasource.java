package es.onebox.mgmt.datasources.ms.channel;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.core.file.exporter.status.model.ExportProcess;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.core.serializer.dto.request.SortOperator;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.datasource.http.ClientRequestBody;
import es.onebox.datasource.http.HttpClient;
import es.onebox.datasource.http.QueryParameters;
import es.onebox.datasource.http.configuration.HttpClientFactoryBuilder;
import es.onebox.datasource.http.method.HttpMethod;
import es.onebox.datasource.http.request.HttpRequestBuilder;
import es.onebox.datasource.http.response.ListType;
import es.onebox.mgmt.channels.contents.enums.ChannelVersion;
import es.onebox.mgmt.channels.externaltools.dto.ChannelExternalToolsNamesDTO;
import es.onebox.mgmt.channels.suggestions.dto.CreateSuggestionTargetRequestDTO;
import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.common.surcharges.dto.SurchargeTypeDTO;
import es.onebox.mgmt.customdomains.common.dto.DomainSettings;
import es.onebox.mgmt.datasources.common.dto.AuthConfig;
import es.onebox.mgmt.datasources.common.dto.BaseCommunicationElement;
import es.onebox.mgmt.datasources.common.dto.CommunicationElementFilter;
import es.onebox.mgmt.datasources.common.dto.Surcharge;
import es.onebox.mgmt.datasources.ms.channel.dto.AdminChannelsResponse;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelAcceptRequest;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelConfig;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelCorsSettings;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelEventSaleRestrictionResponse;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelEventsSaleRestrictions;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelFilter;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelLoyaltyPoints;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelResponse;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelUpdateRequest;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelVouchers;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelsResponse;
import es.onebox.mgmt.datasources.ms.channel.dto.CreateChannel;
import es.onebox.mgmt.datasources.ms.channel.dto.PackItemPriceTypesRequest;
import es.onebox.mgmt.datasources.ms.channel.dto.PackItemPriceTypesResponse;
import es.onebox.mgmt.datasources.ms.channel.dto.ProductSaleRequestApproval;
import es.onebox.mgmt.datasources.ms.channel.dto.SaleRequestChannelCandidatesResponse;
import es.onebox.mgmt.datasources.ms.channel.dto.UpdateFavoriteChannel;
import es.onebox.mgmt.datasources.ms.channel.dto.authvendor.ChannelAuthVendor;
import es.onebox.mgmt.datasources.ms.channel.dto.blacklists.ChannelBlacklist;
import es.onebox.mgmt.datasources.ms.channel.dto.blacklists.ChannelBlacklistFilter;
import es.onebox.mgmt.datasources.ms.channel.dto.blacklists.ChannelBlacklistStatus;
import es.onebox.mgmt.datasources.ms.channel.dto.blacklists.ChannelBlacklistType;
import es.onebox.mgmt.datasources.ms.channel.dto.blacklists.ChannelBlacklistsResponse;
import es.onebox.mgmt.datasources.ms.channel.dto.catalog.ChannelEvent;
import es.onebox.mgmt.datasources.ms.channel.dto.catalog.ChannelEventMsFilter;
import es.onebox.mgmt.datasources.ms.channel.dto.catalog.ChannelEventUpdate;
import es.onebox.mgmt.datasources.ms.channel.dto.catalog.ChannelEvents;
import es.onebox.mgmt.datasources.ms.channel.dto.catalog.ChannelEventsUpdate;
import es.onebox.mgmt.datasources.ms.channel.dto.catalog.ChannelSessions;
import es.onebox.mgmt.datasources.ms.channel.dto.catalog.ChannelSessionsMsFilter;
import es.onebox.mgmt.datasources.ms.channel.dto.contents.ChannelAgreement;
import es.onebox.mgmt.datasources.ms.channel.dto.contents.ChannelAuditedTextBlock;
import es.onebox.mgmt.datasources.ms.channel.dto.contents.ChannelContentClone;
import es.onebox.mgmt.datasources.ms.channel.dto.contents.ChannelFormsResponse;
import es.onebox.mgmt.datasources.ms.channel.dto.contents.ChannelLiterals;
import es.onebox.mgmt.datasources.ms.channel.dto.contents.ChannelTextBlock;
import es.onebox.mgmt.datasources.ms.channel.dto.contents.ChannelTextBlockFilter;
import es.onebox.mgmt.datasources.ms.channel.dto.contents.UpdateChannelProfiledTextBlock;
import es.onebox.mgmt.datasources.ms.channel.dto.contents.UpdateChannelTextBlocks;
import es.onebox.mgmt.datasources.ms.channel.dto.contents.UpdateDefaultChannelForms;
import es.onebox.mgmt.datasources.ms.channel.dto.customresources.CustomResourcesMsDTO;
import es.onebox.mgmt.datasources.ms.channel.dto.customresources.UpdateCustomResourcesMsDTO;
import es.onebox.mgmt.datasources.ms.channel.dto.customresources.assets.CreateCustomResourceAssetsMsDTO;
import es.onebox.mgmt.datasources.ms.channel.dto.customresources.assets.CustomResourceAssetsFilterMs;
import es.onebox.mgmt.datasources.ms.channel.dto.customresources.assets.CustomResourceAssetsMsDTO;
import es.onebox.mgmt.datasources.ms.channel.dto.deliverymethod.ChannelDeliveryMethods;
import es.onebox.mgmt.datasources.ms.channel.dto.domainconfig.DomainConfig;
import es.onebox.mgmt.datasources.ms.channel.dto.emailcontents.ChannelPurchaseContent;
import es.onebox.mgmt.datasources.ms.channel.dto.externaltools.ChannelExternalTool;
import es.onebox.mgmt.datasources.ms.channel.dto.externaltools.ChannelExternalTools;
import es.onebox.mgmt.datasources.ms.channel.dto.faqs.ChannelFAQ;
import es.onebox.mgmt.datasources.ms.channel.dto.faqs.ChannelFAQUpsertRequest;
import es.onebox.mgmt.datasources.ms.channel.dto.faqs.ChannelFAQs;
import es.onebox.mgmt.datasources.ms.channel.dto.notifications.ChannelEmailServer;
import es.onebox.mgmt.datasources.ms.channel.dto.notifications.ChannelEmailTemplates;
import es.onebox.mgmt.datasources.ms.channel.dto.reviews.ChannelReview;
import es.onebox.mgmt.datasources.ms.channel.dto.reviews.ChannelReviewConfigResponse;
import es.onebox.mgmt.datasources.ms.channel.dto.reviews.ChannelReviewConfigUpdate;
import es.onebox.mgmt.datasources.ms.channel.dto.reviews.ChannelReviewConfigUpdateBulk;
import es.onebox.mgmt.datasources.ms.channel.dto.reviews.ChannelReviewScope;
import es.onebox.mgmt.datasources.ms.channel.dto.reviews.ChannelReviewUpdate;
import es.onebox.mgmt.datasources.ms.channel.dto.reviews.ReviewsConfigFilter;
import es.onebox.mgmt.datasources.ms.channel.dto.suggestions.ChannelSuggestionMsFilter;
import es.onebox.mgmt.datasources.ms.channel.dto.suggestions.ChannelSuggestions;
import es.onebox.mgmt.datasources.ms.channel.dto.taxes.ChannelSurchargesTaxes;
import es.onebox.mgmt.datasources.ms.channel.dto.taxes.ChannelSurchargesTaxesUpdate;
import es.onebox.mgmt.datasources.ms.channel.dto.ticketcontent.ChannelTicketContent;
import es.onebox.mgmt.datasources.ms.channel.dto.voucher.CreateVoucher;
import es.onebox.mgmt.datasources.ms.channel.dto.voucher.CreateVoucherGroup;
import es.onebox.mgmt.datasources.ms.channel.dto.voucher.ResetVoucherRequest;
import es.onebox.mgmt.datasources.ms.channel.dto.voucher.SendEmailVoucher;
import es.onebox.mgmt.datasources.ms.channel.dto.voucher.UpdateVoucherBalance;
import es.onebox.mgmt.datasources.ms.channel.dto.voucher.UpdateVouchersBulk;
import es.onebox.mgmt.datasources.ms.channel.dto.voucher.Voucher;
import es.onebox.mgmt.datasources.ms.channel.dto.voucher.VoucherFilter;
import es.onebox.mgmt.datasources.ms.channel.dto.voucher.VoucherGroup;
import es.onebox.mgmt.datasources.ms.channel.dto.voucher.VoucherGroupFilter;
import es.onebox.mgmt.datasources.ms.channel.dto.voucher.VoucherGroupGiftCard;
import es.onebox.mgmt.datasources.ms.channel.dto.voucher.VoucherGroups;
import es.onebox.mgmt.datasources.ms.channel.dto.voucher.Vouchers;
import es.onebox.mgmt.datasources.ms.channel.dto.whatsapptemplates.WhatsappTemplates;
import es.onebox.mgmt.datasources.ms.channel.dto.whitelabelsettings.ChannelWhitelabelSettings;
import es.onebox.mgmt.datasources.ms.channel.enums.SuggestionType;
import es.onebox.mgmt.datasources.ms.channel.enums.VoucherGroupField;
import es.onebox.mgmt.datasources.ms.client.dto.PhoneValidatorChannelConfig;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventSaleRequestChannelFilter;
import es.onebox.mgmt.datasources.ms.event.dto.packs.CreatePack;
import es.onebox.mgmt.datasources.ms.event.dto.packs.CreatePackItems;
import es.onebox.mgmt.datasources.ms.event.dto.packs.CreatePackRate;
import es.onebox.mgmt.datasources.ms.event.dto.packs.Pack;
import es.onebox.mgmt.datasources.ms.event.dto.packs.PackDetail;
import es.onebox.mgmt.datasources.ms.event.dto.packs.PackItem;
import es.onebox.mgmt.datasources.ms.event.dto.packs.PackPrice;
import es.onebox.mgmt.datasources.ms.event.dto.packs.PackRate;
import es.onebox.mgmt.datasources.ms.event.dto.packs.UpdatePack;
import es.onebox.mgmt.datasources.ms.event.dto.packs.UpdatePackItem;
import es.onebox.mgmt.datasources.ms.event.dto.packs.UpdatePackPrice;
import es.onebox.mgmt.datasources.ms.event.dto.packs.UpdatePackRate;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductSaleRequestDetail;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductSaleRequests;
import es.onebox.mgmt.datasources.ms.event.dto.products.SearchProductSaleRequestFilter;
import es.onebox.mgmt.datasources.ms.event.dto.products.UpdateProductSaleRequest;
import es.onebox.mgmt.events.enums.TicketCommunicationElementCategory;
import es.onebox.mgmt.exception.ClientHttpExceptionBuilder;
import es.onebox.mgmt.export.dto.ExportFilter;
import es.onebox.mgmt.packs.dto.comelements.PackCommunicationElement;
import es.onebox.mgmt.packs.enums.PackTagType;
import es.onebox.mgmt.vouchers.dto.VoucherExportFileField;
import es.onebox.tracer.okhttp.TracingInterceptor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MsChannelDatasource extends MsChannelMapping {

    private final HttpClient httpClient;

    @Autowired
    public MsChannelDatasource(@Value("${clients.services.ms-channel}") String baseUrl,
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

    public ChannelsResponse getChannels(Long userOperatorId, ChannelFilter filter) {
        QueryParameters params = new QueryParameters.Builder().
                addQueryParameter("operatorId", userOperatorId).
                addQueryParameters(filter).
                build();
        return httpClient.buildRequest(HttpMethod.GET, CHANNELS)
                .params(params)
                .execute(ChannelsResponse.class);
    }

    public List<Surcharge> getChannelRanges(Long channelId, List<SurchargeTypeDTO> types, List<Integer> currencyIds) {
        QueryParameters.Builder params = new QueryParameters.Builder();
        if (!CommonUtils.isEmpty(types)) {
            for (SurchargeTypeDTO surchargeType : types) {
                params.addQueryParameter("type", surchargeType.toString());
            }
        }
        if (!CommonUtils.isEmpty(currencyIds)) {
            currencyIds.forEach(id -> params.addQueryParameter("currencyId", id));
        }
        return httpClient.buildRequest(HttpMethod.GET, SURCHARGES).pathParams(channelId)
                .params(params.build())
                .execute(ListType.of(Surcharge.class));
    }

    public void setSurcharge(long channelId, List<Surcharge> msChannelSurchargeRequestDTO) {
        httpClient.buildRequest(HttpMethod.POST, SURCHARGES)
                .pathParams(channelId)
                .body(new ClientRequestBody(msChannelSurchargeRequestDTO))
                .execute();
    }

    public IdDTO create(CreateChannel createChannel) {
        return httpClient.buildRequest(HttpMethod.POST, CHANNELS)
                .body(new ClientRequestBody(createChannel))
                .execute(IdDTO.class);
    }

    public ChannelResponse getChannel(Long channelId, Boolean includeDeleted) {
        HttpRequestBuilder request = httpClient.buildRequest(HttpMethod.GET, CHANNEL).pathParams(channelId);
        if (includeDeleted != null) {
            request.params(new QueryParameters.Builder().
                    addQueryParameter("includeDeleted", includeDeleted)
                    .build());
        }
        return request.execute(ChannelResponse.class);
    }

    public void deleteChannel(Long channelId) {
        httpClient.buildRequest(HttpMethod.DELETE, CHANNEL)
                .pathParams(channelId)
                .execute();
    }

    public void updateChannel(long channelId, ChannelUpdateRequest msChannelUpdateRequestDTO) {
        httpClient.buildRequest(HttpMethod.PUT, CHANNEL)
                .pathParams(channelId)
                .body(new ClientRequestBody(msChannelUpdateRequestDTO))
                .execute();
    }

    public ChannelDeliveryMethods getChannelDeliveryMethods(Long channelId) {
        return httpClient.buildRequest(HttpMethod.GET, DELIVERY_METHODS)
                .pathParams(channelId)
                .execute(ChannelDeliveryMethods.class);
    }

    public void updateChannelDeliveryMethods(Long channelId, ChannelDeliveryMethods requestDTO) {
        httpClient.buildRequest(HttpMethod.PUT, DELIVERY_METHODS)
                .pathParams(channelId)
                .body(new ClientRequestBody(requestDTO))
                .execute();
    }

    public void requestChannelEventApproval(Long eventId, Long channelId) {
        httpClient.buildRequest(HttpMethod.POST, CHANNEL_EVENTS + "/{eventId}")
                .pathParams(channelId, eventId)
                .execute();
    }

    public void requestChannelProductApproval(Long productId, Long channelId, Long userId) {
        ProductSaleRequestApproval productSaleRequestApproval = new ProductSaleRequestApproval();
        productSaleRequestApproval.setUserId(userId);
        httpClient.buildRequest(HttpMethod.POST, CHANNEL_PRODUCTS + "/{productId}")
                .pathParams(channelId, productId)
                .body(new ClientRequestBody(productSaleRequestApproval))
                .execute();
    }

    public ProductSaleRequestDetail getSaleRequestDetail(Long saleRequestId) {
        return httpClient.buildRequest(HttpMethod.GET, PRODUCT_SALE_REQUEST + SALE_REQUEST_ID)
                .pathParams(saleRequestId)
                .execute(ProductSaleRequestDetail.class);
    }

    public void deleteSaleRequest(Long saleRequestId) {
        httpClient.buildRequest(HttpMethod.DELETE, PRODUCT_SALE_REQUEST + "/{saleRequestId}")
                .pathParams(saleRequestId)
                .execute();
    }

    public void deleteProductSaleRequestByProductAndChannel(Long productId, Long channelId) {
        httpClient.buildRequest(HttpMethod.DELETE, CHANNEL_PRODUCTS + "/{productId}")
                .pathParams(productId, channelId)
                .execute();
    }


    public void updateSaleRequest(Long saleRequestId, UpdateProductSaleRequest updateProductSaleRequest) {
        httpClient.buildRequest(HttpMethod.PUT, PRODUCT_SALE_REQUEST + SALE_REQUEST_ID)
                .pathParams(saleRequestId)
                .body(new ClientRequestBody(updateProductSaleRequest))
                .execute();
    }

    public ProductSaleRequests searchProductSaleRequests(SearchProductSaleRequestFilter filter) {
        QueryParameters.Builder builder = new QueryParameters.Builder();
        builder.addQueryParameters(filter);
        return httpClient.buildRequest(HttpMethod.GET, PRODUCT_SALE_REQUEST)
                .params(builder.build())
                .execute(ProductSaleRequests.class);
    }

    public void acceptEventRequest(Long eventId, Long channelId, ChannelAcceptRequest acceptRequestDTO) {
        httpClient.buildRequest(HttpMethod.POST, CHANNEL_EVENTS + "/{eventId}/accept-request")
                .pathParams(channelId, eventId)
                .body(new ClientRequestBody(acceptRequestDTO))
                .execute();
    }

    public void updateChannelConfig(Long channelId, ChannelConfig channelConfig) {
        httpClient.buildRequest(HttpMethod.PUT, CONFIG)
                .pathParams(channelId)
                .body(new ClientRequestBody(channelConfig))
                .execute();
    }

    public ChannelConfig getChannelConfig(Long channelId) {
        return httpClient.buildRequest(HttpMethod.GET, CONFIG)
                .pathParams(channelId)
                .execute(ChannelConfig.class);
    }

    public ChannelFormsResponse getFormsByType(Long channelId, String formType) {
        return httpClient.buildRequest(HttpMethod.GET, FORMS)
                .pathParams(channelId, formType)
                .execute(ChannelFormsResponse.class);
    }

    public void updateFormsByType(Long channelId, String formType, UpdateDefaultChannelForms body) {
        httpClient.buildRequest(HttpMethod.PUT, FORMS)
                .pathParams(channelId, formType)
                .body(new ClientRequestBody(body))
                .execute();
    }

    public List<IdNameDTO> getEntityFavoriteChannel(Long entityId) {
        return httpClient.buildRequest(HttpMethod.GET, ENTITY + CHANNELS + FAVOURITE_CHANNELS)
                .pathParams(entityId)
                .execute(ListType.of(IdNameDTO.class));
    }

    public void updateEntityFavoriteChannel(Long entityId, Long channelId, UpdateFavoriteChannel body) {
        httpClient.buildRequest(HttpMethod.PUT, ENTITY + CHANNEL)
                .pathParams(entityId, channelId)
                .body(new ClientRequestBody(body))
                .execute();
    }

    public List<ChannelAgreement> getChannelAgreements(Long channelId) {
        return httpClient.buildRequest(HttpMethod.GET, AGREEMENTS).pathParams(channelId).execute(ListType.of(ChannelAgreement.class));
    }

    public IdDTO createChannelAgreement(Long channelId, ChannelAgreement body) {
        return httpClient.buildRequest(HttpMethod.POST, AGREEMENTS).pathParams(channelId).body(new ClientRequestBody(body))
                .execute(IdDTO.class);
    }

    public void updateChannelAgreement(Long channelId, Long channelAgreementId, ChannelAgreement body) {
        httpClient.buildRequest(HttpMethod.PUT, AGREEMENT).pathParams(channelId, channelAgreementId).body(new ClientRequestBody(body))
                .execute();
    }

    public void deleteChannelAgreement(Long channelId, Long agreementId) {
        httpClient.buildRequest(HttpMethod.DELETE, AGREEMENT).pathParams(channelId, agreementId).execute();
    }

    public ChannelLiterals getChannelMasterLiterals(String appName, String languageCode, String key) {
        return httpClient.buildRequest(HttpMethod.GET, APPS_CONTENTS_TEXT)
                .pathParams(appName, languageCode)
                .params(new QueryParameters.Builder().addQueryParameter("key", key).build())
                .execute(ChannelLiterals.class);
    }

    public ChannelLiterals getChannelLiterals(Long channelId, String languageCode, String key, ChannelVersion channelVersion) {
        QueryParameters parameters = new QueryParameters.Builder()
                .addQueryParameter("key", key)
                .addQueryParameter("channelVersion", channelVersion)
                .build();
        return httpClient.buildRequest(HttpMethod.GET, CONTENTS_TEXT)
                .pathParams(channelId, languageCode)
                .params(parameters)
                .execute(ChannelLiterals.class);
    }

    public void createOrUpdateChannelMasterLiterals(String appName, String languageCode, ChannelLiterals body) {
        httpClient.buildRequest(HttpMethod.POST, APPS_CONTENTS_TEXT)
                .pathParams(appName, languageCode).body(new ClientRequestBody(body)).execute();
    }

    public void createOrUpdateChannelLiterals(Long channelId, String languageCode, ChannelLiterals body, ChannelVersion channelVersion) {
        QueryParameters parameters = new QueryParameters.Builder().addQueryParameter("channelVersion", channelVersion).build();
        httpClient.buildRequest(HttpMethod.POST, CONTENTS_TEXT)
                .pathParams(channelId, languageCode)
                .params(parameters)
                .body(new ClientRequestBody(body)).execute();
    }

    public ChannelEmailTemplates getChannelEmailTemplates(Long channelId) {
        return httpClient.buildRequest(HttpMethod.GET, EMAIL_TEMPLATES)
                .pathParams(channelId).execute(ChannelEmailTemplates.class);
    }

    public void updateChannelEmailTemplates(Long channelId, ChannelEmailTemplates body) {
        httpClient.buildRequest(HttpMethod.PUT, EMAIL_TEMPLATES)
                .pathParams(channelId).body(new ClientRequestBody(body)).execute();
    }

    public ChannelExternalTools getChannelExternalTools(Long channelId) {
        return httpClient.buildRequest(HttpMethod.GET, EXTERNAL_TOOLS)
                .pathParams(channelId).execute(ChannelExternalTools.class);
    }

    public void updateChannelExternalTools(Long channelId, ChannelExternalTool requestDTO) {
        httpClient.buildRequest(HttpMethod.PUT, EXTERNAL_TOOLS)
                .pathParams(channelId)
                .body(new ClientRequestBody(requestDTO))
                .execute();
    }

    public List<ChannelTextBlock> getChannelTextBlocks(Long channelId, ChannelTextBlockFilter filter) {
        QueryParameters.Builder params = new QueryParameters.Builder();
        params.addQueryParameters(filter);
        return httpClient.buildRequest(HttpMethod.GET, CONTENTS_TEXT_BLOCKS).pathParams(channelId)
                .params(params.build())
                .execute(ListType.of(ChannelTextBlock.class));
    }

    public ChannelFAQs getChannelFAQs(Long channelId, String languageCode, List<String> tags, String q) {
        QueryParameters.Builder params = new QueryParameters.Builder().addQueryParameter("languageCode", languageCode);

        if (CollectionUtils.isNotEmpty(tags)) {
            params.addQueryParameter("tag", tags);
        }
        if (StringUtils.isNotBlank(q)) {
            params.addQueryParameter("q", q);
        }

        return httpClient.buildRequest(HttpMethod.GET, FAQS).pathParams(channelId)
                .params(params.build())
                .execute(ChannelFAQs.class);
    }

    public ChannelFAQ getChannelFAQsItem(Long channelId, String key) {
        return httpClient.buildRequest(HttpMethod.GET, FAQ_ITEM)
                .pathParams(channelId, key)
                .execute(ChannelFAQ.class);
    }

    public void addChannelFAQ(Long channelId, ChannelFAQUpsertRequest faq) {
        httpClient.buildRequest(HttpMethod.POST, FAQS).pathParams(channelId)
                .body(new ClientRequestBody(faq))
                .execute();
    }

    public void updateChannelFAQs(Long channelId, ChannelFAQUpsertRequest faqs, String key) {
        httpClient.buildRequest(HttpMethod.PUT, FAQ_ITEM)
                .pathParams(channelId, key)
                .body(new ClientRequestBody(faqs))
                .execute();
    }

    public void bulkUpdateChannelFAQs(Long channelId, ChannelFAQs faqs) {
        httpClient.buildRequest(HttpMethod.PUT, FAQS)
                .pathParams(channelId)
                .body(new ClientRequestBody(faqs))
                .execute();
    }

    public void deleteChannelFAQ(Long channelId, String key) {
        httpClient.buildRequest(HttpMethod.DELETE, FAQ_ITEM)
                .pathParams(channelId, key)
                .execute();
    }

    public List<ChannelAuditedTextBlock> getChannelTextBlocksHistoricalData(Long channelId, Long blockId, String language) {
        return httpClient.buildRequest(HttpMethod.GET, CONTENTS_TEXT_BLOCK_HISTORY).pathParams(channelId, blockId)
                .params(new QueryParameters.Builder().addQueryParameter("language", language).build())
                .execute(ListType.of(ChannelAuditedTextBlock.class));
    }

    public void updateChannelTextBlocks(Long channelId, UpdateChannelTextBlocks body) {
        httpClient.buildRequest(HttpMethod.PUT, CONTENTS_TEXT_BLOCKS).pathParams(channelId)
                .body(new ClientRequestBody(body)).execute();
    }

    public void updateProfiledChannelTextBlocks(Long channelId, Long contentId, List<UpdateChannelProfiledTextBlock> body) {
        httpClient.buildRequest(HttpMethod.PUT, CONTENTS_PROFILED_TEXT_BLOCKS).pathParams(channelId, contentId)
                .body(new ClientRequestBody(body)).execute();
    }

    public VoucherGroups getVoucherGroups(Long userOperatorId, VoucherGroupFilter filter, SortOperator<String> sort) {
        QueryParameters.Builder params = new QueryParameters.Builder().
                addQueryParameter("operatorId", userOperatorId).
                addQueryParameters(filter);

        ConverterUtils.checkSortFields(sort, params, VoucherGroupField::byName);

        return httpClient.buildRequest(HttpMethod.GET, VOUCHER_GROUPS)
                .params(params.build())
                .execute(VoucherGroups.class);
    }

    public VoucherGroup getVoucherGroup(Long voucherGroupId) {
        return httpClient.buildRequest(HttpMethod.GET, VOUCHER_GROUP)
                .pathParams(voucherGroupId)
                .execute(VoucherGroup.class);
    }

    public IdDTO createVoucherGroup(CreateVoucherGroup request) {
        return httpClient.buildRequest(HttpMethod.POST, VOUCHER_GROUPS)
                .body(new ClientRequestBody(request))
                .execute(IdDTO.class);
    }

    public void updateVoucherGroup(VoucherGroup request) {
        httpClient.buildRequest(HttpMethod.PUT, VOUCHER_GROUP)
                .pathParams(request.getId())
                .body(new ClientRequestBody(request))
                .execute();
    }

    public void deleteVoucherGroup(Long voucherGroupId) {
        httpClient.buildRequest(HttpMethod.DELETE, VOUCHER_GROUP)
                .pathParams(voucherGroupId)
                .execute();
    }

    public VoucherGroupGiftCard getVoucherGroupGiftCard(Long voucherGroupId) {
        return httpClient.buildRequest(HttpMethod.GET, VOUCHER_GROUP_GIFT_CARD)
                .pathParams(voucherGroupId)
                .execute(VoucherGroupGiftCard.class);
    }

    public void updateVoucherGroupGiftCard(Long voucherGroupId, VoucherGroupGiftCard request) {
        httpClient.buildRequest(HttpMethod.PUT, VOUCHER_GROUP_GIFT_CARD)
                .pathParams(voucherGroupId)
                .body(new ClientRequestBody(request))
                .execute();
    }


    public Vouchers getVouchers(Long voucherGroupId, VoucherFilter filter) {
        QueryParameters params = new QueryParameters.Builder().
                addQueryParameters(filter).
                build();
        return httpClient.buildRequest(HttpMethod.GET, VOUCHERS)
                .pathParams(voucherGroupId)
                .params(params)
                .execute(Vouchers.class);
    }

    public Voucher getVoucher(Long voucherGroupId, String code) {
        return httpClient.buildRequest(HttpMethod.GET, VOUCHER)
                .pathParams(voucherGroupId, code)
                .execute(Voucher.class);
    }

    public String createVoucher(Long voucherGroupId, CreateVoucher request) {
        return httpClient.buildRequest(HttpMethod.POST, VOUCHERS)
                .pathParams(voucherGroupId)
                .body(new ClientRequestBody(request))
                .execute(String.class);
    }

    public List<String> createVouchers(Long voucherGroupId, List<CreateVoucher> request) {
        return httpClient.buildRequest(HttpMethod.POST, VOUCHERS_BULK)
                .pathParams(voucherGroupId)
                .body(new ClientRequestBody(request))
                .execute(ListType.of(String.class));
    }

    public void updateVoucher(Voucher request) {
        httpClient.buildRequest(HttpMethod.PUT, VOUCHER)
                .pathParams(request.getVoucherGroupId(), request.getCode())
                .body(new ClientRequestBody(request))
                .execute();
    }

    public void updateVoucherBalance(Long voucherGroupId, String code, UpdateVoucherBalance request) {
        httpClient.buildRequest(HttpMethod.PUT, VOUCHER_BALANCE)
                .pathParams(voucherGroupId, code)
                .body(new ClientRequestBody(request))
                .execute();
    }

    public void updateVouchers(Long voucherGroupId, UpdateVouchersBulk request) {
        httpClient.buildRequest(HttpMethod.PUT, VOUCHERS_BULK)
                .pathParams(voucherGroupId)
                .body(new ClientRequestBody(request))
                .execute();
    }

    public void deleteVoucher(Long voucherGroupId, String code) {
        httpClient.buildRequest(HttpMethod.DELETE, VOUCHER)
                .pathParams(voucherGroupId, code)
                .execute();
    }

    public List<BaseCommunicationElement> getVoucherGroupComElements(Long voucherGroupId, CommunicationElementFilter comElementsFilter) {
        return httpClient.buildRequest(HttpMethod.GET, VOUCHER_GROUP_COMPLEMENTS)
                .pathParams(voucherGroupId)
                .params(new QueryParameters.Builder().addQueryParameters(comElementsFilter).build())
                .execute(ListType.of(BaseCommunicationElement.class));
    }

    public void updateVoucherGroupComElements(Long voucherGroupId, List<BaseCommunicationElement> elements) {
        httpClient.buildRequest(HttpMethod.POST, VOUCHER_GROUP_COMPLEMENTS)
                .pathParams(voucherGroupId)
                .body(new ClientRequestBody(elements))
                .execute();
    }

    public List<ChannelTicketContent> getChannelTicketPDFContent(Long channelId, String language, String type) {
        return httpClient.buildRequest(HttpMethod.GET, TICKET_CONTENTS_PDF)
                .pathParams(channelId)
                .params(new QueryParameters.Builder()
                        .addQueryParameter("language", language)
                        .addQueryParameter("type", type)
                        .build())
                .execute(ListType.of(ChannelTicketContent.class));
    }

    public void updateChannelTicketPDFContent(Long channelId, List<ChannelTicketContent> body) {
        httpClient.buildRequest(HttpMethod.PUT, TICKET_CONTENTS_PDF)
                .pathParams(channelId)
                .body(new ClientRequestBody(body))
                .execute();
    }

    public void deleteChannelTicketPDFContent(Long channelId, String language, String type) {
        httpClient.buildRequest(HttpMethod.DELETE, TICKET_CONTENTS_PDF_DELETE)
                .pathParams(channelId, language, type)
                .execute();
    }

    public List<ChannelTicketContent> getChannelTicketPassbookContent(Long channelId, String language, String type) {
        return httpClient.buildRequest(HttpMethod.GET, TICKET_CONTENTS_PASSBOOK)
                .pathParams(channelId)
                .params(new QueryParameters.Builder()
                        .addQueryParameter("language", language)
                        .addQueryParameter("type", type)
                        .build())
                .execute(ListType.of(ChannelTicketContent.class));
    }

    public void updateChannelTicketPassbookContent(Long channelId, List<ChannelTicketContent> body) {
        httpClient.buildRequest(HttpMethod.PUT, TICKET_CONTENTS_PASSBOOK)
                .pathParams(channelId)
                .body(new ClientRequestBody(body))
                .execute();
    }

    public void deleteChannelTicketPassbookContent(Long channelId, String language, String type) {
        httpClient.buildRequest(HttpMethod.DELETE, TICKET_CONTENTS_PASSBOOK_DELETE)
                .pathParams(channelId, language, type)
                .execute();
    }

    public List<ChannelPurchaseContent> getChannelPurchaseContent(Long channelId, String language, List<String> types) {
        QueryParameters.Builder builder = new QueryParameters.Builder();
        types.forEach(t -> builder.addQueryParameter("type", t));
        builder.addQueryParameter("language", language);
        return httpClient.buildRequest(HttpMethod.GET, PURCHASE_CONTENTS)
                .pathParams(channelId)
                .params(builder.build())
                .execute(ListType.of(ChannelPurchaseContent.class));
    }

    public void updateChannelPurchaseContent(Long channelId, List<ChannelPurchaseContent> body) {
        httpClient.buildRequest(HttpMethod.PUT, PURCHASE_CONTENTS)
                .pathParams(channelId)
                .body(new ClientRequestBody(body))
                .execute();
    }

    public void deleteChannelPurchaseContent(Long channelId, String language, String type) {
        httpClient.buildRequest(HttpMethod.DELETE, PURCHASE_CONTENTS_DELETE)
                .pathParams(channelId, language, type)
                .execute();
    }

    public List<ChannelTicketContent> getChannelTicketPrinterContent(Long channelId, String language, String type) {
        return httpClient.buildRequest(HttpMethod.GET, TICKET_CONTENTS_PRINTER)
                .pathParams(channelId)
                .params(new QueryParameters.Builder()
                        .addQueryParameter("language", language)
                        .addQueryParameter("type", type)
                        .build())
                .execute(ListType.of(ChannelTicketContent.class));
    }

    public void updateChannelTicketPrinterContent(Long channelId, List<ChannelTicketContent> body) {
        httpClient.buildRequest(HttpMethod.PUT, TICKET_CONTENTS_PRINTER)
                .pathParams(channelId)
                .body(new ClientRequestBody(body))
                .execute();
    }

    public void deleteChannelTicketPrinterContent(Long channelId, String language, String type) {
        httpClient.buildRequest(HttpMethod.DELETE, TICKET_CONTENTS_PRINTER_DELETE)
                .pathParams(channelId, language, type)
                .execute();
    }

    public ChannelEmailServer getChannelEmailServer(Long channelId) {
        return httpClient.buildRequest(HttpMethod.GET, EMAIL_SERVER)
                .pathParams(channelId)
                .execute(ChannelEmailServer.class);
    }

    public void updateChannelEmailServer(Long channelId, ChannelEmailServer payload) {
        httpClient.buildRequest(HttpMethod.PUT, EMAIL_SERVER)
                .pathParams(channelId)
                .body(new ClientRequestBody(payload))
                .execute();
    }

    public ChannelEvents getChannelEvents(Long channelId, ChannelEventMsFilter request) {
        return httpClient.buildRequest(HttpMethod.GET, CATALOG).pathParams(channelId)
                .params(new QueryParameters.Builder().addQueryParameters(request).build()).execute(ChannelEvents.class);
    }

    public void updateChannelEvents(Long channelId, ChannelEventsUpdate body) {
        httpClient.buildRequest(HttpMethod.PUT, CATALOG)
                .pathParams(channelId)
                .body(new ClientRequestBody(body))
                .execute();
    }

    public ChannelEvent getChannelEvent(Long channelId, Long eventId) {
        return httpClient.buildRequest(HttpMethod.GET, CATALOG_EVENT)
                .pathParams(channelId, eventId)
                .execute(ChannelEvent.class);
    }

    public void putChannelEvent(Long channelId, Long eventId, ChannelEventUpdate channelEventUpdate) {
        httpClient.buildRequest(HttpMethod.PUT, CATALOG_EVENT)
                .pathParams(channelId, eventId)
                .body(new ClientRequestBody(channelEventUpdate))
                .execute();
    }

    public ChannelSessions getChannelEventSessions(Long channelId, Long eventId, ChannelSessionsMsFilter filter) {
        QueryParameters params = new QueryParameters.Builder()
                .addQueryParameters(filter)
                .build();
        return httpClient.buildRequest(HttpMethod.GET, CATALOG_EVENT_SESSIONS)
                .pathParams(channelId, eventId)
                .params(params)
                .execute(ChannelSessions.class);
    }

    public ChannelBlacklistStatus getChannelBlacklistStatus(Long channelId, ChannelBlacklistType type) {
        return httpClient.buildRequest(HttpMethod.GET, BLACKLISTS_STATUS)
                .pathParams(channelId, type)
                .execute(ChannelBlacklistStatus.class);
    }

    public void updateChannelBlacklistStatus(Long channelId, ChannelBlacklistType type, ChannelBlacklistStatus msRequest) {
        httpClient.buildRequest(HttpMethod.PUT, BLACKLISTS_STATUS)
                .pathParams(channelId, type)
                .body(new ClientRequestBody(msRequest))
                .execute();
    }

    public ChannelBlacklistsResponse getChannelBlacklists(Long channelId, ChannelBlacklistType type, ChannelBlacklistFilter filter) {
        return httpClient.buildRequest(HttpMethod.GET, BLACKLISTS)
                .pathParams(channelId, type)
                .params(new QueryParameters.Builder().addQueryParameters(filter).build())
                .execute(ChannelBlacklistsResponse.class);
    }

    public ChannelBlacklist getChannelBlacklistItem(Long channelId, ChannelBlacklistType type, String value) {
        return httpClient.buildRequest(HttpMethod.GET, BLACKLIST_ITEM)
                .pathParams(channelId, type, value)
                .execute(ChannelBlacklist.class);
    }

    public void createChannelBlacklists(Long channelId, ChannelBlacklistType type, List<ChannelBlacklist> body) {
        httpClient.buildRequest(HttpMethod.POST, BLACKLISTS)
                .pathParams(channelId, type)
                .body(new ClientRequestBody(body))
                .execute();
    }

    public void deleteChannelBlacklists(Long channelId, ChannelBlacklistType type, ChannelBlacklistFilter filter) {
        httpClient.buildRequest(HttpMethod.DELETE, BLACKLISTS)
                .pathParams(channelId, type)
                .params(new QueryParameters.Builder().addQueryParameters(filter).build())
                .execute();
    }

    public void deleteChannelBlacklistItem(Long channelId, ChannelBlacklistType type, String value) {
        httpClient.buildRequest(HttpMethod.DELETE, BLACKLIST_ITEM)
                .pathParams(channelId, type, value)
                .execute();
    }

    public ChannelVouchers getChannelVouchersConfig(Long channelId) {
        return httpClient.buildRequest(HttpMethod.GET, CHANNEL_VOUCHERS)
                .pathParams(channelId)
                .execute(ChannelVouchers.class);
    }

    public ChannelLoyaltyPoints getChannelLoyaltyPoints(Long channelId) {
        return httpClient.buildRequest(HttpMethod.GET, CHANNEL_LOYALTY_POINTS)
                .pathParams(channelId)
                .execute(ChannelLoyaltyPoints.class);
    }


    public void updateChannelVouchersConfig(Long channelId, ChannelVouchers body) {
        httpClient.buildRequest(HttpMethod.PUT, CHANNEL_VOUCHERS)
                .pathParams(channelId)
                .body(new ClientRequestBody(body))
                .execute();
    }

    public void updateChannelLoyaltyPoints(Long channelId, ChannelLoyaltyPoints body) {
        httpClient.buildRequest(HttpMethod.PUT, CHANNEL_LOYALTY_POINTS)
                .pathParams(channelId)
                .body(new ClientRequestBody(body))
                .execute();
    }

    public ExportProcess generateVouchersReport(Long voucherGroupId, ExportFilter<VoucherExportFileField> filter) {
        return httpClient.buildRequest(HttpMethod.POST, VOUCHERS_REPORT)
                .pathParams(voucherGroupId)
                .body(new ClientRequestBody(filter))
                .execute(ExportProcess.class);
    }

    public ExportProcess getVouchersReportStatus(Long voucherGroupId, String exportId, Long userId) {
        return httpClient.buildRequest(HttpMethod.GET, VOUCHERS_REPORT_STATUS)
                .pathParams(voucherGroupId, exportId, userId)
                .execute(ExportProcess.class);
    }

    public ChannelAuthVendor getChannelAuthVendors(Long channelId) {
        return httpClient.buildRequest(HttpMethod.GET, CHANNEL_AUTH_VENDORS)
                .pathParams(channelId)
                .execute(ChannelAuthVendor.class);
    }

    public void updateChannelAuthVendors(Long channelId, ChannelAuthVendor body) {
        httpClient.buildRequest(HttpMethod.PUT, CHANNEL_AUTH_VENDORS)
                .pathParams(channelId)
                .body(new ClientRequestBody(body))
                .execute();
    }

    public void cloneChannelTicketContents(Long channelId, ChannelContentClone body) {
        httpClient.buildRequest(HttpMethod.POST, TICKET_CONTENTS_CLONE)
                .pathParams(channelId)
                .body(new ClientRequestBody(body))
                .execute();
    }

    public void cloneChannelPurchaseContents(Long channelId, ChannelContentClone body) {
        httpClient.buildRequest(HttpMethod.POST, PURCHASE_CONTENTS_CLONE)
                .pathParams(channelId)
                .body(new ClientRequestBody(body))
                .execute();
    }

    public void cloneChannelTextBlocksContents(Long channelId, ChannelContentClone body) {
        httpClient.buildRequest(HttpMethod.POST, CONTENTS_TEXT_BLOCKS_CLONE)
                .pathParams(channelId)
                .body(new ClientRequestBody(body))
                .execute();
    }

    public void cloneChannelTextContents(Long channelId, ChannelContentClone body) {
        httpClient.buildRequest(HttpMethod.POST, CONTENTS_TEXT_CLONE)
                .pathParams(channelId)
                .body(new ClientRequestBody(body))
                .execute();
    }

    public List<PackCommunicationElement> getPackCommunicationElements(Long channelId, Long packId, CommunicationElementFilter<PackTagType> filter) {
        return httpClient.buildRequest(HttpMethod.GET, PACK_COMMUNICATION_ELEMENTS)
                .pathParams(channelId, packId)
                .params(new QueryParameters.Builder().addQueryParameters(filter).build())
                .execute(ListType.of(PackCommunicationElement.class));
    }

    public void updatePackCommunicationElements(Long channelId, Long packId, List<PackCommunicationElement> elements) {
        httpClient.buildRequest(HttpMethod.POST, PACK_COMMUNICATION_ELEMENTS)
                .pathParams(channelId, packId)
                .body(new ClientRequestBody(elements))
                .execute();
    }

    public List<ChannelTicketContent> getPackTicketContent(Long channelId, Long packId, String language, String type, TicketCommunicationElementCategory category) {
        return httpClient.buildRequest(HttpMethod.GET, CHANNEL_PACK_TICKET_CONTENTS)
                .pathParams(channelId, packId, category)
                .params(new QueryParameters.Builder()
                        .addQueryParameter("language", language)
                        .addQueryParameter("type", type)
                        .build())
                .execute(ListType.of(ChannelTicketContent.class));
    }

    public void updatePackTicketContent(Long channelId, Long packId, List<ChannelTicketContent> body, TicketCommunicationElementCategory category) {
        httpClient.buildRequest(HttpMethod.PUT, CHANNEL_PACK_TICKET_CONTENTS)
                .pathParams(channelId, packId, category)
                .body(new ClientRequestBody(body))
                .execute();
    }

    public void deletePackTicketContent(Long channelId, Long packId, String language, String type, TicketCommunicationElementCategory category) {
        httpClient.buildRequest(HttpMethod.DELETE, CHANNEL_PACK_TICKET_CONTENTS_DELETE)
                .pathParams(channelId, packId, category, language, type)
                .execute();
    }

    public void sendEmailVoucher(Long voucherGroupId, String code, SendEmailVoucher body) {
        httpClient.buildRequest(HttpMethod.POST, VOUCHER_SEND_EMAIL)
                .pathParams(voucherGroupId, code)
                .body(new ClientRequestBody(body))
                .execute();
    }

    public ChannelEventSaleRestrictionResponse getEventSaleRestrictions(Long channelId) {
        return httpClient.buildRequest(HttpMethod.GET, CHANNEL_EVENT_SALE_RESTRICTIONS)
                .pathParams(channelId)
                .execute(ChannelEventSaleRestrictionResponse.class);
    }

    public void updateEventSaleRestrictions(Long channelId, ChannelEventsSaleRestrictions body) {
        httpClient.buildRequest(HttpMethod.PUT, CHANNEL_EVENT_SALE_RESTRICTIONS)
                .pathParams(channelId)
                .body(new ClientRequestBody(body))
                .execute();
    }

    public void resetExternalTool(Long channelId, ChannelExternalToolsNamesDTO toolName) {
        httpClient.buildRequest(HttpMethod.POST, EXTERNAL_TOOL_RESET)
                .pathParams(channelId, toolName)
                .execute();
    }

    public ChannelSuggestions getSuggestions(Long channelId, ChannelSuggestionMsFilter filter) {
        return httpClient.buildRequest(HttpMethod.GET, CHANNEL_SUGGESTIONS)
                .pathParams(channelId)
                .params(new QueryParameters.Builder().addQueryParameters(filter).build())
                .execute(ChannelSuggestions.class);
    }

    public void addChannelSuggestion(Long channelId, SuggestionType sourceType, Long sourceId, CreateSuggestionTargetRequestDTO createSuggestionTargetRequestDTO) {
        httpClient.buildRequest(HttpMethod.POST, CHANNEL_SUGGESTIONS + "/{SOURCE_TYPE}/{sourceId}")
                .pathParams(channelId, sourceType, sourceId)
                .body(new ClientRequestBody(createSuggestionTargetRequestDTO))
                .execute();
    }

    public void deleteSuggestion(Long channelId, SuggestionType sourceType, Long sourceId, SuggestionType targetType, Long targetId) {
        httpClient.buildRequest(HttpMethod.DELETE, CHANNEL_SUGGESTIONS + "/{SOURCE_TYPE}/{sourceId}/targets/{TARGET_TYPE}/{targetId}")
                .pathParams(channelId, sourceType, sourceId, targetType, targetId)
                .execute();
    }

    public void deleteSuggestions(Long channelId, SuggestionType sourceType, Long sourceId) {
        httpClient.buildRequest(HttpMethod.DELETE, CHANNEL_SUGGESTIONS + "/{SOURCE_TYPE}/{sourceId}")
                .pathParams(channelId, sourceType, sourceId)
                .execute();
    }

    public ChannelWhitelabelSettings getChannelWhitelabelSettings(Long channelId) {
        return httpClient.buildRequest(HttpMethod.GET, WHITELABEL_SETTINGS)
                .pathParams(channelId)
                .execute(ChannelWhitelabelSettings.class);
    }

    public void updateChannelWhitelabelSettings(Long channelId, ChannelWhitelabelSettings request) {
        httpClient.buildRequest(HttpMethod.PUT, WHITELABEL_SETTINGS)
                .pathParams(channelId)
                .body(new ClientRequestBody(request))
                .execute();
    }

    public CustomResourcesMsDTO getCustomResources(Integer channelId) {
        return httpClient.buildRequest(HttpMethod.GET, CHANNEL_CUSTOM_RESOURCE)
                .pathParams(channelId)
                .execute(CustomResourcesMsDTO.class);
    }

    public void createOrUpdateCustomResource(Integer channelId, UpdateCustomResourcesMsDTO updateCustomResourcesMsDTO) {
        httpClient.buildRequest(HttpMethod.PUT, CHANNEL_CUSTOM_RESOURCE)
                .pathParams(channelId)
                .body(new ClientRequestBody(updateCustomResourcesMsDTO))
                .execute();
    }

    public List<Pack> getPacks(Long channelId) {
        return httpClient.buildRequest(HttpMethod.GET, CHANNEL_PACKS)
                .pathParams(channelId)
                .execute(ListType.of(Pack.class));
    }

    public PackDetail getPack(Long channelId, Long packId) {
        return httpClient.buildRequest(HttpMethod.GET, CHANNEL_PACK)
                .pathParams(channelId, packId)
                .execute(PackDetail.class);
    }

    public Pack createPack(Long channelId, CreatePack createPack) {
        return httpClient.buildRequest(HttpMethod.POST, CHANNEL_PACKS)
                .pathParams(channelId)
                .body(new ClientRequestBody(createPack))
                .execute(Pack.class);
    }

    public void updatePack(Long channelId, Long packId, UpdatePack updatePack) {
        httpClient.buildRequest(HttpMethod.PUT, CHANNEL_PACK)
                .pathParams(channelId, packId)
                .body(new ClientRequestBody(updatePack))
                .execute();
    }

    public void deletePack(Long channelId, Long packId) {
        httpClient.buildRequest(HttpMethod.DELETE, CHANNEL_PACK)
                .pathParams(channelId, packId)
                .execute();
    }

    public List<PackItem> getPackItems(Long channelId, Long packId) {
        return httpClient.buildRequest(HttpMethod.GET, CHANNEL_PACK_ITEMS)
                .pathParams(channelId, packId)
                .execute(ListType.of(PackItem.class));
    }

    public void createPackItems(Long channelId, Long packId, CreatePackItems createPackItems) {
        httpClient.buildRequest(HttpMethod.POST, CHANNEL_PACK_ITEMS)
                .pathParams(channelId, packId)
                .body(new ClientRequestBody(createPackItems))
                .execute();
    }

    public void updatePackItem(Long channelId, Long packId, Long packItemId, UpdatePackItem updatePackItem) {
        httpClient.buildRequest(HttpMethod.PUT, CHANNEL_PACK_ITEM)
                .pathParams(channelId, packId, packItemId)
                .body(new ClientRequestBody(updatePackItem))
                .execute();
    }

    public void deletePackItem(Long channelId, Long packId, Long packItemId) {
        httpClient.buildRequest(HttpMethod.DELETE, CHANNEL_PACK_ITEM)
                .pathParams(channelId, packId, packItemId)
                .execute();
    }

    public List<PackRate> getPackRates(Long channelId, Long packId) {
        return httpClient.buildRequest(HttpMethod.GET, CHANNEL_PACK + RATES)
                .pathParams(channelId, packId)
                .execute(ListType.of(PackRate.class));
    }

    public IdDTO createPackRates(Long channelId, Long packId, CreatePackRate createPackRate) {
        return httpClient.buildRequest(HttpMethod.POST, CHANNEL_PACK + RATES)
                .pathParams(channelId, packId)
                .body(new ClientRequestBody(createPackRate))
                .execute(IdDTO.class);
    }

    public void refreshPackRates(Long channelId, Long packId) {
        httpClient.buildRequest(HttpMethod.POST, CHANNEL_PACK + RATES + "/refresh")
                .pathParams(channelId, packId)
                .execute();
    }

    public void updatePackRate(Long channelId, Long packId, Long rateId, UpdatePackRate updatePackRate) {
        httpClient.buildRequest(HttpMethod.PUT, CHANNEL_PACK + RATE)
                .pathParams(channelId, packId, rateId)
                .body(new ClientRequestBody(updatePackRate))
                .execute(IdDTO.class);
    }

    public void deletePackRate(Long channelId, Long packId, Long rateId) {
        httpClient.buildRequest(HttpMethod.DELETE, CHANNEL_PACK + RATE)
                .pathParams(channelId, packId, rateId)
                .execute(IdDTO.class);
    }

    public List<PackPrice> getPackPrices(Long channelId, Long packId) {
        return httpClient.buildRequest(HttpMethod.GET, CHANNEL_PACK + PRICES)
                .pathParams(channelId, packId)
                .execute(ListType.of(PackPrice.class));
    }

    public void updatePackPrices(Long channelId, Long packId, List<UpdatePackPrice> updatePackPrice) {
        httpClient.buildRequest(HttpMethod.PUT, CHANNEL_PACK + PRICES)
                .pathParams(channelId, packId)
                .body(new ClientRequestBody(updatePackPrice))
                .execute();
    }

    public CustomResourceAssetsMsDTO getCustomResourceAssets(Integer channelId, CustomResourceAssetsFilterMs filter) {
        return httpClient.buildRequest(HttpMethod.GET, CHANNEL_CUSTOM_RESOURCE_ASSETS)
                .pathParams(channelId)
                .params(new QueryParameters.Builder().addQueryParameters(filter).build())
                .execute(CustomResourceAssetsMsDTO.class);
    }

    public void addCustomResourceAssets(Integer channelId, CreateCustomResourceAssetsMsDTO createCustomResourceAssetsMsDTO) {
        httpClient.buildRequest(HttpMethod.POST, CHANNEL_CUSTOM_RESOURCE_ASSETS)
                .pathParams(channelId)
                .body(new ClientRequestBody(createCustomResourceAssetsMsDTO))
                .execute();
    }

    public void deleteCustomResourceAsset(Integer channelId, String filename) {
        httpClient.buildRequest(HttpMethod.DELETE, CHANNEL_CUSTOM_RESOURCE_ASSET)
                .pathParams(channelId, filename)
                .execute();
    }

    public WhatsappTemplates getWhatsappTemplatesContents(Long channelId) {
        return httpClient.buildRequest(HttpMethod.GET, WHATSAPP_TEMPLATES)
                .pathParams(channelId)
                .execute(WhatsappTemplates.class);
    }

    public AuthConfig getAuthConfig(Long channelId) {
        return httpClient.buildRequest(HttpMethod.GET, AUTH_CONFIG)
                .pathParams(channelId)
                .execute(AuthConfig.class);
    }

    public void updateAuthConfig(Long channelId, AuthConfig authConfig) {
        httpClient.buildRequest(HttpMethod.PUT, AUTH_CONFIG)
                .pathParams(channelId)
                .body(new ClientRequestBody(authConfig))
                .execute();
    }

    public AdminChannelsResponse getAdminChannels(ChannelFilter filter) {
        QueryParameters params = new QueryParameters.Builder()
                .addQueryParameters(filter)
                .build();
        return httpClient.buildRequest(HttpMethod.GET, PLATFORM_ADMIN_CHANNELS)
                .params(params)
                .execute(AdminChannelsResponse.class);
    }

    public void migrateChannel(Long channelId, Boolean migrateToChannels, Boolean stripeHookChecked) {
        QueryParameters.Builder params = new QueryParameters.Builder();
        if (migrateToChannels != null) {
            params.addQueryParameter("migrateToChannels", migrateToChannels);
        }
        if (stripeHookChecked != null) {
            params.addQueryParameter("stripeHookChecked", stripeHookChecked);
        }
        httpClient.buildRequest(HttpMethod.POST, CHANNEL_MIGRATION)
                .pathParams(channelId)
                .params(params.build())
                .execute();
    }

    public void resetVoucher(Long voucherGroupId, ResetVoucherRequest requestBody) {
        httpClient.buildRequest(HttpMethod.POST, RESET_VOUCHER)
                .pathParams(voucherGroupId)
                .body(new ClientRequestBody(requestBody))
                .execute();
    }

    public DomainSettings getChannelDomainSettings(Long channelId) {
        return httpClient.buildRequest(HttpMethod.GET, CHANNEL_DOMAIN_SETTINGS)
                .pathParams(channelId)
                .execute(DomainSettings.class);
    }

    public void upsertChannelDomainSettings(Long channelId, DomainSettings requestBody) {
        httpClient.buildRequest(HttpMethod.POST, CHANNEL_DOMAIN_SETTINGS)
                .pathParams(channelId)
                .body(new ClientRequestBody(requestBody))
                .execute();
    }

    public void disableChannelDomainSettings(Long channelId) {
        httpClient.buildRequest(HttpMethod.DELETE, CHANNEL_DOMAIN_SETTINGS)
                .pathParams(channelId)
                .execute();
    }

    public ChannelCorsSettings getChannelCorsSettings(Long channelId) {
        return httpClient.buildRequest(HttpMethod.GET, CHANNEL_CORS_SETTINGS)
                .pathParams(channelId)
                .execute(ChannelCorsSettings.class);
    }

    public void upsertChannelCorsSettings(Long channelId, ChannelCorsSettings requestBody) {
        httpClient.buildRequest(HttpMethod.POST, CHANNEL_CORS_SETTINGS)
                .pathParams(channelId)
                .body(new ClientRequestBody(requestBody))
                .execute();
    }

    public void disableChannelCorsSettings(Long channelId) {
        httpClient.buildRequest(HttpMethod.DELETE, CHANNEL_CORS_SETTINGS)
                .pathParams(channelId)
                .execute();
    }

    public DomainConfig getDomainConfig(String domain) {
        QueryParameters.Builder params = new QueryParameters.Builder();
        params.addQueryParameter("domain", domain);
        return httpClient.buildRequest(HttpMethod.GET, DOMAIN_CONFIG)
                .params(params.build())
                .execute(DomainConfig.class);
    }

    public void updateDomainConfig(String domain, DomainConfig body) {
        QueryParameters.Builder params = new QueryParameters.Builder();
        params.addQueryParameter("domain", domain);
        httpClient.buildRequest(HttpMethod.PUT, DOMAIN_CONFIG)
                .params(params.build())
                .body(new ClientRequestBody(body))
                .execute();
    }

    public SaleRequestChannelCandidatesResponse getEventSaleRequestChannelsCandidates(EventSaleRequestChannelFilter filter) {
        QueryParameters.Builder params = new QueryParameters.Builder();
        params.addQueryParameters(filter);
        return httpClient.buildRequest(HttpMethod.GET, SALE_REQUESTS_CHANNELS_CANDIDATES)
                .params(params.build())
                .execute(SaleRequestChannelCandidatesResponse.class);
    }

    public PackItemPriceTypesResponse getPackItemPriceTypes(Long channelId, Long packId, Long packItemId) {
        return httpClient.buildRequest(HttpMethod.GET, PACK_TEMPLATE_PRICE_TYPES)
                .pathParams(channelId, packId, packItemId)
                .execute(PackItemPriceTypesResponse.class);
    }

    public void updatePackItemPriceTypes(Long channelId, Long packId, Long packItemId, PackItemPriceTypesRequest priceTyepsRequest) {
        httpClient.buildRequest(HttpMethod.PUT, PACK_TEMPLATE_PRICE_TYPES)
                .pathParams(channelId, packId, packItemId)
                .body(new ClientRequestBody(priceTyepsRequest))
                .execute();
    }

    public void acceptPackRequest(Long packId, Long channelId) {
        httpClient.buildRequest(HttpMethod.POST, CHANNEL_PACK_ACCEPT_REQUEST)
                .pathParams(channelId, packId)
                .execute();
    }

    public void createPackSaleRequest(Long packId, Long channelId) {
        httpClient.buildRequest(HttpMethod.POST, CHANNEL_PACK_CREATE_SALE_REQUEST)
                .pathParams(channelId, packId)
                .execute();
    }

    public PhoneValidatorChannelConfig getPhoneValidatorChannelConfiguration(Long channelId) {
        return httpClient.buildRequest(HttpMethod.GET, CHANNEL_PHONE_VALIDATORS)
                .pathParams(channelId)
                .execute(PhoneValidatorChannelConfig.class);
    }

    public void updatePhoneValidatorChannelConfiguration(Long channelId, PhoneValidatorChannelConfig phoneValidatorChannelConfig) {
        httpClient.buildRequest(HttpMethod.PUT, CHANNEL_PHONE_VALIDATORS)
                .pathParams(channelId)
                .body(new ClientRequestBody(phoneValidatorChannelConfig))
                .execute();
    }

    public ChannelReview getChannelReview(Integer channelId) {
        return httpClient.buildRequest(HttpMethod.GET, REVIEWS)
                .pathParams(channelId)
                .execute(ChannelReview.class);
    }

    public void updateChannelReview(Integer channelId, ChannelReviewUpdate channelReviewUpdate) {
        httpClient.buildRequest(HttpMethod.PUT, REVIEWS)
                .pathParams(channelId)
                .body(new ClientRequestBody(channelReviewUpdate))
                .execute();
    }

    public ChannelReviewConfigResponse getChannelReviewsConfig(Integer channelId, ReviewsConfigFilter filter) {
        QueryParameters params = new QueryParameters.Builder().
                addQueryParameters(filter).
                build();
        return httpClient.buildRequest(HttpMethod.GET, REVIEWS_CONFIG)
                .pathParams(channelId)
                .params(params)
                .execute(ChannelReviewConfigResponse.class);
    }

    public void updateChannelReviewConfig(Integer channelId, ChannelReviewScope scope, Integer scopeId, ChannelReviewConfigUpdate request) {
        httpClient.buildRequest(HttpMethod.PUT, REVIEWS_CONFIG_SCOPE_ID)
                .pathParams(channelId, scope, scopeId)
                .body(new ClientRequestBody(request))
                .execute();
    }

    public void deleteChannelReviewConfig(Integer channelId, ChannelReviewScope scope, Integer scopeId) {
        httpClient.buildRequest(HttpMethod.DELETE, REVIEWS_CONFIG_SCOPE_ID)
                .pathParams(channelId, scope, scopeId)
                .execute();
    }

    public void upsertChannelReviewsConfigBulk(Integer channelId, ChannelReviewScope scope, ChannelReviewConfigUpdateBulk request) {
        httpClient.buildRequest(HttpMethod.PUT, REVIEWS_CONFIG_SCOPE)
                .pathParams(channelId, scope)
                .body(new ClientRequestBody(request))
                .execute();
    }

    public ChannelSurchargesTaxes getChannelSurchargesTaxes(Long channelId) {
        return httpClient.buildRequest(HttpMethod.GET, SURCHARGES_TAXES)
                .pathParams(channelId)
                .execute(ChannelSurchargesTaxes.class);
    }

    public void updateChannelSurchargesTaxes(Long channelId, ChannelSurchargesTaxesUpdate update) {
        httpClient.buildRequest(HttpMethod.PUT, SURCHARGES_TAXES)
                .pathParams(channelId)
                .body(new ClientRequestBody(update))
                .execute();
    }

}
