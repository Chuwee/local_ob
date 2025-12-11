package es.onebox.event.seasontickets.service.renewals;

import co.elastic.clients.elasticsearch.core.SearchResponse;
import es.onebox.core.exception.CoreErrorCode;
import es.onebox.core.exception.ExceptionBuilder;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.core.serializer.dto.request.BaseRequestFilter;
import es.onebox.core.serializer.dto.request.Direction;
import es.onebox.core.serializer.dto.request.SortOperator;
import es.onebox.core.serializer.dto.response.Metadata;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.core.utils.common.NumberUtils;
import es.onebox.elasticsearch.dao.Page;
import es.onebox.event.common.utils.ConverterUtils;
import es.onebox.event.datasources.ms.client.dto.CustomerExternalProduct;
import es.onebox.event.datasources.ms.client.repository.ExternalProductsRepository;
import es.onebox.event.datasources.ms.order.dto.ProductSearchResponse;
import es.onebox.event.datasources.ms.order.repository.OrdersRepository;
import es.onebox.event.datasources.ms.ticket.dto.TicketDTO;
import es.onebox.event.datasources.ms.ticket.dto.TicketsSearchResponse;
import es.onebox.event.datasources.ms.ticket.dto.seasontickets.UpdateRelatedSeatsResponse;
import es.onebox.event.datasources.ms.ticket.dto.seasontickets.UpdateRelatedSeatsResponseItem;
import es.onebox.event.datasources.ms.ticket.dto.seasontickets.renewals.RenewalExternalOriginSeat;
import es.onebox.event.datasources.ms.ticket.dto.seasontickets.renewals.RenewalSeasonTicketOriginSeat;
import es.onebox.event.datasources.ms.ticket.dto.seasontickets.renewals.RenewalSeasonTicketRenewalSeat;
import es.onebox.event.datasources.ms.ticket.dto.seasontickets.renewals.RenewalSeat;
import es.onebox.event.datasources.ms.ticket.dto.seasontickets.renewals.SeasonTicketRenewalResponse;
import es.onebox.event.datasources.ms.ticket.enums.TicketStatus;
import es.onebox.event.datasources.ms.ticket.repository.SeasonTicketRepository;
import es.onebox.event.datasources.ms.ticket.repository.TicketsRepository;
import es.onebox.event.datasources.ms.venue.dto.CapacityMapDTO;
import es.onebox.event.datasources.ms.venue.dto.RowCapacityDTO;
import es.onebox.event.datasources.ms.venue.dto.SectorDTO;
import es.onebox.event.datasources.ms.venue.repository.VenuesRepository;
import es.onebox.event.events.dao.record.RenewalRecord;
import es.onebox.event.events.dto.VenueDTO;
import es.onebox.event.events.request.RatesFilter;
import es.onebox.event.exception.MsEventRateErrorCode;
import es.onebox.event.exception.MsEventSeasonTicketErrorCode;
import es.onebox.event.externalevents.controller.dto.ExternalEventDTO;
import es.onebox.event.externalevents.service.ExternalEventsService;
import es.onebox.event.seasontickets.amqp.renewals.elastic.RenewalsElasticUpdaterService;
import es.onebox.event.seasontickets.amqp.renewals.purge.PurgeRenewalSeatsProducerService;
import es.onebox.event.seasontickets.amqp.renewals.relatedseats.RenewalsUpdateRelatedSeatsRequestItem;
import es.onebox.event.seasontickets.amqp.renewals.relatedseats.RenewalsUpdateRelatedSeatsService;
import es.onebox.event.seasontickets.converter.SeasonTicketRenewalsConverter;
import es.onebox.event.seasontickets.dao.RenewalDao;
import es.onebox.event.seasontickets.dao.RenewalElasticDao;
import es.onebox.event.seasontickets.dao.couch.AutomaticRenewalStatus;
import es.onebox.event.seasontickets.dao.couch.SeasonTicketReleaseSeat;
import es.onebox.event.seasontickets.dao.couch.SeasonTicketReleaseSeatCouchDao;
import es.onebox.event.seasontickets.dao.couch.SeasonTicketRenewalConfig;
import es.onebox.event.seasontickets.dao.couch.SeasonTicketRenewalConfigCouchDao;
import es.onebox.event.seasontickets.dao.couch.SeasonTicketRenewalCouchDao;
import es.onebox.event.seasontickets.dao.couch.SeasonTicketRenewalCouchDocument;
import es.onebox.event.seasontickets.dao.couch.SeasonTicketRenewalProduct;
import es.onebox.event.seasontickets.dao.couch.SeasonTicketRenewalStatus;
import es.onebox.event.seasontickets.dao.couch.SeasonTicketSeat;
import es.onebox.event.seasontickets.dao.couch.SeasonTicketSeatType;
import es.onebox.event.seasontickets.dao.dto.RenewalDataElastic;
import es.onebox.event.seasontickets.dao.dto.RenewalStatusES;
import es.onebox.event.seasontickets.dto.SeasonTicketDTO;
import es.onebox.event.seasontickets.dto.SeasonTicketInternalGenerationStatus;
import es.onebox.event.seasontickets.dto.SeasonTicketRateDTO;
import es.onebox.event.seasontickets.dto.SeasonTicketRatesDTO;
import es.onebox.event.seasontickets.dto.SeasonTicketsDTO;
import es.onebox.event.seasontickets.dto.renewals.CountRenewalsPurgeResponse;
import es.onebox.event.seasontickets.dto.renewals.DeleteRenewalsRequest;
import es.onebox.event.seasontickets.dto.renewals.DeleteRenewalsResponse;
import es.onebox.event.seasontickets.dto.renewals.DeleteRenewalsResponseItem;
import es.onebox.event.seasontickets.dto.renewals.RelatedRateDTO;
import es.onebox.event.seasontickets.dto.renewals.RenewalCandidateSeasonTicketDTO;
import es.onebox.event.seasontickets.dto.renewals.RenewalCandidatesSeasonTicketsResponse;
import es.onebox.event.seasontickets.dto.renewals.RenewalEntitiesResponse;
import es.onebox.event.seasontickets.dto.renewals.RenewalGenerationStatus;
import es.onebox.event.seasontickets.dto.renewals.RenewalSeasonTicketDTO;
import es.onebox.event.seasontickets.dto.renewals.RenewalSeatsPurgeFilter;
import es.onebox.event.seasontickets.dto.renewals.SeasonTicketAutomaticRenewalStatus;
import es.onebox.event.seasontickets.dto.renewals.SeasonTicketRenewalSeat;
import es.onebox.event.seasontickets.dto.renewals.SeasonTicketRenewalSeatsFilter;
import es.onebox.event.seasontickets.dto.renewals.SeasonTicketRenewalSeatsResponse;
import es.onebox.event.seasontickets.dto.renewals.SeasonTicketRenewalSeatsSummary;
import es.onebox.event.seasontickets.dto.renewals.SeasonTicketRenewalsConfigDTO;
import es.onebox.event.seasontickets.dto.renewals.UniversalSeatIdentifier;
import es.onebox.event.seasontickets.dto.renewals.UpdateAutomaticRenewalStatus;
import es.onebox.event.seasontickets.dto.renewals.UpdateRenewalErrorReason;
import es.onebox.event.seasontickets.dto.renewals.UpdateRenewalRequest;
import es.onebox.event.seasontickets.dto.renewals.UpdateRenewalRequestItem;
import es.onebox.event.seasontickets.dto.renewals.UpdateRenewalResponse;
import es.onebox.event.seasontickets.dto.renewals.UpdateRenewalResponseItem;
import es.onebox.event.seasontickets.dto.renewals.UpdateSeasonTicketRenewalsConfigDTO;
import es.onebox.event.seasontickets.elasticsearch.PaginationUtils;
import es.onebox.event.seasontickets.elasticsearch.RenewalsESUtils;
import es.onebox.event.seasontickets.request.SeasonTicketSearchFilter;
import es.onebox.event.seasontickets.service.SeasonTicketRateService;
import es.onebox.event.seasontickets.service.SeasonTicketService;
import es.onebox.jooq.cpanel.tables.records.CpanelRenewalRecord;
import es.onebox.jooq.exception.EntityNotFoundException;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class SeasonTicketRenewalsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SeasonTicketRenewalsService.class);
    private final Long LIMIT = 1000L;

    private final SeasonTicketService seasonTicketService;
    private final SeasonTicketRenewalCouchDao seasonTicketRenewalCouchDao;
    private final OrdersRepository ordersRepository;
    private final SeasonTicketRepository seasonTicketRepository;
    private final RenewalElasticDao renewalElasticDao;
    private final RenewalsElasticUpdaterService renewalsElasticUpdaterService;
    private final RenewalDao renewalDao;
    private final TicketsRepository ticketsRepository;
    private final VenuesRepository venuesRepository;
    private final RenewalsUpdateRelatedSeatsService renewalsUpdateRelatedSeatsService;
    private final SeasonTicketRateService seasonTicketRateService;
    private final ExternalEventsService externalEventsService;
    private final ExternalProductsRepository externalProductsRepository;
    private final PurgeRenewalSeatsProducerService purgeRenewalSeatsProducerService;
    private final SeasonTicketReleaseSeatCouchDao releaseSeatCouchDao;
    private final SeasonTicketRenewalConfigCouchDao seasonTicketRenewalConfigCouchDao;

    @Autowired
    public SeasonTicketRenewalsService(@Lazy SeasonTicketService seasonTicketService, SeasonTicketRenewalCouchDao seasonTicketRenewalCouchDao,
                                       OrdersRepository ordersRepository, SeasonTicketRepository seasonTicketRepository,
                                       RenewalElasticDao renewalElasticDao, RenewalsElasticUpdaterService renewalsElasticUpdaterService,
                                       RenewalDao renewalDao, TicketsRepository ticketsRepository,
                                       VenuesRepository venuesRepository, RenewalsUpdateRelatedSeatsService renewalsUpdateRelatedSeatsService,
                                       SeasonTicketRateService seasonTicketRateService, ExternalEventsService externalEventsService,
                                       ExternalProductsRepository externalProductsRepository, PurgeRenewalSeatsProducerService purgeRenewalSeatsProducerService,
                                       SeasonTicketReleaseSeatCouchDao releaseSeatCouchDao, SeasonTicketRenewalConfigCouchDao seasonTicketRenewalConfigCouchDao) {
        this.seasonTicketService = seasonTicketService;
        this.seasonTicketRenewalCouchDao = seasonTicketRenewalCouchDao;
        this.ordersRepository = ordersRepository;
        this.seasonTicketRepository = seasonTicketRepository;
        this.renewalElasticDao = renewalElasticDao;
        this.renewalsElasticUpdaterService = renewalsElasticUpdaterService;
        this.renewalDao = renewalDao;
        this.ticketsRepository = ticketsRepository;
        this.venuesRepository = venuesRepository;
        this.renewalsUpdateRelatedSeatsService = renewalsUpdateRelatedSeatsService;
        this.seasonTicketRateService = seasonTicketRateService;
        this.externalEventsService = externalEventsService;
        this.externalProductsRepository = externalProductsRepository;
        this.purgeRenewalSeatsProducerService = purgeRenewalSeatsProducerService;
        this.releaseSeatCouchDao = releaseSeatCouchDao;
        this.seasonTicketRenewalConfigCouchDao = seasonTicketRenewalConfigCouchDao;
    }

    public RenewalCandidatesSeasonTicketsResponse searchRenewalCandidatesSeasonTickets(Long seasonTicketId) {
        SeasonTicketDTO seasonTicketDTO = getAndValidateSeasonTicket(seasonTicketId);

        Long entityId = seasonTicketDTO.getEntityId();

        VenueDTO venueDTO = seasonTicketDTO.getVenues().stream().findFirst()
                .orElseThrow(() -> ExceptionBuilder.build(MsEventSeasonTicketErrorCode.SEASON_TICKET_IN_CREATION));
        Long venueId = venueDTO.getId();

        SortOperator<String> sortOperator = new SortOperator<>();
        sortOperator.addDirection(Direction.DESC, "name");

        SeasonTicketSearchFilter filter = new SeasonTicketSearchFilter();
        filter.setEntityId(entityId);
        filter.setVenueId(venueId);
        filter.setLimit(100L);
        filter.setSort(sortOperator);

        SeasonTicketsDTO searchSeasonTicketsResponse = seasonTicketService.searchSeasonTickets(filter);
        List<RenewalCandidateSeasonTicketDTO> seasonTicketRenewalCompatibleList = searchSeasonTicketsResponse.getData().stream()
                .filter(seasonTicket -> !seasonTicket.getId().equals(seasonTicketDTO.getId()))
                .map(seasonTicket -> SeasonTicketRenewalsConverter.getRenewalCandidateSeasonTicketDTO(seasonTicketDTO, seasonTicket))
                .collect(Collectors.toList());

        return new RenewalCandidatesSeasonTicketsResponse(seasonTicketRenewalCompatibleList);
    }

    public void renewalSeasonTicket(Long renewalSeasonTicketId, RenewalSeasonTicketDTO renewalSeasonTicketDTOParam) {
        Boolean isRenewalsUpdate = validateRenewalStatus(renewalSeasonTicketId);

        if (Boolean.TRUE.equals(renewalSeasonTicketDTOParam.getExternalEvent())) {
            doRenewalFromExternalEvent(renewalSeasonTicketId, renewalSeasonTicketDTOParam, isRenewalsUpdate);
        } else {
            doRenewalFromSeasonTicket(renewalSeasonTicketId, renewalSeasonTicketDTOParam, isRenewalsUpdate);
        }
    }

    public void doRenewalFromSeasonTicket(Long renewalSeasonTicketId,
                                          RenewalSeasonTicketDTO renewalSeasonTicketDTOParam,
                                          Boolean isUpdate) {
        Long originSeasonTicketId = renewalSeasonTicketDTOParam.getOriginSeasonTicketId();
        Map<Long, Long> relatedRatesMap = renewalSeasonTicketDTOParam.getRates().stream()
                .collect(Collectors.toMap(
                        RelatedRateDTO::getOldRateId,
                        RelatedRateDTO::getNewRateId
                ));

        // Validate season tickets
        SeasonTicketDTO originSeasonTicketDTO = seasonTicketService.getSeasonTicket(originSeasonTicketId);
        SeasonTicketDTO renewalSeasonTicketDTO = seasonTicketService.getSeasonTicket(renewalSeasonTicketId);
        SeasonTicketInternalGenerationStatus originGenerationStatus = seasonTicketService.getGenerationStatus(originSeasonTicketId);
        SeasonTicketInternalGenerationStatus renewalGenerationStatus = seasonTicketService.getGenerationStatus(renewalSeasonTicketId);
        SeasonTicketRenewalsValidator.validateRenewalSeasonTicket(renewalSeasonTicketDTO, originSeasonTicketDTO, originGenerationStatus,
                renewalGenerationStatus);

        // Validate selected rates
        verifyRates(originSeasonTicketId, renewalSeasonTicketId, renewalSeasonTicketDTOParam.getRates());

        // Initialize the list with the dtos describing original seats used to perform renewals
        List<RenewalSeasonTicketOriginSeat> originSeats = new ArrayList<>();

        Long totalRenewals = 0L;
        long offset = 0L;
        long limit = 1000L;
        // List renewal products iterating over a limited number of products
        Long total = addRenewalProductsFromOriginSeasonTicket(originSeasonTicketId, limit, offset, originSeats,
                originSeasonTicketDTO.getEntityId(), renewalSeasonTicketDTOParam.getIncludeAllEntities(), renewalSeasonTicketDTOParam.getIncludeBalance());
        offset += limit;
        totalRenewals += total;
        while (offset < total) {
            addRenewalProductsFromOriginSeasonTicket(originSeasonTicketId, limit, offset, originSeats, originSeasonTicketDTO.getEntityId(),
                    renewalSeasonTicketDTOParam.getIncludeAllEntities(), renewalSeasonTicketDTOParam.getIncludeBalance());
            offset += limit;
        }

        if (!originSeats.isEmpty()) {
            // Call to ms-ticket to receive corresponding mapping between original old seats and renewal new seats
            SeasonTicketRenewalResponse renewalResponse = seasonTicketRepository.renewalSeasonTicket(renewalSeasonTicketDTO.getSessionId().longValue(),
                    originSeasonTicketDTO.getSessionId().longValue(), originSeats);

            // Map the renewal seats response over the original seat id
            Map<Long, RenewalSeasonTicketRenewalSeat> renewalsSeatMap = renewalResponse.getRenewalSeats().stream()
                    .collect(Collectors.toMap(
                            RenewalSeasonTicketRenewalSeat::getOriginSeatId,
                            Function.identity()));

            // Store the couch documents found or created to deal with multiple renewals for one user
            Map<String, SeasonTicketRenewalCouchDocument> savedDocuments = new HashMap<>();

            CapacityMapDTO oldCapacityMap = null;
            List<String> addedIds = new ArrayList<>();

            if (isUpdate) {
                oldCapacityMap = venuesRepository.getCapacityMap(originSeasonTicketDTO.getSessionId().longValue());
            }

            Map<Long, CapacityMapDTO> oldCapacities = new HashMap<>();
            oldCapacities.put(originSeasonTicketDTO.getId(), oldCapacityMap);

            // Get a list of couch documents to save into the database
            List<SeasonTicketRenewalCouchDocument> couchDocuments = originSeats.stream()
                    .map(originSeat -> getSeasonTicketRenewalCouchDocument(originSeasonTicketId, renewalSeasonTicketId,
                            renewalsSeatMap, originSeat, savedDocuments, null, relatedRatesMap,
                            false, isUpdate, renewalSeasonTicketDTO.getMemberMandatory(),
                            oldCapacities, addedIds))
                    .distinct()
                    .collect(Collectors.toList());

            seasonTicketRenewalCouchDao.bulkUpsert(couchDocuments);

            if (!isUpdate) {
                registerRenewal(renewalSeasonTicketId, originSeasonTicketId, totalRenewals, false);
                renewalsElasticUpdaterService.sendMessage(renewalSeasonTicketId, totalRenewals);
            } else {
                Integer totalRenewalsWithAdded = updateRenewal(renewalSeasonTicketId, addedIds.size());
                renewalsElasticUpdaterService.sendMessage(renewalSeasonTicketId, totalRenewalsWithAdded.longValue());
            }

            updateRelatedSeatsStatus(originSeats, couchDocuments, renewalsSeatMap.values(), renewalSeasonTicketId, renewalSeasonTicketDTO.getSessionId().longValue());
        } else {
            throw new OneboxRestException(MsEventSeasonTicketErrorCode.SEASON_TICKET_RENEWAL_NO_PRODUCTS_TO_RENEWAL);
        }
    }

    private Long addRenewalProductsFromOriginSeasonTicket(Long originSeasonTicketId,
                                                          Long limit, Long offset,
                                                          List<RenewalSeasonTicketOriginSeat> originSeats,
                                                          Long seasonTicketEntityId,
                                                          Boolean includeAllEntities,
                                                          Boolean includeBalance) {

        List<Long> entityIds = null;
        if (CommonUtils.isFalse(includeAllEntities)) {
            entityIds = Collections.singletonList(seasonTicketEntityId);
        }
        // Get active user products from renewal Season Ticket
        ProductSearchResponse response = ordersRepository.getActiveUserProducts(Collections.singletonList(originSeasonTicketId), entityIds, offset, limit);
        Long total = response.getMetadata().getTotal();

        Double earningsLimitPercentage = BooleanUtils.isTrue(includeBalance) ? getEarningsLimitPercentage(originSeasonTicketId) : null;

        // Create a list of dtos describing original seats used to perform renewals
        List<RenewalSeasonTicketOriginSeat> newOriginSeats = response.getData().stream()
                .filter(orderProduct -> orderProduct.getTicketData() != null)
                .map(orderProduct -> {
                    Double maxEarnings = null;
                    if (earningsLimitPercentage != null) {
                        maxEarnings = NumberUtils.percentageOf(orderProduct.getPrice().getBasePrice(), earningsLimitPercentage);
                    }
                    return SeasonTicketRenewalsConverter.createRenewalSeasonTicketOriginSeat(orderProduct, includeBalance, maxEarnings);
                })
                .toList();
        originSeats.addAll(newOriginSeats);

        return total;
    }

    private Double getEarningsLimitPercentage(Long originSeasonTicketId) {
        Double earningsLimitPercentage = null;
        SeasonTicketReleaseSeat releaseConfig = releaseSeatCouchDao.get(originSeasonTicketId.toString());
        if (releaseConfig != null && releaseConfig.getEarningsLimit() != null && BooleanUtils.isTrue(releaseConfig.getEarningsLimit().getEnabled())) {
            earningsLimitPercentage = NumberUtils.zeroIfNull(releaseConfig.getEarningsLimit().getPercentage());
        }
        return earningsLimitPercentage;
    }

    private SeasonTicketRenewalCouchDocument getSeasonTicketRenewalCouchDocument(Long originSeasonTicketId,
                                                                                 Long renewalSeasonTicketId,
                                                                                 Map<Long, RenewalSeasonTicketRenewalSeat> renewalsSeatMap,
                                                                                 RenewalSeasonTicketOriginSeat originSeat,
                                                                                 Map<String, SeasonTicketRenewalCouchDocument> savedDocuments,
                                                                                 RenewalExternalOriginSeat externalOriginSeat,
                                                                                 Map<Long, Long> relatedRatesMap,
                                                                                 boolean externalOrigin,
                                                                                 boolean isUpdate,
                                                                                 boolean memberMandatory,
                                                                                 Map<Long, CapacityMapDTO> oldCapacityMaps,
                                                                                 List<String> addedIds) {
        SeasonTicketRenewalCouchDocument renewalDocument;
        String userId;
        RenewalSeasonTicketRenewalSeat renewalSeat;

        if (externalOrigin) {
            userId = externalOriginSeat.getUserId();
            renewalSeat = renewalsSeatMap.get(externalOriginSeat.getRenewalProcessIdentifier());
        } else {
            userId = originSeat.getUserId();
            renewalSeat = renewalsSeatMap.get(originSeat.getOriginSeatId());
        }

        // Try to get the couch document first from the already treated documents
        renewalDocument = savedDocuments.get(userId);
        if (renewalDocument == null) {
            renewalDocument = seasonTicketRenewalCouchDao.get(userId);
        }

        if (renewalDocument != null) {
            // There is an existing document on couch for this user
            Map<Long, List<SeasonTicketRenewalProduct>> seasonTicketProductMap = renewalDocument.getSeasonTicketProductMap();
            List<SeasonTicketRenewalProduct> renewalProductList = seasonTicketProductMap.get(renewalSeasonTicketId);
            if (renewalProductList != null) {
                // There is an existing renewal product for this season ticket and this user
                if (!isUpdate || (isUpdate && !memberMandatory &&
                        !checkProductsPresent(renewalProductList, originSeat, externalOriginSeat, originSeasonTicketId,
                                oldCapacityMaps, externalOrigin))) {
                    SeasonTicketRenewalProduct renewalProduct = SeasonTicketRenewalsConverter
                            .createRenewalProduct(originSeasonTicketId, renewalSeat, originSeat,
                                    externalOriginSeat, relatedRatesMap, externalOrigin);
                    renewalProductList.add(renewalProduct);
                    addedIds.add(renewalProduct.getId());
                }
            } else {
                // Create a new renewal product for this season ticket and this user
                renewalProductList = new ArrayList<>();
                SeasonTicketRenewalProduct renewalProduct = SeasonTicketRenewalsConverter
                        .createRenewalProduct(originSeasonTicketId, renewalSeat, originSeat, externalOriginSeat,
                                relatedRatesMap, externalOrigin);
                renewalProductList.add(renewalProduct);
                seasonTicketProductMap.put(renewalSeasonTicketId, renewalProductList);
                addedIds.add(renewalProduct.getId());
            }
        } else {
            // Create a new document on couch for this user
            renewalDocument = SeasonTicketRenewalsConverter.createRenewalCouchDocument(originSeasonTicketId,
                    renewalSeasonTicketId, renewalSeat, originSeat, externalOriginSeat, relatedRatesMap, externalOrigin);
            addedIds.add(renewalDocument.getSeasonTicketProductMap().get(renewalSeasonTicketId).get(0).getId());
        }
        savedDocuments.put(userId, renewalDocument);
        return renewalDocument;
    }

    private boolean checkProductsPresent(List<SeasonTicketRenewalProduct> renewalProductList,
                                         RenewalSeasonTicketOriginSeat originSeat,
                                         RenewalExternalOriginSeat externalOriginSeat,
                                         Long originSeasonTicketId,
                                         Map<Long, CapacityMapDTO> oldCapacityMaps,
                                         boolean externalOrigin) {
        return renewalProductList.stream()
                .anyMatch(product -> checkProductPresent(product, originSeat, externalOriginSeat, originSeasonTicketId,
                        oldCapacityMaps, externalOrigin));
    }

    private boolean checkProductPresent(SeasonTicketRenewalProduct product,
                                        RenewalSeasonTicketOriginSeat originSeat,
                                        RenewalExternalOriginSeat externalOriginSeat,
                                        Long originSeasonTicketId,
                                        Map<Long, CapacityMapDTO> oldCapacityMaps,
                                        boolean externalOrigin) {

        UniversalSeatIdentifier oldUniversalIdentifier = getUniversalIdentifier(product, oldCapacityMaps);
        UniversalSeatIdentifier newUniversalIdentifier = getUniversalIdentifier(originSeat, externalOriginSeat,
                oldCapacityMaps, originSeasonTicketId, externalOrigin);

        if (Objects.nonNull(oldUniversalIdentifier) && Objects.nonNull(newUniversalIdentifier)) {
            return oldUniversalIdentifier.equals(newUniversalIdentifier);
        }
        return Boolean.FALSE;
    }

    private UniversalSeatIdentifier getUniversalIdentifier(SeasonTicketRenewalProduct product,
                                                           Map<Long, CapacityMapDTO> oldCapacityMaps) {
        if (product.getExternalOrigin() &&
                SeasonTicketSeatType.NUMBERED.equals(product.getOriginExternalSeat().getSeatType())) {
            return new UniversalSeatIdentifier(
                    product.getOriginExternalSeat().getSector(),
                    product.getOriginExternalSeat().getRow(),
                    product.getOriginExternalSeat().getSeat());
        } else if (!product.getExternalOrigin() &&
                SeasonTicketSeatType.NUMBERED.equals(product.getOriginSeasonTicketSeat().getSeatType())) {
            CapacityMapDTO capacityMapDTO = oldCapacityMaps.get(product.getOriginSeasonTicketId());
            if (Objects.isNull(capacityMapDTO)) {
                capacityMapDTO = getAndSetCapacityMap(product.getOriginSeasonTicketId(), oldCapacityMaps);
            }
            return getUniversalIdentifierFromCapacity(product.getOriginSeasonTicketSeat().getSectorId(),
                    product.getOriginSeasonTicketSeat().getRowId(), product.getOriginSeasonTicketSeat().getSeatName(),
                    capacityMapDTO);
        }
        return null;
    }

    private UniversalSeatIdentifier getUniversalIdentifier(RenewalSeasonTicketOriginSeat originSeat,
                                                           RenewalExternalOriginSeat externalOriginSeat,
                                                           Map<Long, CapacityMapDTO> oldCapacityMaps,
                                                           Long originSeasonTicketId,
                                                           boolean externalOrigin) {

        if (externalOrigin && Objects.nonNull(externalOriginSeat) &&
                SeasonTicketSeatType.NUMBERED.equals(externalOriginSeat.getSeatType())) {
            return new UniversalSeatIdentifier(
                    externalOriginSeat.getSector(),
                    externalOriginSeat.getRow(),
                    externalOriginSeat.getSeat());
        } else if (!externalOrigin && Objects.nonNull(originSeat) &&
                SeasonTicketSeatType.NUMBERED.equals(originSeat.getSeatType())) {
            CapacityMapDTO capacityMapDTO = oldCapacityMaps.get(originSeasonTicketId);
            if (Objects.isNull(capacityMapDTO)) {
                capacityMapDTO = getAndSetCapacityMap(originSeasonTicketId, oldCapacityMaps);
            }
            return getUniversalIdentifierFromCapacity(originSeat.getOriginSectorId(), originSeat.getOriginRowId(),
                    originSeat.getOriginSeatName(), capacityMapDTO);
        }
        return null;
    }

    private UniversalSeatIdentifier getUniversalIdentifierFromCapacity(Integer sectorId, Integer rowId, String seatName,
                                                                       CapacityMapDTO capacityMapDTO) {
        String sectorName = capacityMapDTO.getSectorMap().stream()
                .filter(sector -> sector.getId().equals(sectorId.longValue()))
                .map(SectorDTO::getCode)
                .findAny().get();
        String rowName = capacityMapDTO.getRows().stream()
                .filter(row -> row.getId().equals(rowId.longValue()))
                .map(RowCapacityDTO::getName)
                .findAny().get();
        return new UniversalSeatIdentifier(
                sectorName,
                rowName,
                seatName);
    }

    private CapacityMapDTO getAndSetCapacityMap(Long seasonTicketId, Map<Long, CapacityMapDTO> oldCapacityMaps) {
        SeasonTicketDTO renewalSeasonTicketDTO = seasonTicketService
                .getSeasonTicket(seasonTicketId);
        CapacityMapDTO capacityMapDTO = venuesRepository
                .getCapacityMap(renewalSeasonTicketDTO.getSessionId().longValue());
        oldCapacityMaps.put(seasonTicketId, capacityMapDTO);
        return capacityMapDTO;
    }

    public SeasonTicketRenewalSeatsResponse getSeasonTicketRenewalSeats(Long seasonTicketId, SeasonTicketRenewalSeatsFilter filter) {
        RenewalRecord renewalRecord = renewalDao.getRenewalData(seasonTicketId);
        if (renewalRecord == null) {
            return createEmptyResponse(filter.getLimit(), filter.getOffset());
        }

        // First we get the summary to avoid some issues while renewals are in purge process
        Long mappedImports = searchRenewalMappedSeatsImportNumber(seasonTicketId);
        Long notMappedImports = searchRenewalNotMappedSeatsImportNumber(seasonTicketId);

        filter.setSeasonTicketId(seasonTicketId);
        SeasonTicketRenewalSeatsResponse result = searchRenewalSeats(filter);

        String name;
        long originSeasonTicketId;
        if (Boolean.TRUE.equals(renewalRecord.getIsExternalEvent())) {
            originSeasonTicketId = renewalRecord.getIdeventoexternooriginal().longValue();
            name = renewalRecord.getOriginExternalEventName();
        } else {
            originSeasonTicketId = renewalRecord.getIdeventooriginal().longValue();
            name = renewalRecord.getOriginSeasonTicketName();
        }
        SeasonTicketRenewalSeatsSummary summary = createSummary(seasonTicketId, originSeasonTicketId, name,
                renewalRecord.getTotalabonos(), CommonUtils.timestampToZonedDateTime(renewalRecord.getFechaimportacionrenovacion()),
                mappedImports.intValue(), notMappedImports.intValue());
        result.setSummary(summary);

        return result;
    }

    public RenewalEntitiesResponse getRenewalEntities(Long seasonTicketId,
                                                      SeasonTicketRenewalSeatsFilter filter) {
        RenewalRecord renewalRecord = renewalDao.getRenewalData(seasonTicketId);
        if (renewalRecord == null) {
            return new RenewalEntitiesResponse();
        }
        filter.setSeasonTicketId(seasonTicketId);
        SearchResponse<RenewalDataElastic> searchResponse = renewalElasticDao.getRenewalEntities(filter);
        return RenewalsESUtils.generateRenewalEntitiesResponse(searchResponse);
    }

    public SeasonTicketRenewalSeatsResponse searchRenewalSeats(SeasonTicketRenewalSeatsFilter filter) {
        SearchResponse<RenewalDataElastic> searchResponse = renewalElasticDao.getRenewalSeats(filter);
        List<SeasonTicketRenewalSeat> renewalSeatList = renewalElasticDao.convertSearchResponseIntoRenewalSeat(searchResponse);
        for (SeasonTicketRenewalSeat seasonTicketRenewalSeat : renewalSeatList) {
            SeasonTicketDTO season = seasonTicketService.getSeasonTicket(seasonTicketRenewalSeat.getSeasonTicketId());
            seasonTicketRenewalSeat.setRenewalSettings(season.getRenewal());
        }
        SeasonTicketRenewalSeatsResponse result = new SeasonTicketRenewalSeatsResponse();
        PaginationUtils.fillPaginationResult(result, filter, searchResponse, renewalSeatList);
        return result;
    }

    private void registerRenewal(Long renewalSeasonTicketId, Long originSeasonTicketId, Long totalRenewals, boolean isExternal) {
        CpanelRenewalRecord renewalRecord = new CpanelRenewalRecord();
        renewalRecord.setIdevento(renewalSeasonTicketId.intValue());
        if (isExternal) {
            renewalRecord.setIdeventoexternooriginal(originSeasonTicketId.intValue());
            renewalRecord.setIsexternalevent(ConverterUtils.isTrueAsByte(true));
        } else {
            renewalRecord.setIdeventooriginal(originSeasonTicketId.intValue());
        }
        renewalRecord.setFechaimportacionrenovacion(new Timestamp(new Date().getTime()));
        renewalRecord.setTotalabonos(totalRenewals.intValue());
        renewalDao.insert(renewalRecord);
    }

    private Integer updateRenewal(Long renewalSeasonTicketId, Integer totalAddedRenewals) {
        RenewalRecord renewalData = renewalDao.getRenewalData(renewalSeasonTicketId);
        int totalRenewals = renewalData.getTotalabonos() + totalAddedRenewals;
        renewalData.setTotalabonos(totalRenewals);
        renewalDao.update(renewalData);
        return totalRenewals;
    }

    public void verifyPendingRenewal(Long renewalSeasonTicketId) {
        try {
            CpanelRenewalRecord renewalRecord = renewalDao.getById(renewalSeasonTicketId.intValue());
            if (renewalRecord != null) {
                throw new OneboxRestException(MsEventSeasonTicketErrorCode.SEASON_TICKET_RENEWAL_IN_PROGRESS);
            }
        } catch (EntityNotFoundException e) {
            LOGGER.debug("Renewal for season ticket {} not found", renewalSeasonTicketId);
        }
    }

    private Long searchRenewalMappedSeatsImportNumber(Long seasonTicketId) {
        SearchResponse<RenewalDataElastic> searchResponse = renewalElasticDao.getRenewalMappedSeats(seasonTicketId);
        return renewalElasticDao.getTotalHits(searchResponse);
    }

    private Long searchRenewalNotMappedSeatsImportNumber(Long seasonTicketId) {
        SearchResponse<RenewalDataElastic> searchResponse = renewalElasticDao.getRenewalNotMappedSeats(seasonTicketId);
        return renewalElasticDao.getTotalHits(searchResponse);
    }

    private SeasonTicketRenewalSeatsResponse createEmptyResponse(Long limit, Long offset) {
        SeasonTicketRenewalSeatsResponse response = new SeasonTicketRenewalSeatsResponse();
        SeasonTicketRenewalSeatsSummary summary = new SeasonTicketRenewalSeatsSummary();
        summary.setGenerationStatus(RenewalGenerationStatus.NOT_INITIATED);
        summary.setAutomaticRenewalStatus(SeasonTicketAutomaticRenewalStatus.NOT_INITIATED);
        response.setSummary(summary);
        response.setData(Collections.emptyList());
        Metadata metadata = new Metadata();
        metadata.setLimit(limit);
        metadata.setOffset(offset);
        metadata.setTotal(0L);
        response.setMetadata(metadata);
        return response;
    }

    private SeasonTicketRenewalSeatsSummary createSummary(Long seasonTicketId, Long originSeasonTicketId, String originSeasonTicketName,
                                                          Integer totalAbonos, ZonedDateTime renewalImportDate,
                                                          Integer mappedImports, Integer notMappedImports) {
        SeasonTicketRenewalSeatsSummary summary = new SeasonTicketRenewalSeatsSummary();
        summary.setOriginSeasonTicketId(originSeasonTicketId);
        summary.setOriginSeasonTicketName(originSeasonTicketName);
        summary.setTotalRenewals(totalAbonos);
        summary.setRenewalImportDate(renewalImportDate);
        summary.setMappedImports(mappedImports);
        summary.setNotMappedImports(notMappedImports);

        RenewalGenerationStatus renewalsGenerationStatus = getRenewalsGenerationStatus(summary.getTotalRenewals(),
                summary.getMappedImports() + summary.getNotMappedImports());
        summary.setGenerationStatus(renewalsGenerationStatus);

        SeasonTicketRenewalConfig config = seasonTicketRenewalConfigCouchDao.get(String.valueOf(seasonTicketId));
        if (config != null) {
            summary.setAutomaticRenewalStatus(config.getAutomaticRenewalStatus() != null
                    ? SeasonTicketAutomaticRenewalStatus.valueOf(config.getAutomaticRenewalStatus().name())
                    : SeasonTicketAutomaticRenewalStatus.NOT_INITIATED
            );
        }

        return summary;
    }

    public UpdateRenewalResponse updateRenewalSeats(Long seasonTicketId, UpdateRenewalRequest request) {
        SeasonTicketInternalGenerationStatus seasonTicketStatus = seasonTicketService.getGenerationStatus(seasonTicketId);

        // Validate generation status
        if (!SeasonTicketInternalGenerationStatus.READY.equals(seasonTicketStatus)) {
            throw new OneboxRestException(MsEventSeasonTicketErrorCode.SEASON_TICKET_RENEWAL_NOT_READY);
        }

        SeasonTicketDTO seasonTicketDTO = getAndValidateSeasonTicket(seasonTicketId);

        List<Long> validRates = getValidRates(seasonTicketId);

        List<Long> seatIds = request.getItems().stream()
                .map(UpdateRenewalRequestItem::getSeatId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        Map<Long, TicketDTO> ticketDTOList = null;
        if (!seatIds.isEmpty()) {
            Long sessionId = seasonTicketDTO.getSessionId().longValue();
            List<TicketStatus> ticketStatus = Collections.singletonList(TicketStatus.AVAILABLE);
            TicketsSearchResponse ticketsSearchResponse = ticketsRepository.getTickets(sessionId, seatIds, ticketStatus);
            ticketDTOList = ticketsSearchResponse.getData().stream()
                    .collect(Collectors.toMap(
                            TicketDTO::getId,
                            Function.identity()));
        }

        // Update seat status list
        List<RenewalsUpdateRelatedSeatsRequestItem> blockSeats = new ArrayList<>();
        List<RenewalsUpdateRelatedSeatsRequestItem> unblockSeats = new ArrayList<>();

        Map<Long, TicketDTO> finalTicketDTOList = ticketDTOList;
        List<UpdateRenewalResponseItem> responseItems = request.getItems().stream()
                .map(requestItem -> updateRenewalSeat(seasonTicketId, requestItem, finalTicketDTOList, blockSeats, unblockSeats, validRates))
                .collect(Collectors.toList());

        // Update seat status
        if (!blockSeats.isEmpty() || !unblockSeats.isEmpty()) {
            UpdateRelatedSeatsResponse updateStatusResponse = seasonTicketRepository.updateRelatedSeasonTicketSeatsStatus(seasonTicketDTO.getSessionId().longValue(), blockSeats, unblockSeats);
            revertBlockedRenewals(seasonTicketId, updateStatusResponse, responseItems);
        }

        UpdateRenewalResponse response = new UpdateRenewalResponse();
        response.setItems(responseItems);
        return response;
    }

    private List<Long> getValidRates(Long seasonTicketId) {
        RatesFilter filter = new RatesFilter();
        filter.setOffset(0L);
        filter.setLimit(99L);
        SeasonTicketRatesDTO seasonTicketRatesDTO = seasonTicketRateService.findRatesBySeasonTicketId(seasonTicketId.intValue(), filter);
        return seasonTicketRatesDTO.getData().stream().map(SeasonTicketRateDTO::getId).collect(Collectors.toList());
    }

    private UpdateRenewalResponseItem updateRenewalSeat(Long seasonTicketId, UpdateRenewalRequestItem requestItem,
                                                        Map<Long, TicketDTO> ticketDTOList,
                                                        List<RenewalsUpdateRelatedSeatsRequestItem> blockSeats,
                                                        List<RenewalsUpdateRelatedSeatsRequestItem> unblockSeats,
                                                        List<Long> validRates) {
        String userId = requestItem.getUserId();
        String id = requestItem.getId();
        Long seatId = requestItem.getSeatId();
        Long rateId = requestItem.getRateId();
        String substatus = requestItem.getRenewalSubstatus();
        Boolean autoRenewal = requestItem.getAutoRenewal();

        // Get couch document
        SeasonTicketRenewalCouchDocument couchDocument = seasonTicketRenewalCouchDao.get(userId);

        List<SeasonTicketRenewalProduct> renewalProductList = null;
        SeasonTicketRenewalProduct renewalProduct = null;

        if (couchDocument != null && couchDocument.getSeasonTicketProductMap() != null) {

            // Get renewal products from season ticket
            renewalProductList = couchDocument.getSeasonTicketProductMap().get(seasonTicketId);
            if (renewalProductList != null) {
                // Get renewal product
                renewalProduct = renewalProductList.stream()
                        .filter(renewalProductOnList -> renewalProductOnList.getId().equals(id))
                        .findFirst()
                        .orElse(null);
            }
        }

        UpdateRenewalResponseItem itemError = validateRenewalUpdate(rateId, validRates, id, userId, seasonTicketId,
                couchDocument, renewalProductList, renewalProduct);
        if (itemError != null) {
            return itemError;
        }

        if (renewalProduct != null) {
            if (seatId != null) {
                TicketDTO ticketDTO = ticketDTOList.get(seatId);
                if (ticketDTO == null) {
                    return returnErrorOnUpdateRenewal(id, UpdateRenewalErrorReason.INVALID_SEAT);
                }

                if (renewalProduct.getRenewalSeasonTicketSeat() != null) {
                    SeasonTicketSeat oldRenewalSeat = renewalProduct.getRenewalSeasonTicketSeat();
                    Long oldRenewalSeatId = oldRenewalSeat.getSeatId();
                    addReleaseSeat(userId, seasonTicketId, id, oldRenewalSeatId, unblockSeats);
                }

                SeasonTicketSeat renewalSeasonTicketSeat = createNewSeasonTicketRenewalSeat(seatId, ticketDTO);
                renewalProduct.setRenewalSeasonTicketSeat(renewalSeasonTicketSeat);
                renewalProduct.setStatus(SeasonTicketRenewalStatus.PENDING_RENEWAL);

                addBlockSeat(userId, seasonTicketId, id, seatId, blockSeats);
            }

            if (rateId != null && validRates.contains(rateId)) {
                renewalProduct.setRenewalRateId(rateId);
            }

            if (autoRenewal != null) {
                renewalProduct.setAutoRenewal(autoRenewal);
            }

            if (rateId == null && seatId == null && autoRenewal == null) {
                renewalProduct.setRenewalSubstatus(substatus);
            }
        }

        // Update couch
        seasonTicketRenewalCouchDao.upsert(userId, couchDocument);

        // Update elastic
        renewalsElasticUpdaterService.sendMessage(userId, seasonTicketId, null);

        UpdateRenewalResponseItem response = new UpdateRenewalResponseItem();
        response.setId(id);
        response.setResult(Boolean.TRUE);
        return response;
    }

    private UpdateRenewalResponseItem validateRenewalUpdate(Long rateId, List<Long> validRates, String renewalId, String userId,
                                                            Long seasonTicketId, SeasonTicketRenewalCouchDocument couchDocument,
                                                            List<SeasonTicketRenewalProduct> renewalProductList,
                                                            SeasonTicketRenewalProduct renewalProduct) {
        if (rateId != null && !validRates.contains(rateId)) {
            return returnErrorOnUpdateRenewal(renewalId, UpdateRenewalErrorReason.INVALID_RATE);
        }

        if (couchDocument == null) {
            return returnErrorOnUpdateRenewal(renewalId, UpdateRenewalErrorReason.USER_HAS_NOT_RENEWALS);
        }

        if (renewalProductList == null) {
            return returnErrorOnUpdateRenewal(renewalId, UpdateRenewalErrorReason.USER_HAS_NOT_RENEWALS_FOR_THIS_SEASON_TICKET);
        }

        if (renewalProduct == null) {
            return returnErrorOnUpdateRenewal(renewalId, UpdateRenewalErrorReason.RENEWAL_PRODUCT_NOT_FOUND);
        }

        // Validate status
        if (SeasonTicketRenewalStatus.RENEWED.equals(renewalProduct.getStatus())
                || SeasonTicketRenewalStatus.REFUNDED.equals(renewalProduct.getStatus())
                || SeasonTicketRenewalStatus.CANCELED.equals(renewalProduct.getStatus())) {
            return returnErrorOnUpdateRenewal(renewalId, UpdateRenewalErrorReason.RENEWAL_ALREADY_RENEWED);
        }

        return null;
    }

    private SeasonTicketSeat createNewSeasonTicketRenewalSeat(Long seatId, TicketDTO ticketDTO) {
        SeasonTicketSeat renewalSeasonTicketSeat = new SeasonTicketSeat();
        renewalSeasonTicketSeat.setSectorId(ticketDTO.getSectorId().intValue());
        renewalSeasonTicketSeat.setSeatId(seatId);
        renewalSeasonTicketSeat.setPriceZoneId(ticketDTO.getPriceTypeId());
        if (isNotNumbered(ticketDTO)) {
            renewalSeasonTicketSeat.setSeatType(SeasonTicketSeatType.NOT_NUMBERED);
            renewalSeasonTicketSeat.setNotNumberedZoneId(ticketDTO.getNotNumberedAreaId().intValue());
        } else {
            renewalSeasonTicketSeat.setSeatType(SeasonTicketSeatType.NUMBERED);
            renewalSeasonTicketSeat.setRowId(ticketDTO.getRow().intValue());
            renewalSeasonTicketSeat.setSeatName(ticketDTO.getSeat());
        }
        return renewalSeasonTicketSeat;
    }

    private UpdateRenewalResponseItem returnErrorOnUpdateRenewal(String id, UpdateRenewalErrorReason reason) {
        UpdateRenewalResponseItem response = new UpdateRenewalResponseItem();
        response.setId(id);
        response.setResult(Boolean.FALSE);
        response.setReason(reason);
        return response;
    }

    private void updateRelatedSeatsStatus(List<? extends RenewalSeat> originSeats,
                                          List<SeasonTicketRenewalCouchDocument> couchDocuments,
                                          Collection<RenewalSeasonTicketRenewalSeat> renewalSeats,
                                          Long renewalSeasonTicketId,
                                          Long renewalSeasonTicketSessionId) {

        Map<Long, RenewalSeat> originSeatMap = originSeats.stream()
                .collect(Collectors.toMap(
                        RenewalSeat::getSeatId,
                        Function.identity()));

        Map<String, SeasonTicketRenewalCouchDocument> couchDocumentsMap = couchDocuments.stream()
                .collect(Collectors.toMap(
                        SeasonTicketRenewalCouchDocument::getUserId,
                        Function.identity()));

        List<RenewalsUpdateRelatedSeatsRequestItem> blockSeats = renewalSeats.stream()
                .map(renewalSeat -> {
                    Long originSeatId = renewalSeat.getOriginSeatId();
                    RenewalSeat originSeat = originSeatMap.get(originSeatId);

                    String userId = originSeat.getUserId();
                    Long seatId = renewalSeat.getRenewalSeatId();

                    SeasonTicketRenewalCouchDocument couchDocument = couchDocumentsMap.get(userId);
                    List<SeasonTicketRenewalProduct> productList = couchDocument.getSeasonTicketProductMap().get(renewalSeasonTicketId);
                    SeasonTicketRenewalProduct renewalProduct = productList.stream()
                            .filter(product -> product.getRenewalSeasonTicketSeat() != null)
                            .filter(product -> product.getRenewalSeasonTicketSeat().getSeatId().equals(seatId))
                            .findAny()
                            .orElse(null);

                    if (renewalProduct == null) {
                        return null;
                    }

                    String renewalId = renewalProduct.getId();

                    RenewalsUpdateRelatedSeatsRequestItem requestItem = new RenewalsUpdateRelatedSeatsRequestItem();
                    requestItem.setUserId(userId);
                    requestItem.setSeasonTicketId(renewalSeasonTicketId);
                    requestItem.setRenewalId(renewalId);
                    requestItem.setSeatId(seatId);
                    return requestItem;

                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        renewalsUpdateRelatedSeatsService.updateSeatsAsync(renewalSeasonTicketSessionId, blockSeats);
    }

    private void addReleaseSeat(String userId, Long seasonTicketId, String renewalId, Long seatId, List<RenewalsUpdateRelatedSeatsRequestItem> unblockSeats) {
        RenewalsUpdateRelatedSeatsRequestItem unblockSeat = new RenewalsUpdateRelatedSeatsRequestItem();
        unblockSeat.setUserId(userId);
        unblockSeat.setSeasonTicketId(seasonTicketId);
        unblockSeat.setRenewalId(renewalId);
        unblockSeat.setSeatId(seatId);
        unblockSeats.add(unblockSeat);
    }

    private void addBlockSeat(String userId, Long seasonTicketId, String renewalId, Long seatId, List<RenewalsUpdateRelatedSeatsRequestItem> blockSeats) {
        RenewalsUpdateRelatedSeatsRequestItem blockSeat = new RenewalsUpdateRelatedSeatsRequestItem();
        blockSeat.setUserId(userId);
        blockSeat.setSeasonTicketId(seasonTicketId);
        blockSeat.setRenewalId(renewalId);
        blockSeat.setSeatId(seatId);
        blockSeats.add(blockSeat);
    }

    private void revertBlockedRenewals(Long seasonTicketId, UpdateRelatedSeatsResponse updateStatusResponse, List<UpdateRenewalResponseItem> responseItems) {
        if (updateStatusResponse.getBlockSeatsResponse() != null) {
            List<UpdateRelatedSeatsResponseItem> blockSeatsResponse = updateStatusResponse.getBlockSeatsResponse();
            List<UpdateRelatedSeatsResponseItem> failedBlockedSeats = blockSeatsResponse.stream()
                    .filter(blockedSeat -> Boolean.FALSE.equals(blockedSeat.getResult()))
                    .collect(Collectors.toList());

            if (!failedBlockedSeats.isEmpty()) {
                failedBlockedSeats.forEach(item -> {
                    revertBlockedRenewals(seasonTicketId, item.getUserId(), item.getRenewalId());

                    UpdateRenewalResponseItem updateRenewalResponseItem = responseItems.stream()
                            .filter(responseItem -> responseItem.getId().equals(item.getRenewalId()))
                            .findFirst()
                            .orElse(null);
                    if (updateRenewalResponseItem != null) {
                        updateRenewalResponseItem.setResult(Boolean.FALSE);
                        updateRenewalResponseItem.setReason(UpdateRenewalErrorReason.INVALID_SEAT);
                    }

                });
            }
        }
    }

    public void revertBlockedRenewals(Long seasonTicketId, String userId, String id) {
        SeasonTicketRenewalCouchDocument couchDocument = seasonTicketRenewalCouchDao.get(userId);
        if (couchDocument != null) {
            List<SeasonTicketRenewalProduct> renewalProductList = couchDocument.getSeasonTicketProductMap().get(seasonTicketId);
            if (renewalProductList != null && !renewalProductList.isEmpty()) {
                SeasonTicketRenewalProduct renewalProduct = renewalProductList.stream()
                        .filter(renewalProductOnList -> renewalProductOnList.getId().equals(id))
                        .findFirst()
                        .orElse(null);
                if (renewalProduct != null) {
                    renewalProduct.setRenewalSeasonTicketSeat(null);
                    renewalProduct.setStatus(SeasonTicketRenewalStatus.MAPPING_SEAT_NOT_FOUND);
                    seasonTicketRenewalCouchDao.upsert(userId, couchDocument);
                    renewalsElasticUpdaterService.sendMessage(userId, seasonTicketId, null);
                }
            }
        }
    }

    public void commitRenewal(Long seasonTicketId, String userId, String renewalId, String orderCode,
                              Long rateId, ZonedDateTime purchaseDate) {
        SeasonTicketRenewalCouchDocument couchDocument = seasonTicketRenewalCouchDao.get(userId);
        if (couchDocument != null) {
            SeasonTicketRenewalProduct renewalProduct = getSeasonTicketRenewalProductFromCouchDocument(couchDocument, seasonTicketId, renewalId);
            if (renewalProduct != null) {
                renewalProduct.setStatus(SeasonTicketRenewalStatus.RENEWED);
                renewalProduct.setOrderCode(orderCode);
                renewalProduct.setPurchaseDate(purchaseDate);
                renewalProduct.setRenewalRateId(rateId);
                renewalProduct.setRenewalSubstatus(null);
                seasonTicketRenewalCouchDao.upsert(userId, couchDocument);
            }
        }
    }

    public void migrateCustomerRenewals(List<String> userIds, Long seasonTicketId) {
        userIds.forEach(userId -> renewalsElasticUpdaterService.sendMessage(userId, seasonTicketId, null));
    }

    private SeasonTicketRenewalProduct getSeasonTicketRenewalProductFromCouchDocument(
            SeasonTicketRenewalCouchDocument couchDocument, Long seasonTicketId, String renewalId) {
        List<SeasonTicketRenewalProduct> renewalProductList = couchDocument.getSeasonTicketProductMap().get(seasonTicketId);
        if (renewalProductList != null && !renewalProductList.isEmpty()) {
            return renewalProductList.stream()
                    .filter(renewalProductOnList -> renewalProductOnList.getId().equals(renewalId))
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    public void deleteRenewalSeat(Long seasonTicketId, String renewalId) {
        SeasonTicketDTO seasonTicketDTO = getAndValidateSeasonTicket(seasonTicketId);

        deleteIndividualRenewalSeat(seasonTicketId, seasonTicketDTO.getSessionId().longValue(), renewalId);

        // Force refresh indices to avoid inconsistencies
        renewalElasticDao.refresh();

        // Update total renewals
        decreaseOrDeleteTotalRenewals(seasonTicketId, 1);
    }

    private void deleteIndividualRenewalSeat(Long seasonTicketId, Long seasonTicketSessionId, String renewalId) {
        RenewalDataElastic elasticDocument = renewalElasticDao.findByID(renewalId);
        validateDeleteRenewal(seasonTicketId, elasticDocument);

        String userId = elasticDocument.getUserId();

        if (hasToBeReleased(elasticDocument)) {
            UpdateRelatedSeatsResponse response = releaseSeatsOnDeleteRenewal(userId, seasonTicketId, seasonTicketSessionId, renewalId, elasticDocument.getActualSeat().getSeatId());
            UpdateRelatedSeatsResponseItem responseItem = response.getUnblockSeatsResponse().stream()
                    .filter(unblockResponse -> unblockResponse.getRenewalId().equals(renewalId))
                    .findAny()
                    .orElseThrow(() -> new OneboxRestException(CoreErrorCode.PERSISTENCE_ERROR));
            if (Boolean.FALSE.equals(responseItem.getResult())) {
                throw new OneboxRestException(MsEventSeasonTicketErrorCode.SEASON_TICKET_RENEWAL_DELETE_NOT_ALLOWED);
            }
        }

        // Delete renewal from couch document
        deleteRenewalFromCouchDocument(seasonTicketId, renewalId, userId);

        // Delete elastic document
        renewalElasticDao.deleteById(renewalId);
    }

    private boolean hasToBeReleased(RenewalDataElastic elasticDocument) {
        return elasticDocument.getRenewalStatus().equals(RenewalStatusES.NOT_RENEWED) && elasticDocument.getActualSeat() != null;
    }

    private void validateDeleteRenewal(Long seasonTicketId, RenewalDataElastic elasticDocument) {
        if (elasticDocument == null || !elasticDocument.getSeasonTicketId().equals(seasonTicketId)) {
            throw new OneboxRestException(MsEventSeasonTicketErrorCode.SEASON_TICKET_RENEWAL_NOT_FOUND);
        }
        if (RenewalStatusES.RENEWED.equals(elasticDocument.getRenewalStatus())) {
            throw new OneboxRestException(MsEventSeasonTicketErrorCode.SEASON_TICKET_RENEWAL_DELETE_RENEWED_NOT_ALLOWED);
        }
    }

    private void deleteRenewalFromCouchDocument(Long seasonTicketId, String renewalId, String userId) {
        SeasonTicketRenewalCouchDocument couchDocument = seasonTicketRenewalCouchDao.get(userId);
        List<SeasonTicketRenewalProduct> renewalProductsForSeasonTicket = couchDocument.getSeasonTicketProductMap().get(seasonTicketId);
        if (renewalProductsForSeasonTicket == null) {
            throw new OneboxRestException(MsEventSeasonTicketErrorCode.SEASON_TICKET_RENEWAL_NOT_FOUND);
        }

        SeasonTicketRenewalProduct renewalProduct = renewalProductsForSeasonTicket.stream()
                .filter(renewal -> renewal.getId().equals(renewalId))
                .findFirst()
                .orElseThrow(() -> new OneboxRestException(MsEventSeasonTicketErrorCode.SEASON_TICKET_RENEWAL_NOT_FOUND));

        renewalProductsForSeasonTicket.remove(renewalProduct);
        if (renewalProductsForSeasonTicket.isEmpty()) {
            couchDocument.getSeasonTicketProductMap().remove(seasonTicketId);
        }

        if (couchDocument.getSeasonTicketProductMap().isEmpty()) {
            seasonTicketRenewalCouchDao.remove(userId);
        } else {
            seasonTicketRenewalCouchDao.upsert(userId, couchDocument);
        }
    }

    private UpdateRelatedSeatsResponse releaseSeatsOnDeleteRenewal(String userId, Long seasonTicketId, Long seasonTicketSessionId, String renewalId, Long seat) {
        RenewalsUpdateRelatedSeatsRequestItem unblockSeat = new RenewalsUpdateRelatedSeatsRequestItem();
        unblockSeat.setUserId(userId);
        unblockSeat.setSeasonTicketId(seasonTicketId);
        unblockSeat.setRenewalId(renewalId);
        unblockSeat.setSeatId(seat);
        return seasonTicketRepository.updateRelatedSeasonTicketSeatsStatus(seasonTicketSessionId, null, Collections.singletonList(unblockSeat));
    }

    private void decreaseOrDeleteTotalRenewals(Long seasonTicketId, int amount) {
        RenewalRecord renewalRecord = renewalDao.getRenewalData(seasonTicketId);
        int previousTotal = renewalRecord.getTotalabonos();
        int newTotal = previousTotal - amount;

        if (newTotal == 0) {
            renewalDao.delete(renewalRecord);
        } else {
            renewalRecord.setTotalabonos(newTotal);
            renewalDao.update(renewalRecord);
        }
    }

    public DeleteRenewalsResponse deleteRenewalSeats(Long seasonTicketId, DeleteRenewalsRequest request) {
        SeasonTicketDTO seasonTicketDTO = getAndValidateSeasonTicket(seasonTicketId);

        List<String> distinctRenewalIds = request.getRenewalIds().stream()
                .distinct()
                .collect(Collectors.toList());

        List<RenewalDataElastic> renewalDataElasticList = distinctRenewalIds.stream()
                .map(renewalElasticDao::findByID)
                .collect(Collectors.toList());

        // Validate all renewals
        renewalDataElasticList.forEach(elasticDocument -> validateDeleteRenewal(seasonTicketId, elasticDocument));

        List<DeleteRenewalsResponseItem> responseItems = performDeleteMultipleRenewals(seasonTicketId, seasonTicketDTO, renewalDataElasticList);

        // Update total renewals
        int amount = renewalDataElasticList.size();
        if (amount > 0) {
            decreaseOrDeleteTotalRenewals(seasonTicketId, amount);
        }

        // Add valid responses
        List<DeleteRenewalsResponseItem> validItems = renewalDataElasticList.stream()
                .map(elasticDocument -> createValidDeleteResponseItem(elasticDocument.getId()))
                .collect(Collectors.toList());
        responseItems.addAll(validItems);

        DeleteRenewalsResponse response = new DeleteRenewalsResponse();
        response.setItems(responseItems);
        return response;
    }

    private void releaseRenewals(Long seasonTicketId, SeasonTicketDTO seasonTicketDTO, List<RenewalDataElastic> renewalDataElasticList, List<RenewalDataElastic> renewalsToRelease, List<DeleteRenewalsResponseItem> responseItems) {
        List<RenewalsUpdateRelatedSeatsRequestItem> unblockSeats = renewalsToRelease.stream()
                .filter(this::hasToBeReleased)
                .map(elasticDocument -> {
                    RenewalsUpdateRelatedSeatsRequestItem unblockSeat = new RenewalsUpdateRelatedSeatsRequestItem();
                    unblockSeat.setUserId(elasticDocument.getUserId());
                    unblockSeat.setSeasonTicketId(seasonTicketId);
                    unblockSeat.setRenewalId(elasticDocument.getId());
                    unblockSeat.setSeatId(elasticDocument.getActualSeat().getSeatId());
                    return unblockSeat;
                })
                .collect(Collectors.toList());

        UpdateRelatedSeatsResponse response = seasonTicketRepository.updateRelatedSeasonTicketSeatsStatus(seasonTicketDTO.getSessionId().longValue(), null, unblockSeats);

        // If there is an error, remove it from renewal elastic list
        if (response.getUnblockSeatsResponse() != null) {
            List<String> errorRenewalIds = response.getUnblockSeatsResponse().stream()
                    .filter(unblockResponse -> Boolean.FALSE.equals(unblockResponse.getResult()))
                    .map(UpdateRelatedSeatsResponseItem::getRenewalId)
                    .collect(Collectors.toList());
            if (!errorRenewalIds.isEmpty()) {
                LOGGER.error("Error releasing renewal seats for season ticket id {} and renewal ids {}", seasonTicketId, errorRenewalIds.toString());

                List<DeleteRenewalsResponseItem> invalidItems = errorRenewalIds.stream()
                        .map(this::createInValidDeleteResponseItem)
                        .collect(Collectors.toList());
                responseItems.addAll(invalidItems);

                renewalDataElasticList.removeIf(renewalDataElastic -> errorRenewalIds.contains(renewalDataElastic.getId()));
            }
        }
    }

    private void deleteSeasonTicketRenewalsList(Long seasonTicketId, List<RenewalDataElastic> renewalDataElasticList) {
        renewalDataElasticList.forEach(elasticDocument -> {
            // Delete renewal from couch document
            deleteRenewalFromCouchDocument(seasonTicketId, elasticDocument.getId(), elasticDocument.getUserId());

            // Delete elastic document
            renewalElasticDao.deleteById(elasticDocument.getId());
        });

        // Force refresh indices to avoid inconsistencies
        renewalElasticDao.refresh();
    }

    private DeleteRenewalsResponseItem createDeleteResponseItem(String renewalId, Boolean valid) {
        DeleteRenewalsResponseItem validItem = new DeleteRenewalsResponseItem();
        validItem.setId(renewalId);
        validItem.setResult(valid);
        return validItem;
    }

    private DeleteRenewalsResponseItem createValidDeleteResponseItem(String renewalId) {
        return createDeleteResponseItem(renewalId, Boolean.TRUE);
    }

    private DeleteRenewalsResponseItem createInValidDeleteResponseItem(String renewalId) {
        return createDeleteResponseItem(renewalId, Boolean.FALSE);
    }

    private void verifyRates(Long originSeasonTicketId, Long renewalSeasonTicketId, List<RelatedRateDTO> rates) {
        RatesFilter filter = new RatesFilter();
        filter.setOffset(0L);
        filter.setLimit(99L);
        verifyOriginRates(originSeasonTicketId, filter, rates);
        verifyRenewalRates(renewalSeasonTicketId, filter, rates);
    }

    private void verifyOriginRates(Long originSeasonTicketId, RatesFilter filter, List<RelatedRateDTO> rates) {
        SeasonTicketRatesDTO originSeasonTicketRatesDTO = seasonTicketRateService.findRatesBySeasonTicketId(originSeasonTicketId.intValue(), filter);
        List<Long> originSeasonTicketRatesId = getRatesId(originSeasonTicketRatesDTO);
        List<Long> selectedOriginSeasonTicketRatesId = rates.stream()
                .map(RelatedRateDTO::getOldRateId)
                .collect(Collectors.toList());
        if (originSeasonTicketRatesDTO.getData().size() != selectedOriginSeasonTicketRatesId.size()) {
            throw new OneboxRestException(MsEventRateErrorCode.INVALID_RATE);
        }
        if (!new HashSet<>(originSeasonTicketRatesId).containsAll(selectedOriginSeasonTicketRatesId)) {
            throw new OneboxRestException(MsEventRateErrorCode.INVALID_RATE);
        }
        if (!new HashSet<>(selectedOriginSeasonTicketRatesId).containsAll(originSeasonTicketRatesId)) {
            throw new OneboxRestException(MsEventRateErrorCode.INVALID_RATE);
        }
    }

    private void verifyRenewalRates(Long renewalSeasonTicketId, RatesFilter filter, List<RelatedRateDTO> rates) {
        SeasonTicketRatesDTO renewalSeasonTicketRatesDTO = seasonTicketRateService.findRatesBySeasonTicketId(renewalSeasonTicketId.intValue(), filter);
        List<Long> renewalSeasonTicketRatesId = getRatesId(renewalSeasonTicketRatesDTO);
        List<Long> selectedRenewalSeasonTicketRatesId = rates.stream()
                .map(RelatedRateDTO::getNewRateId)
                .collect(Collectors.toList());
        if (!renewalSeasonTicketRatesId.containsAll(selectedRenewalSeasonTicketRatesId)) {
            throw new OneboxRestException(MsEventRateErrorCode.INVALID_RATE);
        }
    }

    private List<Long> getRatesId(SeasonTicketRatesDTO seasonTicketRatesDTO) {
        if (seasonTicketRatesDTO != null && seasonTicketRatesDTO.getData() != null) {
            return seasonTicketRatesDTO.getData().stream()
                    .map(SeasonTicketRateDTO::getId)
                    .collect(Collectors.toList());
        } else {
            return null;
        }
    }

    public boolean isRateUsedOnRenewal(Long seasonTicketId, Long rateId) {
        SeasonTicketRenewalSeatsFilter filter = new SeasonTicketRenewalSeatsFilter();
        filter.setSeasonTicketId(seasonTicketId);
        filter.setActualRateId(rateId);
        filter.setLimit(1L);
        SearchResponse<RenewalDataElastic> searchResponse = renewalElasticDao.getRenewalSeats(filter);
        return !searchResponse.hits().hits().isEmpty();
    }

    public boolean isRenewalInProcess(Long renewalSeasonTicketId) {
        try {
            CpanelRenewalRecord renewalRecord = renewalDao.getById(renewalSeasonTicketId.intValue());
            return renewalRecord != null;
        } catch (EntityNotFoundException e) {
            LOGGER.debug("Renewal for season ticket {} not found", renewalSeasonTicketId);
            return false;
        }
    }

    public void refundRenewal(String userId, Long seasonTicketId, String renewalId, String orderCode) {
        SeasonTicketRenewalCouchDocument couchDocument = seasonTicketRenewalCouchDao.get(userId);
        if (couchDocument != null) {
            SeasonTicketRenewalProduct renewalProduct = getSeasonTicketRenewalProductFromCouchDocument(couchDocument, seasonTicketId, renewalId);
            if (renewalProduct != null) {
                renewalProduct.setStatus(SeasonTicketRenewalStatus.REFUNDED);
                renewalProduct.setRefundOrderCode(orderCode);
                seasonTicketRenewalCouchDao.upsert(userId, couchDocument);
                renewalsElasticUpdaterService.sendMessage(userId, seasonTicketId, null);
            }
        }
    }

    public void cancelRenewal(String userId, Long seasonTicketId, String renewalId) {
        SeasonTicketRenewalCouchDocument couchDocument = seasonTicketRenewalCouchDao.get(userId);
        if (couchDocument != null) {
            SeasonTicketRenewalProduct renewalProduct = getSeasonTicketRenewalProductFromCouchDocument(couchDocument, seasonTicketId, renewalId);
            if (renewalProduct != null) {
                renewalProduct.setStatus(SeasonTicketRenewalStatus.CANCELED);
                seasonTicketRenewalCouchDao.upsert(userId, couchDocument);
                renewalsElasticUpdaterService.sendMessage(userId, seasonTicketId, null);
            }
        }
    }

    public void doRenewalFromExternalEvent(Long renewalSeasonTicketId,
                                           RenewalSeasonTicketDTO renewalSeasonTicketDTOParam,
                                           Boolean isUpdate) {
        Long originExternalEventId = renewalSeasonTicketDTOParam.getOriginRenewalExternalEvent();
        Map<Long, Long> relatedRatesMap = renewalSeasonTicketDTOParam.getRates().stream()
                .collect(Collectors.toMap(
                        RelatedRateDTO::getOldRateId,
                        RelatedRateDTO::getNewRateId
                ));

        // Validate season ticket
        ExternalEventDTO originExternalEvent = externalEventsService.getExternalEvent(originExternalEventId);
        SeasonTicketDTO renewalSeasonTicketDTO = seasonTicketService.getSeasonTicket(renewalSeasonTicketId);
        SeasonTicketInternalGenerationStatus renewalGenerationStatus = seasonTicketService.getGenerationStatus(renewalSeasonTicketId);
        SeasonTicketRenewalsValidator.validateRenewalSeasonTicket(renewalSeasonTicketDTO, originExternalEvent, renewalGenerationStatus);

        // Validate selected rates
        verifyRatesFromExternalEvent(originExternalEventId, renewalSeasonTicketId, renewalSeasonTicketDTOParam.getRates());

        List<RenewalExternalOriginSeat> originExternalEventSeats = getRenewalProductsFromOriginExternalEvent(originExternalEvent.getEventId(),
                originExternalEventId, originExternalEvent.getEntityId());

        if (!originExternalEventSeats.isEmpty()) {
            // Call to ms-ticket to receive corresponding mapping between original old seats and renewal new seats
            List<RenewalSeasonTicketOriginSeat> originSeats = originExternalEventSeats.stream()
                    .map(externalEventSeat -> {
                        RenewalSeasonTicketOriginSeat seasonTicketSeat = new RenewalSeasonTicketOriginSeat();
                        seasonTicketSeat.setUserId(externalEventSeat.getUserId());
                        seasonTicketSeat.setOriginSectorName(externalEventSeat.getSector());
                        seasonTicketSeat.setOriginRowName(externalEventSeat.getRow());
                        seasonTicketSeat.setOriginSeatName(externalEventSeat.getSeat());
                        seasonTicketSeat.setOriginSeatId(externalEventSeat.getRenewalProcessIdentifier());

                        seasonTicketSeat.setOriginNotNumberedZoneName(externalEventSeat.getNotNumberedZone());
                        if (SeasonTicketSeatType.NOT_NUMBERED.equals(externalEventSeat.getSeatType())) {
                            seasonTicketSeat.setSeatType(SeasonTicketSeatType.NOT_NUMBERED);
                        } else {
                            seasonTicketSeat.setSeatType(SeasonTicketSeatType.NUMBERED);
                        }

                        return seasonTicketSeat;
                    })
                    .collect(Collectors.toList());
            SeasonTicketRenewalResponse renewalResponse = seasonTicketRepository
                    .renewalSeasonTicket(renewalSeasonTicketDTO.getSessionId().longValue(),
                            null, originSeats);

            // Map the renewal seats response over the original seat id
            Map<Long, RenewalSeasonTicketRenewalSeat> renewalsSeatMap = renewalResponse.getRenewalSeats().stream()
                    .collect(Collectors.toMap(
                            RenewalSeasonTicketRenewalSeat::getOriginSeatId,
                            Function.identity()));

            // Store the couch documents found or created to deal with multiple renewals for one user
            Map<String, SeasonTicketRenewalCouchDocument> savedDocuments = new HashMap<>();
            List<String> addedIds = new ArrayList<>();
            Map<Long, CapacityMapDTO> oldCapacities = new HashMap<>();

            // Get a list of couch documents to save into the database
            List<SeasonTicketRenewalCouchDocument> couchDocuments = originExternalEventSeats.stream()
                    .map(externalOriginSeat -> getSeasonTicketRenewalCouchDocument(originExternalEventId,
                            renewalSeasonTicketId, renewalsSeatMap, null, savedDocuments, externalOriginSeat,
                            relatedRatesMap, true, isUpdate, renewalSeasonTicketDTO.getMemberMandatory(),
                            oldCapacities, addedIds))
                    .distinct()
                    .collect(Collectors.toList());

            seasonTicketRenewalCouchDao.bulkUpsert(couchDocuments);

            if (!isUpdate) {
                registerRenewal(renewalSeasonTicketId, originExternalEventId, (long) originExternalEventSeats.size(), true);
                renewalsElasticUpdaterService.sendMessage(renewalSeasonTicketId, (long) originExternalEventSeats.size());
            } else {
                Integer totalRenewalsWithAdded = updateRenewal(renewalSeasonTicketId, addedIds.size());
                renewalsElasticUpdaterService.sendMessage(renewalSeasonTicketId, totalRenewalsWithAdded.longValue());
            }

            updateRelatedSeatsStatus(originSeats, couchDocuments, renewalsSeatMap.values(), renewalSeasonTicketId, renewalSeasonTicketDTO.getSessionId().longValue());
        } else {
            throw new OneboxRestException(MsEventSeasonTicketErrorCode.SEASON_TICKET_RENEWAL_NO_PRODUCTS_TO_RENEWAL);
        }
    }

    private void verifyRatesFromExternalEvent(Long originExternalEventId, Long renewalSeasonTicketId, List<RelatedRateDTO> rates) {
        RatesFilter filter = new RatesFilter();
        filter.setOffset(0L);
        filter.setLimit(99L);
        verifyOriginExternalEventRates(originExternalEventId, rates);
        verifyRenewalRates(renewalSeasonTicketId, filter, rates);
    }

    private void verifyOriginExternalEventRates(Long originExternalEventId, List<RelatedRateDTO> rates) {
        List<IdNameDTO> externalEventRates = externalEventsService.getRatesForExternalEvent(originExternalEventId);
        List<Long> originExternalEventRatesId = externalEventRates.stream()
                .map(IdNameDTO::getId)
                .collect(Collectors.toList());
        List<Long> selectedOriginExternalEventRatesId = rates.stream()
                .map(RelatedRateDTO::getOldRateId)
                .collect(Collectors.toList());
        if (!new HashSet<>(originExternalEventRatesId).containsAll(selectedOriginExternalEventRatesId)) {
            throw new OneboxRestException(MsEventRateErrorCode.INVALID_RATE);
        }
        if (!new HashSet<>(selectedOriginExternalEventRatesId).containsAll(originExternalEventRatesId)) {
            throw new OneboxRestException(MsEventRateErrorCode.INVALID_RATE);
        }
    }

    private List<RenewalExternalOriginSeat> getRenewalProductsFromOriginExternalEvent(String originExternalEventId,
                                                                                      Long externalEventInternalId,
                                                                                      Integer externalEventEntityId) {
        List<CustomerExternalProduct> productList = externalProductsRepository
                .getExternalProductsFromExternalEvent(externalEventEntityId, originExternalEventId);
        List<IdNameDTO> ratesFromExternalEvent = externalEventsService
                .getRatesForExternalEvent(externalEventInternalId);
        Map<String, Long> rates = ratesFromExternalEvent.stream()
                .collect(Collectors.toMap(
                        IdNameDTO::getName,
                        IdNameDTO::getId
                ));
        List<RenewalExternalOriginSeat> renewalExternalOriginSeats = new ArrayList<>();
        for (int i = 0; i < productList.size(); i++) {
            CustomerExternalProduct externalProduct = productList.get(i);
            Long rateId = rates.get(externalProduct.getRateName());
            renewalExternalOriginSeats.add(SeasonTicketRenewalsConverter
                    .createRenewalExternalOriginSeat(externalProduct, (long) i, rateId));
        }
        return renewalExternalOriginSeats;
    }

    private boolean isNotNumbered(TicketDTO ticketDTO) {
        return ticketDTO.getNotNumberedAreaId() != null && ticketDTO.getNotNumberedAreaId() != 0;
    }

    private SeasonTicketDTO getAndValidateSeasonTicket(Long seasonTicketId) {
        SeasonTicketDTO seasonTicketDTO = seasonTicketService.getSeasonTicket(seasonTicketId);

        // Allow renewal is required
        if (!Boolean.TRUE.equals(seasonTicketDTO.getAllowRenewal())) {
            throw new OneboxRestException(MsEventSeasonTicketErrorCode.SEASON_TICKET_RENEWAL_IS_NOT_ALLOWED);
        }

        return seasonTicketDTO;
    }

    public void schedulePurge(Long seasonTicketId, RenewalSeatsPurgeFilter filter) {
        getAndValidateSeasonTicket(seasonTicketId);

        SearchResponse<RenewalDataElastic> seatsCountAfterPurgeResponse = renewalElasticDao.getRenewalSeatsCountAfterPurge(seasonTicketId, filter);
        Long totalSeats = renewalElasticDao.getTotalHits(seatsCountAfterPurgeResponse);

        RenewalRecord renewalRecord = renewalDao.getRenewalData(seasonTicketId);
        renewalRecord.setTotalabonos(totalSeats.intValue());
        renewalDao.update(renewalRecord);

        purgeRenewalSeatsProducerService.sendMessage(seasonTicketId, filter);
    }

    public void purgeRenewalSeats(Long seasonTicketId, RenewalSeatsPurgeFilter purgeFilter) {
        SeasonTicketDTO seasonTicketDTO = getAndValidateSeasonTicket(seasonTicketId);

        BaseRequestFilter pageFilter = new BaseRequestFilter();
        pageFilter.setOffset(0L);
        pageFilter.setLimit(LIMIT);
        Page page = PaginationUtils.buildPage(pageFilter);

        int subtotal;

        do {
            SearchResponse<RenewalDataElastic> searchResponse = renewalElasticDao.getRenewalSeatsPurge(seasonTicketId, purgeFilter, page);
            List<RenewalDataElastic> renewalDataElasticList = renewalElasticDao.convertSearchResponseIntoRenewalDataElastic(searchResponse);
            performDeleteMultipleRenewals(seasonTicketId, seasonTicketDTO, renewalDataElasticList);

            subtotal = searchResponse.hits().hits().size();
            pageFilter.setOffset((long) subtotal);
        } while (subtotal >= LIMIT);

        SeasonTicketRenewalSeatsFilter filterAfterPurge = new SeasonTicketRenewalSeatsFilter();
        filterAfterPurge.setSeasonTicketId(seasonTicketId);
        SearchResponse<RenewalDataElastic> seatsCountAfterPurgeResponse = renewalElasticDao.getRenewalSeats(filterAfterPurge);
        Long totalSeats = renewalElasticDao.getTotalHits(seatsCountAfterPurgeResponse);

        RenewalRecord renewalRecord = renewalDao.getRenewalData(seasonTicketId);
        if (totalSeats == 0) {
            renewalDao.delete(renewalRecord);
        } else {
            renewalRecord.setTotalabonos(totalSeats.intValue());
            renewalDao.update(renewalRecord);
        }
    }

    private List<DeleteRenewalsResponseItem> performDeleteMultipleRenewals(Long seasonTicketId, SeasonTicketDTO seasonTicketDTO, List<RenewalDataElastic> renewalDataElasticList) {
        // Filter renewals to release their seats and related seats
        List<RenewalDataElastic> renewalsToRelease = renewalDataElasticList.stream()
                .filter(this::hasToBeReleased)
                .collect(Collectors.toList());

        List<DeleteRenewalsResponseItem> responseItems = new ArrayList<>();
        if (!renewalsToRelease.isEmpty()) {
            releaseRenewals(seasonTicketId, seasonTicketDTO, renewalDataElasticList, renewalsToRelease, responseItems);
        }

        // Perform delete
        if (!renewalDataElasticList.isEmpty()) {
            deleteSeasonTicketRenewalsList(seasonTicketId, renewalDataElasticList);
        }
        return responseItems;
    }

    public CountRenewalsPurgeResponse countRenewalsPurge(Long seasonTicketId, RenewalSeatsPurgeFilter filter) {
        getAndValidateSeasonTicket(seasonTicketId);

        SearchResponse seatsCountAfterPurgeResponse = renewalElasticDao.getRenewalSeatsPurgeCount(seasonTicketId, filter);
        Long totalSeats = renewalElasticDao.getTotalHits(seatsCountAfterPurgeResponse);

        return new CountRenewalsPurgeResponse(totalSeats.intValue());
    }

    public Boolean validateRenewalStatus(Long seasonTicketId) {
        RenewalRecord renewalRecord = renewalDao.getRenewalData(seasonTicketId);
        if (renewalRecord == null) {
            return Boolean.FALSE;
        }

        SearchResponse renewalsCount = renewalElasticDao.getRenewalSeatsTotalCount(seasonTicketId);
        Long totalSeats = renewalElasticDao.getTotalHits(renewalsCount);

        RenewalGenerationStatus generationStatus = getRenewalsGenerationStatus(renewalRecord.getTotalabonos(), totalSeats.intValue());
        if (RenewalGenerationStatus.DONE.equals(generationStatus)) {
            return Boolean.TRUE;
        } else {
            throw new OneboxRestException(MsEventSeasonTicketErrorCode.SEASON_TICKET_RENEWAL_IN_PROGRESS);
        }
    }

    private RenewalGenerationStatus getRenewalsGenerationStatus(Integer total, Integer totalRecovered) {
        int pendingRenewals = total - totalRecovered;
        if (pendingRenewals > 0) {
            return RenewalGenerationStatus.IN_PROGRESS;
        } else if (pendingRenewals < 0) {
            return RenewalGenerationStatus.PURGE_IN_PROGRESS;
        } else {
            return RenewalGenerationStatus.DONE;
        }
    }

    public SeasonTicketRenewalsConfigDTO getSeasonTicketRenewalConfig(Long seasonTicketId) {
        SeasonTicketDTO seasonTicketDTO = getAndValidateSeasonTicket(seasonTicketId);
        if (BooleanUtils.isNotTrue(seasonTicketDTO.getAllowRenewal())) {
            throw new OneboxRestException(MsEventSeasonTicketErrorCode.SEASON_TICKET_RENEWAL_IS_NOT_ALLOWED);
        }
        SeasonTicketRenewalConfig renewalsConfig = seasonTicketRenewalConfigCouchDao.get(String.valueOf(seasonTicketId));
        return SeasonTicketRenewalsConverter.toDTO(renewalsConfig);
    }

    public void updateSeasonTicketRenewalConfig(Long seasonTicketId, UpdateSeasonTicketRenewalsConfigDTO config) {
        SeasonTicketDTO seasonTicketDTO = getAndValidateSeasonTicket(seasonTicketId);
        if (BooleanUtils.isNotTrue(seasonTicketDTO.getAllowRenewal())) {
            throw new OneboxRestException(MsEventSeasonTicketErrorCode.SEASON_TICKET_RENEWAL_IS_NOT_ALLOWED);
        }
        SeasonTicketRenewalConfig renewalsConfig = seasonTicketRenewalConfigCouchDao.get(String.valueOf(seasonTicketId));
        SeasonTicketRenewalsConverter.toDocument(renewalsConfig, config);
        seasonTicketRenewalConfigCouchDao.upsert(String.valueOf(seasonTicketId), renewalsConfig);
    }

    public void updateAutomaticRenewalStatus(Long seasonTicketId, UpdateAutomaticRenewalStatus request) {
        SeasonTicketDTO seasonTicketDTO = getAndValidateSeasonTicket(seasonTicketId);
        if (BooleanUtils.isNotTrue(seasonTicketDTO.getAllowRenewal())) {
            throw new OneboxRestException(MsEventSeasonTicketErrorCode.SEASON_TICKET_RENEWAL_IS_NOT_ALLOWED);
        }

        SeasonTicketRenewalConfig renewalsConfig = seasonTicketRenewalConfigCouchDao.get(String.valueOf(seasonTicketId));
        renewalsConfig.setAutomaticRenewalStatus(AutomaticRenewalStatus.valueOf(request.getStatus().name()));
        seasonTicketRenewalConfigCouchDao.upsert(String.valueOf(seasonTicketId), renewalsConfig);
    }
}
