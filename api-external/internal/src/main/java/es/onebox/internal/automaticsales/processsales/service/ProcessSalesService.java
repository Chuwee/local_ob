package es.onebox.internal.automaticsales.processsales.service;

import com.hazelcast.map.IMap;
import com.oneboxtds.datasource.s3.ObjectPolicy;
import com.oneboxtds.datasource.s3.S3BinaryRepository;
import es.onebox.common.config.HazelcastConfiguration;
import es.onebox.common.datasources.avetconfig.dto.ClubConfig;
import es.onebox.common.datasources.avetconfig.repository.IntAvetConfigRepository;
import es.onebox.common.datasources.common.converters.ConvertUtils;
import es.onebox.common.datasources.distribution.dto.AddSeatsDTO;
import es.onebox.common.datasources.distribution.dto.ClientDataParamMap;
import es.onebox.common.datasources.distribution.dto.InvitationRequest;
import es.onebox.common.datasources.distribution.dto.ItemWarning;
import es.onebox.common.datasources.distribution.dto.OrderResponse;
import es.onebox.common.datasources.distribution.dto.SeatDTO;
import es.onebox.common.datasources.distribution.dto.SeatsAutoRequest;
import es.onebox.common.datasources.distribution.dto.attendee.ItemAttendee;
import es.onebox.common.datasources.distribution.dto.attendee.ItemAttendees;
import es.onebox.common.datasources.distribution.dto.deliverymethods.DeliveryMethodsRequestDTO;
import es.onebox.common.datasources.distribution.dto.deliverymethods.OrderDeliveryMethod;
import es.onebox.common.datasources.distribution.dto.deliverymethods.PreConfirmRequestDTO;
import es.onebox.common.datasources.distribution.dto.order.ConfirmRequest;
import es.onebox.common.datasources.distribution.dto.order.items.ItemSeatAllocation;
import es.onebox.common.datasources.distribution.dto.order.items.OrderItem;
import es.onebox.common.datasources.distribution.dto.order.items.allocation.Seat;
import es.onebox.common.datasources.distribution.dto.order.items.allocation.Sector;
import es.onebox.common.datasources.distribution.repository.DistributionRepository;
import es.onebox.common.datasources.mappings.dto.MappingResponse;
import es.onebox.common.datasources.mappings.repository.IntMappingsRepository;
import es.onebox.common.datasources.ms.channel.dto.ChannelDTO;
import es.onebox.common.datasources.ms.channel.dto.MsSaleRequestsResponseDTO;
import es.onebox.common.datasources.ms.channel.dto.config.ChannelConfigDTO;
import es.onebox.common.datasources.ms.channel.enums.ChannelStatus;
import es.onebox.common.datasources.ms.channel.enums.MsSaleRequestsStatus;
import es.onebox.common.datasources.ms.channel.repository.ChannelRepository;
import es.onebox.common.datasources.ms.entity.dto.Operator;
import es.onebox.common.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.common.datasources.ms.event.dto.EventDTO;
import es.onebox.common.datasources.ms.event.dto.SessionDTO;
import es.onebox.common.datasources.ms.event.dto.SessionsDTO;
import es.onebox.common.datasources.ms.event.enums.EventType;
import es.onebox.common.datasources.ms.event.enums.SessionStatus;
import es.onebox.common.datasources.ms.event.repository.MsEventRepository;
import es.onebox.common.datasources.ms.event.request.SessionSearchFilter;
import es.onebox.common.datasources.ms.order.dto.OrderProductDTO;
import es.onebox.common.datasources.ms.order.dto.PreOrderDTO;
import es.onebox.common.datasources.ms.order.repository.MsOrderRepository;
import es.onebox.common.datasources.ms.venue.dto.BaseCodeTag;
import es.onebox.common.datasources.ms.venue.dto.BasePriceType;
import es.onebox.common.datasources.ms.venue.dto.SectorDTO;
import es.onebox.common.datasources.ms.venue.dto.VenueTemplate;
import es.onebox.common.datasources.ms.venue.repository.VenueTemplateRepository;
import es.onebox.common.datasources.oauth2.repository.TokenRepository;
import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.core.exception.ExceptionBuilder;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.file.exporter.generator.export.CsvSeparatorFormat;
import es.onebox.core.file.exporter.generator.export.FileFormat;
import es.onebox.core.file.exporter.status.model.ExportProcess;
import es.onebox.core.logger.util.LogUtil;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.hazelcast.core.service.HazelcastMapService;
import es.onebox.internal.automaticsales.eip.process.GenerateAutomaticSalesProcesssMessage;
import es.onebox.internal.automaticsales.eip.report.ReportQueueProducer;
import es.onebox.internal.automaticsales.export.dto.Delivery;
import es.onebox.internal.automaticsales.export.dto.ExportResponse;
import es.onebox.internal.automaticsales.export.enums.ReportDeliveryType;
import es.onebox.internal.automaticsales.filemanagement.dao.AutomaticSalesCouchDao;
import es.onebox.internal.automaticsales.filemanagement.dto.AutomaticSaleFileData;
import es.onebox.internal.automaticsales.filemanagement.dto.AutomaticSalesExportRequest;
import es.onebox.internal.automaticsales.filemanagement.dto.FileInfo;
import es.onebox.internal.automaticsales.filemanagement.dto.FileInfoDTO;
import es.onebox.internal.automaticsales.filemanagement.dto.SaleRequestDTO;
import es.onebox.internal.automaticsales.filemanagement.dto.SaleRequestListDTO;
import es.onebox.internal.automaticsales.processsales.dto.AutomaticSaleItem;
import es.onebox.internal.automaticsales.processsales.dto.AutomaticSaleRequest;
import es.onebox.internal.automaticsales.processsales.dto.AvetSeatMappings;
import es.onebox.internal.automaticsales.processsales.dto.CustomerData;
import es.onebox.internal.automaticsales.processsales.dto.ProcessSalesConfigurationRequest;
import es.onebox.internal.automaticsales.processsales.dto.ProcessSalesRequest;
import es.onebox.internal.automaticsales.processsales.dto.SaleDTO;
import es.onebox.internal.automaticsales.processsales.dto.UpdateProcessSalesRequest;
import es.onebox.internal.automaticsales.processsales.enums.AttendantFields;
import es.onebox.internal.automaticsales.processsales.enums.AutomaticSalesExecutionStatus;
import es.onebox.internal.automaticsales.processsales.utils.SectorPriceZoneKey;
import es.onebox.internal.automaticsales.processsales.utils.ValidationUtils;
import es.onebox.internal.automaticsales.report.converter.ExportConverter;
import es.onebox.internal.automaticsales.report.dto.AutomaticSalesSearchFilter;
import es.onebox.internal.automaticsales.report.enums.ApiExternalExportType;
import es.onebox.internal.automaticsales.report.enums.AutomaticSalesFields;
import es.onebox.internal.automaticsales.report.model.AutomaticSalesFileField;
import es.onebox.internal.automaticsales.report.model.AutomaticSalesReportFilter;
import es.onebox.internal.automaticsales.report.provider.AutomaticSalesReportProvider;
import es.onebox.internal.automaticsales.utils.HashUtils;
import es.onebox.internal.automaticsales.utils.OrderUtils;
import es.onebox.internal.utils.CsvParseUtils;
import es.onebox.internal.utils.progress.ProgressService;
import es.onebox.internal.utils.progress.enums.ConsumerType;
import es.onebox.internal.utils.progress.enums.EventMessageType;
import es.onebox.internal.utils.progress.enums.StatusMessage;
import es.onebox.internal.utils.progress.model.AutomaticSalesMessage;
import es.onebox.internal.utils.progress.model.ProgressMessage;
import es.onebox.message.broker.producer.queue.DefaultProducer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static es.onebox.internal.automaticsales.processsales.utils.ValidationUtils.checkValidEmail;

