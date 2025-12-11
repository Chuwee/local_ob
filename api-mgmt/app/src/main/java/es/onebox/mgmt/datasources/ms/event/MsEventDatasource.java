package es.onebox.mgmt.datasources.ms.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.core.exception.ErrorCode;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.file.exporter.status.model.ExportProcess;
import es.onebox.core.serializer.dto.common.CodeDTO;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.core.serializer.dto.common.IdNameCodeDTO;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.core.serializer.dto.request.FilterWithOperator;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.core.utils.common.DateUtils;
import es.onebox.datasource.http.ClientRequestBody;
import es.onebox.datasource.http.HttpClient;
import es.onebox.datasource.http.QueryParameters;
import es.onebox.datasource.http.configuration.HttpClientFactoryBuilder;
import es.onebox.datasource.http.method.HttpMethod;
import es.onebox.datasource.http.response.ListType;
import es.onebox.mgmt.channels.enums.ReleaseStatusType;
import es.onebox.mgmt.channels.enums.RequestStatusType;
import es.onebox.mgmt.channels.enums.SaleStatusType;
import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.common.surcharges.dto.SurchargeTypeDTO;
import es.onebox.mgmt.datasources.common.dto.CommunicationElementFilter;
import es.onebox.mgmt.datasources.common.enums.EmailCommunicationElementTagType;
import es.onebox.mgmt.datasources.common.enums.SurchargeType;
import es.onebox.mgmt.datasources.ms.channel.dto.PackItemPriceTypesRequest;
import es.onebox.mgmt.datasources.ms.channel.dto.PackItemPriceTypesResponse;
import es.onebox.mgmt.datasources.ms.channel.dto.ticketcontent.ChannelTicketContent;
import es.onebox.mgmt.datasources.ms.channel.enums.ChannelSubtype;
import es.onebox.mgmt.datasources.ms.entity.dto.Form;
import es.onebox.mgmt.datasources.ms.entity.dto.UpdateSessionsLoyaltyPointsConfig;
import es.onebox.mgmt.datasources.ms.event.dto.CreateEventTierSaleGroup;
import es.onebox.mgmt.datasources.ms.event.dto.EventPassbookTemplates;
import es.onebox.mgmt.datasources.ms.event.dto.ProductLiterals;
import es.onebox.mgmt.datasources.ms.event.dto.ProductTicketLiterals;
import es.onebox.mgmt.datasources.ms.event.dto.ProductValueLiterals;
import es.onebox.mgmt.datasources.ms.event.dto.Tier;
import es.onebox.mgmt.datasources.ms.event.dto.TierCommunicationElement;
import es.onebox.mgmt.datasources.ms.event.dto.TierExtended;
import es.onebox.mgmt.datasources.ms.event.dto.Tiers;
import es.onebox.mgmt.datasources.ms.event.dto.UpdateEventTierSaleGroup;
import es.onebox.mgmt.datasources.ms.event.dto.b2b.B2BSeatPublishingConfig;
import es.onebox.mgmt.datasources.ms.event.dto.b2b.EventChannelB2BAssignations;
import es.onebox.mgmt.datasources.ms.event.dto.b2b.UpdateChannelEventAssignations;
import es.onebox.mgmt.datasources.ms.event.dto.customertypes.EventCustomerType;
import es.onebox.mgmt.datasources.ms.event.dto.customertypes.UpdateEventCustomerTypes;
import es.onebox.mgmt.datasources.ms.event.dto.event.AttendantFields;
import es.onebox.mgmt.datasources.ms.event.dto.event.Attribute;
import es.onebox.mgmt.datasources.ms.event.dto.event.AvailableFields;
import es.onebox.mgmt.datasources.ms.event.dto.event.ChannelEventImageConfigDTO;
import es.onebox.mgmt.datasources.ms.event.dto.event.CreateAttendantField;
import es.onebox.mgmt.datasources.ms.event.dto.event.CreateEventData;
import es.onebox.mgmt.datasources.ms.event.dto.event.EmailCommunicationElement;
import es.onebox.mgmt.datasources.ms.event.dto.event.Event;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventAttendantsConfigDTO;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventChannel;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventChannels;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventCommunicationElement;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventPostBookingQuestions;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventRates;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventSearchFilter;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventSurcharge;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventTagType;
import es.onebox.mgmt.datasources.ms.event.dto.event.Events;
import es.onebox.mgmt.datasources.ms.event.dto.event.ExternalBarcodeEventConfig;
import es.onebox.mgmt.datasources.ms.event.dto.event.PostBookingQuestions;
import es.onebox.mgmt.datasources.ms.event.dto.event.Product;
import es.onebox.mgmt.datasources.ms.event.dto.event.ProductEventsFilter;
import es.onebox.mgmt.datasources.ms.event.dto.event.Rate;
import es.onebox.mgmt.datasources.ms.event.dto.event.RateGroup;
import es.onebox.mgmt.datasources.ms.event.dto.event.RateGroupType;
import es.onebox.mgmt.datasources.ms.event.dto.event.RateRestricted;
import es.onebox.mgmt.datasources.ms.event.dto.event.RateRestrictions;
import es.onebox.mgmt.datasources.ms.event.dto.event.RatesGroup;
import es.onebox.mgmt.datasources.ms.event.dto.event.RequestSalesEventChannel;
import es.onebox.mgmt.datasources.ms.event.dto.event.SeasonTicketSurcharge;
import es.onebox.mgmt.datasources.ms.event.dto.event.TicketCommunicationElement;
import es.onebox.mgmt.datasources.ms.event.dto.event.UpdateEventChannel;
import es.onebox.mgmt.datasources.ms.event.dto.event.UpdateEventPostBookingQuestions;
import es.onebox.mgmt.datasources.ms.event.dto.event.UpdateRateRestrictions;
import es.onebox.mgmt.datasources.ms.event.dto.event.VenueTemplatePrice;
import es.onebox.mgmt.datasources.ms.event.dto.externalevent.ExternalEvent;
import es.onebox.mgmt.datasources.ms.event.dto.externalevent.ExternalEventType;
import es.onebox.mgmt.datasources.ms.event.dto.packs.CreatePack;
import es.onebox.mgmt.datasources.ms.event.dto.packs.CreatePackItems;
import es.onebox.mgmt.datasources.ms.event.dto.packs.CreatePackRate;
import es.onebox.mgmt.datasources.ms.event.dto.packs.Pack;
import es.onebox.mgmt.datasources.ms.event.dto.packs.PackDetail;
import es.onebox.mgmt.datasources.ms.event.dto.packs.PackItem;
import es.onebox.mgmt.datasources.ms.event.dto.packs.PackItemSubItemsResponse;
import es.onebox.mgmt.datasources.ms.event.dto.packs.PackPrice;
import es.onebox.mgmt.datasources.ms.event.dto.packs.PackRate;
import es.onebox.mgmt.datasources.ms.event.dto.packs.PacksFilterRequest;
import es.onebox.mgmt.datasources.ms.event.dto.packs.PacksResponse;
import es.onebox.mgmt.datasources.ms.event.dto.packs.UpdatePack;
import es.onebox.mgmt.datasources.ms.event.dto.packs.UpdatePackItem;
import es.onebox.mgmt.datasources.ms.event.dto.packs.UpdatePackPrice;
import es.onebox.mgmt.datasources.ms.event.dto.packs.UpdatePackRate;
import es.onebox.mgmt.datasources.ms.event.dto.packs.channel.PackChannel;
import es.onebox.mgmt.datasources.ms.event.dto.packs.channel.PackChannelCreate;
import es.onebox.mgmt.datasources.ms.event.dto.packs.channel.PackChannelRequestSales;
import es.onebox.mgmt.datasources.ms.event.dto.packs.channel.PackChannels;
import es.onebox.mgmt.datasources.ms.event.dto.packs.channel.UpdatePackChannel;
import es.onebox.mgmt.datasources.ms.event.dto.pricesimulation.VenueConfigPricesSimulation;
import es.onebox.mgmt.datasources.ms.event.dto.products.AddProductEvents;
import es.onebox.mgmt.datasources.ms.event.dto.products.CreateDeliveryPoint;
import es.onebox.mgmt.datasources.ms.event.dto.products.CreateProduct;
import es.onebox.mgmt.datasources.ms.event.dto.products.CreateProductAttribute;
import es.onebox.mgmt.datasources.ms.event.dto.products.CreateProductAttributeValue;
import es.onebox.mgmt.datasources.ms.event.dto.products.CreateProductChannels;
import es.onebox.mgmt.datasources.ms.event.dto.products.CreateProductChannelsResponse;
import es.onebox.mgmt.datasources.ms.event.dto.products.CreateProductCommunicationElementImage;
import es.onebox.mgmt.datasources.ms.event.dto.products.CreateProductCommunicationElementsText;
import es.onebox.mgmt.datasources.ms.event.dto.products.DeliveryPoint;
import es.onebox.mgmt.datasources.ms.event.dto.products.DeliveryPoints;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductAttribute;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductAttributeValue;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductAttributeValues;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductAttributes;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductChannel;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductChannelSessions;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductChannelSessionsFilter;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductChannels;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductCommunicationElementsImage;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductCommunicationElementsText;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductDelivery;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductDeliveryPointRelation;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductDeliveryPointRelations;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductEventDeliveryPoints;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductEvents;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductLanguages;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductPublishingSessions;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductSessionDeliveryPoints;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductSessions;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductSurcharge;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductTicketContentImagePassbookList;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductTicketContentImagePdfList;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductTicketContentTextList;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductVariant;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductVariants;
import es.onebox.mgmt.datasources.ms.event.dto.products.Products;
import es.onebox.mgmt.datasources.ms.event.dto.products.SearchDeliveryPointFilter;
import es.onebox.mgmt.datasources.ms.event.dto.products.SearchProductDeliveryPointRelationFilter;
import es.onebox.mgmt.datasources.ms.event.dto.products.SearchProductFilter;
import es.onebox.mgmt.datasources.ms.event.dto.products.SearchProductVariantsFilter;
import es.onebox.mgmt.datasources.ms.event.dto.products.UpdateDeliveryPoint;
import es.onebox.mgmt.datasources.ms.event.dto.products.UpdateProduct;
import es.onebox.mgmt.datasources.ms.event.dto.products.UpdateProductChannel;
import es.onebox.mgmt.datasources.ms.event.dto.products.UpdateProductEvent;
import es.onebox.mgmt.datasources.ms.event.dto.products.UpdateProductEventDeliveryPoints;
import es.onebox.mgmt.datasources.ms.event.dto.products.UpdateProductLanguages;
import es.onebox.mgmt.datasources.ms.event.dto.products.UpdateProductSession;
import es.onebox.mgmt.datasources.ms.event.dto.products.UpdateProductSessionDeliveryPoints;
import es.onebox.mgmt.datasources.ms.event.dto.products.UpdateProductSessions;
import es.onebox.mgmt.datasources.ms.event.dto.products.UpdateProductVariant;
import es.onebox.mgmt.datasources.ms.event.dto.products.UpdateProductVariantPrices;
import es.onebox.mgmt.datasources.ms.event.dto.products.UpsertProductDeliveryPointRelation;
import es.onebox.mgmt.datasources.ms.event.dto.products.producttickettemplate.ProductTicketTemplateLiteral;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.AssignSessionRequest;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.AssignSessionResponse;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.ChangeSeatSeasonTicketPriceCompleteRelation;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.ChangeSeatSeasonTicketPriceFilter;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.ChangeSeatSeasonTicketPriceRelations;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.CountRenewalsPurgeResponse;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.CreateSeasonTicketData;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.DeleteRenewalsRequest;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.DeleteRenewalsResponse;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.RenewalCandidatesSeasonTicketsRepositoryResponse;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.RenewalEntitiesRepositoryResponse;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.RenewalSeasonTicket;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicket;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicketChangeSeat;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicketDatasourceStatus;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicketFilter;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicketLoyaltyPointsConfig;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicketPresaleConfigDTO;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicketRate;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicketRates;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicketRedemption;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicketReleaseSeat;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicketRenewalsRepositoryResponse;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicketTax;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicketTransferSeat;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.UpdateSeasonTicketRedemption;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.UpdateSeasonTicketTransferSeat;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicketSessionValidationMsEventResponse;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicketSessions;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicketSessionsEventList;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTickets;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.UnAssignSessionResponse;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.UpdateChangeSeatSeasonTicketPriceRelation;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.UpdateRenewalRequest;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.UpdateRenewalResponse;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.UpdateSeasonTicketStatus;
import es.onebox.mgmt.datasources.ms.event.dto.secondarymarket.EventSecondaryMarketConfig;
import es.onebox.mgmt.datasources.ms.event.dto.secondarymarket.SeasonTicketSecondaryMarketConfig;
import es.onebox.mgmt.datasources.ms.event.dto.secondarymarket.SessionSecondaryMarketConfig;
import es.onebox.mgmt.datasources.ms.event.dto.session.CloneSessionData;
import es.onebox.mgmt.datasources.ms.event.dto.session.CreateSessionData;
import es.onebox.mgmt.datasources.ms.event.dto.session.EventCommunicationElementBulk;
import es.onebox.mgmt.datasources.ms.event.dto.session.ExternalBarcodeSessionConfig;
import es.onebox.mgmt.datasources.ms.event.dto.session.ExternalSessionConfig;
import es.onebox.mgmt.datasources.ms.event.dto.session.LinkedSession;
import es.onebox.mgmt.datasources.ms.event.dto.session.LoyaltyPointsConfig;
import es.onebox.mgmt.datasources.ms.event.dto.session.PreSaleConfigDTO;
import es.onebox.mgmt.datasources.ms.event.dto.session.PriceType;
import es.onebox.mgmt.datasources.ms.event.dto.session.PriceTypes;
import es.onebox.mgmt.datasources.ms.event.dto.session.Session;
import es.onebox.mgmt.datasources.ms.event.dto.session.SessionAttendantsConfigDTO;
import es.onebox.mgmt.datasources.ms.event.dto.session.SessionBulkUpdateResponse;
import es.onebox.mgmt.datasources.ms.event.dto.session.SessionExternalSessions;
import es.onebox.mgmt.datasources.ms.event.dto.session.SessionGroupConfig;
import es.onebox.mgmt.datasources.ms.event.dto.session.SessionRefundConditions;
import es.onebox.mgmt.datasources.ms.event.dto.session.SessionSaleConstraint;
import es.onebox.mgmt.datasources.ms.event.dto.session.SessionSaleRestriction;
import es.onebox.mgmt.datasources.ms.event.dto.session.Sessions;
import es.onebox.mgmt.datasources.ms.event.dto.session.SessionsGroups;
import es.onebox.mgmt.datasources.ms.event.dto.session.UpdateSaleRestriction;
import es.onebox.mgmt.datasources.ms.event.dto.session.UpdateSessionsData;
import es.onebox.mgmt.datasources.ms.event.dto.session.dynamicprice.DynamicPrice;
import es.onebox.mgmt.datasources.ms.event.dto.session.dynamicprice.DynamicPriceConfig;
import es.onebox.mgmt.datasources.ms.event.dto.session.dynamicprice.DynamicPriceStatusRequest;
import es.onebox.mgmt.datasources.ms.event.dto.session.dynamicprice.DynamicPriceZone;
import es.onebox.mgmt.datasources.ms.event.dto.session.dynamicprice.DynamicRatesPrice;
import es.onebox.mgmt.datasources.ms.event.dto.tags.SessionTagRequest;
import es.onebox.mgmt.datasources.ms.event.dto.tags.SessionTagResponse;
import es.onebox.mgmt.datasources.ms.event.dto.tags.SessionTagsResponse;
import es.onebox.mgmt.datasources.ms.event.dto.tickettemplate.TicketTemplate;
import es.onebox.mgmt.datasources.ms.event.dto.tickettemplate.TicketTemplateCommunicationElement;
import es.onebox.mgmt.datasources.ms.event.dto.tickettemplate.TicketTemplateDesign;
import es.onebox.mgmt.datasources.ms.event.dto.tickettemplate.TicketTemplateLiteral;
import es.onebox.mgmt.datasources.ms.event.dto.tickettemplate.TicketTemplateLiteralElementFilter;
import es.onebox.mgmt.datasources.ms.event.dto.tickettemplate.TicketTemplateTagType;
import es.onebox.mgmt.datasources.ms.event.dto.tickettemplate.TicketTemplates;
import es.onebox.mgmt.datasources.ms.event.dto.tickettemplate.TicketTemplatesFilter;
import es.onebox.mgmt.datasources.ms.event.dto.tour.Tour;
import es.onebox.mgmt.datasources.ms.event.dto.tour.TourEventFilter;
import es.onebox.mgmt.datasources.ms.event.dto.tour.Tours;
import es.onebox.mgmt.datasources.ms.venue.dto.template.CreateVenueTemplatePriceTypeRestriction;
import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTemplatePriceTypeRestriction;
import es.onebox.mgmt.entities.dto.AttributeRequestValuesDTO;
import es.onebox.mgmt.events.avetrestrictions.mapper.AvetSectorRestriction;
import es.onebox.mgmt.events.avetrestrictions.mapper.AvetSectorRestrictionCreate;
import es.onebox.mgmt.events.avetrestrictions.mapper.AvetSectorRestrictions;
import es.onebox.mgmt.events.avetrestrictions.mapper.UpdateAvetSectorRestriction;
import es.onebox.mgmt.events.dto.TierChannelContentFilter;
import es.onebox.mgmt.events.dto.channel.EventChannelSearchFilter;
import es.onebox.mgmt.events.enums.EventChannelField;
import es.onebox.mgmt.events.enums.TicketCommunicationElementCategory;
import es.onebox.mgmt.events.enums.TourStatus;
import es.onebox.mgmt.events.postbookingquestions.dto.PostBookingQuestionsFilter;
import es.onebox.mgmt.events.tours.dto.TourFilter;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.exception.ClientHttpExceptionBuilder;
import es.onebox.mgmt.export.enums.ExportType;
import es.onebox.mgmt.packs.dto.PackItemSubitemFilterDTO;
import es.onebox.mgmt.datasources.ms.event.dto.packs.UpdatePackItemSubitemsRequest;
import es.onebox.mgmt.packs.dto.comelements.PackCommunicationElement;
import es.onebox.mgmt.packs.enums.PackTagType;
import es.onebox.mgmt.products.dto.ProductSessionDeliveryPointsFilterDTO;
import es.onebox.mgmt.products.dto.ProductSessionSearchFilterDTO;
import es.onebox.mgmt.products.dto.SearchProductAttributeValueFilterDTO;
import es.onebox.mgmt.products.enums.ProductCommunicationElementsImagesType;
import es.onebox.mgmt.products.enums.ticketContent.ProductTicketContentType;
import es.onebox.mgmt.producttickettemplate.dto.ProductTicketTemplateLiteralElementFilter;
import es.onebox.mgmt.producttickettemplate.dto.ProductTicketTemplateLiterals;
import es.onebox.mgmt.salerequests.dto.PriceTypeFilter;
import es.onebox.mgmt.salerequests.pricesimulation.dto.PriceSimulationExportFilter;
import es.onebox.mgmt.seasontickets.dto.CreateEventChannelDTO;
import es.onebox.mgmt.seasontickets.dto.renewals.SeasonTicketRenewalFilter;
import es.onebox.mgmt.seasontickets.dto.renewals.SeasonTicketRenewalPurgeFilter;
import es.onebox.mgmt.seasontickets.dto.renewals.SeasonTicketRenewalsExportFilter;
import es.onebox.mgmt.seasontickets.dto.renewals.SeasonTicketRenewalsFilter;
import es.onebox.mgmt.seasontickets.dto.sessions.SeasonTicketSessionsEventsFilter;
import es.onebox.mgmt.seasontickets.dto.sessions.SeasonTicketSessionsSearchFilter;
import es.onebox.mgmt.seasontickets.enums.SeasonTicketRenewalSeatsSortableField;
import es.onebox.mgmt.seasontickets.enums.SeasonTicketSessionSortableField;
import es.onebox.mgmt.sessions.dto.DayOfWeekDTO;
import es.onebox.mgmt.sessions.dto.SessionSearchFilter;
import es.onebox.mgmt.sessions.dto.SessionsGroupsSearchFilter;
import es.onebox.mgmt.sessions.dto.UpdateSessionTicketContentsBulk;
import es.onebox.mgmt.sessions.enums.SessionField;
import es.onebox.mgmt.sessions.enums.SessionStatus;
import es.onebox.mgmt.tickettemplates.dto.CloneTemplateRequest;
import es.onebox.tracer.okhttp.TracingInterceptor;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MsEventDatasource extends MsEventMapping {

    private static final String STATUS = "status";
    private static final String ENTITY_ID = "entityId";
    private static final String RATE_GROUP_ID_PARAM = "rateGroupId";
    private static final String RATE_GROUP_PRODUCT_ID_PARAM = "rateGroupProductId";
    private static final String SESSION_ID_PARAM = "sessionId";
    private static final String MAPPING_STATUS_PARAM = "mappingStatus";
    private static final String RENEWAL_STATUS_PARAM = "renewalStatus";
    private static final String RENEWAL_SUBSTATUS_PARAM = "renewalSubstatus";
    private static final String RENEWAL_STRICT_STATUS = "strictStatus";
    private static final String SEASON_TICKET_TRANSFER_SEAT = "/season-tickets/{seasonTicketId}/transfer-seat";
    private static final String SEASON_TICKET_REDEMPTION = "/season-tickets/{seasonTicketId}/redemption";
    private static final String RENEWAL_AUTORENEWAL_PARAM = "autoRenewal";
    private static final String BIRTHDAY_PARAM = "birthday";
    private static final String LANGUAGE = "language";
    private static final String TYPE_PARAM = "type";

    private final HttpClient httpClient;


    public static ErrorCode getErrorCode(String msEventErrorCode) {
        return ERROR_CODES.getOrDefault(msEventErrorCode, ApiMgmtErrorCode.GENERIC_ERROR);
    }

    @Autowired
    public MsEventDatasource(@Value("${clients.services.ms-event}") String baseUrl,
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

    public Event getEvent(Long eventId) {
        return httpClient.buildRequest(HttpMethod.GET, EVENTS + EVENT_ID)
                .pathParams(eventId)
                .execute(Event.class);
    }

    public Events getEvents(EventSearchFilter filter) {
        QueryParameters.Builder params = new QueryParameters.Builder();
        params.addQueryParameters(filter);
        return httpClient.buildRequest(HttpMethod.GET, EVENTS)
                .params(params.build())
                .execute(Events.class);
    }

    public Long createEvent(CreateEventData eventData) {
        return httpClient.buildRequest(HttpMethod.POST, EVENTS)
                .body(new ClientRequestBody(eventData))
                .execute(IdDTO.class).getId();
    }

    public void updateEvent(Event updateEvent) {
        httpClient.buildRequest(HttpMethod.PUT, EVENTS + EVENT_ID)
                .pathParams(updateEvent.getId())
                .body(new ClientRequestBody(updateEvent))
                .execute();
    }

    public EventRates getEventRates(Integer eventId) {
        return httpClient.buildRequest(HttpMethod.GET, EVENTS + EVENT_ID + RATES)
                .pathParams(eventId)
                .execute(EventRates.class);
    }

    public EventRates getSessionRates(Integer eventId, Integer sessionId) {
        return httpClient.buildRequest(HttpMethod.GET, EVENTS + EVENT_ID + SESSIONS + SESSION_ID + RATES)
                .pathParams(eventId, sessionId)
                .execute(EventRates.class);
    }

    public Long createEventRate(Integer eventId, Rate rate) {
        return httpClient.buildRequest(HttpMethod.POST, EVENTS + EVENT_ID + RATES)
                .pathParams(eventId)
                .body(new ClientRequestBody(rate))
                .execute(IdDTO.class).getId();
    }

    public void updateEventRates(Long eventId, List<Rate> rates) {
        httpClient.buildRequest(HttpMethod.PUT, EVENTS + EVENT_ID + RATES)
                .pathParams(eventId)
                .body(new ClientRequestBody(rates))
                .execute();
    }

    public void updateEventRate(Long eventId, Long rateId, Rate rate) {
        httpClient.buildRequest(HttpMethod.PUT, EVENTS + EVENT_ID + RATES + RATE_ID)
                .pathParams(eventId, rateId)
                .body(new ClientRequestBody(rate))
                .execute();
    }

    public void deleteEventRate(Long eventId, Long rateId) {
        httpClient.buildRequest(HttpMethod.DELETE, EVENTS + EVENT_ID + RATES + RATE_ID)
                .pathParams(eventId, rateId)
                .execute();
    }

    public List<RateRestricted> getRestrictedRates(Long eventId) {
        return httpClient.buildRequest(HttpMethod.GET, EVENTS + EVENT_ID + RATES + RESTRICTIONS)
                .pathParams(eventId)
                .execute(ListType.of(RateRestricted.class));
    }

    public List<IdNameCodeDTO> getRatesExternalTypes(Long eventId) {
        return httpClient.buildRequest(HttpMethod.GET, EVENTS + EVENT_ID + RATES + EXTERNAL_TYPES)
                .pathParams(eventId)
                .execute(ListType.of(IdNameCodeDTO.class));
    }

    public RateRestrictions getEventRateRestrictions(Long eventId, Long rateId) {
        return httpClient.buildRequest(HttpMethod.GET, EVENTS + EVENT_ID + RATES + RATE_ID + RESTRICTIONS)
                .pathParams(eventId, rateId)
                .execute(RateRestrictions.class);
    }

    public void updateRateRestrictions(Long eventId, Long rateId, UpdateRateRestrictions restriction) {
        httpClient.buildRequest(HttpMethod.POST, EVENTS + EVENT_ID + RATES + RATE_ID + RESTRICTIONS)
                .pathParams(eventId, rateId)
                .body(new ClientRequestBody(restriction))
                .execute();
    }

    public void deleteEventRateRestrictions(Long eventId, Long rateId) {
        httpClient.buildRequest(HttpMethod.DELETE, EVENTS + EVENT_ID + RATES + RATE_ID + RESTRICTIONS)
                .pathParams(eventId, rateId)
                .execute();
    }

    public List<RateRestricted> getSessionRatesRestrictions(Long eventId, Long sessionId) {
        return httpClient.buildRequest(HttpMethod.GET, EVENTS + EVENT_ID + SESSIONS + SESSION_ID + RATES + RESTRICTIONS)
                .pathParams(eventId, sessionId)
                .execute(ListType.of(RateRestricted.class));
    }

    public void upsertSessionRatesRestrictions(Long eventId, Long sessionId, Long rateId, UpdateRateRestrictions restrictions) {
        httpClient.buildRequest(HttpMethod.POST, EVENTS + EVENT_ID + SESSIONS + SESSION_ID + RATES + RATE_ID + RESTRICTIONS)
                .pathParams(eventId, sessionId, rateId)
                .body(new ClientRequestBody(restrictions))
                .execute();
    }

    public void deleteSessionRatesRestrictions(Long eventId, Long sessionId, Long rateId) {
        httpClient.buildRequest(HttpMethod.DELETE, EVENTS + EVENT_ID + SESSIONS + SESSION_ID + RATES + RATE_ID + RESTRICTIONS)
                .pathParams(eventId, sessionId, rateId)
                .execute();
    }

    public Session getSession(Long eventId, Long sessionId) {
        return httpClient.buildRequest(HttpMethod.GET, EVENTS + EVENT_ID + SESSIONS + SESSION_ID)
                .pathParams(eventId, sessionId)
                .execute(Session.class);
    }

    public Sessions getSessions(long operatorId, Long eventId, SessionSearchFilter filter) {

        QueryParameters.Builder params = fillGetSessionsFilter(operatorId, null, filter);

        return httpClient.buildRequest(HttpMethod.GET, EVENTS + EVENT_ID + SESSIONS)
                .pathParams(eventId)
                .params(params.build())
                .execute(Sessions.class);
    }

    public List<LinkedSession> getLinkedSessions(Long eventId, Long sessionId) {
        return httpClient.buildRequest(HttpMethod.GET, EVENTS + EVENT_ID + SESSIONS + SESSION_ID + LINKED_SESSIONS)
                .pathParams(eventId, sessionId)
                .execute(ListType.of(LinkedSession.class));
    }

    public Sessions getSessionsByEventIds(long operatorId, List<Long> eventIds, SessionSearchFilter filter) {

        QueryParameters.Builder params = fillGetSessionsFilter(operatorId, eventIds, filter);

        return httpClient.buildRequest(HttpMethod.GET, SESSIONS)
                .params(params.build())
                .execute(Sessions.class);
    }

    public Session getSessionWithoutEventId(Long sessionId) {
        return httpClient.buildRequest(HttpMethod.GET, SESSIONS + SESSION_ID)
                .pathParams(sessionId)
                .execute(Session.class);
    }

    public Long createSession(Long eventId, CreateSessionData sessionData) {

        return httpClient.buildRequest(HttpMethod.POST, EVENTS + EVENT_ID + SESSIONS)
                .pathParams(eventId)
                .body(new ClientRequestBody(sessionData))
                .execute(IdDTO.class).getId();
    }

    public List<Long> createSessions(Long eventId, List<CreateSessionData> sessionsData) {
        return httpClient.buildRequest(HttpMethod.POST, EVENTS + EVENT_ID + SESSIONS + BULK)
                .pathParams(eventId)
                .body(new ClientRequestBody(sessionsData))
                .execute(ListType.of(Long.class));
    }

    public Long cloneSession(Long eventId, Long sourceSessionId, CloneSessionData sessionData) {
        return httpClient.buildRequest(HttpMethod.POST, EVENTS + EVENT_ID + SESSIONS + SESSION_ID + "/clone")
                .pathParams(eventId, sourceSessionId)
                .body(new ClientRequestBody(sessionData))
                .execute(IdDTO.class).getId();
    }

    public void updateSession(Long eventId, Session updateSession) {
        httpClient.buildRequest(HttpMethod.PUT, EVENTS + EVENT_ID + SESSIONS + SESSION_ID)
                .pathParams(eventId, updateSession.getId())
                .body(new ClientRequestBody(updateSession))
                .execute();
    }

    public SessionBulkUpdateResponse updateSessions(Long eventId, List<Long> ids, Session sessionData, Boolean preview) {
        UpdateSessionsData bulkUpdateData = new UpdateSessionsData();
        bulkUpdateData.setIds(ids);
        bulkUpdateData.setValue(sessionData);
        QueryParameters.Builder params = new QueryParameters.Builder()
                .addQueryParameter("preview", String.valueOf(CommonUtils.isTrue(preview)));
        return httpClient.buildRequest(HttpMethod.PUT, EVENTS + EVENT_ID + SESSIONS + BULK)
                .params(params.build())
                .pathParams(eventId)
                .body(new ClientRequestBody(bulkUpdateData))
                .execute(SessionBulkUpdateResponse.class);
    }

    public ExternalSessionConfig getSessionExternalSessions(Long sessionId) {
        return httpClient.buildRequest(HttpMethod.GET, "/external-sessions" + SESSION_ID + "/config")
                .pathParams(sessionId)
                .execute(ExternalSessionConfig.class);
    }

    public void updateSessionExternalSessions(Long sessionId, SessionExternalSessions updateSession) {
        httpClient.buildRequest(HttpMethod.PUT, "/external-sessions" + SESSION_ID + "/config")
                .pathParams(sessionId)
                .body(new ClientRequestBody(updateSession))
                .execute();
    }

    public List<PreSaleConfigDTO> getSessionPreSale(Long eventId, Long sessionId) {
        return httpClient.buildRequest(HttpMethod.GET, EVENTS + EVENT_ID + SESSIONS + SESSION_ID + PRESALES)
                .pathParams(eventId, sessionId)
                .execute(ListType.of(PreSaleConfigDTO.class));
    }

    public PreSaleConfigDTO createSessionPreSale(Long eventId, Long sessionId, PreSaleConfigDTO preSale) {
        return httpClient.buildRequest(HttpMethod.POST, EVENTS + EVENT_ID + SESSIONS + SESSION_ID + PRESALES)
                .pathParams(eventId, sessionId)
                .body(new ClientRequestBody(preSale))
                .execute(PreSaleConfigDTO.class);
    }

    public void updateSessionPreSale(Long eventId, Long sessionId, Long presalesId, PreSaleConfigDTO preSale) {
        httpClient.buildRequest(HttpMethod.PUT, EVENTS + EVENT_ID + SESSIONS + SESSION_ID + PRESALES + PRESALE_ID)
                .pathParams(eventId, sessionId, presalesId)
                .body(new ClientRequestBody(preSale))
                .execute();
    }

    public void deleteSessionPreSale(Long eventId, Long sessionId, Long presalesId) {
        httpClient.buildRequest(HttpMethod.DELETE, EVENTS + EVENT_ID + SESSIONS + SESSION_ID + PRESALES + PRESALE_ID)
                .pathParams(eventId, sessionId, presalesId)
                .execute();
    }

    public List<SeasonTicketPresaleConfigDTO> getSeasonTicketPresale(Long seasonTicketId) {
        return httpClient.buildRequest(HttpMethod.GET, SEASON_TICKETS + SEASON_TICKET_ID + PRESALES)
                .pathParams(seasonTicketId)
                .execute(ListType.of(SeasonTicketPresaleConfigDTO.class));
    }

    public SeasonTicketPresaleConfigDTO createSeasonTicketPresale(Long seasonTicketId, SeasonTicketPresaleConfigDTO preSale) {
        return httpClient.buildRequest(HttpMethod.POST, SEASON_TICKETS + SEASON_TICKET_ID + PRESALES)
                .pathParams(seasonTicketId)
                .body(new ClientRequestBody(preSale))
                .execute(SeasonTicketPresaleConfigDTO.class);
    }

    public void updateSeasonTicketPresale(Long seasonTicketId, Long presalesId, SeasonTicketPresaleConfigDTO preSale) {
        httpClient.buildRequest(HttpMethod.PUT, SEASON_TICKETS + SEASON_TICKET_ID + PRESALES + PRESALE_ID)
                .pathParams(seasonTicketId, presalesId)
                .body(new ClientRequestBody(preSale))
                .execute();
    }

    public void deleteSeasonTicketPresale(Long seasonTicketId, Long presalesId) {
        httpClient.buildRequest(HttpMethod.DELETE, SEASON_TICKETS + SEASON_TICKET_ID + PRESALES + PRESALE_ID)
                .pathParams(seasonTicketId, presalesId)
                .execute();
    }

    public List<VenueTemplatePrice> getVenueTemplatePrices(Long eventId, Long templateId, List<Long> sessionIdList, List<Integer> rateGroupList, List<Integer> rateGroupProductList) {
        QueryParameters.Builder params = new QueryParameters.Builder();
        if (CollectionUtils.isNotEmpty(sessionIdList)) {
            sessionIdList.forEach(id -> params.addQueryParameter(SESSION_ID_PARAM, id));
        }
        if (CollectionUtils.isNotEmpty(rateGroupList)) {
            rateGroupList.forEach(id -> params.addQueryParameter(RATE_GROUP_ID_PARAM, id));
        }
        if (CollectionUtils.isNotEmpty(rateGroupProductList)) {
            rateGroupProductList.forEach(id -> params.addQueryParameter(RATE_GROUP_PRODUCT_ID_PARAM, id));
        }

        return httpClient.buildRequest(HttpMethod.GET, VENUE_TEMPLATE_PRICES)
                .pathParams(eventId, templateId)
                .params(params.build())
                .execute(ListType.of(VenueTemplatePrice.class));
    }

    public void updateVenueTemplatePrices(Long eventId, Long templateId, List<VenueTemplatePrice> prices) {
        httpClient.buildRequest(HttpMethod.PUT, VENUE_TEMPLATE_PRICES)
                .pathParams(eventId, templateId)
                .body(new ClientRequestBody(prices))
                .execute();
    }

    public List<EventCommunicationElement> getEventCommunicationElements(Long eventId, CommunicationElementFilter<EventTagType> filter) {
        return httpClient.buildRequest(HttpMethod.GET, EVENT_COMMUNICATION_ELEMENTS)
                .pathParams(eventId)
                .params(new QueryParameters.Builder().addQueryParameters(filter).build())
                .execute(ListType.of(EventCommunicationElement.class));
    }

    public List<EmailCommunicationElement> getEventEmailCommunicationElements(Long eventId, CommunicationElementFilter<EmailCommunicationElementTagType> filter) {
        return httpClient.buildRequest(HttpMethod.GET, EMAIL_COMMUNICATION_ELEMENTS)
                .pathParams(eventId)
                .params(new QueryParameters.Builder().addQueryParameters(filter).build())
                .execute(ListType.of(EmailCommunicationElement.class));
    }

    public void updateEventEmailCommunicationElements(Long eventId, List<EmailCommunicationElement> elements) {
        httpClient.buildRequest(HttpMethod.POST, EMAIL_COMMUNICATION_ELEMENTS)
                .pathParams(eventId)
                .body(new ClientRequestBody(elements))
                .execute();
    }

    public List<TicketCommunicationElement> getEventTicketCommunicationElements(Long eventId, CommunicationElementFilter<?> filter, TicketCommunicationElementCategory type) {
        return httpClient.buildRequest(HttpMethod.GET, TICKET_COMMUNICATION_ELEMENTS)
                .pathParams(eventId, type)
                .params(new QueryParameters.Builder().addQueryParameters(filter).build())
                .execute(ListType.of(TicketCommunicationElement.class));
    }

    public void updateEventTicketCommunicationElements(Long eventId, Set<TicketCommunicationElement> elements, TicketCommunicationElementCategory type) {
        httpClient.buildRequest(HttpMethod.POST, TICKET_COMMUNICATION_ELEMENTS)
                .pathParams(eventId, type)
                .body(new ClientRequestBody(elements))
                .execute();
    }

    public void deleteEventTicketCommunicationElement(Long eventId, String language, String tag, TicketCommunicationElementCategory type) {
        httpClient.buildRequest(HttpMethod.DELETE, TICKET_COMMUNICATION_ELEMENTS + TICKET_COMMUNICATION_URL)
                .pathParams(eventId, type, language, tag)
                .execute();
    }

    public void deleteEventEmailCommunicationElement(Long eventId, String language, EmailCommunicationElementTagType type) {
        httpClient.buildRequest(HttpMethod.DELETE, EMAIL_COMMUNICATION_ELEMENTS + "/languages/{language}/types/{type}")
                .pathParams(eventId, language, type)
                .execute();
    }

    public void updateEventCommunicationElements(Long eventId, List<EventCommunicationElement> elements) {
        httpClient.buildRequest(HttpMethod.POST, EVENT_COMMUNICATION_ELEMENTS)
                .pathParams(eventId)
                .body(new ClientRequestBody(elements))
                .execute();
    }

    public void updateSessionCommunicationElements(Long eventId, Long sessionId, List<EventCommunicationElement> elements) {
        httpClient.buildRequest(HttpMethod.POST, SESSION_COMMUNICATION_ELEMENTS)
                .pathParams(eventId, sessionId)
                .body(new ClientRequestBody(elements))
                .execute();
    }

    public void updateSessionCommunicationElementsBulk(Long eventId, EventCommunicationElementBulk data) {
        httpClient.buildRequest(HttpMethod.POST, SESSION_COMMUNICATION_ELEMENTS_BULK)
                .pathParams(eventId)
                .body(new ClientRequestBody(data))
                .execute();
    }

    public void deleteSessionCommunicationElementsBulk(Long eventId, String language, List<Long> sessionIds) {
        QueryParameters.Builder params = new QueryParameters.Builder();
        sessionIds.forEach(id -> params.addQueryParameter(SESSION_ID_PARAM, id));

        httpClient.buildRequest(HttpMethod.DELETE, SESSION_COMMUNICATION_ELEMENTS_BULK + LANGUAGES + "/{language}")
                .pathParams(eventId, language)
                .params(params.build())
                .execute();
    }

    public List<EventCommunicationElement> getSessionCommunicationElements(Long eventId, Long sessionId, CommunicationElementFilter<?> filter) {
        return httpClient
                .buildRequest(HttpMethod.GET, SESSION_COMMUNICATION_ELEMENTS)
                .pathParams(eventId, sessionId)
                .params(new QueryParameters.Builder().addQueryParameters(filter).build())
                .execute(ListType.of(EventCommunicationElement.class));

    }

    public List<TicketCommunicationElement> getSessionTicketCommunicationElements(Long eventId, Long sessionId, CommunicationElementFilter<?> filter, TicketCommunicationElementCategory type) {
        return httpClient.buildRequest(HttpMethod.GET, SESSION_TICKET_COMMUNICATION_ELEMENTS)
                .pathParams(eventId, sessionId, type)
                .params(new QueryParameters.Builder().addQueryParameters(filter).build())
                .execute(ListType.of(TicketCommunicationElement.class));
    }

    public void deleteSessionTicketCommunicationElements(Long eventId, Long sessionId, String language, String tag,
                                                         TicketCommunicationElementCategory type) {
        httpClient.buildRequest(HttpMethod.DELETE, SESSION_TICKET_COMMUNICATION_ELEMENTS + TICKET_COMMUNICATION_URL)
                .pathParams(eventId, sessionId, type, language, tag)
                .execute();
    }

    public void deleteSessionTicketCommunicationElementBulk(final Long eventId, final List<Long> sessionIds, final String languageCode,
                                                            final String tag, TicketCommunicationElementCategory type) {
        QueryParameters.Builder params = new QueryParameters.Builder();
        sessionIds.forEach(id -> params.addQueryParameter(SESSION_ID_PARAM, id));

        httpClient.buildRequest(HttpMethod.DELETE, SESSION_TICKET_COMMUNICATION_ELEMENTS_BULK + TICKET_COMMUNICATION_URL)
                .pathParams(eventId, type, languageCode, tag)
                .params(params.build())
                .execute();
    }

    public void deleteImageSessionTicketCommunicationElementsBulk(final Long eventId, final List<Long> sessionIds, final String languageCode,
                                                                  TicketCommunicationElementCategory type) {
        QueryParameters.Builder params = new QueryParameters.Builder();
        sessionIds.forEach(id -> params.addQueryParameter(SESSION_ID_PARAM, id));

        httpClient.buildRequest(HttpMethod.DELETE, SESSION_TICKET_COMMUNICATION_ELEMENTS_BULK + "/images/languages/{language}")
                .pathParams(eventId, type, languageCode)
                .params(params.build())
                .execute();
    }

    public EventChannels getEventChannels(Long eventId, EventChannelSearchFilter filter) {
        QueryParameters.Builder params = new QueryParameters.Builder();
        if (filter != null) {
            ConverterUtils.checkSortFields(filter.getSort(), params, EventChannelField::byName);
            ConverterUtils.checkFilterFields(filter.getFields(), params, EventChannelField::byName);
            addLimitAndOffset(filter.getLimit(), filter.getOffset(), params);
            if (StringUtils.isNotEmpty(filter.getQ())) {
                params.addQueryParameter("q", filter.getQ());
            }
            if (CollectionUtils.isNotEmpty(filter.getType())) {
                filter.getType().forEach(type -> params.addQueryParameter("subtype", ChannelSubtype.getById(type.getId())));
            }
            if (filter.getEntityId() != null) {
                params.addQueryParameter(ENTITY_ID, filter.getEntityId());
            }
            if (CollectionUtils.isNotEmpty(filter.getRequestStatusType())) {
                filter.getRequestStatusType().forEach(type -> params.addQueryParameter("requestStatus", RequestStatusType.getById(type.getId())));
            }
            if (CollectionUtils.isNotEmpty(filter.getSaleStatusType())) {
                filter.getSaleStatusType().forEach(type -> params.addQueryParameter("saleStatus", SaleStatusType.getById(type.getId())));
            }
            if (CollectionUtils.isNotEmpty(filter.getReleaseStatusType())) {
                filter.getReleaseStatusType().forEach(type -> params.addQueryParameter("releaseStatus", ReleaseStatusType.getById(type.getId())));
            }
        }

        return httpClient.buildRequest(HttpMethod.GET, EVENTS + EVENT_ID + CHANNELS)
                .pathParams(eventId)
                .params(params.build())
                .execute(EventChannels.class);
    }

    public void deleteEventChannel(Long eventId, Long channelId) {
        httpClient.buildRequest(HttpMethod.DELETE, EVENTS + EVENT_ID + CHANNELS + "/{channelId}")
                .pathParams(eventId, channelId)
                .execute();
    }

    public void requestChannelApproval(Long eventId, Long channelId, Long userId) {
        RequestSalesEventChannel rsec = new RequestSalesEventChannel();
        rsec.setUserId(userId);
        httpClient.buildRequest(HttpMethod.POST, EVENTS + EVENT_ID + CHANNELS + CHANNEL_ID + "/request-approval")
                .pathParams(eventId, channelId)
                .body(new ClientRequestBody(rsec))
                .execute();
    }

    public SeasonTicket getSeasonTicket(Long seasonTicketId) {
        return httpClient.buildRequest(HttpMethod.GET, SEASON_TICKETS + SEASON_TICKET_ID)
                .pathParams(seasonTicketId)
                .execute(SeasonTicket.class);
    }

    public SeasonTickets getSeasonTickets(SeasonTicketFilter filter) {
        QueryParameters.Builder params = new QueryParameters.Builder();
        params.addQueryParameters(filter);
        return httpClient.buildRequest(HttpMethod.GET, SEASON_TICKETS)
                .params(params.build())
                .execute(SeasonTickets.class);
    }

    public Long createSeasonTicket(CreateSeasonTicketData createSeasonTicketData) {
        return httpClient.buildRequest(HttpMethod.POST, SEASON_TICKETS)
                .body(new ClientRequestBody(createSeasonTicketData))
                .execute(IdDTO.class).getId();
    }

    public void updateSeasonTicket(Long seasonTicketId, SeasonTicket seasonTicket) {
        httpClient.buildRequest(HttpMethod.PUT, SEASON_TICKETS + SEASON_TICKET_ID)
                .pathParams(seasonTicketId)
                .body(new ClientRequestBody(seasonTicket))
                .execute();
    }

    public EventChannel getEventChannel(Long eventId, Long channelId) {
        return httpClient.buildRequest(HttpMethod.GET, EVENT_CHANNEL)
                .pathParams(eventId, channelId)
                .execute(EventChannel.class);
    }

    public void createEventChannel(Long eventId, Long channelId) {
        CreateEventChannelDTO createEventChannel = new CreateEventChannelDTO();
        createEventChannel.setChannelId(channelId);

        httpClient.buildRequest(HttpMethod.POST, EVENT_CHANNELS)
                .pathParams(eventId)
                .body(new ClientRequestBody(createEventChannel))
                .execute();
    }

    public void updateEventChannel(Long eventId, Long channelId, UpdateEventChannel updateEventChannel) {
        httpClient.buildRequest(HttpMethod.PUT, EVENT_CHANNEL)
                .pathParams(eventId, channelId)
                .body(new ClientRequestBody(updateEventChannel))
                .execute();
    }

    public SeasonTicketRates getSeasonTicketRates(Integer seasonTicketId) {
        return httpClient.buildRequest(HttpMethod.GET, SEASON_TICKETS + SEASON_TICKET_ID + RATES)
                .pathParams(seasonTicketId)
                .execute(SeasonTicketRates.class);
    }

    public Long createSeasonTicketRate(Integer seasonTicketId, SeasonTicketRate rate) {

        return httpClient.buildRequest(HttpMethod.POST, SEASON_TICKETS + SEASON_TICKET_ID + RATES)
                .pathParams(seasonTicketId)
                .body(new ClientRequestBody(rate))
                .execute(IdDTO.class).getId();
    }

    public void updateSeasonTicketRates(Long seasonTicketId, List<SeasonTicketRate> rates) {
        httpClient.buildRequest(HttpMethod.PUT, SEASON_TICKETS + SEASON_TICKET_ID + RATES)
                .pathParams(seasonTicketId)
                .body(new ClientRequestBody(rates))
                .execute();
    }

    public void updateSeasonTicketRate(Long seasonTicketId, Long rateId, SeasonTicketRate rate) {
        httpClient.buildRequest(HttpMethod.PUT, SEASON_TICKETS + SEASON_TICKET_ID + RATES + RATE_ID)
                .pathParams(seasonTicketId, rateId)
                .body(new ClientRequestBody(rate))
                .execute();
    }

    public void deleteSeasonTicketRate(Long seasonTicketId, Long rateId) {
        httpClient.buildRequest(HttpMethod.DELETE, SEASON_TICKETS + SEASON_TICKET_ID + RATES + RATE_ID)
                .pathParams(seasonTicketId, rateId)
                .execute();
    }

    public SeasonTicketDatasourceStatus getSeasonTicketStatus(Long seasonTicketId) {
        return httpClient.buildRequest(HttpMethod.GET, SEASON_TICKETS + SEASON_TICKET_ID + SEASON_TICKET_STATUS)
                .pathParams(seasonTicketId)
                .execute(SeasonTicketDatasourceStatus.class);
    }

    public void updateSeasonTicketStatus(Long seasonTicketId, UpdateSeasonTicketStatus updateSeasonTicketStatus) {
        httpClient.buildRequest(HttpMethod.PUT, SEASON_TICKETS + SEASON_TICKET_ID + SEASON_TICKET_STATUS)
                .pathParams(seasonTicketId)
                .body(new ClientRequestBody(updateSeasonTicketStatus))
                .execute();
    }

    public List<VenueTemplatePrice> getSeasonTicketPrices(Long seasonTicketId) {
        return httpClient.buildRequest(HttpMethod.GET, SEASON_TICKET_PRICES)
                .pathParams(seasonTicketId)
                .execute(ListType.of(VenueTemplatePrice.class));
    }

    public void updateSeasonTicketPrices(Long seasonTicketId, List<VenueTemplatePrice> prices) {
        httpClient.buildRequest(HttpMethod.PUT, SEASON_TICKET_PRICES)
                .pathParams(seasonTicketId)
                .body(new ClientRequestBody(prices))
                .execute();
    }

    public void setSurcharge(long eventId, List<EventSurcharge> msEventSurchargeRequestDTO) {
        httpClient.buildRequest(HttpMethod.POST, EVENTS + EVENT_ID + SURCHARGES)
                .pathParams(eventId)
                .body(new ClientRequestBody(msEventSurchargeRequestDTO))
                .execute();
    }

    public void deleteSurcharge(long eventId) {
        httpClient.buildRequest(HttpMethod.DELETE, EVENTS + EVENT_ID + SURCHARGES)
                .pathParams(eventId)
                .execute();
    }

    public List<EventSurcharge> getSurcharges(Long eventId, List<SurchargeTypeDTO> types) {
        QueryParameters.Builder params = new QueryParameters.Builder();
        if (!CommonUtils.isEmpty(types)) {
            for (SurchargeTypeDTO surchargeType : types) {
                params.addQueryParameter("type", surchargeType.toString());
            }
        }
        return httpClient.buildRequest(HttpMethod.GET, EVENTS + EVENT_ID + SURCHARGES)
                .pathParams(eventId)
                .params(params.build())
                .execute(ListType.of(EventSurcharge.class));
    }

    public void deleteSeasonTicket(Long seasonTicketId) {
        httpClient.buildRequest(HttpMethod.DELETE, SEASON_TICKETS + SEASON_TICKET_ID)
                .pathParams(seasonTicketId)
                .execute();
    }

    public Tour getTour(Long tourId, TourEventFilter filter) {
        return httpClient.buildRequest(HttpMethod.GET, TOURS + TOUR_ID)
                .pathParams(tourId)
                .params(new QueryParameters.Builder().addQueryParameters(filter).build())
                .execute(Tour.class);
    }

    public Tours getTours(TourFilter filter) {
        QueryParameters.Builder params = new QueryParameters.Builder();

        params.addQueryParameters(filter);

        return httpClient.buildRequest(HttpMethod.GET, TOURS)
                .params(params.build())
                .execute(Tours.class);
    }

    public Long createTour(String tourName, Long entityId) {
        Tour tour = new Tour();
        tour.setName(tourName);
        tour.setEntity(new IdNameDTO(entityId));
        return httpClient.buildRequest(HttpMethod.POST, TOURS)
                .body(new ClientRequestBody(tour))
                .execute(IdDTO.class).getId();
    }

    public void updateTour(Long tourId, String tourName, TourStatus status) {
        Tour tour = new Tour();
        tour.setName(tourName);
        if (status != null) {
            tour.setStatus(es.onebox.mgmt.datasources.ms.event.dto.tour.TourStatus.valueOf(status.name()));
        }
        httpClient.buildRequest(HttpMethod.PUT, TOURS + TOUR_ID)
                .pathParams(tourId)
                .body(new ClientRequestBody(tour))
                .execute();
    }

    public void deleteTour(Long tourId) {
        Tour tour = new Tour();
        tour.setStatus(es.onebox.mgmt.datasources.ms.event.dto.tour.TourStatus.DELETED);
        httpClient.buildRequest(HttpMethod.PUT, TOURS + TOUR_ID)
                .pathParams(tourId)
                .body(new ClientRequestBody(tour))
                .execute();
    }

    public List<EventCommunicationElement> getTourCommunicationElements(Long eventId, CommunicationElementFilter<?> filter) {
        return httpClient.buildRequest(HttpMethod.GET, TOUR_COMMUNICATION_ELEMENTS)
                .pathParams(eventId)
                .params(new QueryParameters.Builder().addQueryParameters(filter).build())
                .execute(ListType.of(EventCommunicationElement.class));
    }

    public void updateTourCommunicationElements(Long eventId, List<EventCommunicationElement> elements) {
        httpClient.buildRequest(HttpMethod.POST, TOUR_COMMUNICATION_ELEMENTS)
                .pathParams(eventId)
                .body(new ClientRequestBody(elements))
                .execute();
    }

    public TicketTemplate getTicketTemplate(Long ticketTemplateId) {
        return httpClient.buildRequest(HttpMethod.GET, TICKET_TEMPLATES + TICKET_TEMPLATE_ID)
                .pathParams(ticketTemplateId)
                .execute(TicketTemplate.class);
    }

    public TicketTemplates getTicketTemplates(TicketTemplatesFilter filter) {
        QueryParameters.Builder params = new QueryParameters.Builder();
        params.addQueryParameters(filter);
        return httpClient.buildRequest(HttpMethod.GET, TICKET_TEMPLATES)
                .params(params.build())
                .execute(TicketTemplates.class);
    }

    public Long createTicketTemplate(String name, Long entityId, Long entityDefaultLangId, Long designId) {
        TicketTemplate template = new TicketTemplate();
        template.setName(name);
        template.setEntity(new IdNameDTO(entityId));
        template.setDefaultLanguage(entityDefaultLangId);
        template.setDesign(new TicketTemplateDesign(designId));
        return httpClient.buildRequest(HttpMethod.POST, TICKET_TEMPLATES)
                .body(new ClientRequestBody(template))
                .execute(IdDTO.class).getId();
    }

    public void updateTicketTemplate(Long ticketTemplateId, TicketTemplate template) {
        httpClient.buildRequest(HttpMethod.PUT, TICKET_TEMPLATES + TICKET_TEMPLATE_ID)
                .pathParams(ticketTemplateId)
                .body(new ClientRequestBody(template))
                .execute();
    }

    public Long cloneTicketTemplate(Long ticketTemplateId, CloneTemplateRequest body) {
        return httpClient.buildRequest(HttpMethod.POST, TICKET_TEMPLATE_CLONE).pathParams(ticketTemplateId)
                .body(new ClientRequestBody(body)).execute(IdDTO.class).getId();
    }

    public void deleteTicketTemplate(Long ticketTemplateId) {
        httpClient.buildRequest(HttpMethod.DELETE, TICKET_TEMPLATES + TICKET_TEMPLATE_ID)
                .pathParams(ticketTemplateId)
                .execute();
    }

    public List<TicketTemplateCommunicationElement> getTicketTemplateCommunicationElements(Long ticketTemplateId,
                                                                                           CommunicationElementFilter<TicketTemplateTagType> filter) {
        return httpClient.buildRequest(HttpMethod.GET, TICKET_TEMPLATE_COMMUNICATION_ELEMENTS)
                .pathParams(ticketTemplateId)
                .params(new QueryParameters.Builder().addQueryParameters(filter).build())
                .execute(ListType.of(TicketTemplateCommunicationElement.class));
    }

    public void updateTicketTemplateCommunicationElements(Long ticketTemplateId, List<TicketTemplateCommunicationElement> elements) {
        httpClient.buildRequest(HttpMethod.POST, TICKET_TEMPLATE_COMMUNICATION_ELEMENTS)
                .pathParams(ticketTemplateId)
                .body(new ClientRequestBody(elements))
                .execute();
    }

    public List<TicketTemplateLiteral> getTicketTemplateLiterals(Long ticketTemplateId,
                                                                 TicketTemplateLiteralElementFilter filter) {
        return httpClient.buildRequest(HttpMethod.GET, TICKET_TEMPLATE_LITERALS)
                .pathParams(ticketTemplateId)
                .params(new QueryParameters.Builder().addQueryParameters(filter).build())
                .execute(ListType.of(TicketTemplateLiteral.class));
    }

    public void updateTicketTemplateLiterals(Long ticketTemplateId, List<TicketTemplateLiteral> elements) {
        httpClient.buildRequest(HttpMethod.POST, TICKET_TEMPLATE_LITERALS)
                .pathParams(ticketTemplateId)
                .body(new ClientRequestBody(elements))
                .execute();
    }

    public List<TicketTemplateDesign> getTicketTemplateModels() {
        return httpClient.buildRequest(HttpMethod.GET, TICKET_TEMPLATES + TICKET_TEMPLATE_DESIGNS)
                .execute(ListType.of(TicketTemplateDesign.class));
    }

    public Long createEventTier(Long eventId, Tier tier) {
        return httpClient.buildRequest(HttpMethod.POST, EVENTS + EVENT_ID + TIERS)
                .pathParams(eventId)
                .body(new ClientRequestBody(tier))
                .execute(IdDTO.class).getId();
    }

    public Tiers getEventTiers(Long eventId, Long venueTemplateId, Boolean active, Long limit, Long offset) {
        QueryParameters.Builder params = new QueryParameters.Builder()
                .addQueryParameter("venueTemplateId", venueTemplateId)
                .addQueryParameter("active", active);
        addLimitAndOffset(limit, offset, params);
        return httpClient.buildRequest(HttpMethod.GET, EVENTS + EVENT_ID + TIERS)
                .pathParams(eventId)
                .params(params.build())
                .execute(Tiers.class);
    }

    public TierExtended getEventTier(Long eventId, Long tierId) {
        return httpClient.buildRequest(HttpMethod.GET, EVENTS + EVENT_ID + TIERS + TIER_ID)
                .pathParams(eventId, tierId)
                .execute(TierExtended.class);
    }

    public Tier updateEventTier(Long eventId, Long tierId, Tier tier) {
        return httpClient.buildRequest(HttpMethod.PUT, EVENTS + EVENT_ID + TIERS + TIER_ID)
                .pathParams(eventId, tierId)
                .body(new ClientRequestBody(tier))
                .execute(Tier.class);
    }

    public void deleteEventTier(Long eventId, Long tierId) {
        httpClient.buildRequest(HttpMethod.DELETE, EVENTS + EVENT_ID + TIERS + TIER_ID)
                .pathParams(eventId, tierId)
                .execute();
    }

    public void deleteEventTierLimit(Long eventId, Long tierId) {
        httpClient.buildRequest(HttpMethod.DELETE, EVENTS + EVENT_ID + TIERS + TIER_ID + LIMIT)
                .pathParams(eventId, tierId)
                .execute();
    }

    public SeasonTicketSessions getSeasonTicketCandidateSessions(SeasonTicketSessionsSearchFilter filter, Long seasonTicketId) {
        QueryParameters.Builder params = new QueryParameters.Builder()
                .addQueryParameter("eventId", filter.getEventId())
                .addQueryParameter("assignationStatus", filter.getAssignationStatus())
                .addQueryParameter("sessionId", filter.getSessionId());
        addDateParameters(filter.getStartDate(), null, params);
        addLimitAndOffset(filter.getLimit(), filter.getOffset(), params);
        ConverterUtils.addFreeSearch(filter.getFreeSearch(), params);
        ConverterUtils.checkSortFields(filter.getSort(), params, SeasonTicketSessionSortableField::byName);

        return httpClient.buildRequest(HttpMethod.GET, SEASON_TICKET_SESSIONS)
                .params(params.build())
                .pathParams(seasonTicketId)
                .execute(SeasonTicketSessions.class);
    }

    public SeasonTicketSessionsEventList getSeasonTicketSessionsEvents(Long seasonTicketId, SeasonTicketSessionsEventsFilter filter) {

        QueryParameters.Builder params = new QueryParameters.Builder();
        ConverterUtils.addFreeSearch(filter.getFreeSearch(), params);
        addLimitAndOffset(filter.getLimit(), filter.getOffset(), params);

        return httpClient.buildRequest(HttpMethod.GET, SEASON_TICKET_SESSIONS + EVENTS)
                .pathParams(seasonTicketId)
                .params(params.build())
                .execute(SeasonTicketSessionsEventList.class);
    }

    public SeasonTicketSessionValidationMsEventResponse verifySessionsFromSeasonTicket(Long seasonTicketId,
                                                                                       Long sessionId,
                                                                                       Boolean includeSeats) {

        QueryParameters.Builder params = new QueryParameters.Builder();
        params.addQueryParameter("includeSeats", includeSeats);
        return httpClient.buildRequest(HttpMethod.GET, SEASON_TICKET_SESSIONS + SESSION_ID + VALIDATIONS)
                .pathParams(seasonTicketId, sessionId)
                .params(params.build())
                .execute(SeasonTicketSessionValidationMsEventResponse.class);
    }

    public void createEventTierSaleGroup(Long eventId, Long tierId, CreateEventTierSaleGroup createEventTierSaleGroup) {
        httpClient.buildRequest(HttpMethod.POST, EVENTS + EVENT_ID + TIERS + TIER_ID + SALE_GROUPS)
                .pathParams(eventId, tierId)
                .body(new ClientRequestBody(createEventTierSaleGroup))
                .execute();
    }

    public void updateEventTierSaleGroup(Long eventId, Long tierId, Long saleGroupId,
                                         UpdateEventTierSaleGroup updateEventTierSaleGroup) {
        httpClient.buildRequest(HttpMethod.PUT, EVENTS + EVENT_ID + TIERS + TIER_ID + SALE_GROUPS + SALE_GROUP_ID)
                .pathParams(eventId, tierId, saleGroupId)
                .body(new ClientRequestBody(updateEventTierSaleGroup))
                .execute();
    }

    public void deleteEventTierSaleGroup(Long eventId, Long tierId, Long saleGroupId) {
        httpClient.buildRequest(HttpMethod.DELETE, EVENTS + EVENT_ID + TIERS + TIER_ID + SALE_GROUPS + SALE_GROUP_ID)
                .pathParams(eventId, tierId, saleGroupId)
                .execute();
    }

    public void upsertTierCommElements(Long eventId, Long tierId, List<TierCommunicationElement> commElements) {
        httpClient.buildRequest(HttpMethod.POST, EVENTS + EVENT_ID + TIERS + TIER_ID + COMM_ELEMENTS)
                .pathParams(eventId, tierId)
                .body(new ClientRequestBody(commElements))
                .execute();
    }

    public List<TierCommunicationElement> getTierCommElements(Long eventId, Long tierId, TierChannelContentFilter filter) {
        QueryParameters params = new QueryParameters.Builder().addQueryParameters(filter).build();
        return httpClient.buildRequest(HttpMethod.GET, EVENTS + EVENT_ID + TIERS + TIER_ID + COMM_ELEMENTS)
                .pathParams(eventId, tierId)
                .params(params)
                .execute(ListType.of(TierCommunicationElement.class));
    }


    public AssignSessionResponse assignSession(Long seasonTicketId, Long targetSessionId, Boolean updateBarcodes) {
        AssignSessionRequest request = new AssignSessionRequest();
        request.setSessionId(targetSessionId);
        request.setUpdateBarcodes(updateBarcodes);

        return httpClient.buildRequest(HttpMethod.POST, SEASON_TICKET_SESSIONS)
                .pathParams(seasonTicketId)
                .body(new ClientRequestBody(request))
                .execute(AssignSessionResponse.class);
    }

    public UnAssignSessionResponse unAssignSession(Long seasonTicketId, Long targetSessionId, Boolean updateBarcodes) {
        QueryParameters.Builder params = new QueryParameters.Builder().addQueryParameter("updateBarcodes", updateBarcodes);
        return httpClient.buildRequest(HttpMethod.DELETE, SEASON_TICKET_SESSIONS_SESSION)
                .pathParams(seasonTicketId, targetSessionId)
                .params(params.build())
                .execute(UnAssignSessionResponse.class);
    }

    public void upsertSaleConstraints(Long eventId, Long sessionId, SessionSaleConstraint request) {
        httpClient.buildRequest(HttpMethod.PUT, EVENTS + EVENT_ID + SESSIONS + SESSION_ID + SALE_CONSTRAINTS)
                .pathParams(eventId, sessionId)
                .body(new ClientRequestBody(request))
                .execute();
    }

    public SessionSaleConstraint getSaleConstraints(Long eventId, Long sessionId) {
        return httpClient.buildRequest(HttpMethod.GET, EVENTS + EVENT_ID + SESSIONS + SESSION_ID + SALE_CONSTRAINTS)
                .pathParams(eventId, sessionId)
                .execute(SessionSaleConstraint.class);

    }

    public List<EventCommunicationElement> getSeasonTicketCommunicationElements(Long seasonTicketId, CommunicationElementFilter<EventTagType> filter) {
        return httpClient.buildRequest(HttpMethod.GET, SEASON_TICKET_COMMUNICATION_ELEMENTS)
                .pathParams(seasonTicketId)
                .params(new QueryParameters.Builder().addQueryParameters(filter).build())
                .execute(ListType.of(EventCommunicationElement.class));
    }

    public void updateSeasonTicketCommunicationElements(Long seasonTicketId, List<EventCommunicationElement> elements) {
        httpClient.buildRequest(HttpMethod.POST, SEASON_TICKET_COMMUNICATION_ELEMENTS)
                .pathParams(seasonTicketId)
                .body(new ClientRequestBody(elements))
                .execute();
    }

    public List<EmailCommunicationElement> getSeasonTicketEmailCommunicationElements(Long seasonTicketId, CommunicationElementFilter<EmailCommunicationElementTagType> filter) {
        return httpClient.buildRequest(HttpMethod.GET, SEASON_TICKET_EMAIL_COMMUNICATION_ELEMENTS)
                .pathParams(seasonTicketId)
                .params(new QueryParameters.Builder().addQueryParameters(filter).build())
                .execute(ListType.of(EmailCommunicationElement.class));
    }

    public void updateSeasonTicketEmailCommunicationElements(Long seasonTicketId, List<EmailCommunicationElement> elements) {
        httpClient.buildRequest(HttpMethod.POST, SEASON_TICKET_EMAIL_COMMUNICATION_ELEMENTS)
                .pathParams(seasonTicketId)
                .body(new ClientRequestBody(elements))
                .execute();
    }

    public void deleteSeasonTicketEmailCommunicationElement(Long seasonTicketId, String language, EmailCommunicationElementTagType type) {
        httpClient.buildRequest(HttpMethod.DELETE, SEASON_TICKET_EMAIL_COMMUNICATION_ELEMENTS + "/languages/{language}/types/{type}")
                .pathParams(seasonTicketId, language, type)
                .execute();
    }

    public List<TicketCommunicationElement> getSeasonTicketTicketCommunicationElements(Long seasonTicketId, CommunicationElementFilter<?> filter, TicketCommunicationElementCategory type) {
        return httpClient.buildRequest(HttpMethod.GET, SEASON_TICKET_TICKET_COMMUNICATION_ELEMENTS)
                .pathParams(seasonTicketId, type)
                .params(new QueryParameters.Builder().addQueryParameters(filter).build())
                .execute(ListType.of(TicketCommunicationElement.class));
    }

    public void updateSeasonTicketTicketCommunicationElements(Long seasonTicketId, Set<TicketCommunicationElement> elements, TicketCommunicationElementCategory type) {
        httpClient.buildRequest(HttpMethod.POST, SEASON_TICKET_TICKET_COMMUNICATION_ELEMENTS)
                .pathParams(seasonTicketId, type)
                .body(new ClientRequestBody(elements))
                .execute();
    }

    public void deleteSeasonTicketTicketCommunicationElement(Long seasonTicketId, String language, String tag, TicketCommunicationElementCategory type) {
        httpClient.buildRequest(HttpMethod.DELETE, SEASON_TICKET_TICKET_COMMUNICATION_ELEMENTS + TICKET_COMMUNICATION_URL)
                .pathParams(seasonTicketId, type, language, tag)
                .execute();
    }

    public List<SeasonTicketSurcharge> getSeasonTicketSurcharges(Long seasonTicketId, List<SurchargeTypeDTO> types) {
        QueryParameters.Builder params = new QueryParameters.Builder();
        if (!CommonUtils.isEmpty(types)) {
            for (SurchargeTypeDTO surchargeType : types) {
                params.addQueryParameter("type", surchargeType.toString());
            }
        }
        return httpClient.buildRequest(HttpMethod.GET, SEASON_TICKETS + SEASON_TICKET_ID + SURCHARGES)
                .pathParams(seasonTicketId)
                .params(params.build())
                .execute(ListType.of(SeasonTicketSurcharge.class));
    }

    public void setSeasonTicketSurcharge(Long seasonTicketId, List<EventSurcharge> msEventSurchargeRequestDTO) {
        httpClient.buildRequest(HttpMethod.POST, SEASON_TICKETS + SEASON_TICKET_ID + SURCHARGES)
                .pathParams(seasonTicketId)
                .body(new ClientRequestBody(msEventSurchargeRequestDTO))
                .execute();
    }

    public List<VenueConfigPricesSimulation> getPriceSimulation(Long saleRequestId) {
        return httpClient.buildRequest(HttpMethod.GET, PRICE_SIMULATION_EVENT_CHANNEL)
                .pathParams(saleRequestId)
                .execute(ListType.of(VenueConfigPricesSimulation.class));
    }

    public List<VenueConfigPricesSimulation> getPriceSimulation(Long eventId, Long channelId) {
        return httpClient.buildRequest(HttpMethod.GET, PRICE_SIMULATION_CHANNEL_EVENT)
                .pathParams(eventId, channelId)
                .execute(ListType.of(VenueConfigPricesSimulation.class));
    }

    public EventPassbookTemplates getEventPassbookTemplateCode(Long eventId) {
        return httpClient.buildRequest(HttpMethod.GET, EVENTS + EVENT_ID + PASSBOOK_TEMPLATE_CODE)
                .pathParams(eventId)
                .execute(EventPassbookTemplates.class);
    }

    public SessionAttendantsConfigDTO getSessionAttendantsConfig(Long sessionId, Long eventId) {
        return httpClient.buildRequest(HttpMethod.GET, ATTENDANTS_SESSIONS)
                .pathParams(eventId, sessionId)
                .execute(SessionAttendantsConfigDTO.class);
    }

    public void upsertSessionAttendantsConfig(Long sessionId, Long eventId, SessionAttendantsConfigDTO sessionAttendantsConfig) {
        httpClient.buildRequest(HttpMethod.PUT, ATTENDANTS_SESSIONS)
                .pathParams(eventId, sessionId)
                .body(new ClientRequestBody(sessionAttendantsConfig))
                .execute();
    }

    public void deleteSessionAttendantsConfig(Long sessionId, Long eventId) {
        httpClient.buildRequest(HttpMethod.DELETE, ATTENDANTS_SESSIONS)
                .pathParams(eventId, sessionId)
                .execute();
    }

    public EventAttendantsConfigDTO getEventAttendantsConfig(Long eventId) {
        return httpClient.buildRequest(HttpMethod.GET, ATTENDANTS_EVENTS)
                .pathParams(eventId)
                .execute(EventAttendantsConfigDTO.class);
    }

    public void upsertEventAttendantsConfig(Long eventId, EventAttendantsConfigDTO eventAttendantsConfigDTO) {
        httpClient.buildRequest(HttpMethod.POST, ATTENDANTS_EVENTS)
                .pathParams(eventId)
                .body(new ClientRequestBody(eventAttendantsConfigDTO))
                .execute();
    }

    public void deleteEventAttendantsConfig(Long eventId) {
        httpClient.buildRequest(HttpMethod.DELETE, ATTENDANTS_EVENTS)
                .pathParams(eventId)
                .execute();
    }

    public void updateSessionTicketCommunicationElements(Long eventId, Long sessionId, Set<TicketCommunicationElement> elements, TicketCommunicationElementCategory category) {
        httpClient.buildRequest(HttpMethod.POST, SESSION_TICKET_COMMUNICATION_ELEMENTS)
                .pathParams(eventId, sessionId, category)
                .body(new ClientRequestBody(elements))
                .execute();
    }

    public void updateSessionTicketCommunicationElementsBulk(Long eventId, UpdateSessionTicketContentsBulk dto, TicketCommunicationElementCategory category) {
        httpClient.buildRequest(HttpMethod.POST, SESSION_TICKET_COMMUNICATION_ELEMENTS_BULK)
                .pathParams(eventId, category)
                .body(new ClientRequestBody(dto))
                .execute();
    }

    public List<EventSurcharge> getEventChannelSurcharges(Long eventId, Long channelId, List<SurchargeType> types) {
        String type = null;
        if (CollectionUtils.isNotEmpty(types)) {
            type = types.stream().map(SurchargeType::name).collect(Collectors.joining(","));
        }

        return httpClient.buildRequest(HttpMethod.GET, EVENTS + EVENT_ID + CHANNELS + CHANNEL_ID + SURCHARGES)
                .pathParams(eventId, channelId)
                .params(new QueryParameters.Builder().addQueryParameter("type", type).build())
                .execute(ListType.of(EventSurcharge.class));
    }

    public void setEventChannelSurcharges(Long eventId, Long channelId, List<EventSurcharge> msEventSurchargeRequestDTO) {
        httpClient.buildRequest(HttpMethod.POST, EVENTS + EVENT_ID + CHANNELS + CHANNEL_ID + SURCHARGES)
                .pathParams(eventId, channelId)
                .body(new ClientRequestBody(msEventSurchargeRequestDTO))
                .execute();
    }

    public List<Attribute> getEventAttributes(Long eventId) {
        return httpClient.buildRequest(HttpMethod.GET, EVENTS + EVENT_ID + ATTRIBUTES)
                .pathParams(eventId)
                .execute(ListType.of(Attribute.class));
    }

    public void putEventAttributes(Long eventId, AttributeRequestValuesDTO attributeRequestValuesDTO) {
        httpClient.buildRequest(HttpMethod.PUT, EVENTS + EVENT_ID + ATTRIBUTES)
                .pathParams(eventId)
                .body(new ClientRequestBody(attributeRequestValuesDTO))
                .execute();
    }

    public List<Attribute> getSessionAttributes(Long eventId, Long sessionId) {
        return httpClient.buildRequest(HttpMethod.GET, EVENTS + EVENT_ID + SESSIONS + SESSION_ID + ATTRIBUTES)
                .pathParams(eventId, sessionId)
                .execute(ListType.of(Attribute.class));
    }

    public void putSessionAttributes(Long eventId, Long sessionId, AttributeRequestValuesDTO attributeRequestValuesDTO) {
        httpClient.buildRequest(HttpMethod.PUT, EVENTS + EVENT_ID + SESSIONS + SESSION_ID + ATTRIBUTES)
                .pathParams(eventId, sessionId)
                .body(new ClientRequestBody(attributeRequestValuesDTO))
                .execute();
    }

    public PriceTypes getPriceTypes(Long eventId, Long sessionId) {
        return httpClient.buildRequest(HttpMethod.GET, GET_SESSION_PRICE_TYPES)
                .pathParams(eventId, sessionId)
                .execute(PriceTypes.class);

    }

    public void updatePriceTypes(Long eventId, Long sessionId, Long priceTypeId, PriceType priceType) {
        httpClient.buildRequest(HttpMethod.PUT, PUT_PRICE_TYPE)
                .pathParams(eventId, sessionId, priceTypeId)
                .body(new ClientRequestBody(priceType))
                .execute();
    }

    public SessionGroupConfig getSessionGroup(Long eventId, Long sessionId) {
        return httpClient.buildRequest(HttpMethod.GET, GROUP_CONFIG)
                .pathParams(eventId, sessionId)
                .execute(SessionGroupConfig.class);
    }

    public void updateSessionGroup(Long eventId, Long sessionId, SessionGroupConfig config) {
        httpClient.buildRequest(HttpMethod.PUT, GROUP_CONFIG)
                .pathParams(eventId, sessionId)
                .body(new ClientRequestBody(config))
                .execute();
    }

    public void deleteSessionGroup(Long eventId, Long sessionId) {
        httpClient.buildRequest(HttpMethod.DELETE, GROUP_CONFIG)
                .pathParams(eventId, sessionId)
                .execute();
    }

    public RenewalCandidatesSeasonTicketsRepositoryResponse searchRenewalCandidatesSeasonTickets(Long seasonTicketId) {
        return httpClient.buildRequest(HttpMethod.GET, SEASON_TICKET_RENEWAL_CANDIDATES)
                .pathParams(seasonTicketId)
                .execute(RenewalCandidatesSeasonTicketsRepositoryResponse.class);
    }

    public RenewalEntitiesRepositoryResponse getRenewalEntities(Long seasonTicketId,
                                                                SeasonTicketRenewalFilter filter) {
        QueryParameters.Builder params = getParams(filter);
        return httpClient.buildRequest(HttpMethod.GET, SEASON_TICKET_RENEWAL_ENTITIES)
                .pathParams(seasonTicketId)
                .params(params.build())
                .execute(RenewalEntitiesRepositoryResponse.class);
    }

    @NotNull
    private QueryParameters.Builder getParams(SeasonTicketRenewalFilter filter) {
        QueryParameters.Builder params = new QueryParameters.Builder();
        params.addQueryParameter(MAPPING_STATUS_PARAM, filter.getMappingStatus());
        params.addQueryParameter(RENEWAL_STATUS_PARAM, filter.getRenewalStatus());
        params.addQueryParameter(RENEWAL_SUBSTATUS_PARAM, filter.getRenewalSubstatus());
        params.addQueryParameter(RENEWAL_STRICT_STATUS, true);
        params.addQueryParameter(RENEWAL_AUTORENEWAL_PARAM, filter.getAutoRenewal());
        addLimitAndOffset(filter.getLimit(), filter.getOffset(), params);
        ConverterUtils.addFreeSearch(filter.getFreeSearch(), params);
        ConverterUtils.addEntityId(filter.getEntityId(), params);
        ConverterUtils.checkSortFields(filter.getSort(), params, SeasonTicketRenewalSeatsSortableField::byName);
        if (!CommonUtils.isEmpty(filter.getBirthday())) {
            for (FilterWithOperator<ZonedDateTime> startDateFilter : filter.getBirthday()) {
                params.addQueryParameter(BIRTHDAY_PARAM, serializeFilterDateOperator(startDateFilter));
            }
        }
        return params;
    }

    public void setRenewalSeasonTicketDTO(Long seasonTicketId, RenewalSeasonTicket renewalSeasonTicket) {
        httpClient.buildRequest(HttpMethod.POST, SEASON_TICKET_RENEWALS)
                .pathParams(seasonTicketId)
                .body(new ClientRequestBody(renewalSeasonTicket))
                .execute();
    }

    public SeasonTicketRenewalsRepositoryResponse getRenewalsSeasonTicket(Long seasonTicketId, SeasonTicketRenewalFilter filter) {
        QueryParameters.Builder params = getParams(filter);
        return httpClient.buildRequest(HttpMethod.GET, SEASON_TICKET_RENEWALS)
                .pathParams(seasonTicketId)
                .params(params.build())
                .execute(SeasonTicketRenewalsRepositoryResponse.class);
    }

    public SeasonTicketRenewalsRepositoryResponse getRenewals(SeasonTicketRenewalsFilter filter) {
        QueryParameters.Builder params = new QueryParameters.Builder();
        addLimitAndOffset(filter.getLimit(), filter.getOffset(), params);
        ConverterUtils.addEntityId(filter.getEntityId(), params);
        ConverterUtils.addFreeSearch(filter.getFreeSearch(), params);
        ConverterUtils.addSeasonTicketId(filter.getSeasonTicketId(), params);
        return httpClient.buildRequest(HttpMethod.GET, RENEWALS)
                .params(params.build())
                .execute(SeasonTicketRenewalsRepositoryResponse.class);
    }

    public UpdateRenewalResponse updateRenewalSeats(Long seasonTicketId, UpdateRenewalRequest request) {
        return httpClient.buildRequest(HttpMethod.PUT, SEASON_TICKET_RENEWALS)
                .pathParams(seasonTicketId)
                .body(new ClientRequestBody(request))
                .execute(UpdateRenewalResponse.class);
    }

    public es.onebox.mgmt.datasources.ms.event.dto.event.PriceTypes getPriceTypes(Long eventId, PriceTypeFilter filter) {
        QueryParameters params = new QueryParameters.Builder().
                addQueryParameters(filter).
                build();
        return httpClient.buildRequest(HttpMethod.GET, GET_PRICE_TYPE)
                .pathParams(eventId)
                .params(params)
                .execute(es.onebox.mgmt.datasources.ms.event.dto.event.PriceTypes.class);
    }

    public SessionRefundConditions getSessionRefundConditions(final Long eventId, final Long sessionId) {
        return httpClient.buildRequest(HttpMethod.GET, REFUND_CONDITIONS)
                .pathParams(eventId, sessionId)
                .execute(SessionRefundConditions.class);

    }

    public void updateSessionRefundConditions(final Long eventId, final Long sessionId,
                                              final SessionRefundConditions sessionRefundConditions) {

        httpClient.buildRequest(HttpMethod.PUT, REFUND_CONDITIONS)
                .pathParams(eventId, sessionId)
                .body(new ClientRequestBody(sessionRefundConditions))
                .execute();
    }

    public void deleteRenewalSeat(Long seasonTicketId, String renewalId) {
        httpClient.buildRequest(HttpMethod.DELETE, SEASON_TICKET_RENEWAL_ID)
                .pathParams(seasonTicketId, renewalId)
                .execute();
    }

    public DeleteRenewalsResponse deleteRenewalSeats(Long seasonTicketId, DeleteRenewalsRequest request) {
        QueryParameters.Builder params = new QueryParameters.Builder();

        if (!CommonUtils.isEmpty(request.getRenewalIds())) {
            for (String renewalId : request.getRenewalIds()) {
                params.addQueryParameter("renewalIds", renewalId);
            }
        }

        return httpClient.buildRequest(HttpMethod.DELETE, SEASON_TICKET_RENEWALS)
                .pathParams(seasonTicketId)
                .params(params.build())
                .execute(DeleteRenewalsResponse.class);
    }

    public Session getSession(Long sessionId) {
        return httpClient.buildRequest(HttpMethod.GET, SESSIONS + SESSION_ID)
                .pathParams(sessionId)
                .execute(Session.class);
    }

    public ExternalBarcodeSessionConfig getExternalBarcodeSessionConfig(Long sessionId) {
        return httpClient.buildRequest(HttpMethod.GET, SESSION_EXTERNAL_BARCODE_CONFIG)
                .pathParams(sessionId)
                .execute(ExternalBarcodeSessionConfig.class);
    }

    public void updateExternalBarcodeSessionConfig(Long sessionId, ExternalBarcodeSessionConfig body) {
        httpClient.buildRequest(HttpMethod.PUT, SESSION_EXTERNAL_BARCODE_CONFIG)
                .pathParams(sessionId)
                .body(new ClientRequestBody(body))
                .execute();
    }

    public ExternalBarcodeEventConfig getExternalBarcodeEntityConfig(Long eventId) {
        return httpClient.buildRequest(HttpMethod.GET, EXTERNAL_BARCODE_CONFIG)
                .pathParams(eventId)
                .execute(ExternalBarcodeEventConfig.class);
    }

    public void updateExternalBarcodeEntityConfig(Long eventId, ExternalBarcodeEventConfig body) {
        httpClient.buildRequest(HttpMethod.PUT, EXTERNAL_BARCODE_CONFIG)
                .pathParams(eventId)
                .body(new ClientRequestBody(body))
                .execute();
    }

    public VenueTemplatePriceTypeRestriction getVenueTemplatePriceTypeRestrictions(Long eventId, Long templateId, Long priceTypeId) {
        return httpClient.buildRequest(HttpMethod.GET, EVENTS + EVENT_ID + PRICE_TYPE_RESTRICTION)
                .pathParams(eventId, templateId, priceTypeId)
                .execute(VenueTemplatePriceTypeRestriction.class);
    }

    public void upsertVenueTemplatePriceTypeRestrictions(Long eventId, Long templateId, Long priceTypeId, CreateVenueTemplatePriceTypeRestriction restrictions) {
        httpClient.buildRequest(HttpMethod.PUT, EVENTS + EVENT_ID + PRICE_TYPE_RESTRICTION)
                .pathParams(eventId, templateId, priceTypeId)
                .body(new ClientRequestBody(restrictions))
                .execute();
    }

    public void deleteVenueTemplatePriceTypeRestriction(Long eventId, Long templateId, Long priceTypeId) {
        httpClient.buildRequest(HttpMethod.DELETE, EVENTS + EVENT_ID + PRICE_TYPE_RESTRICTION)
                .pathParams(eventId, templateId, priceTypeId).execute();
    }

    public AvailableFields getAvailableFields() {
        return httpClient.buildRequest(HttpMethod.GET, "/attendants-available-fields")
                .execute(AvailableFields.class);
    }

    public AttendantFields getAttendantFields(Long eventId) {
        return httpClient.buildRequest(HttpMethod.GET, EVENTS + EVENT_ID + "/fields")
                .pathParams(eventId)
                .execute(AttendantFields.class);
    }

    public void createAttendantFields(Long eventId, Set<CreateAttendantField> body) {
        httpClient.buildRequest(HttpMethod.POST, EVENTS + EVENT_ID + "/fields")
                .pathParams(eventId)
                .body(new ClientRequestBody(body))
                .execute();
    }


    public void upsertSaleRestrictions(Long eventId, Long sessionId, Long lockedPriceTypeId, UpdateSaleRestriction request) {
        httpClient.buildRequest(HttpMethod.PUT, EVENTS + EVENT_ID + SESSIONS + SESSION_ID + PRICE_TYPES + PRICE_TYPE_ID +
                        RESTRICTIONS)
                .pathParams(eventId, sessionId, lockedPriceTypeId)
                .body(new ClientRequestBody(request))
                .execute();
    }

    public SessionSaleRestriction getSaleRestriction(Long eventId, Long sessionId, Long lockedPriceTypeId) {
        return httpClient.buildRequest(HttpMethod.GET, EVENTS + EVENT_ID + SESSIONS + SESSION_ID + PRICE_TYPES +
                        PRICE_TYPE_ID + RESTRICTIONS)
                .pathParams(eventId, sessionId, lockedPriceTypeId)
                .execute(SessionSaleRestriction.class);

    }

    public void deleteRestriction(Long eventId, Long sessionId, Long lockedPriceTypeId) {
        httpClient.buildRequest(HttpMethod.DELETE, EVENTS + EVENT_ID + SESSIONS + SESSION_ID + PRICE_TYPES +
                        PRICE_TYPE_ID + RESTRICTIONS)
                .pathParams(eventId, sessionId, lockedPriceTypeId)
                .execute();
    }

    public List<IdNameDTO> getAllVenueTemplateRestrictions(Long eventId, Long templateId) {
        return httpClient.buildRequest(HttpMethod.GET, EVENTS + EVENT_ID + "/venue-templates/{venueTemplateId}/restricted-price-types")
                .pathParams(eventId, templateId)
                .execute(ListType.of(IdNameDTO.class));
    }

    public List<IdNameDTO> getSessionRestrictions(Long eventId, Long sessionId) {
        return httpClient.buildRequest(HttpMethod.GET, EVENTS + EVENT_ID + SESSIONS + SESSION_ID + "/restricted-price-types")
                .pathParams(eventId, sessionId)
                .execute(ListType.of(IdNameDTO.class));

    }

    public void resetEventVenueTemplatesPricesCurrency(Long eventId) {
        httpClient.buildRequest(HttpMethod.PATCH, EVENTS + EVENT_ID + "/venue-templates/reset-price-currency")
                .pathParams(eventId)
                .execute();

    }

    public List<ExternalEvent> getExternalEvents(Long entityId, ExternalEventType eventType) {
        QueryParameters params = new QueryParameters.Builder()
                .addQueryParameter(ENTITY_ID, entityId)
                .addQueryParameter("eventType", eventType)
                .build();
        return httpClient.buildRequest(HttpMethod.GET, EXTERNAL_EVENTS)
                .params(params)
                .execute(ListType.of(ExternalEvent.class));
    }

    public SessionsGroups getSessionsGroups(Long operatorId, Long eventId, SessionsGroupsSearchFilter filter) {
        QueryParameters.Builder params = fillGetSessionsFilter(operatorId, Collections.singletonList(eventId), filter);
        params.addQueryParameter("groupType", filter.getGroupType());

        return httpClient.buildRequest(HttpMethod.GET, EVENTS + EVENT_ID + SESSIONS + GROUPS)
                .pathParams(eventId)
                .params(params.build())
                .execute(SessionsGroups.class);
    }

    public List<IdNameDTO> getExternalEventRates(Long internalId) {
        return httpClient.buildRequest(HttpMethod.GET, EXTERNAL_EVENT_RATES)
                .pathParams(internalId)
                .execute(ListType.of(IdNameDTO.class));
    }

    public ExternalEvent getExternalEvent(Long id) {
        return httpClient.buildRequest(HttpMethod.GET, EXTERNAL_EVENTS + "/{internalId}")
                .pathParams(id)
                .execute(ExternalEvent.class);
    }

    public void updateBarcodes(Long seasonTicketId) {
        httpClient.buildRequest(HttpMethod.POST, SEASON_TICKET_SESSIONS + "/update-barcodes")
                .pathParams(seasonTicketId)
                .execute();
    }

    public EventChannelB2BAssignations getEventChannelAssignation(Long eventId, Long channelId) {
        return httpClient.buildRequest(HttpMethod.GET, EVENT_CHANNEL_B2B_ASSIGNATIONS)
                .pathParams(eventId, channelId)
                .execute(EventChannelB2BAssignations.class);
    }

    public void updateEventChannelAssignation(Long eventId, Long channelId, UpdateChannelEventAssignations body) {
        httpClient.buildRequest(HttpMethod.PUT, EVENT_CHANNEL_B2B_ASSIGNATIONS)
                .pathParams(eventId, channelId)
                .body(new ClientRequestBody(body))
                .execute();
    }

    public void deleteEventChannelAssignation(Long eventId, Long channelId) {
        httpClient.buildRequest(HttpMethod.DELETE, EVENT_CHANNEL_B2B_ASSIGNATIONS)
                .pathParams(eventId, channelId)
                .execute();
    }

    private QueryParameters.Builder fillGetSessionsFilter(Long operatorId, List<Long> eventIds, SessionSearchFilter filter) {
        QueryParameters.Builder params = new QueryParameters.Builder();

        if (CollectionUtils.isNotEmpty(filter.getId())) {
            filter.getId().forEach(id -> params.addQueryParameter("ids", id));
        }
        if (CollectionUtils.isNotEmpty(filter.getStartDate())) {
            filter.getStartDate().forEach(op -> params.addQueryParameter("startDate", op));
        }
        if (filter.getEndDate() != null) {
            params.addQueryParameter("endDate", serializeFilterDateOperator(filter.getEndDate()));
        }
        if (filter.getOlsonId() != null) {
            params.addQueryParameter("olsonId", filter.getOlsonId());
        }
        if (filter.getDaysOfWeek() != null) {
            params.addQueryParameter("daysOfWeek", filter.getDaysOfWeek().stream()
                    .map(DayOfWeekDTO::name).collect(Collectors.joining(",")));
        }
        if (CollectionUtils.isNotEmpty(filter.getStatus())) {
            for (SessionStatus eventStatus : filter.getStatus()) {
                params.addQueryParameter(STATUS, eventStatus.name());
            }
        }
        if (CollectionUtils.isNotEmpty(filter.getType())) {
            for (es.onebox.mgmt.sessions.enums.SessionType sessionType : filter.getType()) {
                params.addQueryParameter("type",
                        es.onebox.mgmt.datasources.ms.event.dto.session.SessionType.getById(sessionType.getType()).name());
            }
        }
        if (CollectionUtils.isNotEmpty(filter.getHourRange())) {
            for (String s : filter.getHourRange()) {
                params.addQueryParameter("hourPeriods", s);
            }
        }

        if (filter.getVenueTemplateId() != null) {
            params.addQueryParameter("venueConfigId", filter.getVenueTemplateId());
        }

        ConverterUtils.addFreeSearch(filter.getFreeSearch(), params);
        ConverterUtils.checkSortFields(filter.getSort(), params, SessionField::byName);
        ConverterUtils.checkFilterFields(filter.getFields(), params, SessionField::byName);
        addLimitAndOffset(filter.getLimit(), filter.getOffset(), params);

        fillOperatorAndEntity(params, operatorId, filter.getEntityId());
        if (CollectionUtils.isNotEmpty(eventIds)) {
            eventIds.forEach(id -> params.addQueryParameter("eventId", id));
        }
        if (filter.isGetQueueitInfo()) {
            params.addQueryParameter("getQueueitInfo", filter.isGetQueueitInfo());
        }
        if (filter.getIncludeDynamicPriceConfig() != null) {
            params.addQueryParameter("includeDynamicPriceConfig", filter.getIncludeDynamicPriceConfig());
        }

        return params;
    }

    private String serializeFilterDateOperator(FilterWithOperator<ZonedDateTime> filter) {
        String strOperator = "";
        if (filter.getOperator() != null) {
            strOperator = filter.getOperator().getKey() + ":";
        }
        return strOperator + DateUtils.formatISODateTime(filter.getValue());
    }

    private void fillOperatorAndEntity(QueryParameters.Builder params, Long operatorId, Long entityId) {
        if (operatorId == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, "Operator is mandatory field", null);
        }
        params.addQueryParameter("operatorId", operatorId)
                .addQueryParameter(ENTITY_ID, entityId);
    }

    private void addDateParameters(List<FilterWithOperator<ZonedDateTime>> startDate, List<FilterWithOperator<ZonedDateTime>> endDate, QueryParameters.Builder params) {
        if (!CommonUtils.isEmpty(startDate)) {
            for (FilterWithOperator<ZonedDateTime> startDateFilter : startDate) {
                params.addQueryParameter("startDate", serializeFilterDateOperator(startDateFilter));
            }
        }
        if (!CommonUtils.isEmpty(endDate)) {
            for (FilterWithOperator<ZonedDateTime> endDateFilter : endDate) {
                params.addQueryParameter("endDate", serializeFilterDateOperator(endDateFilter));
            }
        }
    }

    private void addLimitAndOffset(Long limit, Long offset, QueryParameters.Builder params) {
        params.addQueryParameter("limit", limit)
                .addQueryParameter("offset", offset);
    }

    public void purgeSeasonTicketRenewalSeats(Long seasonTicketId, SeasonTicketRenewalPurgeFilter filter) {
        QueryParameters.Builder params = getRenewalsPurgeQueryParameters(filter);
        httpClient.buildRequest(HttpMethod.POST, SEASON_TICKET_RENEWALS_PURGE)
                .pathParams(seasonTicketId)
                .params(params.build())
                .execute();
    }

    private QueryParameters.Builder getRenewalsPurgeQueryParameters(SeasonTicketRenewalPurgeFilter filter) {
        QueryParameters.Builder params = new QueryParameters.Builder();
        params.addQueryParameter(MAPPING_STATUS_PARAM, filter.getMappingStatus());
        params.addQueryParameter(RENEWAL_STATUS_PARAM, filter.getRenewalStatus());
        ConverterUtils.addFreeSearch(filter.getFreeSearch(), params);
        if (!CommonUtils.isEmpty(filter.getBirthday())) {
            for (FilterWithOperator<ZonedDateTime> startDateFilter : filter.getBirthday()) {
                params.addQueryParameter(BIRTHDAY_PARAM, serializeFilterDateOperator(startDateFilter));
            }
        }
        return params;
    }

    public CountRenewalsPurgeResponse countRenewalsPurge(Long seasonTicketId, SeasonTicketRenewalPurgeFilter filter) {
        QueryParameters.Builder params = getRenewalsPurgeQueryParameters(filter);
        return httpClient.buildRequest(HttpMethod.GET, SEASON_TICKET_RENEWALS_PURGE)
                .pathParams(seasonTicketId)
                .params(params.build())
                .execute(CountRenewalsPurgeResponse.class);
    }

    public void createChangeSeatPricesTable(Long seasonTicketId, ChangeSeatSeasonTicketPriceRelations priceRelations) {
        httpClient.buildRequest(HttpMethod.POST, SEASON_TICKET_CHANGE_SEATS_PRICES)
                .pathParams(seasonTicketId)
                .body(new ClientRequestBody(priceRelations))
                .execute();
    }

    public List<ChangeSeatSeasonTicketPriceCompleteRelation> searchChangeSeatPriceRelations(Long seasonTicketId, ChangeSeatSeasonTicketPriceFilter priceFilter) {
        QueryParameters.Builder params = new QueryParameters.Builder();
        params.addQueryParameters(priceFilter);
        return httpClient.buildRequest(HttpMethod.GET, SEASON_TICKET_CHANGE_SEATS_PRICES)
                .pathParams(seasonTicketId)
                .params(params.build())
                .execute(ListType.of(ChangeSeatSeasonTicketPriceCompleteRelation.class));
    }

    public void updateChangeSeatPriceRelations(Long seasonTicketId, List<UpdateChangeSeatSeasonTicketPriceRelation> updatePriceRelations) {
        httpClient.buildRequest(HttpMethod.PUT, SEASON_TICKET_CHANGE_SEATS_PRICES)
                .pathParams(seasonTicketId)
                .body(new ClientRequestBody(updatePriceRelations))
                .execute();
    }

    public RatesGroup getEventRatesGroup(Integer eventId, RateGroupType type) {
        QueryParameters.Builder params = new QueryParameters.Builder();
        params.addQueryParameter(TYPE_PARAM, type);
        return httpClient.buildRequest(HttpMethod.GET, EVENTS + EVENT_ID + RATES_GROUP)
                .params(params.build())
                .pathParams(eventId)
                .execute(RatesGroup.class);
    }

    public Long createEventRateGroup(Integer eventId, RateGroup rate) {
        return httpClient.buildRequest(HttpMethod.POST, EVENTS + EVENT_ID + RATES_GROUP)
                .pathParams(eventId)
                .body(new ClientRequestBody(rate))
                .execute(IdDTO.class).getId();
    }

    public void updateEventRatesGroup(Long eventId, List<RateGroup> rates) {
        httpClient.buildRequest(HttpMethod.PUT, EVENTS + EVENT_ID + RATES_GROUP)
                .pathParams(eventId)
                .body(new ClientRequestBody(rates))
                .execute();
    }

    public void updateEventRateGroup(Long eventId, Long rateId, RateGroup rate) {
        httpClient.buildRequest(HttpMethod.PUT, EVENTS + EVENT_ID + RATES_GROUP + RATE_ID)
                .pathParams(eventId, rateId)
                .body(new ClientRequestBody(rate))
                .execute();
    }

    public void deleteEventRateGroup(Long eventId, Long rateId) {
        httpClient.buildRequest(HttpMethod.DELETE, EVENTS + EVENT_ID + RATES_GROUP + RATE_ID)
                .pathParams(eventId, rateId)
                .execute();
    }

    public ExportProcess generateRenewalsReport(Long seasonTicketId, SeasonTicketRenewalsExportFilter filter,
                                                SeasonTicketRenewalFilter queryParams) {
        QueryParameters.Builder params = new QueryParameters.Builder();
        params.addQueryParameter(MAPPING_STATUS_PARAM, queryParams.getMappingStatus());
        params.addQueryParameter(RENEWAL_STATUS_PARAM, queryParams.getRenewalStatus());
        params.addQueryParameter(RENEWAL_AUTORENEWAL_PARAM, queryParams.getAutoRenewal());
        params.addQueryParameter(RENEWAL_SUBSTATUS_PARAM, queryParams.getRenewalSubstatus());
        addLimitAndOffset(1000L, 0L, params);
        ConverterUtils.addFreeSearch(queryParams.getFreeSearch(), params);
        ConverterUtils.checkSortFields(queryParams.getSort(), params, SeasonTicketRenewalSeatsSortableField::byName);
        if (!CommonUtils.isEmpty(queryParams.getBirthday())) {
            for (FilterWithOperator<ZonedDateTime> startDateFilter : queryParams.getBirthday()) {
                params.addQueryParameter(BIRTHDAY_PARAM, serializeFilterDateOperator(startDateFilter));
            }
        }
        params.addQueryParameter("seasonTicketId", seasonTicketId);
        ClientRequestBody body = new ClientRequestBody(filter);
        return httpClient.buildRequest(HttpMethod.POST, REPORT)
                .body(body)
                .params(params.build())
                .execute(ExportProcess.class);
    }

    public ExportProcess getExportStatus(String exportId, Long userId, ExportType exportType) {
        QueryParameters params = new QueryParameters.Builder()
                .addQueryParameter("exportType", exportType)
                .build();
        return httpClient.buildRequest(HttpMethod.GET, EXPORT_STATUS)
                .pathParams(exportId, userId)
                .params(params)
                .execute(ExportProcess.class);
    }

    public SeasonTicketChangeSeat getSeasonTicketChangeSeat(Long seasonTicketId) {
        return httpClient.buildRequest(HttpMethod.GET, SEASON_TICKET_CHANGE_SEATS)
                .pathParams(seasonTicketId)
                .execute(SeasonTicketChangeSeat.class);
    }

    public void updateSeasonTicketChangeSeat(Long seasonTicketId, SeasonTicketChangeSeat updateChangeSeat) {
        ClientRequestBody body = new ClientRequestBody(updateChangeSeat);
        httpClient.buildRequest(HttpMethod.PUT, SEASON_TICKET_CHANGE_SEATS)
                .pathParams(seasonTicketId)
                .body(body)
                .execute();
    }

    public B2BSeatPublishingConfig getSeatPublishingConfig(Long eventId, Long channelId, Long venueTemplateId) {
        return httpClient.buildRequest(HttpMethod.GET, EVENT_B2B_PUBLISHING_CONFIG)
                .pathParams(eventId, channelId, venueTemplateId)
                .execute(B2BSeatPublishingConfig.class);
    }

    public void updateSeatPublishingConfig(Long eventId, Long channelId, Long venueTemplateId, B2BSeatPublishingConfig b2BSeatPublishingConfig) {
        ClientRequestBody body = new ClientRequestBody(b2BSeatPublishingConfig);
        httpClient.buildRequest(HttpMethod.PUT, EVENT_B2B_PUBLISHING_CONFIG)
                .pathParams(eventId, channelId, venueTemplateId)
                .body(body)
                .execute();
    }

    public Product getProduct(Long productId) {
        return httpClient.buildRequest(HttpMethod.GET, PRODUCTS + PRODUCT_ID)
                .pathParams(productId)
                .execute(Product.class);
    }

    public Long createProduct(CreateProduct product) {
        return httpClient.buildRequest(HttpMethod.POST, PRODUCTS)
                .body(new ClientRequestBody(product))
                .execute(IdDTO.class).getId();
    }

    public ProductTicketTemplateLiterals getProductTicketTemplateLiterals(Long ticketTemplateId, ProductTicketTemplateLiteralElementFilter filter) {
        return httpClient.buildRequest(HttpMethod.GET, PRODUCT_TICKET_TEMPLATE_LITERALS)
                .pathParams(ticketTemplateId)
                .params(new QueryParameters.Builder().addQueryParameters(filter).build())
                .execute(ProductTicketTemplateLiterals.class);
    }

    public void updateProductTicketTemplateLiterals(Long productTicketTemplateId, List<ProductTicketTemplateLiteral> literalListDTO) {
        httpClient.buildRequest(HttpMethod.POST, PRODUCT_TICKET_TEMPLATE_LITERALS)
                .pathParams(productTicketTemplateId)
                .body(new ClientRequestBody(literalListDTO))
                .execute();
    }

    public Long createDeliveryPoint(CreateDeliveryPoint createDeliveryPoint) {
        return httpClient.buildRequest(HttpMethod.POST, DELIVERY_POINTS)
                .body(new ClientRequestBody(createDeliveryPoint))
                .execute(IdDTO.class).getId();
    }

    public DeliveryPoint getDeliveryPoint(Long deliveryPointId) {
        return httpClient.buildRequest(HttpMethod.GET, DELIVERY_POINTS + DELIVERY_POINT)
                .pathParams(deliveryPointId)
                .execute(DeliveryPoint.class);
    }

    public DeliveryPoints searchDeliveryPoint(SearchDeliveryPointFilter searchDeliveryPointFilter) {
        QueryParameters.Builder builder = new QueryParameters.Builder();
        builder.addQueryParameters(searchDeliveryPointFilter);
        return httpClient.buildRequest(HttpMethod.GET, DELIVERY_POINTS)
                .params(builder.build())
                .execute(DeliveryPoints.class);
    }

    public void deleteDeliveryPoint(Long deliveryPointId) {
        httpClient.buildRequest(HttpMethod.DELETE, DELIVERY_POINTS + DELIVERY_POINT)
                .pathParams(deliveryPointId)
                .execute();
    }

    public DeliveryPoint updateDeliveryPoint(Long deliveryPointId, UpdateDeliveryPoint updateDeliveryPoint) {
        return httpClient.buildRequest(HttpMethod.PUT, DELIVERY_POINTS + DELIVERY_POINT)
                .pathParams(deliveryPointId)
                .body(new ClientRequestBody(updateDeliveryPoint))
                .execute(DeliveryPoint.class);
    }

    public ProductLanguages getProductLanguages(Long productId) {
        return httpClient.buildRequest(HttpMethod.GET, PRODUCTS_LANGUAGES)
                .pathParams(productId)
                .execute(ProductLanguages.class);
    }

    public ProductLanguages updateProductLanguages(Long productId, UpdateProductLanguages updateProductLanguages) {
        return httpClient.buildRequest(HttpMethod.PUT, PRODUCTS_LANGUAGES)
                .pathParams(productId)
                .body(new ClientRequestBody(updateProductLanguages))
                .execute(ProductLanguages.class);
    }

    public ProductCommunicationElementsText createProductCommunicationElementsText(Long productId, CreateProductCommunicationElementsText createProductCommunicationElementsText) {
        return httpClient.buildRequest(HttpMethod.POST, PRODUCTS_COMMUNICATION_ELEMENTS + "/texts")
                .pathParams(productId)
                .body(new ClientRequestBody(createProductCommunicationElementsText))
                .execute(ProductCommunicationElementsText.class);
    }

    public ProductCommunicationElementsText getProductCommunicationElementsText(Long productId) {
        return httpClient.buildRequest(HttpMethod.GET, PRODUCTS_COMMUNICATION_ELEMENTS + "/texts")
                .pathParams(productId)
                .execute(ProductCommunicationElementsText.class);
    }

    public void createProductCommunicationElementsImage(Long productId, List<CreateProductCommunicationElementImage> createProductCommunicationElementImages) {
        httpClient.buildRequest(HttpMethod.POST, PRODUCTS_COMMUNICATION_ELEMENTS + "/images")
                .pathParams(productId)
                .body(new ClientRequestBody(createProductCommunicationElementImages))
                .execute();
    }

    public ProductCommunicationElementsImage getProductCommunicationElementsImages(Long productId) {
        return httpClient.buildRequest(HttpMethod.GET, PRODUCTS_COMMUNICATION_ELEMENTS + "/images")
                .pathParams(productId)
                .execute(ProductCommunicationElementsImage.class);
    }

    public void deleteProductCommunicationElementImage(Long productId, String language, ProductCommunicationElementsImagesType type, Integer position) {
        httpClient.buildRequest(HttpMethod.DELETE, PRODUCTS_COMMUNICATION_ELEMENTS + "/images/languages/{language}/types/{type}/positions/{position}")
                .pathParams(productId, language, type, position)
                .execute();
    }

    public void updateProduct(Long productId, UpdateProduct updateProduct) {
        httpClient.buildRequest(HttpMethod.PUT, PRODUCTS + PRODUCT_ID)
                .pathParams(productId)
                .body(new ClientRequestBody(updateProduct))
                .execute();
    }

    public ProductVariants searchProductVariants(Long productId, SearchProductVariantsFilter filter) {
        QueryParameters.Builder builder = new QueryParameters.Builder();
        builder.addQueryParameters(filter);

        return httpClient.buildRequest(HttpMethod.GET, PRODUCTS_VARIANTS)
                .pathParams(productId)
                .params(builder.build())
                .execute(ProductVariants.class);
    }

    public ProductVariant getProductVariant(Long productId, Long variantId) {
        return httpClient
                .buildRequest(HttpMethod.GET, PRODUCTS_VARIANTS + VARIANT_ID)
                .pathParams(productId, variantId)
                .execute(ProductVariant.class);
    }

    public List<IdNameDTO> createVariantProductVariants(Long productId) {
        return httpClient
                .buildRequest(HttpMethod.POST, PRODUCTS_VARIANTS)
                .pathParams(productId)
                .execute(ListType.of(IdNameDTO.class));
    }

    public void updateProductVariant(Long productId, Long variantId, UpdateProductVariant productVariant) {
        httpClient.buildRequest(HttpMethod.PUT, PRODUCTS_VARIANTS + VARIANT_ID)
                .pathParams(productId, variantId)
                .body(new ClientRequestBody(productVariant))
                .execute();
    }

    public void updateProductVariantPrices(Long productId, UpdateProductVariantPrices updateProductVariantPrices) {
        httpClient.buildRequest(HttpMethod.PUT, PRODUCTS_VARIANTS + PRICES)
                .pathParams(productId)
                .body(new ClientRequestBody(updateProductVariantPrices))
                .execute();
    }

    public void deleteProduct(Long productId) {
        httpClient.buildRequest(HttpMethod.DELETE, PRODUCTS + "/{productId}")
                .pathParams(productId)
                .execute();
    }

    public Products searchProducts(SearchProductFilter searchProductFilter) {
        QueryParameters.Builder builder = new QueryParameters.Builder();
        builder.addQueryParameters(searchProductFilter);
        return httpClient.buildRequest(HttpMethod.GET, PRODUCTS)
                .params(builder.build())
                .execute(Products.class);
    }

    public ProductChannelSessions getProductChannelSessions(Long productId, Long channelId,
                                                            ProductChannelSessionsFilter filter) {
        QueryParameters.Builder builder = new QueryParameters.Builder();
        if (filter.getStatus() != null) {
            builder.addQueryParameter("status", filter.getStatus());
        }
        if (filter.getOffset() != null) {
            builder.addQueryParameter("offset", filter.getOffset());
        }
        if (filter.getLimit() != null) {
            builder.addQueryParameter("limit", filter.getLimit());
        }
        return httpClient.buildRequest(HttpMethod.GET, PRODUCT_CHANNEL_SESSIONS)
                .pathParams(productId, channelId)
                .params(builder.build())
                .execute(ProductChannelSessions.class);
    }

    public ProductPublishingSessions getProductPublishingSessions(Long productId, Long eventId) {
        return httpClient.buildRequest(HttpMethod.GET, PRODUCT_EVENT_PUBLISHING_SESSIONS)
                .pathParams(productId, eventId)
                .execute(ProductPublishingSessions.class);
    }


    public void updateProductSessions(Long productId, Long eventId,
                                      UpdateProductSessions updateProductSessions) {
        httpClient.buildRequest(HttpMethod.PUT, PRODUCT_EVENT_PUBLISHING_SESSIONS)
                .pathParams(productId, eventId)
                .body(new ClientRequestBody(updateProductSessions)).execute();
    }

    public void updateProductSession(Long productId, Long eventId, Long sessionId, UpdateProductSession updateProductSession) {
        httpClient.buildRequest(HttpMethod.PUT, PRODUCT_EVENT_SESSION)
                .pathParams(productId, eventId, sessionId)
                .body(new ClientRequestBody(updateProductSession))
                .execute();
    }

    public ProductSessions getProductSessions(Long productId, Long eventId, ProductSessionSearchFilterDTO filter) {
        QueryParameters.Builder paramsBuilder = new QueryParameters.Builder();
        if (filter.getQ() != null) {
            paramsBuilder.addQueryParameter("freeSearch", filter.getQ());
        }
        if (filter.getHasOverride() != null) {
            paramsBuilder.addQueryParameter("hasOverride", filter.getQ());
        }
        if (CollectionUtils.isNotEmpty(filter.getStartDate())) {
            filter.getStartDate().forEach(op -> paramsBuilder.addQueryParameter("startDate", op));
        }
        if (filter.getLimit() != null) {
            paramsBuilder.addQueryParameter("limit", filter.getLimit());
        }
        if (filter.getOffset() != null) {
            paramsBuilder.addQueryParameter("offset", filter.getOffset());
        }

        return httpClient.buildRequest(HttpMethod.GET, PRODUCT_EVENT_SESSIONS)
                .pathParams(productId, eventId)
                .params(paramsBuilder.build())
                .execute(ProductSessions.class);
    }

    public ProductChannels getProductChannels(Long productId) {
        return httpClient.buildRequest(HttpMethod.GET, PRODUCTS_CHANNELS)
                .pathParams(productId)
                .execute(ProductChannels.class);
    }

    public ProductChannel getProductChannel(Long productId, Long channelId) {
        return httpClient.buildRequest(HttpMethod.GET, PRODUCTS_CHANNELS + CHANNEL_ID)
                .pathParams(productId, channelId)
                .execute(ProductChannel.class);
    }

    public CreateProductChannelsResponse createProductChannels(Long productId, CreateProductChannels createProductChannels) {
        return httpClient.buildRequest(HttpMethod.POST, PRODUCTS_CHANNELS)
                .pathParams(productId)
                .body(new ClientRequestBody(createProductChannels))
                .execute(CreateProductChannelsResponse.class);
    }

    public void updateProductChannel(Long productId, Long channelId, UpdateProductChannel updateProductChannel) {
        httpClient.buildRequest(HttpMethod.PUT, PRODUCT_CHANNEL)
                .pathParams(productId, channelId)
                .body(new ClientRequestBody(updateProductChannel))
                .execute();
    }

    public void deleteProductChannel(Long productId, Long channelId) {
        httpClient.buildRequest(HttpMethod.DELETE, PRODUCT_CHANNEL)
                .pathParams(productId, channelId)
                .execute();
    }

    public ProductDelivery getProductDelivery(Long productId) {
        return httpClient.buildRequest(HttpMethod.GET, PRODUCTS_DELIVERY)
                .pathParams(productId)
                .execute(ProductDelivery.class);
    }

    public ProductDelivery updateProductDelivery(Long productId, ProductDelivery productDelivery) {
        return httpClient.buildRequest(HttpMethod.PUT, PRODUCTS_DELIVERY)
                .pathParams(productId)
                .body(new ClientRequestBody(productDelivery))
                .execute(ProductDelivery.class);
    }

    public void upsertProductDeliveryPointRelation(Long productId, UpsertProductDeliveryPointRelation upsertProductDeliveryPointRelation) {
        httpClient.buildRequest(HttpMethod.POST, PRODUCTS_DELIVERY_POINTS_RELATIONS)
                .pathParams(productId)
                .body(new ClientRequestBody(upsertProductDeliveryPointRelation))
                .execute();
    }

    public ProductDeliveryPointRelation getProductDeliveryPointRelation(Long productId, Long deliveryPointId) {
        return httpClient.buildRequest(HttpMethod.GET, PRODUCTS_DELIVERY_POINTS_RELATIONS + PRODUCTS_DELIVERY_POINT_ID)
                .pathParams(productId, deliveryPointId)
                .execute(ProductDeliveryPointRelation.class);
    }

    public ProductDeliveryPointRelations searchProductDeliveryPointRelations(Long productId, SearchProductDeliveryPointRelationFilter searchProductDeliveryPointRelationFilter) {
        QueryParameters.Builder builder = new QueryParameters.Builder();
        builder.addQueryParameters(searchProductDeliveryPointRelationFilter);
        return httpClient.buildRequest(HttpMethod.GET, PRODUCTS_DELIVERY_POINTS_RELATIONS)
                .pathParams(productId)
                .params(builder.build())
                .execute(ProductDeliveryPointRelations.class);
    }

    public Long createProductAttribute(Long productId, CreateProductAttribute productAttribute) {
        return httpClient.buildRequest(HttpMethod.POST, PRODUCTS_ATTRIBUTES)
                .pathParams(productId)
                .body(new ClientRequestBody(productAttribute))
                .execute(IdDTO.class).getId();
    }

    public ProductAttribute getProductAttribute(Long productId, Long attributeId) {
        return httpClient.buildRequest(HttpMethod.GET, PRODUCTS_ATTRIBUTES + ATTRIBUTE_ID)
                .pathParams(productId, attributeId)
                .execute(ProductAttribute.class);
    }

    public ProductAttributes getProductAttributes(Long productId) {
        return httpClient.buildRequest(HttpMethod.GET, PRODUCTS_ATTRIBUTES)
                .pathParams(productId)
                .execute(ProductAttributes.class);
    }

    public void updateProductAttribute(Long productId, Long attributeId, ProductAttribute productAttribute) {
        httpClient.buildRequest(HttpMethod.PUT, PRODUCTS_ATTRIBUTES + ATTRIBUTE_ID)
                .pathParams(productId, attributeId)
                .body(new ClientRequestBody(productAttribute))
                .execute();
    }

    public void deleteProductAttribute(Long productId, Long attributeId) {
        httpClient.buildRequest(HttpMethod.DELETE, PRODUCTS_ATTRIBUTES + ATTRIBUTE_ID)
                .pathParams(productId, attributeId)
                .execute();
    }

    public ProductEventDeliveryPoints getProductEventDeliveryPoints(Long productId, Long eventId) {
        return httpClient.buildRequest(HttpMethod.GET, PRODUCTS_EVENTS_DELIVERY_POINTS)
                .pathParams(productId, eventId)
                .execute(ProductEventDeliveryPoints.class);
    }

    public ProductEventDeliveryPoints updateProductEventDeliveryPoints(Long productId, Long eventId, UpdateProductEventDeliveryPoints updateProductEventDeliveryPoints) {
        return httpClient.buildRequest(HttpMethod.PUT, PRODUCTS_EVENTS_DELIVERY_POINTS)
                .pathParams(productId, eventId)
                .body(new ClientRequestBody(updateProductEventDeliveryPoints))
                .execute(ProductEventDeliveryPoints.class);
    }

    public ProductEvents createProductEvents(Long productId, AddProductEvents productEvents) {
        return httpClient.buildRequest(HttpMethod.POST, PRODUCT_EVENTS)
                .pathParams(productId)
                .body(new ClientRequestBody(productEvents))
                .execute(ProductEvents.class);
    }

    public ProductEvents getProductEvents(Long productId, ProductEventsFilter filter) {
        QueryParameters.Builder builder = new QueryParameters.Builder();
        builder.addQueryParameters(filter);
        return httpClient.buildRequest(HttpMethod.GET, PRODUCT_EVENTS)
                .pathParams(productId)
                .params(builder.build())
                .execute(ProductEvents.class);
    }

    public void updateProductEvents(Long productId, Long eventId, UpdateProductEvent updateProductEvent) {
        httpClient.buildRequest(HttpMethod.PUT, PRODUCT_EVENT_ID)
                .pathParams(productId, eventId)
                .body(new ClientRequestBody(updateProductEvent))
                .execute(ProductEvents.class);
    }

    public void deleteProductEvent(Long productId, Long eventId) {
        httpClient.buildRequest(HttpMethod.DELETE, PRODUCT_EVENT_ID)
                .pathParams(productId, eventId)
                .execute(ProductEvents.class);
    }

    public ProductSessionDeliveryPoints getProductSessionDeliveryPoints(Long productId, Long eventId,
                                                                        ProductSessionDeliveryPointsFilterDTO filter) {
        QueryParameters.Builder paramsBuilder = new QueryParameters.Builder();
        if (CollectionUtils.isNotEmpty(filter.getStartDate())) {
            filter.getStartDate().forEach(op -> paramsBuilder.addQueryParameter("startDate", op));
        }
        if (CollectionUtils.isNotEmpty(filter.getEndDate())) {
            filter.getEndDate().forEach(op -> paramsBuilder.addQueryParameter("endDate", op));
        }
        if (filter.getLimit() != null) {
            paramsBuilder.addQueryParameter("limit", filter.getLimit());
        }
        if (filter.getOffset() != null) {
            paramsBuilder.addQueryParameter("offset", filter.getOffset());
        }
        if(filter.getDaysOfWeek() != null) {
            paramsBuilder.addQueryParameter("daysOfWeek", filter.getDaysOfWeek().stream()
                    .map(DayOfWeekDTO::name).collect(Collectors.joining(",")));
        }
        if(filter.getOlsonId() != null) {
            paramsBuilder.addQueryParameter("olsonId", filter.getOlsonId());
        }
        if (CollectionUtils.isNotEmpty(filter.getStatus())) {
            for (SessionStatus eventStatus : filter.getStatus()) {
                paramsBuilder.addQueryParameter(STATUS, eventStatus.name());
            }
        }
        ConverterUtils.addFreeSearch(filter.getFreeSearch(), paramsBuilder);

        return httpClient.buildRequest(HttpMethod.GET, PRODUCTS_SESSIONS_DELIVERY_POINTS)
                .pathParams(productId, eventId)
                .params(paramsBuilder.build())
                .execute(ProductSessionDeliveryPoints.class);
    }

    public ProductSessionDeliveryPoints updateProductSessionDeliveryPoints(Long productId, Long eventId, UpdateProductSessionDeliveryPoints updateProductSessionDeliveryPoints) {
        return httpClient.buildRequest(HttpMethod.PUT, PRODUCTS_SESSIONS_DELIVERY_POINTS)
                .pathParams(productId, eventId)
                .body(new ClientRequestBody(updateProductSessionDeliveryPoints))
                .execute(ProductSessionDeliveryPoints.class);
    }

    public String createAvetSectorRestriction(Long eventId, AvetSectorRestrictionCreate avetSectorRestrictionCreate) {
        return httpClient.buildRequest(HttpMethod.POST, AVET_SECTOR_RESTRICTIONS)
                .pathParams(eventId)
                .body(new ClientRequestBody(avetSectorRestrictionCreate))
                .execute(CodeDTO.class).getCode();
    }

    public AvetSectorRestrictions getAvetSectorRestrictions(Long eventId) {
        return httpClient.buildRequest(HttpMethod.GET, AVET_SECTOR_RESTRICTIONS)
                .pathParams(eventId)
                .execute(AvetSectorRestrictions.class);
    }

    public AvetSectorRestriction getAvetSectorRestriction(Long eventId, String restrictionId) {
        return httpClient.buildRequest(HttpMethod.GET, AVET_SECTOR_RESTRICTION)
                .pathParams(eventId, restrictionId)
                .execute(AvetSectorRestriction.class);
    }

    public void updateAvetSectorRestriction(Long eventId, String restrictionId, UpdateAvetSectorRestriction updateAvetSectorRestriction) {
        httpClient.buildRequest(HttpMethod.PUT, AVET_SECTOR_RESTRICTION)
                .pathParams(eventId, restrictionId)
                .body(new ClientRequestBody(updateAvetSectorRestriction))
                .execute();
    }

    public void deleteAvetSectorRestriction(Long eventId, String restrictionId) {
        httpClient.buildRequest(HttpMethod.DELETE, AVET_SECTOR_RESTRICTION)
                .pathParams(eventId, restrictionId)
                .execute();
    }

    public SeasonTicketReleaseSeat getSeasonTicketReleaseSeat(Long seasonTicketId) {
        return httpClient.buildRequest(HttpMethod.GET, SEASON_TICKET_RELEASE_SEAT)
                .pathParams(seasonTicketId)
                .execute(SeasonTicketReleaseSeat.class);
    }

    public void updateSeasonTicketReleaseSeat(Long seasonTicketId, SeasonTicketReleaseSeat seasonTicketReleaseSeat) {
        httpClient.buildRequest(HttpMethod.PUT, SEASON_TICKET_RELEASE_SEAT)
                .pathParams(seasonTicketId)
                .body(new ClientRequestBody(seasonTicketReleaseSeat))
                .execute();
    }

    public SeasonTicketTransferSeat getSeasonTicketTransferSeat(Long seasonTicketId) {
        return httpClient.buildRequest(HttpMethod.GET, SEASON_TICKET_TRANSFER_SEAT)
                .pathParams(seasonTicketId)
                .execute(SeasonTicketTransferSeat.class);
    }

    public void updateSeasonTicketTransferSeat(Long seasonTicketId, UpdateSeasonTicketTransferSeat seasonTicketTransferSeat) {
        httpClient.buildRequest(HttpMethod.PUT, SEASON_TICKET_TRANSFER_SEAT)
                .pathParams(seasonTicketId)
                .body(new ClientRequestBody(seasonTicketTransferSeat))
                .execute();
    }

    public SeasonTicketRedemption getSeasonTicketRedemption(Long seasonTicketId) {
        return httpClient.buildRequest(HttpMethod.GET, SEASON_TICKET_REDEMPTION)
                .pathParams(seasonTicketId)
                .execute(SeasonTicketRedemption.class);
    }

    public void updateSeasonTicketRedemption(Long seasonTicketId, UpdateSeasonTicketRedemption seasonTicketRedemption) {
        httpClient.buildRequest(HttpMethod.PUT, SEASON_TICKET_REDEMPTION)
                .pathParams(seasonTicketId)
                .body(new ClientRequestBody(seasonTicketRedemption))
                .execute();
    }

    public SessionTagsResponse getSessionTags(Long eventId, Long sessionId) {
        return httpClient.buildRequest(HttpMethod.GET, EVENT_TAGS)
                .pathParams(eventId, sessionId)
                .execute(SessionTagsResponse.class);
    }

    public SessionTagResponse createSessionTag(Long eventId, Long sessionId, SessionTagRequest sessionTagRequest) {
        return httpClient.buildRequest(HttpMethod.POST, EVENT_TAGS)
                .pathParams(eventId, sessionId)
                .body(new ClientRequestBody(sessionTagRequest))
                .execute(SessionTagResponse.class);
    }

    public void updateSessionTag(Long eventId, Long sessionId, Long positionId, SessionTagRequest sessionTagRequest) {
        httpClient.buildRequest(HttpMethod.PUT, EVENT_TAGS_POSITION)
                .pathParams(eventId, sessionId, positionId)
                .body(new ClientRequestBody(sessionTagRequest))
                .execute();
    }

    public void deleteSessionTag(Long eventId, Long sessionId, Long positionId) {
        httpClient.buildRequest(HttpMethod.DELETE, EVENT_TAGS_POSITION)
                .pathParams(eventId, sessionId, positionId)
                .execute();
    }

    public ExportProcess generatePriceSimulation(Long saleRequestId,
                                                 PriceSimulationExportFilter filter) {
        ClientRequestBody body = new ClientRequestBody(filter);
        return httpClient.buildRequest(HttpMethod.POST, REPORT_PRICE_SIMULATION)
                .pathParams(saleRequestId)
                .body(body)
                .execute(ExportProcess.class);
    }

    public EventSecondaryMarketConfig getEventSecondaryMarketConfig(Long eventId) {
        return httpClient.buildRequest(HttpMethod.GET, EVENT_SECONDARY_MARKET_CONFIG)
                .pathParams(eventId)
                .execute(EventSecondaryMarketConfig.class);
    }

    public void createEventSecondaryMarketConfig(Long eventId, EventSecondaryMarketConfig eventSecondaryMarketConfig) {
        httpClient.buildRequest(HttpMethod.POST, EVENT_SECONDARY_MARKET_CONFIG)
                .pathParams(eventId)
                .body(new ClientRequestBody(eventSecondaryMarketConfig))
                .execute();
    }

    public SeasonTicketSecondaryMarketConfig getSeasonTicketSecondaryMarketConfig(Long eventId) {
        return httpClient.buildRequest(HttpMethod.GET, EVENT_SECONDARY_MARKET_CONFIG)
                .pathParams(eventId)
                .execute(SeasonTicketSecondaryMarketConfig.class);
    }

    public void createSeasonTicketSecondaryMarketConfig(Long eventId, SeasonTicketSecondaryMarketConfig seasonTicketSecondaryMarketConfig) {
        httpClient.buildRequest(HttpMethod.POST, EVENT_SECONDARY_MARKET_CONFIG)
                .pathParams(eventId)
                .body(new ClientRequestBody(seasonTicketSecondaryMarketConfig))
                .execute();
    }

    public SessionSecondaryMarketConfig getSessionSecondaryMarketConfig(Long sessionId) {
        return httpClient.buildRequest(HttpMethod.GET, SESSION_SECONDARY_MARKET_CONFIG)
                .pathParams(sessionId)
                .execute(SessionSecondaryMarketConfig.class);
    }

    public void createSessionSecondaryMarketConfig(Long sessionId, SessionSecondaryMarketConfig eventSecondaryMarketConfig) {
        httpClient.buildRequest(HttpMethod.POST, SESSION_SECONDARY_MARKET_CONFIG)
                .pathParams(sessionId)
                .body(new ClientRequestBody(eventSecondaryMarketConfig))
                .execute();
    }

    public void deleteSessionSecondaryMarketConfig(Long sessionId) {
        httpClient.buildRequest(HttpMethod.DELETE, SESSION_SECONDARY_MARKET_CONFIG)
                .pathParams(sessionId)
                .execute();
    }

    public ProductAttributeValue getProductAttributeValue(Long productId, Long attributeId, Long valueId) {
        return httpClient.buildRequest(HttpMethod.GET, PRODUCTS_ATTRIBUTES + ATTRIBUTE_ID + VALUES + VALUE_ID)
                .pathParams(productId, attributeId, valueId)
                .execute(ProductAttributeValue.class);
    }

    public Long createProductAttributeValue(Long productId, Long attributeId, CreateProductAttributeValue productAttributeValue) {
        return httpClient.buildRequest(HttpMethod.POST, PRODUCTS_ATTRIBUTE_VALUES)
                .pathParams(productId, attributeId)
                .body(new ClientRequestBody(productAttributeValue))
                .execute(IdDTO.class).getId();
    }

    public ProductAttributeValues getProductAttributeValues(Long productId, Long attributeId,
                                                            SearchProductAttributeValueFilterDTO searchProductAttributeValueFilterDTO) {
        QueryParameters.Builder builder = new QueryParameters.Builder();
        builder.addQueryParameters(searchProductAttributeValueFilterDTO);

        return httpClient.buildRequest(HttpMethod.GET, PRODUCTS_ATTRIBUTE_VALUES)
                .pathParams(productId, attributeId, searchProductAttributeValueFilterDTO)
                .params(builder.build())
                .execute(ProductAttributeValues.class);
    }

    public void updateProductAttributeValue(Long productId, Long attributeId, Long valueId,
                                            ProductAttributeValue productAttributeValue) {
        httpClient.buildRequest(HttpMethod.PUT, PRODUCTS_ATTRIBUTE_VALUES + VALUE_ID)
                .pathParams(productId, attributeId, valueId)
                .body(new ClientRequestBody(productAttributeValue))
                .execute();
    }

    public void deleteProductAttributeValue(Long productId, Long attributeId, Long valueId) {
        httpClient.buildRequest(HttpMethod.DELETE, PRODUCTS_ATTRIBUTE_VALUES + VALUE_ID)
                .pathParams(productId, attributeId, valueId)
                .execute();
    }

    public ProductLiterals getProductAttributeLiterals(Long productId, Long attributeId, String languageCode) {
        QueryParameters parameters = new QueryParameters.Builder()
                .addQueryParameter(LANGUAGE, languageCode)
                .build();
        return httpClient.buildRequest(HttpMethod.GET, ATTRIBUTE_CONTENTS_TEXT)
                .pathParams(productId, attributeId)
                .params(parameters)
                .execute(ProductLiterals.class);
    }

    public void createOrUpdateProductAttributeLiterals(Long productId, Long attributeId, ProductLiterals body) {
        httpClient.buildRequest(HttpMethod.POST, ATTRIBUTE_CONTENTS_TEXT)
                .pathParams(productId, attributeId)
                .body(new ClientRequestBody(body)).execute();
    }

    public ProductLiterals getProductValueLiterals(Long productId, Long attributeId, Long valueId, String languageCode) {
        QueryParameters parameters = new QueryParameters.Builder()
                .addQueryParameter(LANGUAGE, languageCode)
                .build();
        return httpClient.buildRequest(HttpMethod.GET, VALUE_CONTENTS_TEXT)
                .pathParams(productId, attributeId, valueId)
                .params(parameters)
                .execute(ProductLiterals.class);
    }

    public ProductValueLiterals getProductBulkValueLiterals(Long productId, Long attributeId, String languageCode) {
        QueryParameters parameters = new QueryParameters.Builder()
                .addQueryParameter(LANGUAGE, languageCode)
                .build();
        return httpClient.buildRequest(HttpMethod.GET, BULK_VALUE_CONTENTS_TEXT)
                .pathParams(productId, attributeId)
                .params(parameters)
                .execute(ProductValueLiterals.class);
    }

    public void createOrUpdateProductValueLiterals(Long productId, Long attributeId, Long valueId, ProductLiterals body) {
        httpClient.buildRequest(HttpMethod.POST, VALUE_CONTENTS_TEXT)
                .pathParams(productId, attributeId, valueId)
                .body(new ClientRequestBody(body)).execute();
    }

    public void createOrUpdateProductBulkValueLiterals(Long productId, Long attributeId, ProductValueLiterals body) {
        httpClient.buildRequest(HttpMethod.POST, BULK_VALUE_CONTENTS_TEXT)
                .pathParams(productId, attributeId)
                .body(new ClientRequestBody(body)).execute();
    }

    public ProductTicketLiterals getProductTicketLiterals(Long productId, String languageCode, String key) {
        QueryParameters parameters = new QueryParameters.Builder()
                .addQueryParameter("key", key)
                .build();
        return httpClient.buildRequest(HttpMethod.GET, TICKET_CONTENTS_TEXT)
                .pathParams(productId, languageCode)
                .params(parameters)
                .execute(ProductTicketLiterals.class);
    }

    public void createOrUpdateProductTicketLiterals(Long productId, String languageCode, ProductTicketLiterals body) {
        httpClient.buildRequest(HttpMethod.POST, TICKET_CONTENTS_TEXT)
                .pathParams(productId, languageCode)
                .body(new ClientRequestBody(body)).execute();
    }

    public void setProductSurcharge(long productId, List<ProductSurcharge> msProductSurchargeRequestDTO) {
        httpClient.buildRequest(HttpMethod.POST, PRODUCTS + PRODUCT_ID + SURCHARGES)
                .pathParams(productId)
                .body(new ClientRequestBody(msProductSurchargeRequestDTO))
                .execute();
    }

    public List<ProductSurcharge> getProductSurcharges(Long productId, List<SurchargeTypeDTO> types) {
        QueryParameters.Builder params = new QueryParameters.Builder();
        if (!CommonUtils.isEmpty(types)) {
            for (SurchargeTypeDTO surchargeType : types) {
                params.addQueryParameter("types", surchargeType.toString());
            }
        }
        return httpClient.buildRequest(HttpMethod.GET, PRODUCTS + PRODUCT_ID + SURCHARGES)
                .pathParams(productId)
                .params(params.build())
                .execute(ListType.of(ProductSurcharge.class));
    }

    public LoyaltyPointsConfig getLoyaltyPointsConfig(Long eventId, Long sessionId) {
        return httpClient.buildRequest(HttpMethod.GET, EVENTS + EVENT_ID + SESSIONS + SESSION_ID + "/loyalty-points")
                .pathParams(eventId, sessionId)
                .execute(LoyaltyPointsConfig.class);
    }

    public void updateLoyaltyPointsConfig(Long eventId, Long sessionId, UpdateSessionsLoyaltyPointsConfig updateLoyaltyPointsConfig) {
        httpClient.buildRequest(HttpMethod.PUT, EVENTS + EVENT_ID + SESSIONS + SESSION_ID + "/loyalty-points")
                .pathParams(eventId, sessionId)
                .body(new ClientRequestBody(updateLoyaltyPointsConfig))
                .execute();
    }

    public SeasonTicketLoyaltyPointsConfig getSeasonTicketLoyaltyPoints(Long seasonTicketId) {
        return httpClient.buildRequest(HttpMethod.GET, SEASON_TICKET_LOYALTY_POINTS)
                .pathParams(seasonTicketId)
                .execute(SeasonTicketLoyaltyPointsConfig.class);
    }

    public void updateSeasonTicketLoyaltyPoints(Long seasonTicketId, SeasonTicketLoyaltyPointsConfig seasonTicketLoyaltyPointsConfigs) {
        httpClient.buildRequest(HttpMethod.PUT, SEASON_TICKET_LOYALTY_POINTS)
                .pathParams(seasonTicketId)
                .body(new ClientRequestBody(seasonTicketLoyaltyPointsConfigs))
                .execute();
    }

    public Form getSeasonTicketForm(Long seasonTicketId, String formType) {
        return httpClient.buildRequest(HttpMethod.GET, SEASON_TICKET_FORMS)
                .pathParams(seasonTicketId, formType)
                .execute(Form.class);
    }

    public void updateSeasonTicketForm(Long seasonTicketId, String formType, Form updateForm) {
        httpClient.buildRequest(HttpMethod.PUT, SEASON_TICKET_FORMS)
                .pathParams(seasonTicketId, formType)
                .body(new ClientRequestBody(updateForm))
                .execute();
    }

    public void createProductTicketContentPdfText(Long productId, ProductTicketContentTextList contentTextLists, ProductTicketContentType type) {
        httpClient.buildRequest(HttpMethod.POST, PRODUCT_TICKET_CONTENTS_TEXT_PDF)
                .pathParams(productId, type)
                .body(new ClientRequestBody(contentTextLists))
                .execute();
    }

    public ProductTicketContentTextList getProductTicketContentPdfText(Long productId) {
        return httpClient.buildRequest(HttpMethod.GET, PRODUCT_TICKET_CONTENTS_TEXT_PDF)
                .pathParams(productId)
                .execute(ProductTicketContentTextList.class);
    }

    public void createProductTicketContentPdfImage(Long productId, ProductTicketContentImagePdfList contentTextLists, ProductTicketContentType type) {
        httpClient.buildRequest(HttpMethod.POST, PRODUCT_TICKET_CONTENTS_IMAGE_PDF)
                .pathParams(productId, type)
                .body(new ClientRequestBody(contentTextLists))
                .execute();
    }

    public ProductTicketContentImagePdfList getProductTicketContentPdfImage(Long productId) {
        return httpClient.buildRequest(HttpMethod.GET, PRODUCT_TICKET_CONTENTS_IMAGE_PDF)
                .pathParams(productId)
                .execute(ProductTicketContentImagePdfList.class);
    }

    public void deleteProductTicketContentsPdfImage(Long productId, String language) {
        httpClient.buildRequest(HttpMethod.DELETE, PRODUCT_TICKET_CONTENTS_IMAGE_PDF + "/languages/{language}")
                .pathParams(productId, language)
                .execute();
    }

    public void createProductTicketContentPassbookText(Long productId, ProductTicketContentTextList contentTextLists, ProductTicketContentType type) {
        httpClient.buildRequest(HttpMethod.POST, PRODUCT_TICKET_CONTENTS_TEXT_PASSBOOK)
                .pathParams(productId, type)
                .body(new ClientRequestBody(contentTextLists))
                .execute();
    }

    public ProductTicketContentTextList getProductTicketContentPassbookText(Long productId) {
        return httpClient.buildRequest(HttpMethod.GET, PRODUCT_TICKET_CONTENTS_TEXT_PASSBOOK)
                .pathParams(productId)
                .execute(ProductTicketContentTextList.class);
    }

    public void createProductTicketContentPassbookImage(Long productId, ProductTicketContentImagePassbookList contentTextLists, ProductTicketContentType type) {
        httpClient.buildRequest(HttpMethod.POST, PRODUCT_TICKET_CONTENTS_IMAGE_PASSBOOK)
                .pathParams(productId, type)
                .body(new ClientRequestBody(contentTextLists))
                .execute();
    }

    public ProductTicketContentImagePassbookList getProductTicketContentPassbookImage(Long productId) {
        return httpClient.buildRequest(HttpMethod.GET, PRODUCT_TICKET_CONTENTS_IMAGE_PASSBOOK)
                .pathParams(productId)
                .execute(ProductTicketContentImagePassbookList.class);
    }

    public void deleteProductTicketContentsPassbookImage(Long productId, String language) {
        httpClient.buildRequest(HttpMethod.DELETE, PRODUCT_TICKET_CONTENTS_IMAGE_PASSBOOK + "/languages/{language}")
                .pathParams(productId, language)
                .execute();
    }

    /* Start Dynamic price */
    public DynamicPriceConfig getSessionDynamicPriceConfig(Long eventId, Long sessionId, Boolean initialize) {
        QueryParameters parameters = new QueryParameters.Builder()
                .addQueryParameter("initialize", initialize)
                .build();
        return httpClient.buildRequest(HttpMethod.GET, DYNAMIC_PRICE)
                .pathParams(eventId, sessionId)
                .params(parameters)
                .execute(DynamicPriceConfig.class);
    }

    public DynamicPriceZone getDynamicPriceZone(Long eventId, Long sessionId, Long idPriceZone) {
        return httpClient.buildRequest(HttpMethod.GET, DYNAMIC_PRICE_PRICE_ZONE)
                .pathParams(eventId, sessionId, idPriceZone)
                .execute(DynamicPriceZone.class);
    }

    public List<DynamicRatesPrice> getDynamicRatePrice(Long eventId, Long sessionId, Long idPriceZone) {
        return httpClient.buildRequest(HttpMethod.GET, DYNAMIC_PRICE_PRICE_ZONE_RATES)
                .pathParams(eventId, sessionId, idPriceZone)
                .execute(ListType.of(DynamicRatesPrice.class));
    }

    public void createOrUpdateDynamicPrice(Long eventId, Long sessionId, Long idPriceZone, List<DynamicPrice> dynamicPrices) {
        httpClient.buildRequest(HttpMethod.POST, DYNAMIC_PRICE_PRICE_ZONE)
                .pathParams(eventId, sessionId, idPriceZone)
                .body(new ClientRequestBody(dynamicPrices))
                .execute();
    }

    public void deleteSessionDynamicPriceConfig(Long eventId, Long sessionId, Long idPriceZone, Integer orderId) {
        httpClient.buildRequest(HttpMethod.DELETE, DYNAMIC_PRICE_ORDER_ID)
                .pathParams(eventId, sessionId, idPriceZone, orderId)
                .execute();
    }

    public void activateDynamicPriceConfig(Long eventId, Long sessionId, DynamicPriceStatusRequest request) {
        httpClient.buildRequest(HttpMethod.PUT, DYNAMIC_PRICE)
                .pathParams(eventId, sessionId)
                .body(new ClientRequestBody(request))
                .execute();
    }
    /* End Dynamic price */

    public List<EventCustomerType> getEventCustomerTypes(Long eventId) {
        return httpClient.buildRequest(HttpMethod.GET, EVENT_CUSTOMER_TYPES)
                .pathParams(eventId)
                .execute(ListType.of(EventCustomerType.class));
    }

    public void putEventCustomerTypes(Long eventId, UpdateEventCustomerTypes eventCustomerTypes) {
        httpClient.buildRequest(HttpMethod.PUT, EVENT_CUSTOMER_TYPES)
                .body(new ClientRequestBody(eventCustomerTypes))
                .pathParams(eventId)
                .execute();
    }

    public List<EventCommunicationElement> getChannelEventCommunicationElements(Long eventId, Long channelId, CommunicationElementFilter<EventTagType> filter) {
        return httpClient.buildRequest(HttpMethod.GET, CHANNEL_EVENT_COMMUNICATION_ELEMENTS)
                .pathParams(eventId, channelId)
                .params(new QueryParameters.Builder().addQueryParameters(filter).build())
                .execute(ListType.of(EventCommunicationElement.class));
    }


    public List<ChannelEventImageConfigDTO> getChannelEventImageConfig(Long eventId, Long channelId) {
        return httpClient.buildRequest(HttpMethod.GET, CHANNEL_EVENT_COMMUNICATION_ELEMENTS + "/images-config")
                .pathParams(eventId, channelId)
                .execute(ListType.of(ChannelEventImageConfigDTO.class));
    }

    public void updateChannelEventCommunicationElements(Long eventId, Long channelId, List<EventCommunicationElement> elements) {
        httpClient.buildRequest(HttpMethod.POST, CHANNEL_EVENT_COMMUNICATION_ELEMENTS)
                .pathParams(eventId, channelId)
                .body(new ClientRequestBody(elements))
                .execute();
    }

    public List<EventCommunicationElement> getChannelSessionCommunicationElements(Long eventId, Long sessionId, Long channelId, CommunicationElementFilter<EventTagType> filter) {
        return httpClient.buildRequest(HttpMethod.GET, CHANNEL_SESSION_COMMUNICATION_ELEMENTS)
                .pathParams(eventId, sessionId, channelId)
                .params(new QueryParameters.Builder().addQueryParameters(filter).build())
                .execute(ListType.of(EventCommunicationElement.class));
    }

    public void updateChannelSessionCommunicationElements(Long eventId, Long sessionId, Long channelId, List<EventCommunicationElement> elements) {
        httpClient.buildRequest(HttpMethod.POST, CHANNEL_SESSION_COMMUNICATION_ELEMENTS)
                .pathParams(eventId, sessionId, channelId)
                .body(new ClientRequestBody(elements))
                .execute();
    }

    public PacksResponse getPacks(PacksFilterRequest filter) {
        QueryParameters.Builder params = new QueryParameters.Builder();
        params.addQueryParameters(filter);
        return httpClient.buildRequest(HttpMethod.GET, PACKS)
                .params(params.build())
                .execute(PacksResponse.class);
    }

    public PackDetail getPack(Long packId) {
        return httpClient.buildRequest(HttpMethod.GET, PACK)
                .pathParams(packId)
                .execute(PackDetail.class);
    }

    public Pack createPack(CreatePack createPack) {
        return httpClient.buildRequest(HttpMethod.POST, PACKS)
                .body(new ClientRequestBody(createPack))
                .execute(Pack.class);
    }

    public void updatePack(Long packId, UpdatePack updatePack) {
        httpClient.buildRequest(HttpMethod.PUT, PACK)
                .pathParams(packId)
                .body(new ClientRequestBody(updatePack))
                .execute();
    }

    public void deletePack(Long packId) {
        httpClient.buildRequest(HttpMethod.DELETE, PACK)
                .pathParams(packId)
                .execute();
    }

    public List<PackItem> getPackItems(Long packId) {
        return httpClient.buildRequest(HttpMethod.GET, PACK_ITEMS)
                .pathParams(packId)
                .execute(ListType.of(PackItem.class));
    }

    public void createPackItems(Long packId, CreatePackItems createPackItems) {
        httpClient.buildRequest(HttpMethod.POST, PACK_ITEMS)
                .pathParams(packId)
                .body(new ClientRequestBody(createPackItems))
                .execute();
    }

    public void updatePackItem(Long packId, Long packItemId, UpdatePackItem updatePackItem) {
        httpClient.buildRequest(HttpMethod.PUT, PACK_ITEM)
                .pathParams(packId, packItemId)
                .body(new ClientRequestBody(updatePackItem))
                .execute();
    }

    public void deletePackItem(Long packId, Long packItemId) {
        httpClient.buildRequest(HttpMethod.DELETE, PACK_ITEM)
                .pathParams(packId, packItemId)
                .execute();
    }

    public List<PackRate> getPackRates(Long packId) {
        return httpClient.buildRequest(HttpMethod.GET, PACK + RATES)
                .pathParams(packId)
                .execute(ListType.of(PackRate.class));
    }

    public IdDTO createPackRates(Long packId, CreatePackRate createPackRate) {
        return httpClient.buildRequest(HttpMethod.POST, PACK + RATES)
                .pathParams(packId)
                .body(new ClientRequestBody(createPackRate))
                .execute(IdDTO.class);
    }

    public void refreshPackRates(Long packId) {
        httpClient.buildRequest(HttpMethod.POST, PACK + RATES + "/refresh")
                .pathParams(packId)
                .execute();
    }

    public void updatePackRate(Long packId, Long rateId, UpdatePackRate updatePackRate) {
        httpClient.buildRequest(HttpMethod.PUT, PACK + RATES + RATE_ID)
                .pathParams(packId, rateId)
                .body(new ClientRequestBody(updatePackRate))
                .execute(IdDTO.class);
    }

    public void deletePackRate(Long packId, Long rateId) {
        httpClient.buildRequest(HttpMethod.DELETE, PACK + RATES + RATE_ID)
                .pathParams(packId, rateId)
                .execute(IdDTO.class);
    }

    public List<PackPrice> getPackPrices(Long packId) {
        return httpClient.buildRequest(HttpMethod.GET, PACK + PRICES)
                .pathParams(packId)
                .execute(ListType.of(PackPrice.class));
    }

    public void updatePackPrices(Long packId, List<UpdatePackPrice> updatePackPrice) {
        httpClient.buildRequest(HttpMethod.PUT, PACK + PRICES)
                .pathParams(packId)
                .body(new ClientRequestBody(updatePackPrice))
                .execute();
    }

    public List<PackCommunicationElement> getPackCommunicationElements(Long packId, CommunicationElementFilter<PackTagType> filter) {
        return httpClient.buildRequest(HttpMethod.GET, PACK_COMMUNICATION_ELEMENTS)
                .pathParams(packId)
                .params(new QueryParameters.Builder().addQueryParameters(filter).build())
                .execute(ListType.of(PackCommunicationElement.class));
    }

    public void updatePackCommunicationElements(Long packId, List<PackCommunicationElement> elements) {
        httpClient.buildRequest(HttpMethod.POST, PACK_COMMUNICATION_ELEMENTS)
                .pathParams(packId)
                .body(new ClientRequestBody(elements))
                .execute();
    }

    public List<ChannelTicketContent> getPackTicketContent(Long packId, String language, String type, TicketCommunicationElementCategory category) {
        return httpClient.buildRequest(HttpMethod.GET, PACK_TICKET_CONTENTS)
                .pathParams(packId, category)
                .params(new QueryParameters.Builder()
                        .addQueryParameter("language", language)
                        .addQueryParameter("type", type)
                        .build())
                .execute(ListType.of(ChannelTicketContent.class));
    }

    public void updatePackTicketContent(Long packId, List<ChannelTicketContent> body, TicketCommunicationElementCategory category) {
        httpClient.buildRequest(HttpMethod.PUT, PACK_TICKET_CONTENTS)
                .pathParams(packId, category)
                .body(new ClientRequestBody(body))
                .execute();
    }

    public void deletePackTicketContent(Long packId, String language, String type, TicketCommunicationElementCategory category) {
        httpClient.buildRequest(HttpMethod.DELETE, PACK_TICKET_CONTENTS_DELETE)
                .pathParams(packId, category, language, type)
                .execute();
    }

    public PackItemPriceTypesResponse getPackItemPriceTypes(Long packId, Long packItemId) {
        return httpClient.buildRequest(HttpMethod.GET, PACK_ITEM_PRICE_TYPES)
                .pathParams(packId, packItemId)
                .execute(PackItemPriceTypesResponse.class);
    }

    public void updatePackItemPriceTypes(Long packId, Long packItemId, PackItemPriceTypesRequest priceTyepsRequest) {
        httpClient.buildRequest(HttpMethod.PUT, PACK_ITEM_PRICE_TYPES)
                .pathParams(packId, packItemId)
                .body(new ClientRequestBody(priceTyepsRequest))
                .execute();
    }

    public PackItemSubItemsResponse getPackItemSubitems(Long packId, Long packItemId, PackItemSubitemFilterDTO filter) {
        QueryParameters params = new QueryParameters.Builder().addQueryParameters(filter).build();
        return httpClient.buildRequest(HttpMethod.GET, PACK_ITEM_SUBITEMS)
                .pathParams(packId, packItemId)
                .params(params)
                .execute(PackItemSubItemsResponse.class);
    }

    public void updatePackItemSubitems(Long packId, Long packItemId, UpdatePackItemSubitemsRequest request) {
        httpClient.buildRequest(HttpMethod.PUT, PACK_ITEM_SUBITEMS)
                .pathParams(packId, packItemId)
                .body(new ClientRequestBody(request))
                .execute();
    }

    public PackChannels getPackChannels(Long packId) {
        return httpClient.buildRequest(HttpMethod.GET, PACK + CHANNELS)
                .pathParams(packId)
                .execute(PackChannels.class);
    }

    public PackChannel getPackChannel(Long packId, Long channelId) {
        return httpClient.buildRequest(HttpMethod.GET, PACK + CHANNELS + CHANNEL_ID)
                .pathParams(packId, channelId)
                .execute(PackChannel.class);
    }

    public void createPackChannel(Long packId, List<Long> channelIds) {
        PackChannelCreate request = new PackChannelCreate();
        request.setChannelIds(channelIds);
        httpClient.buildRequest(HttpMethod.POST, PACK + CHANNELS)
                .pathParams(packId)
                .body(new ClientRequestBody(request))
                .execute();
    }

    public void updatePackChannel(Long packId, Long channelId, UpdatePackChannel request) {
        httpClient.buildRequest(HttpMethod.PUT, PACK + CHANNELS + CHANNEL_ID)
                .pathParams(packId, channelId)
                .body(new ClientRequestBody(request))
                .execute();
    }

    public void deletePackChannel(Long packId, Long channelId) {
        httpClient.buildRequest(HttpMethod.DELETE, PACK + CHANNELS + CHANNEL_ID)
                .pathParams(packId, channelId)
                .execute();
    }

    public void requestPackChannelApproval(Long packId, Long channelId, Long userId) {
        PackChannelRequestSales request = new PackChannelRequestSales();
        request.setUserId(userId);
        httpClient.buildRequest(HttpMethod.POST, PACK + CHANNELS + CHANNEL_ID + "/request-approval")
                .pathParams(packId, channelId)
                .body(new ClientRequestBody(request))
                .execute();
    }

    public PostBookingQuestions getPostBookingQuestions(PostBookingQuestionsFilter filter) {
        QueryParameters.Builder builder = new QueryParameters.Builder();
        builder.addQueryParameters(filter);
        return httpClient.buildRequest(HttpMethod.GET, POST_BOOKING_QUESTIONS)
                .params(builder.build())
                .execute(PostBookingQuestions.class);
    }

    public EventPostBookingQuestions getEventPostBookingQuestions(Integer eventId) {
        return httpClient.buildRequest(HttpMethod.GET, EVENT_POST_BOOKING_QUESTIONS)
                .pathParams(eventId)
                .execute(EventPostBookingQuestions.class);
    }

    public void updateEventPostBookingQuestions(Integer eventId, UpdateEventPostBookingQuestions request) {
        httpClient.buildRequest(HttpMethod.PUT, EVENT_POST_BOOKING_QUESTIONS)
                .pathParams(eventId)
                .body(new ClientRequestBody(request))
                .execute();
    }

    public void updateSessionTaxes(Long sessionId, SeasonTicketTax taxes) {
        httpClient.buildRequest(HttpMethod.PUT, SESSIONS + SESSION_ID + "/taxes")
                .pathParams(sessionId)
                .body(new ClientRequestBody(taxes))
                .execute();
    }
}


