package es.onebox.event.events.service;

import es.onebox.core.exception.CoreErrorCode;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.response.MetadataBuilder;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.core.utils.common.DateUtils;
import es.onebox.event.attendants.AttendantsConfigService;
import es.onebox.event.catalog.dao.CatalogChannelEventCouchDao;
import es.onebox.event.catalog.elasticsearch.dao.ChannelEventElasticDao;
import es.onebox.event.catalog.elasticsearch.dao.ChannelSessionElasticDao;
import es.onebox.event.datasources.ms.channel.dto.ChannelConfigDTO;
import es.onebox.event.datasources.ms.channel.repository.ChannelsRepository;
import es.onebox.event.datasources.ms.order.repository.OrdersRepository;
import es.onebox.event.events.amqp.emailnotification.EmailNotificationMessage;
import es.onebox.event.events.amqp.emailnotification.EmailNotificationService;
import es.onebox.event.events.amqp.eventnotification.ExternalEventConsumeNotificationMessage;
import es.onebox.event.events.amqp.requestchannelnotification.RequestChannelNotificationMessage;
import es.onebox.event.events.converter.EventChannelRecordConverter;
import es.onebox.event.events.converter.SaleGroupsConverter;
import es.onebox.event.events.dao.ChannelCurrenciesDao;
import es.onebox.event.events.dao.ChannelDao;
import es.onebox.event.events.dao.ChannelEventSurchargeRangeDao;
import es.onebox.event.events.dao.EntityDao;
import es.onebox.event.events.dao.EventDao;
import es.onebox.event.events.dao.SaleGroupDao;
import es.onebox.event.events.dao.SalesGroupAssignmentDao;
import es.onebox.event.events.dto.BaseEventChannelDTO;
import es.onebox.event.events.dto.EventChannelDTO;
import es.onebox.event.events.dto.EventChannelsDTO;
import es.onebox.event.events.dto.UpdateEventChannelDTO;
import es.onebox.event.events.dto.UpdateEventChannelSettingsDTO;
import es.onebox.event.events.enums.ChannelEventStatus;
import es.onebox.event.events.enums.ChannelSubtype;
import es.onebox.event.events.enums.EventChannelStatus;
import es.onebox.event.events.enums.EventType;
import es.onebox.event.events.postbookingquestions.service.PostBookingQuestionsService;
import es.onebox.event.events.utils.EventStatusUtil;
import es.onebox.event.events.utils.EventUtils;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.priceengine.request.EventChannelSearchFilter;
import es.onebox.event.priceengine.simulation.dao.ChannelEventDao;
import es.onebox.event.priceengine.simulation.record.EventChannelRecord;
import es.onebox.event.seasontickets.dao.SessionElasticDao;
import es.onebox.event.seasontickets.service.SeasonTicketService;
import es.onebox.event.secondarymarket.dao.EventSecondaryMarketConfigCouchDao;
import es.onebox.event.secondarymarket.domain.EnabledChannel;
import es.onebox.event.secondarymarket.domain.EventSecondaryMarketConfig;
import es.onebox.event.secondarymarket.utils.SecondaryMarketUtils;
import es.onebox.event.sessions.dao.SessionChannelCouchDao;
import es.onebox.event.sessions.dao.SessionConfigCouchDao;
import es.onebox.event.sessions.dao.SessionDao;
import es.onebox.event.sessions.dao.record.SessionRecord;
import es.onebox.event.sessions.domain.sessionconfig.SessionConfig;
import es.onebox.event.sessions.dto.SessionStatus;
import es.onebox.event.sessions.request.SessionSearchFilter;
import es.onebox.jooq.annotation.MySQLRead;
import es.onebox.jooq.annotation.MySQLWrite;
import es.onebox.jooq.cpanel.tables.records.CpanelCanalEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelCanalRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelCuposConfigRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelEntidadRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoRecord;
import es.onebox.message.broker.producer.queue.DefaultProducer;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static es.onebox.core.utils.common.CommonUtils.isNull;
import static es.onebox.event.events.converter.EventConverter.ONE;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Service
public class EventChannelService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventChannelService.class);

    @Autowired
    private EventChannelEraserService eventChannelEraserService;
    @Autowired
    private ChannelEventEraserService channelEventEraserService;
    @Autowired
    private AttendantsConfigService attendantsConfigService;
    @Autowired
    private OrdersRepository ordersRepository;
    @Autowired
    private ChannelEventElasticDao channelEventElasticDao;
    @Autowired
    private ChannelSessionElasticDao channelSessionElasticDao;
    @Autowired
    private ChannelEventDao channelEventDao;
    @Autowired
    private SessionDao sessionDao;
    @Autowired
    private SessionChannelCouchDao sessionChannelCouchDao;
    @Autowired
    private CatalogChannelEventCouchDao catalogChannelEventCouchDao;
    @Autowired
    private SalesGroupAssignmentDao salesGroupAssignmentDao;
    @Autowired
    private EventDao eventDao;
    @Autowired
    private ChannelDao channelDao;
    @Autowired
    private ChannelCurrenciesDao channelCurrencyDao;
    @Autowired
    private EmailNotificationService emailNotificationService;
    @Autowired
    private DefaultProducer externalEventConsumeNotificationProducer;
    @Qualifier("requestChannelNotificationProducer")
    @Autowired
    private DefaultProducer requestChannelNotificationProducer;
    @Autowired
    private SaleGroupDao saleGroupDao;
    @Autowired
    private EventSecondaryMarketConfigCouchDao eventSecondaryMarketConfigCouchDao;
    @Autowired
    private EntityDao entityDao;
    @Autowired
    private ChannelEventSurchargeRangeDao channelEventSurchargeRangeDao;
    @Value("${onebox.repository.S3SecureUrl}")
    private String s3domain;
    @Value("${onebox.repository.fileBasePath}")
    private String fileBasePath;
    @Autowired
    private ChannelsRepository channelsRepository;
    @Autowired
    private SessionConfigCouchDao sessionConfigCouchDao;
    @Autowired
    private SessionElasticDao sessionElasticDao;
    @Autowired
    private SeasonTicketService seasonTicketService;
    @Autowired
    private PostBookingQuestionsService postBookingQuestionsService;

    @MySQLWrite
    public void delete(Long eventId, Long channelId) {

        if (ordersRepository.countByEventAndChannel(eventId, channelId) > 0L) {
            throw OneboxRestException.builder(MsEventErrorCode.EVENT_CHANNEL_HAS_SALES).
                    setMessage("Cannot delete event: " + eventId + " - channel: " + channelId + " - already has sales").build();
        }

        UpdateEventChannelDTO updateData = generateUnpublishedEventChannelDTO();
        updateEventChannel(eventId, channelId, updateData);

        eventChannelEraserService.delete(eventId, channelId);
        channelEventEraserService.delete(channelId, eventId);

        channelEventElasticDao.delete(channelId, eventId);
        channelSessionElasticDao.deleteByEventAndChannel(eventId, channelId);

        deleteEnabledChannelFromEventSecMktConfig(eventId, channelId);

        SessionSearchFilter sessionSearchFilter = new SessionSearchFilter();
        sessionSearchFilter.setEventId(Collections.singletonList(eventId));

        List<SessionRecord> sessions = sessionDao.findSessions(sessionSearchFilter, null);

        for (SessionRecord session : sessions) {
            sessionChannelCouchDao.remove(session.getIdsesion().toString(), channelId.toString());
        }
        catalogChannelEventCouchDao.remove(channelId.toString(), eventId.toString());

        attendantsConfigService.deleteChannelAttendantsConfig(eventId, channelId);

        postBookingQuestionsService.deleteChannelPostBookingQuestionsRelation(channelId.intValue());
    }

    @MySQLWrite
    public void requestChannelApproval(Long eventId, Long channelId, Long userId) {
        if (eventId == null || eventId <= 0) {
            throw new OneboxRestException(CoreErrorCode.BAD_PARAMETER, "event id is mandatory and must be positive",
                    null);
        }
        if (channelId == null || channelId <= 0) {
            throw new OneboxRestException(CoreErrorCode.BAD_PARAMETER, "channel id is mandatory and must be positive",
                    null);
        }
        if (userId == null || userId <= 0) {
            throw new OneboxRestException(CoreErrorCode.BAD_PARAMETER, "userId is mandatory", null);
        }

        CpanelCanalEventoRecord channelEvent = channelEventDao.getChannelEvent(channelId.intValue(), eventId.intValue())
                .orElseThrow(() -> new OneboxRestException(MsEventErrorCode.EVENT_CHANNEL_NOT_FOUND));

        if (!EventChannelStatus.PENDING_REQUEST.getId().equals(channelEvent.getEstadorelacion())) {
            throw new OneboxRestException(MsEventErrorCode.REQUEST_NOT_PENDING);
        }

        boolean hasDynamicPrices = hasActiveDynamicPrices(eventId);
        if (hasDynamicPrices) {
            ChannelConfigDTO channelConfig = channelsRepository.getChannelConfigCached(channelId);
            if (channelConfig != null && !Boolean.TRUE.equals(channelConfig.getV4Enabled())) {
                throw new OneboxRestException(MsEventErrorCode.DYNAMIC_PRICES_REQUIRE_V4_CHANNEL);
            }
        }

        CpanelEventoRecord event = eventDao.getById(eventId.intValue());
        if (entityDao.getById(entityDao.getEntityInfo(event.getIdentidad()).operatorId()).getUsemulticurrency() == 1 &&
                isNotValidCurrency(channelId, event)) {
            throw new OneboxRestException(MsEventErrorCode.EVENT_CHANNEL_CURRENCY_NOT_MATCH);
        }

        channelEvent.setEstadorelacion(EventChannelStatus.PENDING.getId());
        channelEventDao.update(channelEvent);
        emailNotificationService.sendEmailNotification(EmailNotificationMessage.NotificationType.SOLICITUD_EVENTO,
                userId.intValue(), channelEvent.getIdcanaleevento(), null);

        ExternalEventConsumeNotificationMessage externalEventConsumeNotificationMessage =
                new ExternalEventConsumeNotificationMessage();
        externalEventConsumeNotificationMessage.setChannelId(channelId.intValue());
        externalEventConsumeNotificationMessage.setEventId(eventId.intValue());
        try {
            externalEventConsumeNotificationProducer.sendMessage(externalEventConsumeNotificationMessage);
        } catch (Exception e) {
            LOGGER.warn("[AMQP CLIENT] ExternalEventConsumeNotificationService Message could not be send", e);
        }
        RequestChannelNotificationMessage requestChannelNotificationMessage = new RequestChannelNotificationMessage();
        requestChannelNotificationMessage.setChannelId(channelId.intValue());
        requestChannelNotificationMessage.setEventId(eventId.intValue());
        requestChannelNotificationMessage.setUserId(userId.intValue());
        try {
            requestChannelNotificationProducer.sendMessage(requestChannelNotificationMessage);
        } catch (Exception e) {
            LOGGER.warn("[AMQP CLIENT] Request channel notification Message could not be send", e);
        }
    }

    private boolean hasActiveDynamicPrices(Long eventId) {
        List<SessionRecord> sessions = sessionDao.findSessionsByEventId(eventId.intValue());
        return !CollectionUtils.isEmpty(sessions) && sessions.stream()
                .map(session -> sessionConfigCouchDao.get(String.valueOf(session.getIdsesion())))
                .anyMatch(config -> config != null &&
                        config.getSessionDynamicPriceConfig() != null &&
                        Boolean.TRUE.equals(config.getSessionDynamicPriceConfig().getActive()));
    }

    private boolean isNotValidCurrency(Long channelId, CpanelEventoRecord event) {
        List<Long> currencies = channelCurrencyDao.getCurrenciesByChannelId(channelId);
        if (CollectionUtils.isEmpty(currencies)) {
            CpanelCanalRecord channel = channelDao.getById(channelId.intValue());
            return !channel.getCurrency().equals(event.getIdcurrency());
        }
        return currencies.stream()
                .noneMatch(currency -> currency == event.getIdcurrency().longValue());
    }

    @MySQLRead
    public EventChannelsDTO getEventChannels(Long eventId, EventChannelSearchFilter filter) {
        EventChannelsDTO eventChannelsDTO = new EventChannelsDTO();

        List<EventChannelRecord> channelEvents = channelEventDao.findChannelEvents(eventId, filter);
        List<SessionRecord> sessions = sessionDao.findSessionsByEventId(eventId.intValue());

        List<SessionConfig> secMktSessionConfigs = null;
        EventSecondaryMarketConfig secMktConfig = eventSecondaryMarketConfigCouchDao.get(String.valueOf(eventId));
        if (secMktConfig != null && CollectionUtils.isNotEmpty(secMktConfig.getEnabledChannels())) {
            secMktSessionConfigs = sessionConfigCouchDao.getSessionConfigsByEvent(eventId);
        }

        List<BaseEventChannelDTO> channels = EventChannelRecordConverter.fromEntityToBase(channelEvents, sessions,
                getS3Repository(), secMktConfig, secMktSessionConfigs, channelsRepository::getChannelConfigCached);

        if (filter.getRequestStatus() != null && filter.getReleaseStatus() != null && filter.getSaleStatus() != null) {
            List<BaseEventChannelDTO> filteredChannels = new ArrayList<>();

            for (BaseEventChannelDTO channel : channels) {
                boolean matchStatusSale = filter.getSaleStatus().stream().anyMatch(statusSale -> statusSale.name().equals(channel.getStatus().getSale().name()));
                boolean matchStatusRelease = filter.getReleaseStatus().stream().anyMatch(statusRelease -> statusRelease.name().equals(channel.getStatus().getRelease().name()));
                if (matchStatusSale && matchStatusRelease) {
                    filteredChannels.add(channel);
                }
            }
            channels = filteredChannels;
        }

        eventChannelsDTO.setData(channels);

        eventChannelsDTO.setMetadata(MetadataBuilder.build(filter, channelEventDao.countByFilter(eventId, filter)));

        return eventChannelsDTO;
    }

    @MySQLRead
    public EventChannelDTO getEventChannel(Long eventId, Long channelId) {
        EventChannelRecord channelEvent = channelEventDao.getChannelEventDetailed(channelId.intValue(), eventId.intValue());
        if (isNull(channelEvent)) {
            throw new OneboxRestException(MsEventErrorCode.EVENT_CHANNEL_NOT_FOUND);
        }

        List<SessionRecord> sessions = sessionDao.findSessionsByEventId(eventId.intValue());

        CpanelEntidadRecord channelEntity = Optional.ofNullable(entityDao.getEntityByChannelId(channelId))
                .orElseThrow(() -> new OneboxRestException(MsEventErrorCode.CHANNEL_NOT_FOUND));

        // If the channel's entity allows secondary market we should inform secundaryMarketEnabled,
        // then we check if the channel id is present in eventSecundaryConfig (couchbase) to determine the value
        EventSecondaryMarketConfig secMrktConfig = CommonUtils.isTrue(channelEntity.getAllowsecmkt()) ?
                Optional.ofNullable(eventSecondaryMarketConfigCouchDao.get(String.valueOf(eventId))).
                        orElse(new EventSecondaryMarketConfig(new ArrayList<>()))
                : null;

        List<SessionConfig> sessionConfigsByEvent = sessionConfigCouchDao.getSessionConfigsByEvent(eventId);
        EventChannelDTO eventChannelDTO = EventChannelRecordConverter.fromEntity(
                channelEvent,
                sessions,
                secMrktConfig,
                getS3Repository(),
                channelsRepository.getChannelConfigCached(channelId),
                sessionConfigsByEvent
        );

        eventChannelDTO.setSaleGroups(SaleGroupsConverter
                .fromEntity(saleGroupDao.getByEventIdWithAssignmentsInfo(eventId.intValue(), channelId.intValue()), channelEvent.getId()));

        return eventChannelDTO;
    }

    @MySQLWrite
    public void createEventChannel(Long eventId, Long channelId) {
        if (isNull(eventId)) {
            throw new OneboxRestException(MsEventErrorCode.EVENT_ID_MANDATORY);
        }
        if (isNull(channelId)) {
            throw new OneboxRestException(MsEventErrorCode.CHANNEL_ID_MANDATORY);
        }
        if (channelEventDao.getChannelEvent(channelId.intValue(), eventId.intValue()).isPresent()) {
            throw new OneboxRestException(MsEventErrorCode.EVENT_CHANNEL_EXISTS);
        }
        CpanelEventoRecord event = eventDao.findById(eventId.intValue());
        if (isNull(event)) {
            throw new OneboxRestException(MsEventErrorCode.EVENT_NOT_FOUND);
        }
        CpanelCanalRecord channel = channelDao.getById(channelId.intValue());
        if (isNull(channel)) {
            throw new OneboxRestException(MsEventErrorCode.CHANNEL_NOT_FOUND);
        }
        if (entityDao.getById(entityDao.getEntityInfo(event.getIdentidad()).operatorId()).getUsemulticurrency() == 1 &&
                isNotValidCurrency(channelId, event)) {
            throw new OneboxRestException(MsEventErrorCode.EVENT_CHANNEL_CURRENCY_NOT_MATCH);
        }

        CpanelCanalEventoRecord record = new CpanelCanalEventoRecord();
        record.setIdcanal(channelId.intValue());
        record.setIdevento(eventId.intValue());
        record.setEstadorelacion(ChannelEventStatus.PENDING_REQUESTED.getId());
        record.setUsarecargoevento((byte) 1);
        record.setUsarecargoeventopromocion((byte) 1);
        record.setUsafechasevento((byte) 1);
        record.setTodosgruposventa((byte) 1);
        record.setEnventa((byte) 1);
        record.setPublicado((byte) 1);

        //Dirty hack to allow ob portal bookings in the shadows
        ChannelSubtype channelSubtype = ChannelSubtype.getById(channel.getIdsubtipocanal());
        if (ChannelSubtype.PORTAL_WEB.equals(channelSubtype)) {
            record.setReservasactivas((byte) 1);
        }
        CpanelCanalEventoRecord insertedRecord = channelEventDao.insert(record);
        channelEventSurchargeRangeDao.inheritChargesRangesFromEvent(insertedRecord.getIdcanaleevento(), eventId.intValue());
        if (channelSubtype == ChannelSubtype.PORTAL_WEB
                || channelSubtype == ChannelSubtype.BOX_OFFICE_WEB
                || channelSubtype == ChannelSubtype.BOX_OFFICE_ONEBOX) {
            attendantsConfigService.addChannelToAttendantsConfig(eventId, channelId);
        }

        EventSecondaryMarketConfig eventSecMktConfig = eventSecondaryMarketConfigCouchDao.get(String.valueOf(eventId));
        if (!EventType.PRODUCT.equals(EventType.byId(event.getTipoevento()))
                && !EventType.AVET.equals(EventType.byId(event.getTipoevento()))
                && !ChannelSubtype.PORTAL_B2B.equals(channelSubtype)
                && eventSecMktConfig != null
                && CommonUtils.isTrue(eventSecMktConfig.getEnabled())) {

            if (eventSecMktConfig.getEnabledChannels() == null) {
                eventSecMktConfig.setEnabledChannels(new ArrayList<>());
            }
            EnabledChannel enableChannel = new EnabledChannel(channelId);

            List<SessionConfig> eventSessionConfigs = sessionConfigCouchDao.getSessionConfigsByEvent(eventId);
            enableChannel.setStartDate(SecondaryMarketUtils.findFirstSessionSecMktStartDate(eventSessionConfigs));
            enableChannel.setEndDate(SecondaryMarketUtils.findLastSessionSecMktEndDate(eventSessionConfigs));

            eventSecMktConfig.getEnabledChannels().add(enableChannel);
            eventSecondaryMarketConfigCouchDao.upsert(String.valueOf(eventId), eventSecMktConfig);
        }
    }

    @MySQLWrite
    public void updateEventChannel(Long eventId, Long channelId, UpdateEventChannelDTO updateData) {
        if (isNull(eventId)) {
            throw new OneboxRestException(MsEventErrorCode.EVENT_ID_MANDATORY);
        }
        if (isNull(channelId)) {
            throw new OneboxRestException(MsEventErrorCode.CHANNEL_ID_MANDATORY);
        }
        CpanelEntidadRecord channelEntityRecord = Optional.ofNullable(entityDao.getEntityByChannelId(channelId))
                .orElseThrow(() -> new OneboxRestException(MsEventErrorCode.CHANNEL_NOT_FOUND));

        CpanelCanalEventoRecord record = channelEventDao.getChannelEvent(channelId.intValue(), eventId.intValue())
                .orElseThrow(() -> new OneboxRestException(MsEventErrorCode.EVENT_CHANNEL_NOT_FOUND));

        validateUpdateData(eventId, updateData, record, channelEntityRecord);

        EventChannelRecordConverter.updateRecord(record, updateData);

        channelEventDao.update(record);

        if (updateData != null) {
            updateSecondaryMarketEnabled(eventId, channelId, record, updateData.getSettings(), channelEntityRecord);
        }
        updateSalesGroups(updateData, record);
    }

    private void updateSalesGroups(UpdateEventChannelDTO updateData, CpanelCanalEventoRecord record) {
        if (updateData.getUseAllSaleGroups() != null || updateData.getSaleGroups() != null) {
            salesGroupAssignmentDao.deleteByChannelEventId(record.getIdcanaleevento());
            if (!CommonUtils.isEmpty(updateData.getSaleGroups())) {
                salesGroupAssignmentDao.bulkInsertByChannelEvent(updateData.getSaleGroups(), record.getIdcanaleevento());
            }
        }
    }

    private void updateSecondaryMarketEnabled(Long eventId, Long channelId, CpanelCanalEventoRecord canalEventoRecord,
                                              UpdateEventChannelSettingsDTO updateSettings, CpanelEntidadRecord channelEntityRecord) {
        if (updateSettings == null || updateSettings.getSecondaryMarketEnabled() == null
                || channelEntityRecord.getAllowsecmkt() == null || channelEntityRecord.getAllowsecmkt() == 0) {
            return;
        }
        // TODO: Add event entity visibility to channel entity
        EventSecondaryMarketConfig secMrktConfig = Optional.ofNullable(eventSecondaryMarketConfigCouchDao.get(String.valueOf(eventId)))
                .orElse(new EventSecondaryMarketConfig(new ArrayList<>()));

        if (secMrktConfig.getEnabledChannels() == null) {
            secMrktConfig.setEnabledChannels(new ArrayList<>());
        }

        final Optional<EnabledChannel> alreadyEnabled = secMrktConfig.getEnabledChannels().stream().filter(ch -> ch.getId().equals(channelId)).findAny();
        boolean secondaryMarketEnabled = CommonUtils.isTrue(updateSettings.getSecondaryMarketEnabled());

        if (secondaryMarketEnabled) {
            EnabledChannel channel = new EnabledChannel(channelId);

            final boolean useEventDates = CommonUtils.isTrue(updateSettings.getUseEventDates()) ||
                    (updateSettings.getUseEventDates() == null && CommonUtils.isTrue(canalEventoRecord.getUsafechasevento()));
            if (useEventDates) {
                List<SessionConfig> eventSessions = sessionConfigCouchDao.getSessionConfigsByEvent(eventId);
                channel.setStartDate(SecondaryMarketUtils.findFirstSessionSecMktStartDate(eventSessions));
                channel.setEndDate(SecondaryMarketUtils.findLastSessionSecMktEndDate(eventSessions));
            } else {
                channel.setStartDate(updateSettings.getSecondaryMarketStartDate());
                channel.setEndDate(updateSettings.getSecondaryMarketEndDate());
            }

            alreadyEnabled.ifPresentOrElse(
                    presentChannel -> secMrktConfig.getEnabledChannels().set(secMrktConfig.getEnabledChannels().indexOf(presentChannel), channel),
                    () -> secMrktConfig.getEnabledChannels().add(channel)
            );
        } else if (alreadyEnabled.isPresent() && Boolean.FALSE.equals(updateSettings.getSecondaryMarketEnabled())) {
            secMrktConfig.getEnabledChannels().remove(alreadyEnabled.get());
        }
        eventSecondaryMarketConfigCouchDao.upsert(String.valueOf(eventId), secMrktConfig);
    }

    private void deleteEnabledChannelFromEventSecMktConfig(Long eventId, Long channelId) {
        EventSecondaryMarketConfig secMktConfig = eventSecondaryMarketConfigCouchDao.get(String.valueOf(eventId));
        if (secMktConfig != null && CollectionUtils.isNotEmpty(secMktConfig.getEnabledChannels())) {
            Optional<EnabledChannel> secMktEnabledChannel = secMktConfig.getEnabledChannels().stream()
                    .filter(channel -> channelId.equals(channel.getId()))
                    .findAny();
            if (secMktEnabledChannel.isPresent()) {
                secMktConfig.getEnabledChannels().remove(secMktEnabledChannel.get());
                eventSecondaryMarketConfigCouchDao.upsert(String.valueOf(eventId), secMktConfig);
            }
        }
    }

    private void validateUpdateData(Long eventId, UpdateEventChannelDTO updateEventChannel,
                                    CpanelCanalEventoRecord record, CpanelEntidadRecord channelEntityRecord) {
        if (updateEventChannel != null) {
            CpanelEventoRecord eventRecord = eventDao.getById(eventId.intValue());
            if (updateEventChannel.getSettings() != null) {
                validateSettings(updateEventChannel.getSettings(), eventRecord, channelEntityRecord, record);
            }
            validateSaleGroups(updateEventChannel, eventRecord, record);
        }
    }

    private void validateSaleGroups(UpdateEventChannelDTO updateEventChannel,
                                    CpanelEventoRecord eventRecord, CpanelCanalEventoRecord record) {
        if (updateEventChannel.getUseAllSaleGroups() != null &&
                CommonUtils.isTrue(updateEventChannel.getUseAllSaleGroups())) {
            updateEventChannel.setSaleGroups(null);
            return;
        }
        if (!CommonUtils.isEmpty(updateEventChannel.getSaleGroups())) {
            if (CommonUtils.isTrue(updateEventChannel.getUseAllSaleGroups()) ||
                    (updateEventChannel.getUseAllSaleGroups() == null && CommonUtils.isTrue(record.getTodosgruposventa()))) {
                throw new OneboxRestException(MsEventErrorCode.USE_ALL_SALE_GROUPS_MANDATORY);
            }

            boolean activityEvent = EventUtils.isActivity(eventRecord.getTipoevento());

            List<Integer> templatesWithQuota = new ArrayList<>();
            Set<Long> saleGroups = Optional.ofNullable(saleGroupDao.getByEventId(eventRecord.getIdevento().longValue()))
                    .orElseThrow(() -> new OneboxRestException(MsEventErrorCode.INVALID_SALE_GROUPS))
                    .stream()
                    .peek(eventQuota -> checkActivityQuota(activityEvent, eventQuota, updateEventChannel.getSaleGroups(), templatesWithQuota))
                    .map(CpanelCuposConfigRecord::getIdcupo)
                    .map(Integer::longValue)
                    .collect(Collectors.toSet());
            if (!saleGroups.containsAll(updateEventChannel.getSaleGroups())) {
                throw new OneboxRestException(MsEventErrorCode.INVALID_SALE_GROUPS);
            }
        }
    }

    private void checkActivityQuota(boolean activityEvent, CpanelCuposConfigRecord quota, List<Long> requestQuota, List<Integer> templatesWithQuota) {
        if (activityEvent && requestQuota.contains(quota.getIdcupo().longValue())) {
            if (templatesWithQuota.contains(quota.getIdconfiguracion())) {
                throw new OneboxRestException(MsEventErrorCode.INVALID_ACTIVITY_SALE_GROUPS);
            }
            templatesWithQuota.add(quota.getIdconfiguracion());
        }
    }

    private void validateSettings(UpdateEventChannelSettingsDTO updateEventChannelSettings,
                                  CpanelEventoRecord eventRecord, CpanelEntidadRecord channelEntityRecord, CpanelCanalEventoRecord cpanelCanalEventoRecord) {

        validateSecondaryMarketSettings(updateEventChannelSettings,
                CommonUtils.isTrue(channelEntityRecord.getAllowsecmkt()),
                cpanelCanalEventoRecord.getIdcanal().longValue(),
                CommonUtils.isTrue(cpanelCanalEventoRecord.getUsafechasevento()),
                EventType.byId(eventRecord.getTipoevento()));

        if (updateEventChannelSettings.getUseEventDates() != null &&
                CommonUtils.isTrue(updateEventChannelSettings.getUseEventDates())) {
            updateEventChannelSettings.setReleaseDate(null);
            updateEventChannelSettings.setSaleEndDate(null);
            updateEventChannelSettings.setSaleStartDate(null);

            //Dirty hack to allow ob portal bookings by default
            CpanelCanalRecord channel = this.channelDao.getById(cpanelCanalEventoRecord.getIdcanal());
            if (ONE.equals(eventRecord.getPermitereservas()) && ChannelSubtype.PORTAL_WEB.getIdSubtipo() == channel.getIdsubtipocanal().intValue()) {
                var sessions = this.sessionDao.findSessionsByEventId(eventRecord.getIdevento());
                sessions.stream().filter(sessionRecord -> !sessionRecord.getEstado().equals(SessionStatus.DELETED.getId()))
                        .forEach(sessionRecord -> {
                            updateEventChannelSettings.setBookingStartDate(EventStatusUtil.applyEventChannelBookingStartDate(updateEventChannelSettings.getBookingStartDate(), sessionRecord));
                            updateEventChannelSettings.setBookingEndDate(EventStatusUtil.applyEventChannelBookingEndDate(updateEventChannelSettings.getBookingEndDate(), sessionRecord));
                        });
            } else {
                updateEventChannelSettings.setBookingStartDate(null);
                updateEventChannelSettings.setBookingEndDate(null);
            }

            return;
        }

        final boolean releaseDateRequiredAndNull = isTrue(updateEventChannelSettings.getReleaseEnabled())
                && isNull(updateEventChannelSettings.getReleaseDate());

        final boolean saleDatesRequiredAndNull = isTrue(updateEventChannelSettings.getSaleEnabled())
                && (isNull(updateEventChannelSettings.getSaleStartDate())
                || isNull(updateEventChannelSettings.getSaleEndDate()));


        final boolean bookingAllowed = ONE.equals(eventRecord.getPermitereservas());
        final boolean bookingDatesRequiredAndNull = (bookingAllowed && isTrue(updateEventChannelSettings.getBookingEnabled()))
                && (isNull(updateEventChannelSettings.getBookingStartDate())
                || isNull(updateEventChannelSettings.getBookingEndDate()));

        if (releaseDateRequiredAndNull || saleDatesRequiredAndNull || bookingDatesRequiredAndNull) {
            throw new OneboxRestException(MsEventErrorCode.EVENT_CHANNEL_SETTINGS_DATES_MANDATORY);
        }

        if (!isNull(updateEventChannelSettings.getSaleStartDate()) &&
                !isNull(updateEventChannelSettings.getSaleEndDate()) && DateUtils.toMillis(updateEventChannelSettings.getSaleStartDate())
                >= DateUtils.toMillis(updateEventChannelSettings.getSaleEndDate())) {
            throw new OneboxRestException(MsEventErrorCode.START_GREATER_END_DATE);
        }

        if (!isNull(updateEventChannelSettings.getBookingStartDate()) &&
                !isNull(updateEventChannelSettings.getBookingEndDate()) && DateUtils.toMillis(updateEventChannelSettings.getBookingStartDate())
                >= DateUtils.toMillis(updateEventChannelSettings.getBookingEndDate())) {
            throw new OneboxRestException(MsEventErrorCode.START_GREATER_END_DATE);
        }
    }

    private void validateSecondaryMarketSettings(UpdateEventChannelSettingsDTO updateEventChannelSettings,
                                                 boolean secMktAllowedByEntity,
                                                 Long channelId,
                                                 boolean currentlyUsingEventDates,
                                                 EventType eventType) {
        if (CommonUtils.isTrue(updateEventChannelSettings.getSecondaryMarketEnabled())) {

            if ((EventType.AVET.equals(eventType) || EventType.PRODUCT.equals(eventType))
                    &&
                    (updateEventChannelSettings.getSecondaryMarketEnabled() != null
                            || updateEventChannelSettings.getSecondaryMarketStartDate() != null
                            || updateEventChannelSettings.getSecondaryMarketEndDate() != null)) {
                throw new OneboxRestException(MsEventErrorCode.EVENT_TYPE_DOES_NOT_SUPPORT_SECONDARY_MARKET);
            }

            final boolean willUseEventDates = CommonUtils.isTrue(updateEventChannelSettings.getUseEventDates())
                    || (updateEventChannelSettings.getUseEventDates() == null && currentlyUsingEventDates);

            final boolean secMktDatesRequiredAndNull = (isNull(updateEventChannelSettings.getSecondaryMarketStartDate())
                    || isNull(updateEventChannelSettings.getSecondaryMarketEndDate())) && !willUseEventDates;

            if (secMktDatesRequiredAndNull) {
                throw new OneboxRestException(MsEventErrorCode.EVENT_CHANNEL_SETTINGS_DATES_MANDATORY);
            }

            if (!isNull(updateEventChannelSettings.getSecondaryMarketStartDate()) &&
                    !isNull(updateEventChannelSettings.getSecondaryMarketEndDate()) &&
                    !updateEventChannelSettings.getSecondaryMarketStartDate().isBefore(updateEventChannelSettings.getSecondaryMarketEndDate())) {
                throw new OneboxRestException(MsEventErrorCode.START_GREATER_END_DATE);
            }

            if (!secMktAllowedByEntity) {
                throw new OneboxRestException(MsEventErrorCode.SECONDARY_MARKET_NOT_ALLOWED_BY_ENTITY);
            }

            ChannelConfigDTO channelConfig = channelsRepository.getChannelConfigCached(channelId);
            boolean secMktAllowedByChannelTypeAndV4 = ChannelSubtype.PORTAL_WEB.getIdSubtipo() == channelConfig.getChannelType()
                    && (CommonUtils.isTrue(channelConfig.getV4Enabled()) || CommonUtils.isTrue(channelConfig.getV4ConfigEnabled()));

            if (!secMktAllowedByChannelTypeAndV4) {
                throw new OneboxRestException(MsEventErrorCode.SECONDARY_MARKET_NOT_SUPPORTED);
            }
        }
    }


    private String getS3Repository() {
        return this.s3domain + this.fileBasePath;
    }

    private UpdateEventChannelDTO generateUnpublishedEventChannelDTO() {
        UpdateEventChannelDTO updateData = new UpdateEventChannelDTO();
        UpdateEventChannelSettingsDTO settingsDTO = new UpdateEventChannelSettingsDTO();
        settingsDTO.setSaleEnabled(false);
        settingsDTO.setReleaseEnabled(false);
        settingsDTO.setSecondaryMarketEnabled(false);
        updateData.setSettings(settingsDTO);

        return updateData;
    }

}