@Service
public class ProcessSalesService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessSalesService.class);
    public static final String FILE_NAME_PROCESSED_WITH_ERRORS = "_end_with_errors";
    public static String NO_OWNER = "NO_OWNER";
    private static final String GROUP_SECTOR_RESERVATION_CONFLICT = "SECTOR_RESERVATION_ERROR";
    private static final String GENERIC_ERROR = "GENERIC_ERROR";
    private static final String UNKNOWN = "UNKNOWN";
    private static final String DELIVERY_EMAIL_ADDRESS = "address";
    private final S3BinaryRepository s3AutomaticSalesRepository;
    private final TokenRepository tokenRepository;
    private final MsEventRepository eventRepository;
    private final VenueTemplateRepository venuesRepository;
    private final IntMappingsRepository intMappingsRepository;
    private final IntAvetConfigRepository intAvetConfigRepository;
    private final DistributionRepository distributionRepository;
    private final DefaultProducer automaticSalesProcessProducer;
    private final ChannelRepository channelRepository;
    private final AutomaticSalesCouchDao automaticSalesCouchDao;
    private final EntitiesRepository entitiesRepository;
    private final ReportQueueProducer reportQueueProducer;
    private final ProgressService progressService;
    private final HazelcastMapService hazelcastMapService;

    public static final String EVENTS_PATH = "events/";
    public static final String SESSIONS_PATH = "sessions/";
    public static final String ENTITIES_PATH = "entities/";
    public static final String FOLDER_SEPARATOR = "/";
    public static final String EXTENSION_CSV = ".csv";
    private final MsOrderRepository msOrderRepository;
    private final ValidationService validationService;

    @Autowired
    public ProcessSalesService(@Qualifier("s3AutomaticSalesRepository") S3BinaryRepository s3AutomaticSalesRepository,
                               TokenRepository tokenRepository,
                               MsEventRepository eventRepository, VenueTemplateRepository venuesRepository,
                               IntMappingsRepository intMappingsRepository, IntAvetConfigRepository intAvetConfigRepository,
                               DistributionRepository distributionRepository,
                               @Qualifier("automaticSalesProcessProducer") DefaultProducer automaticSalesProcessProducer,
                               ChannelRepository channelRepository, AutomaticSalesCouchDao automaticSalesCouchDao,
                               EntitiesRepository entitiesRepository, ReportQueueProducer reportQueueProducer, ProgressService progressService,
                               HazelcastMapService hazelcastMapService, MsOrderRepository msOrderRepository, ValidationService validationService) {
        this.s3AutomaticSalesRepository = s3AutomaticSalesRepository;
        this.tokenRepository = tokenRepository;
        this.eventRepository = eventRepository;
        this.venuesRepository = venuesRepository;
        this.intMappingsRepository = intMappingsRepository;
        this.intAvetConfigRepository = intAvetConfigRepository;
        this.distributionRepository = distributionRepository;
        this.automaticSalesProcessProducer = automaticSalesProcessProducer;
        this.channelRepository = channelRepository;
        this.automaticSalesCouchDao = automaticSalesCouchDao;
        this.entitiesRepository = entitiesRepository;
        this.reportQueueProducer = reportQueueProducer;
        this.progressService = progressService;
        this.hazelcastMapService = hazelcastMapService;
        this.msOrderRepository = msOrderRepository;
        this.validationService = validationService;
    }

    public void processSales(Long sessionId, ProcessSalesRequest request) {

        AutomaticSalesExecutionStatus inProgress = getSemaphore(sessionId);

        if (inProgress != null && inProgress.equals(AutomaticSalesExecutionStatus.IN_PROGRESS)) {
            throw new OneboxRestException(ApiExternalErrorCode.ALREADY_EXECUTING_AUTOMATIC_SALES);
        }
        List<SaleDTO> sales = request.getSaleRequestListDTO().stream().map(ProcessSalesService::getSaleDTO).toList();

        validateSalesWithConfig(
                sessionId,
                sales,
                request.getProcessSalesConfigurationRequest()
        );

        validateFile(sales, sessionId, request.getProcessSalesConfigurationRequest().getChannelId());

        bulk(sessionId, request.getSaleRequestListDTO(), request.getProcessSalesConfigurationRequest());

        try {
            automaticSalesProcessProducer.sendMessage(GenerateAutomaticSalesProcesssMessage.of(request.getProcessSalesConfigurationRequest(), sessionId));
        } catch (Exception e) {
            LOGGER.warn("[PROCESS SALES] - automaticSalesProcessProducer Message could not be send", e);
        }

    }

    public String processIndividualSale(AutomaticSaleRequest request) {
        if (request.getChannelId() == null) {
            throw new OneboxRestException(ApiExternalErrorCode.ERROR_PROCESSING_SALES, "channel not found", null);
        }
        CustomerData customerData = request.getCustomerData();
        if (customerData == null || customerData.getLanguage() == null || customerData.getName() == null) {
            throw new OneboxRestException(ApiExternalErrorCode.ERROR_PROCESSING_SALES, "customer data not found", null);
        }
        List<AutomaticSaleItem> saleItems = request.getItems();
        if (CollectionUtils.isEmpty(saleItems) ||
                saleItems.stream().anyMatch(r -> r.getSessionId() == null) ||
                saleItems.stream().anyMatch(r -> r.getPriceTypeId() == null)) {
            throw new OneboxRestException(ApiExternalErrorCode.ERROR_PROCESSING_SALES, "invalid items sale request", null);
        }

        ChannelConfigDTO channelConfig = this.channelRepository.getChannelConfig(request.getChannelId());
        String token = tokenRepository.getSellerChannelToken(channelConfig.getId(), channelConfig.getApiKey());
        Map<Long, List<AutomaticSaleItem>> requestBySession = saleItems.stream()
                .collect(Collectors.groupingBy(AutomaticSaleItem::getSessionId));

        try {
            OrderResponse orderResponse = distributionRepository.createOrder(token, customerData.getLanguage());

            for (Map.Entry<Long, List<AutomaticSaleItem>> sessionRequest : requestBySession.entrySet()) {
                Integer sessionId = sessionRequest.getKey().intValue();
                List<AutomaticSaleItem> sessionItems = sessionRequest.getValue();
                Map<Long, Long> itemsByPriceZone = sessionItems.stream()
                        .collect(Collectors.groupingBy(AutomaticSaleItem::getPriceTypeId, Collectors.counting()));
                for (Map.Entry<Long, Long> sessionPriceTypeItems : itemsByPriceZone.entrySet()) {
                    SeatsAutoRequest seatsAutoDTO = new SeatsAutoRequest(sessionId, sessionPriceTypeItems.getValue(),
                            sessionPriceTypeItems.getKey(), null, null);
                    orderResponse = distributionRepository.addSeatsAuto(token, orderResponse.getId(), seatsAutoDTO, null);
                }
            }

            if (BooleanUtils.isTrue(request.getInvitation())) {
                InvitationRequest invitationRequest = new InvitationRequest();
                invitationRequest.setItems(orderResponse.getItems().stream().map(OrderItem::getId).toList());
                distributionRepository.addInvitations(token, orderResponse.getId(), invitationRequest);
            }

            orderResponse = distributionRepository.addBuyerData(token, orderResponse.getId(), fillClientData(customerData));

            PreConfirmRequestDTO preConfirmRequest = new PreConfirmRequestDTO();
            preConfirmRequest.setDeliveryMethod(OrderDeliveryMethod.EMAIL);
            orderResponse = distributionRepository.preConfirm(token, orderResponse.getId(), preConfirmRequest);

            ConfirmRequest confirmRequest = new ConfirmRequest(null, null, false);
            orderResponse = distributionRepository.confirm(token, orderResponse.getId(), confirmRequest);

            LOGGER.info("[AUTOMATIC-SALES][{}][{}] - Finish the process sale with {} items, {}",
                    customerData.getEmail(), request.getSourceCode(), saleItems.size(), orderResponse.getCode());

            return orderResponse.getCode();

        } catch (Exception e) {
            LOGGER.error("[AUTOMATIC-SALES] sessionIds: " + StringUtils.join(requestBySession.keySet(), ",") +
                    " - Error processing automatic sale", e);
            throw new RuntimeException(e);
        }
    }

    public void validateSalesWithConfig(Long sessionId, List<SaleDTO> sales, ProcessSalesConfigurationRequest config) {

        SessionDTO session = eventRepository.getSession(sessionId);

        if (config.getFilename() == null) {
            throw new OneboxRestException(ApiExternalErrorCode.ERROR_VOID_FILENAME);
        }
        if (CollectionUtils.isEmpty(sales)) {
            throw new OneboxRestException(ApiExternalErrorCode.ERROR_VOID_PROCESSING_FILE);
        }
        if (config.getUseLocators()) {
            SaleDTO sale = sales.stream().filter(currentSale -> StringUtils.isBlank(currentSale.getOriginalLocator())).findFirst().orElse(null);
            if (sale != null) {
                throw new OneboxRestException(ApiExternalErrorCode.ORIGINAL_LOCATOR_NULL);
            }
        }
        if (config.getUseSeatMappings()) {
            SaleDTO sale = sales.stream().filter(currentSale -> currentSale.getSeatId() == null).findFirst().orElse(null);
            if (sale != null) {
                throw new OneboxRestException(ApiExternalErrorCode.SEAT_ID_NULL);
            }
        }

        if (BooleanUtils.isTrue(config.getAllowSkipNonAdjacentSeats()) && BooleanUtils.isTrue(config.getAllowBreakAdjacentSeats())) {
            throw new OneboxRestException(ApiExternalErrorCode.INVALID_ADJACENCY_CONFIGURATION);
        }

        if (config.getChannelId() == null) {
            throw new OneboxRestException(ApiExternalErrorCode.CHANNEL_ID_REQUIRED);
        }

        ChannelDTO channel = channelRepository.getChannel(config.getChannelId());

        if (!channel.getStatus().equals(ChannelStatus.ACTIVE)) {
            throw new OneboxRestException(ApiExternalErrorCode.WRONG_CHANNEL_STATUS);
        }

        MsSaleRequestsResponseDTO saleRequest = channelRepository.getSaleRequests(config.getChannelId(), session.getEventId());

        if (saleRequest == null || CollectionUtils.isEmpty(saleRequest.getData()) || !saleRequest.getData().get(0).getStatus().equals(MsSaleRequestsStatus.ACCEPTED)) {
            throw new OneboxRestException(ApiExternalErrorCode.WRONG_SALES_REQUEST_STATUS);
        }

        if (!config.getDefaultPurchaseLanguage()) {
            SaleDTO saleLanguageCode = sales.stream().filter(currentSale -> StringUtils.isBlank(currentSale.getLanguage())).findFirst().orElse(null);
            if (saleLanguageCode != null) {
                throw new OneboxRestException(ApiExternalErrorCode.LANGUAGE_CODE_NULL);
            }
            ChannelConfigDTO channelConfig = this.channelRepository.getChannelConfig(config.getChannelId());

            List<String> channelLanguageCodes = channelConfig.getChannelLanguages().stream().map(languageCode -> ConvertUtils.toLanguageTag(languageCode.getCode())).collect(Collectors.toList());

            sales.forEach(currentSale -> {
                if (!channelLanguageCodes.contains(currentSale.getLanguage())) {
                    throw OneboxRestException.builder(ApiExternalErrorCode.INVALID_LANGUAGE).build();
                }
            });
        }

        Long venueTemplateId = session.getVenueConfigId();

        if (!BooleanUtils.isTrue(config.getUseSeatMappings())) {

            SaleDTO salePriceZone = sales.stream().filter(currentSale -> StringUtils.isBlank(currentSale.getPriceZone())).findFirst().orElse(null);
            if (salePriceZone != null) {
                throw new OneboxRestException(ApiExternalErrorCode.PRICE_ZONE_ID_NULL);
            }

            List<String> priceTypes = venuesRepository.getPriceTypes(venueTemplateId).stream().map(BaseCodeTag::getName).collect(Collectors.toList());
            List<SaleDTO> salesWithWrongPriceTypes = sales.stream()
                    .filter(currentSale -> !priceTypes.contains(currentSale.getPriceZone()))
                    .toList();

            if (CollectionUtils.isNotEmpty(salesWithWrongPriceTypes)) {
                String wrongGroups = salesWithWrongPriceTypes.stream()
                        .map(group -> String.valueOf(group.getGroup()))
                        .collect(Collectors.joining(", "));
                throw ExceptionBuilder.build(ApiExternalErrorCode.PRICE_ZONE_ID_NOT_EXISTS, wrongGroups);
            }

            List<String> sectorList = venuesRepository.getSectors(venueTemplateId).stream().map(SectorDTO::getName).collect(Collectors.toList());
            List<SaleDTO> salesWithWrongSectors = sales.stream()
                    .filter(currentSale -> !sectorList.contains(currentSale.getSector()) && !StringUtils.isBlank(currentSale.getSector()))
                    .toList();

            if (CollectionUtils.isNotEmpty(salesWithWrongSectors)) {
                String wrongGroups = salesWithWrongSectors.stream()
                        .map(group -> String.valueOf(group.getGroup()))
                        .collect(Collectors.joining(", "));
                throw ExceptionBuilder.build(ApiExternalErrorCode.SECTOR_ID_NOT_EXISTS, wrongGroups);
            }
        }

        String incorrectMailGroups = sales.stream().filter(sale -> !checkValidEmail(sale.getEmail())).map(group -> String.valueOf(group.getGroup())).collect(Collectors.joining(", "));
        if (!StringUtils.isBlank(incorrectMailGroups)) {
            throw ExceptionBuilder.build(ApiExternalErrorCode.WRONG_EMAIL_IN_ROWS, incorrectMailGroups);
        }

        List<SaleDTO> salesNotProcessed = sales.stream().filter(sale -> !sale.isProcessed()).collect(Collectors.toList());
        if (salesNotProcessed.isEmpty()) {
            throw new OneboxRestException(ApiExternalErrorCode.ERROR_VOID_PROCESSING_FILE);
        }

    }

    public void executeProcess(Long sessionId, ProcessSalesConfigurationRequest config) {

        SessionDTO session = eventRepository.getSession(sessionId);
        setSemaphore(sessionId, AutomaticSalesExecutionStatus.IN_PROGRESS);
        try {
            executeProcessSales(session, config);
        } catch (Exception ex) {
            setSemaphore(sessionId, AutomaticSalesExecutionStatus.ERROR);
            sendProgress(session.getEntityId(), session.getEventId(), sessionId, 100, StatusMessage.ERROR);
            throw ex;
        }
        setSemaphore(sessionId, AutomaticSalesExecutionStatus.DONE);
    }

    public ExportResponse executeProcessSales(SessionDTO session, ProcessSalesConfigurationRequest config) {

        ChannelDTO channel = channelRepository.getChannel(config.getChannelId());
        EventDTO event = eventRepository.getEvent(session.getEventId());
        int longSleep = getLongSleep(event);

        LOGGER.info("[PROCESS SALES][{}][{}] Start process sales", session.getName(), channel.getName());
        ZonedDateTime start = ZonedDateTime.now();
        Long sessionId = session.getId();

        Map<Long, String> failed = new HashMap<>();
        Set<String> noAdjacentSeatFoundGroups = new HashSet<>();

        String path = preparePath(config.getFilename(), session);

        checkIfFileExists(path);

        List<SaleDTO> downloadedSalesList;
        try (InputStream inputStream = new ByteArrayInputStream(s3AutomaticSalesRepository.download(path))) {
            downloadedSalesList = CsvParseUtils.fromCSV(inputStream, SaleDTO.class);
        } catch (Exception e) {
            LOGGER.error("[PROCESS SALES][{}] - Error processing sales csv File", session.getName());
            throw new OneboxRestException(ApiExternalErrorCode.ERROR_PROCESSING_SALES);
        }

        String previewToken = getPreviewToken(sessionId);

        Long venueTemplateId = session.getVenueConfigId();

        Map<Long, List<SaleDTO>> groups;

        List<SaleDTO> sales = downloadedSalesList.stream().filter(sale -> !sale.isProcessed()).collect(Collectors.toList());
        List<SaleDTO> salesProcessed = downloadedSalesList.stream().filter(SaleDTO::isProcessed).collect(Collectors.toList());

        groups = sales.stream().collect(Collectors.groupingBy(SaleDTO::getGroup));

        int processedGroups = 0;
        Integer progress = calculateProgress(groups.size(), processedGroups++);
        sendProgress(session.getEntityId(), session.getEventId(), sessionId, progress, StatusMessage.IN_PROGRESS);

        List<Long> sortedGroups = groups.keySet().stream().sorted().collect(Collectors.toList());

        if (config.isSort()) {
            sortedGroups = getSortedSalesList(groups);
        }

        ChannelConfigDTO channelConfig = this.channelRepository.getChannelConfig(config.getChannelId());
        String token = tokenRepository.getSellerChannelToken(channel.getId(), channelConfig.getApiKey());

        List<SaleDTO> resultSalesDTO = new ArrayList<>();

        List<BasePriceType> priceTypes = venuesRepository.getPriceTypes(venueTemplateId);
        List<SectorDTO> sectorList = venuesRepository.getSectors(venueTemplateId);

        for (Long groupId : sortedGroups) {
            List<SaleDTO> salesDTO = groups.get(groupId);
            String language = salesDTO.stream()
                    .filter(Objects::nonNull)
                    .findFirst()
                    .map(SaleDTO::getLanguage)
                    .orElse(null);

            SaleDTO owner = null;
            Long externalSeatId;

            AddSeatsDTO addSeats = new AddSeatsDTO();
            addSeats.setSeats(new ArrayList<>());

            for (SaleDTO saleDTO : salesDTO) {
                if (saleDTO.isOwner()) {
                    owner = saleDTO;
                }

                if (config.getUseSeatMappings()) {
                    if (config.getUseOBIdsForSeatMappings()) {
                        addSeats.getSeats().add(createSeat(saleDTO.getSeatId(), sessionId));
                    } else {
                        externalSeatId = getOBSeatId(saleDTO.getSeatId(), sessionId, session.getEntityId(), config);
                        addSeats.getSeats().add(createSeat(externalSeatId, sessionId));
                    }
                }
            }

            if (salesDTO.stream().noneMatch(SaleDTO::isOwner)) {
                LOGGER.error("[PROCESS SALES][{}][{}] - Skip group {} because there is no owner", session.getName(), groupId, groupId);
                failed.put(groupId, NO_OWNER);
                setErrorInSaleGroup(salesDTO, NO_OWNER, "");
                resultSalesDTO.addAll(salesDTO);
                continue;
            }

            OrderResponse orderResponse = null;

            try {
                if (isProcessBlocked(sessionId)) {
                    saveProcess(groups, failed, resultSalesDTO, true);
                    LOGGER.error("[PROCESS SALES] - Stopped execution by hazelcast");
                    break;
                }

                if (EventType.AVET.equals(event.getType()) && hasDifferentSectors(salesDTO)) {
                    LOGGER.error("[PROCESS SALES][{}][{}] - Skip group {} because has different sectors", session.getName(), groupId, groupId);
                    failed.put(groupId, GROUP_SECTOR_RESERVATION_CONFLICT);
                    setErrorInSaleGroup(salesDTO, GROUP_SECTOR_RESERVATION_CONFLICT, "This group has several sectors");
                    resultSalesDTO.addAll(salesDTO);
                    continue;
                }

                if (BooleanUtils.isTrue(config.getUseSeatMappings())) {

                    orderResponse = distributionRepository.createOrder(token, language);
                    LOGGER.info("[PROCESS SALES][{}][{}] - Init the process from group {}, created cart: {}", session.getName(), groupId, groupId, orderResponse.getId());

                    addSeatPreviewToken(addSeats, previewToken);

                    try {
                        orderResponse = distributionRepository.addSeats(token, orderResponse.getId(), addSeats, null);

                        Thread.sleep(5);
                    } catch (OneboxRestException e) {
                        try {
                            distributionRepository.releaseSeats(token, orderResponse.getId(), null);
                        } catch (Exception ex) {
                            LOGGER.error("[PROCESS SALES][{}][{}] - Error trying to release seats. {}", session.getName(), groupId, e.getLocalizedMessage());
                        }
                        failed.put(salesDTO.get(0).getGroup(), e.getErrorCode() + " " + e.getMessage());
                        setErrorInSaleGroup(salesDTO, e.getErrorCode(), e.getMessage(), orderResponse);
                        LOGGER.error("[PROCESS SALES][{}][{}] - Continue group {} with error {}. Continue with next...", session.getName(), groupId, owner.getGroup(), e.getMessage());
                        resultSalesDTO.addAll(salesDTO);
                        continue;
                    } catch (Exception e) {
                        try {
                            distributionRepository.releaseSeats(token, orderResponse.getId(), null);
                        } catch (Exception ex) {
                            LOGGER.error("[PROCESS SALES][{}][{}] - Error trying to release seats. {}", session.getName(), groupId, e.getLocalizedMessage());
                        }
                        failed.put(salesDTO.get(0).getGroup(), e.getMessage());
                        setErrorInSaleGroup(salesDTO, Optional.ofNullable(e.getCause()).map(Throwable::getMessage).orElse(GENERIC_ERROR), e.getLocalizedMessage(), orderResponse);
                        LOGGER.error("[PROCESS SALES][{}][{}] - Continue group {} with error {}. Continue with next...", session.getName(), groupId, owner.getGroup(), e.getMessage());
                        resultSalesDTO.addAll(salesDTO);
                        continue;
                    }
                } else {

                    orderResponse = distributionRepository.createOrder(token, language);
                    LOGGER.info("[PROCESS SALES][{}][{}] - Init the process from group {}, created cart: {}", session.getName(), groupId, owner.getGroup(), orderResponse.getId());

                    Map<SectorPriceZoneKey, List<SaleDTO>> subGroupSales = getSalesBySectorAndPriceZone(salesDTO);
                    try {
                        for (Map.Entry<SectorPriceZoneKey, List<SaleDTO>> entry : subGroupSales.entrySet()) {
                            SeatsAutoRequest seatsAutoDTO = new SeatsAutoRequest(Math.toIntExact(sessionId),
                                    (long) entry.getValue().size(), getPriceZoneId(priceTypes, entry.getKey().getPriceZone()),
                                    getSectorId(sectorList, entry.getKey().getSector()), previewToken);
                            orderResponse = distributionRepository.addSeatsAuto(token, orderResponse.getId(), seatsAutoDTO, null);
                            Thread.sleep(longSleep);
                        }
                    } catch (OneboxRestException e) {
                        try {
                            distributionRepository.releaseSeats(token, orderResponse.getId(), null);
                        } catch (Exception ex) {
                            LOGGER.error("[PROCESS SALES][{}][{}] - Error trying to release seats. {}", session.getName(), groupId, e.getLocalizedMessage());
                        }
                        failed.put(salesDTO.get(0).getGroup(), e.getErrorCode() + " " + e.getMessage());
                        setErrorInSaleGroup(salesDTO, e.getErrorCode(), e.getMessage(), orderResponse);
                        LOGGER.error("[PROCESS SALES][{}][{}] - Continue group {} with error {}. Continue with next...", session.getName(), groupId, owner.getGroup(), e.getMessage());
                        resultSalesDTO.addAll(salesDTO);
                        continue;
                    } catch (Exception e) {
                        try {
                            distributionRepository.releaseSeats(token, orderResponse.getId(), null);
                        } catch (Exception ex) {
                            LOGGER.error("[PROCESS SALES][{}][{}] - Error trying to release seats. {}", session.getName(), groupId, e.getLocalizedMessage());
                        }
                        failed.put(salesDTO.get(0).getGroup(), e.getMessage());
                        setErrorInSaleGroup(salesDTO, Optional.ofNullable(e.getCause()).map(Throwable::getMessage).orElse(GENERIC_ERROR), e.getLocalizedMessage(), orderResponse);
                        LOGGER.error("[PROCESS SALES][{}][{}] - Continue group {} with error {}. Continue with next...", session.getName(), groupId, owner.getGroup(), e.getMessage());
                        resultSalesDTO.addAll(salesDTO);
                        continue;
                    }
                }

                List<OrderItem> orderItems = orderResponse.getItems();
                for (OrderItem item : orderItems) {
                    ItemSeatAllocation allocation = item.getAllocation();
                    LOGGER.info("[PROCESS SALES][{}][{}] - Added seat of sector: {}, row: {}, seatId: {}", session.getName(), groupId, Optional.ofNullable(allocation.getSector()).map(Sector::getCode).orElse(UNKNOWN),
                            Optional.ofNullable(allocation.getRow()).map(IdNameDTO::getName).orElse(UNKNOWN),
                            Optional.ofNullable(allocation.getSeat()).map(Seat::getName).orElse(UNKNOWN));
                }

                Thread.sleep(longSleep);

                boolean adjacentSeats = validateAdjacentSeats(orderResponse);
                if (BooleanUtils.isFalse(config.getUseSeatMappings())
                        && !BooleanUtils.isTrue(config.getAllowBreakAdjacentSeats())
                        && !adjacentSeats) {
                    failed.put(salesDTO.get(0).getGroup(), ApiExternalErrorCode.NO_ADJACENTS_SEATS_FOUND.getErrorCode());
                    setErrorInSaleGroup(salesDTO, ApiExternalErrorCode.NO_ADJACENTS_SEATS_FOUND.getErrorCode(), ApiExternalErrorCode.NO_ADJACENTS_SEATS_FOUND.getMessage());
                    releaseSeats(token, resultSalesDTO, salesDTO, orderResponse, ApiExternalErrorCode.NO_ADJACENTS_SEATS_FOUND.getMessage());
                    if (BooleanUtils.isTrue(config.getAllowSkipNonAdjacentSeats())) {
                        LOGGER.error("[PROCESS SALES][{}][{}] - Continue group {} with error {}. Continue with next...", session.getName(), groupId, groupId, ApiExternalErrorCode.NO_ADJACENTS_SEATS_FOUND.getMessage());
                        noAdjacentSeatFoundGroups.add(String.valueOf(salesDTO.get(0).getGroup()));
                        continue;
                    }
                    LOGGER.error("[PROCESS SALES][{}][{}] - Stop the script. Add best seats with no adjacent seats for email {}", groupId, session.getName(), owner.getEmail());
                    saveProcess(groups, failed, resultSalesDTO, false);
                    break;
                } else if (!adjacentSeats) {
                    LOGGER.warn("[PROCESS SALES][{}][{}] - Add best seats with no adjacent seats for email {}", groupId, session.getName(), owner.getEmail());
                }
                Map<OrderItem, SaleDTO> attendantsMap = mergeToMap(orderResponse.getItems(), salesDTO);

                if (!BooleanUtils.isTrue(config.getSkipAddAttendant())) {
                    orderResponse = distributionRepository.addItemAttendees(token, orderResponse.getId(), buildAttendants(attendantsMap, config));
                }
                DeliveryMethodsRequestDTO deliveryMethod = new DeliveryMethodsRequestDTO();
                deliveryMethod.setType(OrderDeliveryMethod.EMAIL);
                orderResponse = distributionRepository.setDeliveryMethods(token, orderResponse.getId(), deliveryMethod);

                orderResponse = distributionRepository.addBuyerData(token, orderResponse.getId(), fillClientData(owner));

                orderResponse = distributionRepository.preConfirm(token, orderResponse.getId(), null);
                Thread.sleep(longSleep);

                ConfirmRequest confirmRequest = new ConfirmRequest(null, null, config.getForceMultiTicket());

                orderResponse = distributionRepository.confirm(token, orderResponse.getId(), confirmRequest);

                LOGGER.info("[PROCESS SALES][{}][{}] - Finish the process for group {}, {}", session.getName(), groupId, owner.getGroup(), orderResponse.getCode());

                Thread.sleep(longSleep);

                setProcessedInfo(salesDTO, orderResponse.getCode(), channel.getName());

                if (BooleanUtils.isTrue(config.getUseLocators())) {
                    LOGGER.info("[PROCESS SALES][{}][{}] - Locators match: {}, {}", session.getName(), groupId, salesDTO.get(0).getOriginalLocator(), orderResponse.getId());
                }
                resultSalesDTO.addAll(salesDTO);
            } catch (OneboxRestException e) {
                LOGGER.error("[PROCESS SALES][{}][{}] - Stop the script. Some error ocurred with {} email.", session.getName(), groupId, owner.getEmail(), e);
                failed.put(salesDTO.get(0).getGroup(), e.getMessage());
                setErrorInSaleGroup(salesDTO, e.getErrorCode(), e.getMessage(), orderResponse);
                releaseSeats(token, resultSalesDTO, salesDTO, orderResponse, e.getLocalizedMessage());

                sendProgress(session.getEntityId(), session.getEventId(), sessionId, 100, StatusMessage.ERROR);

            } catch (Exception e) {
                LOGGER.error("[PROCESS SALES][{}][{}] - Stop the script. Some error ocurred with {} email.", session.getName(), groupId, owner.getEmail(), e);
                failed.put(salesDTO.get(0).getGroup(), e.getMessage());
                setErrorInSaleGroup(salesDTO, Optional.ofNullable(e.getCause()).map(Throwable::getMessage).orElse(GENERIC_ERROR), e.getLocalizedMessage(), orderResponse);
                releaseSeats(token, resultSalesDTO, salesDTO, orderResponse, e.getLocalizedMessage());

                sendProgress(session.getEntityId(), session.getEventId(), sessionId, 100, StatusMessage.ERROR);
            }

            progress = calculateProgress(sortedGroups.size(), processedGroups++);
            if (progress != 100) {
                sendProgress(session.getEntityId(), session.getEventId(), sessionId, progress, StatusMessage.IN_PROGRESS);
            }
        }
        deleteOldUnprocessedFile(path);
        resultSalesDTO.addAll(salesProcessed);
        List<SaleDTO> resultSalesDTOSorted = resultSalesDTO.stream().sorted(Comparator.comparing(SaleDTO::getGroup)).collect(Collectors.toList());
        String finalFilename = getFileName(config.getFilename(), session, failed);
        uploadFileS3(preparePath(finalFilename, session), resultSalesDTOSorted);

        ZonedDateTime end = ZonedDateTime.now();
        saveCouchFileData(config.getFilename(), finalFilename, sessionId, channel.getName(), resultSalesDTOSorted, start, end, noAdjacentSeatFoundGroups);

        AutomaticSalesExportRequest request = new AutomaticSalesExportRequest();
        request.setFilename(finalFilename + EXTENSION_CSV);
        Delivery delivery = new Delivery();
        Map<String, Object> map = new HashMap<>();
        map.put(DELIVERY_EMAIL_ADDRESS, config.getReceiptEmail());
        delivery.setProperties(map);
        delivery.setType(ReportDeliveryType.EMAIL);
        request.setDelivery(delivery);

        Duration duration = Duration.between(start, end);
        long assignedTickets = resultSalesDTOSorted.stream().filter(SaleDTO::isProcessed).count();
        int successGroups = resultSalesDTOSorted.stream().filter(SaleDTO::isProcessed).collect(Collectors.groupingBy(SaleDTO::getGroup)).size();
        LOGGER.info("[PROCESS SALES][{}][{}] - Finish process sales. Assigned tickets: {}. Groups processed: {}, Success groups: {}, Failed groups: {}. Time: {}:{}:{}",
                session.getName(), channel.getName(), assignedTickets, groups.size(), successGroups,
                groups.size() - successGroups, duration.toHours(), duration.toMinutes() % 60, duration.toSeconds() % 60);

        ExportResponse response = exportFile(sessionId, request);

        sendProgress(session.getEntityId(), session.getEventId(), sessionId, 100, StatusMessage.DONE);

        return response;
    }

    private Map<SectorPriceZoneKey, List<SaleDTO>> getSalesBySectorAndPriceZone(List<SaleDTO> sales) {
        return sales.stream()
                .collect(Collectors.groupingBy(sale -> new SectorPriceZoneKey(sale.getSector(), sale.getPriceZone())));
    }

    private Long getPriceZoneId(List<BasePriceType> priceTypes, String priceZoneName) {
        return priceTypes
                .stream()
                .filter(priceType -> priceType.getName().equals(priceZoneName))
                .map(BasePriceType::getId).findFirst().orElse(null);
    }

    private Long getSectorId(List<SectorDTO> sectorList, String sectorName) {
        return sectorList
                .stream()
                .filter(sector -> sector.getName().equals(sectorName))
                .map(SectorDTO::getId).findFirst().orElse(null);
    }

    private int getLongSleep(EventDTO event) {
        if (EventType.AVET.equals(event.getType())) {
            return 100;
        }
        return 15;
    }

    private void saveProcess(Map<Long, List<SaleDTO>> groups, Map<Long, String> failed, List<SaleDTO> resultSalesDTO, boolean setError) {
        List<Long> groupsRemain = getGroupsRemain(groups, failed);
        LOGGER.info("[PROCESS SALES] saveProcess, groupsRemain: {}", LogUtil.collectionToString(groupsRemain));
        for (Long group : groupsRemain) {
            List<SaleDTO> sales = groups.get(group);
            if (setError) {
                failed.put(sales.get(0).getGroup(), ApiExternalErrorCode.ERROR_STOPPED_EXECUTION_AUTOMATIC_SALES.getMessage());
                setErrorInSaleGroup(sales, ApiExternalErrorCode.ERROR_STOPPED_EXECUTION_AUTOMATIC_SALES.name(), ApiExternalErrorCode.ERROR_STOPPED_EXECUTION_AUTOMATIC_SALES.getMessage(), null);
            }
            resultSalesDTO.addAll(sales);
        }
    }

    @NotNull
    private static List<Long> getGroupsRemain(Map<Long, List<SaleDTO>> groups, Map<Long, String> failed) {
        List<Long> groupsRemain = groups.entrySet().stream()
                .filter(entry -> entry.getValue() != null &&
                        !entry.getValue().isEmpty() &&
                        entry.getValue().stream().allMatch(sale -> sale != null && !sale.isProcessed()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        groupsRemain.removeAll(failed.keySet());
        return groupsRemain;
    }

    private String getPreviewToken(Long sessionId) {

        SessionSearchFilter sessionSearchFilter = new SessionSearchFilter();
        sessionSearchFilter.setId(List.of(sessionId));
        SessionsDTO sessions = eventRepository.getSessions(sessionSearchFilter);

        if (sessions.getData().size() != 1) {
            throw new OneboxRestException(ApiExternalErrorCode.SESSION_NOT_FOUND);
        }

        SessionDTO session = sessions.getData().get(0);

        if (session.getStatus() == SessionStatus.PREVIEW) {
            return HashUtils.encodeHashIds(session.getId());
        }
        return null;
    }

    private boolean hasDifferentSectors(List<SaleDTO> salesDTO) {
        return !salesDTO.isEmpty() && salesDTO.stream().map(SaleDTO::getSector).distinct().count() > 1;
    }

    private void addSeatPreviewToken(AddSeatsDTO addSeats, String previewToken) {

        if (previewToken != null && !previewToken.isEmpty()) {
            addSeats.getSeats().forEach(seat -> seat.setSessionPreviewToken(previewToken));
        }
    }

    private boolean isProcessBlocked(Long sessionId) {
        AutomaticSalesExecutionStatus semaphore = getSemaphore(sessionId);
        return semaphore != null && semaphore.equals(AutomaticSalesExecutionStatus.BLOCKED);
    }

    private void saveCouchFileData(String filename, String completeFilename, Long sessionId, String channelName,
                                   List<SaleDTO> resultSalesDTO, ZonedDateTime start, ZonedDateTime end,
                                   Set<String> noAdjacentSeatFoundGroups) {
        AutomaticSaleFileData fileData = automaticSalesCouchDao.getFileData(String.valueOf(sessionId), filename);
        Integer processed = Math.toIntExact(resultSalesDTO.stream().filter(SaleDTO::isProcessed).count());
        Integer total = resultSalesDTO.size();
        FileInfo fileInfo = prepareFileInfo(filename, channelName, start, end, processed, total, noAdjacentSeatFoundGroups);

        if (fileData == null) {
            fileData = new AutomaticSaleFileData();
            fileData.setDocumentId(sessionId + "_" + filename);
            fileData.setSessionId(sessionId);
            fileData.setFilename(filename);
        }
        fileData.setFileInfo(prepareInfoList(fileInfo, fileData.getFileInfo()));

        automaticSalesCouchDao.bulkUpsert(Collections.singletonList(fileData));
    }

    private String getFileName(String filename, SessionDTO session, Map<Long, String> failed) {
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date());
        if (!failed.isEmpty()) {
            filename = filename + FILE_NAME_PROCESSED_WITH_ERRORS;
        }
        String finalFilename = filename + "_" + timeStamp;
        return finalFilename;
    }

    public void bulk(Long sessionId, SaleRequestListDTO request, ProcessSalesConfigurationRequest processSalesConfigurationRequest) {
        SessionDTO session = eventRepository.getSession(sessionId);

        String path = preparePath(processSalesConfigurationRequest.getFilename(), session);

        deleteOldUnprocessedFile(path);

        List<SaleDTO> sales = request.stream().map(ProcessSalesService::getSaleDTO).collect(Collectors.toList());

        uploadFileS3(path, sales);
    }

    public void deleteOldUnprocessedFile(String path) {
        boolean alreadyExistsFile = s3AutomaticSalesRepository.existObject(path);
        if (alreadyExistsFile) {
            s3AutomaticSalesRepository.delete(path);
        }
    }

    public void uploadFileS3(String path, List<SaleDTO> sales) {
        String strCsvData = CsvParseUtils.toCsv(sales, SaleDTO.class);
        InputStream targetStream = new ByteArrayInputStream(strCsvData.getBytes());

        ObjectPolicy objectPolicy = ObjectPolicy.builder().contentType("text/csv").build();

        s3AutomaticSalesRepository.upload(path, targetStream, false, objectPolicy);
    }

    public List<FileInfoDTO> getAutomaticSalesFileList(Long sessionId) {
        AutomaticSalesSearchFilter searchFilter = new AutomaticSalesSearchFilter();
        searchFilter.setLimit(AutomaticSalesReportProvider.PAGE_SIZE);

        List<AutomaticSaleFileData> fileData = automaticSalesCouchDao.getBySessionId(String.valueOf(sessionId), 0, Math.toIntExact(AutomaticSalesReportProvider.PAGE_SIZE), null);

        return fileData.stream().map(this::toDTO).collect(Collectors.toList());
    }

    private FileInfoDTO toDTO(AutomaticSaleFileData fileInfo) {
        FileInfo fileInfoLast = fileInfo.getFileInfo().get(fileInfo.getFileInfo().size() - 1);

        FileInfoDTO file = new FileInfoDTO();

        file.setTotal(fileInfoLast.getTotal());
        file.setProcessed(fileInfoLast.getProcessed());
        file.setEndDate(fileInfoLast.getEndDate());
        file.setChannelName(fileInfoLast.getChannelName());
        file.setStartDate(fileInfoLast.getStartDate());
        file.setFilename(fileInfo.getFilename());
        if (CollectionUtils.isNotEmpty(fileInfo.getFileInfo()) && fileInfo.getFileInfo().get(fileInfo.getFileInfo().size() - 1).getNoAdjacentGroups() != null) {
            List<String> nonAdjacentGroups = fileInfo.getFileInfo().get(fileInfo.getFileInfo().size() - 1).getNoAdjacentGroups();
            file.setNonAdjacentGroups(nonAdjacentGroups);
        }
        return file;
    }

    public String preparePath(String filename, SessionDTO session) {
        return preparePath(filename, session, true);
    }

    public String preparePath(String filename, SessionDTO session, boolean hasExtension) {
        if (hasExtension) {
            return ENTITIES_PATH + session.getEntityId() + FOLDER_SEPARATOR + EVENTS_PATH + session.getEventId() + FOLDER_SEPARATOR + SESSIONS_PATH + session.getId() + FOLDER_SEPARATOR + filename + EXTENSION_CSV;
        } else {
            return ENTITIES_PATH + session.getEntityId() + FOLDER_SEPARATOR + EVENTS_PATH + session.getEventId() + FOLDER_SEPARATOR + SESSIONS_PATH + session.getId() + FOLDER_SEPARATOR + filename;
        }
    }

    public static SaleDTO getSaleDTO(SaleRequestDTO saleRequest) {
        SaleDTO sale = new SaleDTO();
        sale.setGroup(saleRequest.getGroup());
        sale.setNum(saleRequest.getNum());
        sale.setName(saleRequest.getName());
        sale.setFirstSurname(saleRequest.getFirstSurname());
        sale.setSecondSurname(saleRequest.getSecondSurname());
        sale.setDni(saleRequest.getDni());
        sale.setPhone(saleRequest.getPhone());
        sale.setEmail(saleRequest.getEmail() != null ? saleRequest.getEmail().trim() : null);
        sale.setSector(saleRequest.getSector() == null ? " " : saleRequest.getSector());
        sale.setPriceZone(saleRequest.getPriceZone() == null ? " " : saleRequest.getPriceZone());
        sale.setOwner(saleRequest.isOwner());
        sale.setSeatId(saleRequest.getSeatId());
        sale.setOriginalLocator(saleRequest.getOriginalLocator() == null ? " " : saleRequest.getOriginalLocator());
        sale.setLanguage(saleRequest.getLanguage() == null ? " " : saleRequest.getLanguage());
        sale.setProcessed(saleRequest.isProcessed());
        sale.setErrorCode(sale.getErrorCode() == null ? " " : sale.getErrorCode());
        sale.setErrorDescription(sale.getErrorDescription() == null ? " " : sale.getErrorDescription());
        sale.setOrderId(saleRequest.getOrderId() == null ? " " : saleRequest.getOrderId());
        sale.setTraceId(" ");
        sale.setExtraField(saleRequest.getExtraField() == null ? " " : saleRequest.getExtraField());
        return sale;
    }

    public void validateFile(List<SaleDTO> sales, Long sessionId, Long channelId) {
        SaleDTO hasNullGroup = sales.stream().filter(sale -> Objects.isNull(sale.getGroup())).findFirst().orElse(null);
        SaleDTO hasNullNum = sales.stream().filter(sale -> Objects.isNull(sale.getNum())).findFirst().orElse(null);

        if (!Objects.isNull(hasNullGroup) || !Objects.isNull(hasNullNum)) {
            throw new OneboxRestException(ApiExternalErrorCode.INPUT_NULL_VALUES);
        }

        SessionDTO session = eventRepository.getSession(sessionId);

        validationService.validateEventFields(sales, session.getEventId(), channelId);
        validationService.validateChannelFields(sales, channelId);
    }

    public ExportResponse exportFile(Long sessionId, AutomaticSalesExportRequest request) {
        SessionDTO session = eventRepository.getSession(sessionId);

        Operator operator = entitiesRepository
                .getCachedOperator(session.getEntityId());

        String path = preparePath(request.getFilename(), session, false);

        AutomaticSalesReportFilter filter =
                getExportFilter(request, operator, sessionId, path);

        ExportProcess exportProcess = reportQueueProducer.push(sessionId, filter, ApiExternalExportType.AUTOMATIC_SALES);

        return new ExportResponse(exportProcess.getExportId());
    }

    public static AutomaticSalesReportFilter getExportFilter(AutomaticSalesExportRequest request, Operator operator, Long sessionId, String filename) {
        AutomaticSalesReportFilter filter = new AutomaticSalesReportFilter();
        filter.setEmail(ExportConverter.extractEmail(request.getDelivery()));
        filter.setType(ApiExternalExportType.AUTOMATIC_SALES);
        filter.setFormat(FileFormat.CSV);
        filter.setCsvSeparatorFormat(CsvSeparatorFormat.COMMA);
        filter.setTimeZone(operator.getTimezone().getValue());
        filter.setSessionId(sessionId);
        filter.setQ(filename);
        filter.setFields(getAllFilterFields());
        filter.setLanguage(operator.getLanguage().getCode());
        return filter;
    }

    private static List<AutomaticSalesFileField> getAllFilterFields() {
        List<AutomaticSalesFileField> automaticSaleFields = new ArrayList<>();
        automaticSaleFields.add(getAutomaticSalesFileField(AutomaticSalesFields.GROUP));
        automaticSaleFields.add(getAutomaticSalesFileField(AutomaticSalesFields.NUM));
        automaticSaleFields.add(getAutomaticSalesFileField(AutomaticSalesFields.NAME));
        automaticSaleFields.add(getAutomaticSalesFileField(AutomaticSalesFields.FIRST_SURNAME));
        automaticSaleFields.add(getAutomaticSalesFileField(AutomaticSalesFields.SECOND_SURNAME));
        automaticSaleFields.add(getAutomaticSalesFileField(AutomaticSalesFields.DNI));
        automaticSaleFields.add(getAutomaticSalesFileField(AutomaticSalesFields.PHONE));
        automaticSaleFields.add(getAutomaticSalesFileField(AutomaticSalesFields.EMAIL));
        automaticSaleFields.add(getAutomaticSalesFileField(AutomaticSalesFields.SECTOR));
        automaticSaleFields.add(getAutomaticSalesFileField(AutomaticSalesFields.PRICE_ZONE));
        automaticSaleFields.add(getAutomaticSalesFileField(AutomaticSalesFields.OWNER));
        automaticSaleFields.add(getAutomaticSalesFileField(AutomaticSalesFields.SEAT_ID));
        automaticSaleFields.add(getAutomaticSalesFileField(AutomaticSalesFields.ORIGINAL_LOCATOR));
        automaticSaleFields.add(getAutomaticSalesFileField(AutomaticSalesFields.LANGUAGE));
        automaticSaleFields.add(getAutomaticSalesFileField(AutomaticSalesFields.PROCESSED));
        automaticSaleFields.add(getAutomaticSalesFileField(AutomaticSalesFields.ERROR_CODE));
        automaticSaleFields.add(getAutomaticSalesFileField(AutomaticSalesFields.ERROR_DESCRIPTION));
        automaticSaleFields.add(getAutomaticSalesFileField(AutomaticSalesFields.ORDER_ID));
        automaticSaleFields.add(getAutomaticSalesFileField(AutomaticSalesFields.TRACE_ID));
        automaticSaleFields.add(getAutomaticSalesFileField(AutomaticSalesFields.EXTRA_FIELD));
        return automaticSaleFields;
    }

    @NotNull
    private static List<FileInfo> prepareInfoList(FileInfo fileInfo, List<FileInfo> list) {
        if (CollectionUtils.isEmpty(list)) {
            list = new ArrayList<>();
        }
        list.add(fileInfo);
        return list;
    }

    private static FileInfo prepareFileInfo(String filename, String channelName, ZonedDateTime start, ZonedDateTime end, Integer processed, Integer total, Set<String> noAdjacentSeatFoundGroups) {
        FileInfo fileInfo = new FileInfo();
        fileInfo.setFilename(filename);
        fileInfo.setChannelName(channelName);
        fileInfo.setProcessed(processed);
        fileInfo.setTotal(total);
        fileInfo.setEndDate(end);
        fileInfo.setStartDate(start);
        fileInfo.setNoAdjacentGroups(noAdjacentSeatFoundGroups.stream().toList());
        return fileInfo;
    }

    public static AutomaticSalesFileField getAutomaticSalesFileField(AutomaticSalesFields field) {
        AutomaticSalesFileField fileField = new AutomaticSalesFileField();
        fileField.setField(field);
        return fileField;
    }

    private void checkIfFileExists(String path) {
        boolean alreadyExistsFile = s3AutomaticSalesRepository.existObject(path);
        if (!alreadyExistsFile) {
            LOGGER.info("[PROCESS SALES] - File not found");
            throw new OneboxRestException(ApiExternalErrorCode.AUTOMATIC_SALES_FILE_NOT_FOUND);
        }
    }

    private List<Long> getSortedSalesList(Map<Long, List<SaleDTO>> groups) {
        return groups.entrySet().stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue(Comparator.comparingInt(List::size))))
                .map(Map.Entry::getKey).collect(Collectors.toList());
    }

    private AvetSeatMappings fillSeatMappings(Long sessionId) {
        AvetSeatMappings avetSeatMappings = new AvetSeatMappings();
        SessionDTO session = eventRepository.getSession(sessionId);
        VenueTemplate venueTemplate = venuesRepository.getVenueTemplate(session.getVenueConfigId());
        avetSeatMappings.setEntityId(venueTemplate.getVenue().getEntity().getId());
        Integer capacityId = (Integer) venueTemplate.getExternalData().get("capacity_id");
        avetSeatMappings.setCapacityId(capacityId);
        Integer matchId = (Integer) session.getExternalData().get("match_id");
        avetSeatMappings.setMatchId(matchId);
        LOGGER.info("[PROCESS SALES] - SeatMappings filled with: {}", avetSeatMappings);
        return avetSeatMappings;
    }

    private SeatDTO createSeat(Long seatId, Long sessionId) {
        SeatDTO seat = new SeatDTO();
        seat.setId(seatId);
        seat.setSessionId(sessionId);
        return seat;
    }

    public boolean validateAdjacentSeats(OrderResponse orderResponse) {

        if (!OrderUtils.filterOrderWarnings(orderResponse.getItems()).contains(ItemWarning.SESSION_NON_CONSECUTIVE_SEAT)) {
            return true;
        }

        PreOrderDTO preOrder = msOrderRepository.getPreOrderInfo(orderResponse.getId());

        Map<SectorPriceZoneKey, List<OrderProductDTO>> sectorPriceZones = preOrder.getProducts().stream()
                .collect(Collectors.groupingBy(product ->
                        new SectorPriceZoneKey(product.getTicketData().getSectorName(), product.getTicketData().getPriceZoneName())));

        for (Map.Entry<SectorPriceZoneKey, List<OrderProductDTO>> entry : sectorPriceZones.entrySet()) {
            if (!ValidationUtils.validateSeatsPositions(entry.getValue())) {
                return false;
            }
        }

        return true;
    }

    public static <K, V> Map<K, V> mergeToMap(List<K> keys, List<V> values) {
        if (keys.size() != values.size()) {
            throw new IllegalArgumentException("[PROCESS SALES] - Cannot combine lists with dissimilar sizes");
        }

        return IntStream.range(0, keys.size()).boxed()
                .collect(Collectors.toMap(keys::get, values::get));
    }

    private Map<String, Object> fillClientData(SaleDTO saleDTO) {
        ClientDataParamMap paramsMap = new ClientDataParamMap();
        paramsMap.put(ClientDataParamMap.FIRST_NAME, saleDTO.getName());
        paramsMap.put(ClientDataParamMap.LAST_NAME, getLastName(saleDTO.getFirstSurname(), saleDTO.getSecondSurname()));
        paramsMap.put(ClientDataParamMap.EMAIL, saleDTO.getEmail());
        //paramsMap.put(ClientDataParamMap.TICKET_EMAIL, saleDTO.getEmail());
        if (StringUtils.isNotBlank(saleDTO.getPhone())) {
            paramsMap.put(ClientDataParamMap.PHONE, saleDTO.getPhone());
        }

        //paramsMap.put(ClientDataParamMap.IDENTIFICATION, saleDTO.getDni());

        return paramsMap.getParamMap();
    }

    private Map<String, Object> fillClientData(CustomerData customerData) {
        ClientDataParamMap paramsMap = new ClientDataParamMap();
        paramsMap.put(ClientDataParamMap.FIRST_NAME, customerData.getName());
        paramsMap.put(ClientDataParamMap.LAST_NAME, getLastName(customerData.getFirstSurname(), customerData.getSecondSurname()));
        paramsMap.put(ClientDataParamMap.EMAIL, customerData.getEmail());
        paramsMap.put(ClientDataParamMap.ALLOW_COMMERCIAL_MAILING, BooleanUtils.isTrue(customerData.getAllowCommercialMailing()));
        if (StringUtils.isNotBlank(customerData.getDni())) {
            paramsMap.put(ClientDataParamMap.IDENTIFICATION, customerData.getDni());
        }
        if (StringUtils.isNotBlank(customerData.getPhone())) {
            paramsMap.put(ClientDataParamMap.PHONE, customerData.getPhone());
        }
        if (StringUtils.isNotBlank(customerData.getCity())) {
            paramsMap.put(ClientDataParamMap.CITY, customerData.getCity());
        }
        if (StringUtils.isNotBlank(customerData.getCountry())) {
            paramsMap.put(ClientDataParamMap.COUNTRY, customerData.getCountry());
        }
        if (StringUtils.isNotBlank(customerData.getUserId())) {
            paramsMap.put(ClientDataParamMap.USER_ID, customerData.getUserId());
        }
        if (StringUtils.isNotBlank(customerData.getExternalClientId())) {
            paramsMap.put(ClientDataParamMap.EXTERNAL_CLIENT_ID, customerData.getExternalClientId());
        }
        if (customerData.getGender() != null) {
            paramsMap.put(ClientDataParamMap.GENDER, customerData.getGender().name());
        }
        if (customerData.getInternationalPhone() != null) {
            paramsMap.put(ClientDataParamMap.PHONE, customerData.getInternationalPhone().getNumber());
            paramsMap.put(ClientDataParamMap.PHONE_PREFIX, customerData.getInternationalPhone().getPrefix());
        }
        if (MapUtils.isNotEmpty(customerData.getAdditionalInfo())) {
            for (Map.Entry<String, Object> entry : customerData.getAdditionalInfo().entrySet()) {
                paramsMap.put(CommonUtils.camelCaseToSnakeCase(entry.getKey()), entry.getValue());
            }
        }
        return paramsMap.getParamMap();
    }

    private static String getLastName(String firstSurname, String secondSurname) {
        String lastName = firstSurname;
        if (StringUtils.isNotBlank(secondSurname)) {
            lastName = lastName + " " + secondSurname;
        }
        return lastName;
    }

    private static void setErrorInSaleGroup(List<SaleDTO> salesDTO, String errorCode, String errorDescription, OrderResponse order) {
        salesDTO.forEach(sale -> {
            sale.setProcessed(false);
            sale.setErrorCode(errorCode);
            sale.setErrorDescription(errorDescription);
            if (order != null) {
                sale.setTraceId(order.getTraceId());
            }
        });
    }

    private static void setErrorInSaleGroup(List<SaleDTO> salesDTO, String errorCode, String errorDescription) {
        salesDTO.forEach(sale -> {
            sale.setProcessed(false);
            sale.setErrorCode(errorCode);
            sale.setErrorDescription(errorDescription);
        });
    }

    private static void setProcessedInfo(List<SaleDTO> salesDTO, String orderId, String channelName) {
        salesDTO.forEach(sale -> {
            sale.setProcessed(true);
            sale.setOrderId(orderId);
            sale.setErrorDescription(null);
            sale.setErrorCode(null);
        });
    }

    private ItemAttendees buildAttendants(Map<OrderItem, SaleDTO> attendants, ProcessSalesConfigurationRequest config) {
        ItemAttendees attendantList = new ItemAttendees();
        attendantList.addAll(buildAttendantSeat(attendants, config));
        return attendantList;
    }

    private List<ItemAttendee> buildAttendantSeat(Map<OrderItem, SaleDTO> attendants, ProcessSalesConfigurationRequest config) {
        List<ItemAttendee> attendantSeats = new ArrayList<>();

        for (Map.Entry<OrderItem, SaleDTO> entry : attendants.entrySet()) {
            ItemAttendee attendantSeat = new ItemAttendee();
            HashMap<String, Object> map = new HashMap<>();
            map.put(AttendantFields.ATTENDANT_NAME.name(), entry.getValue().getName());
            map.put(AttendantFields.ATTENDANT_SURNAME.name(), entry.getValue().getFirstSurname());
            map.put(AttendantFields.ATTENDANT_ID_NUMBER.name(), entry.getValue().getDni());
            if (config.getExtraFieldValue() != null) {
                map.put(config.getExtraFieldValue().name(), entry.getValue().getExtraField());
            }
            attendantSeat.setField(map);
            attendantSeat.setItem_id(entry.getKey().getId());
            attendantSeats.add(attendantSeat);
        }
        return attendantSeats;
    }

    private void releaseSeats(String token, List<SaleDTO> resultSalesDTO, List<SaleDTO> salesDTO, OrderResponse orderResponse, String message) {
        try {
            distributionRepository.releaseSeats(token, orderResponse.getId(), null);
        } catch (Exception ex) {
            LOGGER.error("[PROCESS SALES] - Error trying to release seats. {}", message);
        }
        resultSalesDTO.addAll(salesDTO);
    }

    private Long getOBSeatId(Long externalSeatId, Long sessionId, Long entityId, ProcessSalesConfigurationRequest config) {
        AvetSeatMappings avetSeatMappings = null;

        if (BooleanUtils.isTrue(config.getUseSeatMappings()) && BooleanUtils.isFalse(config.getUseOBIdsForSeatMappings())) {
            avetSeatMappings = fillSeatMappings(sessionId);
        }

        ClubConfig clubConfig = intAvetConfigRepository.getClubByEntityId(entityId);

        Boolean isWS = clubConfig.getWsConnectionVersion() != null;
        Boolean isSocket = clubConfig.getConnectionBySocket();

        if (isWS.equals(null) && isSocket.equals(null)) {
            LOGGER.error("[PROCESS SALES] - Bad AVET configuration");
            throw new OneboxRestException(ApiExternalErrorCode.ERROR_RETRIEVING_AVET_CONFIG);
        }

        if (BooleanUtils.isTrue(isWS)) {
            MappingResponse mapping = intMappingsRepository.getOBSeatId(
                    avetSeatMappings.getEntityId(),
                    avetSeatMappings.getCapacityId(),
                    avetSeatMappings.getMatchId(),
                    externalSeatId);
            return mapping.getMappedId() != null ? Long.valueOf(mapping.getMappedId()) : null;
        }
        return null;
    }

    private AutomaticSalesMessage buildProgressMessage(Long entityId, Long sessionId, Long eventId) {
        AutomaticSalesMessage progressMessage = new AutomaticSalesMessage("NOTIFICATION");
        progressMessage.setId(sessionId);
        progressMessage.setEventId(eventId);
        progressMessage.setEntityId(entityId);
        progressMessage.setSessionId(sessionId);
        progressMessage.setType(EventMessageType.AUTOMATIC_SALES);
        return progressMessage;
    }

    private void sendProgress(Long entityId, Long eventId, Long sessionId, Integer progress, StatusMessage status) {
        try {
            ProgressMessage progressMessage = buildProgressMessage(entityId, sessionId, eventId);
            progressService.sendNotificationProgress(progressMessage, progress, status, ConsumerType.EVENT);
            LOGGER.info("[AUTOMATIC-SALES] Async processing for automatic sales process with id:{} is at {}% progress.", sessionId, progress);
        } catch (Exception e) {
            LOGGER.error("[AUTOMATIC-SALES] id: " + sessionId + " - Error sending notification", e);
        }
    }

    private Integer calculateProgress(int size, int i) {
        return (int) ((i * 100f) / size);
    }

    private void setSemaphore(Long sessionId, AutomaticSalesExecutionStatus value) {
        hazelcastMapService.putIntoMapWithTTL(HazelcastConfiguration.API_EXTERNAL_AUTOMATIC_SALES_MAP, sessionId.toString(), value, 240, TimeUnit.MINUTES);
    }

    public AutomaticSalesExecutionStatus getSemaphore(Long sessionId) {
        return hazelcastMapService.getObjectFromMap(HazelcastConfiguration.API_EXTERNAL_AUTOMATIC_SALES_MAP, sessionId.toString());
    }

    public List<Long> getActiveSessionsProcessSales() {
        IMap<String, AutomaticSalesExecutionStatus> processSales = hazelcastMapService.getMap(HazelcastConfiguration.API_EXTERNAL_AUTOMATIC_SALES_MAP);
        return processSales.entrySet().stream()
                .filter(entry -> entry.getValue().equals(AutomaticSalesExecutionStatus.IN_PROGRESS))
                .map(entry -> Long.valueOf(entry.getKey())).toList();
    }

    public void modifyProcessSales(Long sessionId, UpdateProcessSalesRequest request) {
        AutomaticSalesExecutionStatus semaphore = getSemaphore(sessionId);
        if (semaphore != null && semaphore.equals(AutomaticSalesExecutionStatus.IN_PROGRESS)) {
            setSemaphore(sessionId, request.getAutomaticSalesExecutionStatus());
        } else {
            throw new OneboxRestException(ApiExternalErrorCode.ERROR_PROCESS_NOT_FOUND);
        }
    }
}
