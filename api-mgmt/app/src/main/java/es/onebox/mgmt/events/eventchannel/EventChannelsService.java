package es.onebox.mgmt.events.eventchannel;

import com.queue_it.connector.integrationconfig.CustomerIntegration;
import com.queue_it.connector.integrationconfig.IntegrationConfigModel;
import es.onebox.core.exception.CoreErrorCode;
import es.onebox.core.exception.ExceptionBuilder;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.security.Roles;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.mgmt.channels.converter.ChannelConverter;
import es.onebox.mgmt.channels.dto.ChannelLanguagesDTO;
import es.onebox.mgmt.channels.dto.UpdateFavoriteChannelDTO;
import es.onebox.mgmt.channels.utils.ChannelsUrlUtils;
import es.onebox.mgmt.common.BaseLinkDTO;
import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.common.MasterdataService;
import es.onebox.mgmt.common.ticketpreview.TicketPreviewDTO;
import es.onebox.mgmt.common.ticketpreview.converter.TicketPreviewConverter;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelAcceptRequest;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelConfig;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelResponse;
import es.onebox.mgmt.datasources.ms.channel.enums.ChannelStatus;
import es.onebox.mgmt.datasources.ms.channel.enums.ChannelSubtype;
import es.onebox.mgmt.datasources.ms.channel.enums.ChannelType;
import es.onebox.mgmt.datasources.ms.channel.repositories.ChannelsRepository;
import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.MsSaleRequestDTO;
import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.MsSaleRequestsFilter;
import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.MsSaleRequestsResponseDTO;
import es.onebox.mgmt.datasources.ms.channel.salerequests.enums.MsSaleRequestsStatus;
import es.onebox.mgmt.datasources.ms.channel.salerequests.repositories.SaleRequestsRepository;
import es.onebox.mgmt.datasources.ms.crm.MsCrmDatasource;
import es.onebox.mgmt.datasources.ms.crm.dto.SubscriptionDTO;
import es.onebox.mgmt.datasources.ms.entity.dto.User;
import es.onebox.mgmt.datasources.ms.entity.repository.UsersRepository;
import es.onebox.mgmt.datasources.ms.event.dto.event.Event;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventAttendantsConfigDTO;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventChannel;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventChannels;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventSaleRequestChannelFilter;
import es.onebox.mgmt.datasources.ms.event.dto.session.Sessions;
import es.onebox.mgmt.datasources.ms.event.repository.AttendantTicketsRepository;
import es.onebox.mgmt.datasources.ms.event.repository.EventChannelsRepository;
import es.onebox.mgmt.datasources.ms.event.repository.EventsRepository;
import es.onebox.mgmt.datasources.ms.ticket.dto.TicketPreviewRequest;
import es.onebox.mgmt.datasources.ms.ticket.enums.TicketPreviewType;
import es.onebox.mgmt.datasources.ms.ticket.repository.TicketPreviewRepository;
import es.onebox.mgmt.events.converter.EventChannelContentsConverter;
import es.onebox.mgmt.events.converter.EventChannelConverter;
import es.onebox.mgmt.events.dto.channel.EventChannelContentLinks;
import es.onebox.mgmt.events.dto.channel.EventChannelContentPublishedLinks;
import es.onebox.mgmt.events.dto.channel.EventChannelContentSessionsLinksResponse;
import es.onebox.mgmt.events.dto.channel.EventChannelDTO;
import es.onebox.mgmt.events.dto.channel.EventChannelSearchFilter;
import es.onebox.mgmt.events.dto.channel.EventChannelsResponse;
import es.onebox.mgmt.events.dto.channel.SaleRequestChannelCandidatesResponseDTO;
import es.onebox.mgmt.events.dto.channel.EventSaleRequestChannelFilterDTO;
import es.onebox.mgmt.events.dto.channel.SessionLinksFilter;
import es.onebox.mgmt.events.dto.channel.UpdateEventChannelDTO;
import es.onebox.mgmt.exception.ApiMgmtChannelsErrorCode;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.queueit.QueueItService;
import es.onebox.mgmt.queueit.utils.QueueITUtils;
import es.onebox.mgmt.security.SecurityManager;
import es.onebox.mgmt.security.SecurityUtils;
import es.onebox.mgmt.sessions.dto.SessionSearchFilter;
import es.onebox.mgmt.sessions.enums.SessionStatus;
import es.onebox.mgmt.validation.ValidationService;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static es.onebox.mgmt.sessions.enums.SessionField.ID;
import static es.onebox.mgmt.sessions.enums.SessionField.NAME;
import static es.onebox.mgmt.sessions.enums.SessionField.RELEASEDATE;
import static es.onebox.mgmt.sessions.enums.SessionField.STARTDATE;
import static es.onebox.mgmt.sessions.enums.SessionField.STATUS;


@Service
public class EventChannelsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventChannelsService.class);

    @Autowired
    private EventsRepository eventsRepository;
    @Autowired
    private SecurityManager securityManager;
    @Autowired
    private EventChannelsRepository eventChannelsRepository;
    @Autowired
    private ChannelsRepository channelsRepository;
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private MsCrmDatasource msCrmDatasource;
    @Autowired
    private MasterdataService masterdataService;
    @Autowired
    private TicketPreviewRepository ticketPreviewRepository;
    @Autowired
    private SaleRequestsRepository saleRequestsRepository;
    @Autowired
    private AttendantTicketsRepository attendantTicketsRepository;
    @Autowired
    private QueueItService queueItService;
    @Autowired
    private ValidationService validationService;

    @Value("${onebox.portal}")
    private String urlPortal;

    @Value("${onebox.webapps.channels.url}")
    private String urlChannel;

    public EventChannelsResponse getEventChannels(Long eventId, EventChannelSearchFilter filter) {

        Event event = eventsRepository.getEvent(eventId);
        if (event == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.EVENT_NOT_FOUND);
        }
        securityManager.checkEntityAccessible(event.getEntityId());

        EventChannels eventChannels = eventChannelsRepository.getEventChannels(eventId, filter);

        return EventChannelConverter.fromEntity(eventChannels);
    }

    public void createEventChannel(Long eventId, Long channelId) {
        if (channelId == null || channelId <= 0) {
            throw new OneboxRestException(ApiMgmtErrorCode.CHANNEL_ID_INVALID);
        }
        Event event = validationService.getAndCheckEvent(eventId);
        ChannelResponse channel = channelsRepository.getChannel(channelId);
        if (channel == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.CHANNEL_NOT_FOUND);
        }

        // The B2B channel must belong to the same entity as the event
        if (ChannelSubtype.PORTAL_B2B.equals(channel.getSubtype()) && !event.getEntityId().equals(channel.getEntityId())) {
            throw new OneboxRestException(ApiMgmtErrorCode.ENTITY_CHANNEL_INVALID_REL);
        }

        try {
            eventChannelsRepository.getEventChannel(eventId, channelId);
        } catch (OneboxRestException e) {
            if (e.getErrorCode() == null || !e.getErrorCode().equals(ApiMgmtErrorCode.EVENT_CHANNEL_NOT_FOUND.getErrorCode())) {
                throw new OneboxRestException(ApiMgmtErrorCode.GENERIC_ERROR);
            }
        }
        eventChannelsRepository.createEventChannel(eventId, channelId);
    }

    public void deleteEventChannel(Long eventId, Long channelId) {
        if (eventId == null || eventId <= 0) {
            throw new OneboxRestException(ApiMgmtErrorCode.INVALID_EVENT_ID);
        }
        if (channelId == null || channelId <= 0) {
            throw new OneboxRestException(ApiMgmtErrorCode.CHANNEL_ID_INVALID);
        }
        Event event = eventsRepository.getEvent(eventId);
        if (event == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.EVENT_NOT_FOUND);
        }
        securityManager.checkEntityAccessible(event.getEntityId());
        try {
            eventChannelsRepository.getEventChannel(eventId, channelId);
        } catch (OneboxRestException e) {
            if (e.getErrorCode() != null && e.getErrorCode().equals(ApiMgmtErrorCode.EVENT_CHANNEL_NOT_FOUND.getErrorCode())) {
                throw new OneboxRestException(ApiMgmtErrorCode.EVENT_CHANNEL_NOT_FOUND);
            }
            throw e;
        }
        eventChannelsRepository.deleteEventChannel(eventId, channelId);
    }

    public void requestChannelApproval(Long eventId, Long channelId) {
        if (eventId == null || eventId <= 0) {
            throw new OneboxRestException(ApiMgmtErrorCode.INVALID_EVENT_ID);
        }
        if (channelId == null || channelId <= 0) {
            throw new OneboxRestException(ApiMgmtErrorCode.CHANNEL_ID_INVALID);
        }
        Event event = eventsRepository.getEvent(eventId);
        if (event == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.EVENT_NOT_FOUND);
        }
        securityManager.checkEntityAccessible(event.getEntityId());
        EventChannel eventChannel;
        try {
            eventChannel = eventChannelsRepository.getEventChannel(eventId, channelId);
        } catch (OneboxRestException e) {
            if (e.getErrorCode() != null && e.getErrorCode().equals(ApiMgmtErrorCode.EVENT_CHANNEL_NOT_FOUND.getErrorCode())) {
                throw new OneboxRestException(ApiMgmtErrorCode.EVENT_CHANNEL_NOT_FOUND);
            }
            throw e;
        }
        User u= usersRepository.getUser(SecurityUtils.getUsername(), SecurityUtils.getUserOperatorId(),
                SecurityUtils.getApiKey());

        eventChannelsRepository.requestChannelApproval(eventId, channelId, u.getId());

        channelsRepository.requestChannelEventApproval(eventId, channelId);

        if (SecurityUtils.hasAnyRole(Roles.ROLE_CNL_MGR, Roles.ROLE_OPR_MGR) &&
                eventChannel.getChannel().getEntityId().equals(event.getEntityId())) {
            acceptRequest(eventId, channelId, u.getId(), eventChannel.getChannel().getEntityId());
        }
    }

    private void acceptRequest(Long eventId, Long channelId, Long userId, Long channelEntityId) {
        ChannelAcceptRequest channelAcceptRequestDTO = new ChannelAcceptRequest();
        channelAcceptRequestDTO.setUserId(userId);
        List<SubscriptionDTO> subscriptionLists = msCrmDatasource.getSubscriptionLists(null, channelEntityId);
        Long mailingListId = null;
        if (subscriptionLists != null) {
            mailingListId = subscriptionLists.stream()
                    .filter(SubscriptionDTO::getDefault)
                    .map(SubscriptionDTO::getId)
                    .map(Integer::longValue)
                    .findAny()
                    .orElse(null);
        }
        channelAcceptRequestDTO.setChannelMailingListId(mailingListId);
        channelsRepository.acceptEventRequest(eventId, channelId, channelAcceptRequestDTO);
    }

    public EventChannelDTO getEventChannel(Long eventId, Long channelId) {
        if (eventId == null || eventId <= 0) {
            throw new OneboxRestException(ApiMgmtErrorCode.INVALID_EVENT_ID);
        }
        if (channelId == null || channelId <= 0) {
            throw new OneboxRestException(ApiMgmtErrorCode.CHANNEL_ID_INVALID);
        }
        Event event = eventsRepository.getEvent(eventId);
        if (event == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.EVENT_NOT_FOUND);
        }
        securityManager.checkEntityAccessible(event.getEntityId());
        try {
            ChannelResponse channel = channelsRepository.getChannel(channelId);
            Map<Long, String> languagesByIds = masterdataService.getLanguagesByIds();
            ChannelLanguagesDTO languages = ChannelConverter.convertToLanguageDTO(channel.getLanguages(), languagesByIds);

            EventChannel eventChannel = eventChannelsRepository.getEventChannel(eventId, channelId);

            return EventChannelConverter.fromEntity(eventChannel, languages, channel.getForceSquarePictures());
        } catch (OneboxRestException e) {
            if (e.getErrorCode().equals(ApiMgmtErrorCode.EVENT_CHANNEL_NOT_FOUND.getErrorCode())) {
                throw new OneboxRestException(ApiMgmtErrorCode.EVENT_CHANNEL_NOT_FOUND);
            } else if (e.getErrorCode().equals(ApiMgmtErrorCode.CHANNEL_NOT_FOUND.getErrorCode())) {
                throw new OneboxRestException(ApiMgmtErrorCode.CHANNEL_NOT_FOUND);
            }
            throw e;
        }
    }

    public void updateEventChannel(Long eventId, Long channelId, UpdateEventChannelDTO updateData) {
        if (eventId == null || eventId <= 0) {
            throw new OneboxRestException(ApiMgmtErrorCode.INVALID_EVENT_ID);
        }
        if (channelId == null || channelId <= 0) {
            throw new OneboxRestException(ApiMgmtErrorCode.CHANNEL_ID_INVALID);
        }
        if (CommonUtils.isNull(updateData)) {
            throw new OneboxRestException(CoreErrorCode.BAD_PARAMETER, "Update data is mandatory", null);
        }

        Event event = eventsRepository.getEvent(eventId);
        if (event == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.EVENT_NOT_FOUND);
        }

        securityManager.checkEntityAccessible(event.getEntityId());

        try {
            EventChannel eventChannel = eventChannelsRepository.getEventChannel(eventId, channelId);

            if (updateData.getSettings() != null && updateData.getSettings().getSecondaryMarketSale() != null) {

                boolean enablingSecMkt = CommonUtils.isTrue(updateData.getSettings().getSecondaryMarketSale().getEnabled());
                boolean enablingSecMktDates = updateData.getSettings().getSecondaryMarketSale().getStartDate() != null
                        && updateData.getSettings().getSecondaryMarketSale().getEndDate() != null;

                if (enablingSecMkt && CommonUtils.isFalse(updateData.getSettings().getUseEventDates()) && !enablingSecMktDates) {
                    throw new OneboxRestException(ApiMgmtErrorCode.EVENT_CHANNEL_SETTINGS_DATES_MANDATORY);
                }
                if (enablingSecMktDates && !updateData.getSettings().getSecondaryMarketSale()
                        .getStartDate().isBefore(updateData.getSettings().getSecondaryMarketSale().getEndDate())) {
                    throw new OneboxRestException(ApiMgmtErrorCode.START_GREATER_END_DATE);
                }
                if (enablingSecMkt) {
                    if (!ChannelSubtype.PORTAL_WEB.equals(eventChannel.getChannel().getType())
                            || (CommonUtils.isFalse(eventChannel.getChannel().getV4Enabled())
                            && CommonUtils.isFalse(eventChannel.getChannel().getV4ConfigEnabled()))) {
                        throw new OneboxRestException(ApiMgmtErrorCode.SECONDARY_MARKET_NOT_SUPPORTED);
                    }
                }
            }
        } catch (OneboxRestException e) {
            if (e.getErrorCode().equals(ApiMgmtErrorCode.CHANNEL_NOT_FOUND.getErrorCode())) {
                throw new OneboxRestException(ApiMgmtErrorCode.CHANNEL_NOT_FOUND);
            }
            if (e.getErrorCode().equals(ApiMgmtErrorCode.EVENT_CHANNEL_NOT_FOUND.getErrorCode())) {
                throw new OneboxRestException(ApiMgmtErrorCode.EVENT_CHANNEL_NOT_FOUND);
            }
            throw e;
        }

        eventChannelsRepository.updateEventChannel(eventId, channelId, EventChannelConverter.fromDTO(updateData));
    }

    public List<EventChannelContentLinks> getEventChannelContentLinks(Long eventId, Long channelId) {
        Event event = validationService.getAndCheckEvent(eventId);
        ChannelResponse channel = checkChannelAndGetChannel(eventId, channelId);
        ChannelLanguagesDTO languages = getLanguages(channel);

        ChannelConfig channelConfig = channelsRepository.getChannelConfig(channelId);
        Boolean v4Enabled = channelConfig != null && (channelConfig.getV4Enabled() != null ? channelConfig.getV4Enabled() : false);
        Boolean externalWhitelabel = validationService.getAndCheckExternalWhitelabel(channelConfig, channel);

        return EventChannelContentsConverter.convertToEventChannelContentLinks(urlChannel, urlPortal, channel, event, languages, v4Enabled, externalWhitelabel);
    }

    public EventChannelContentSessionsLinksResponse getEventChannelContentLinksByLanguage(Long eventId, Long channelId,
                                                                                          String language, SessionLinksFilter filter,
                                                                                          HttpServletRequest request) {
        Event event = validationService.getAndCheckEvent(eventId);
        ChannelResponse channel = checkChannelAndGetChannel(eventId, channelId);
        ChannelLanguagesDTO languages = getLanguages(channel);

        String currentLang = languages.getSelectedLanguageCode().stream().filter(e -> e.equals(language)).findFirst().orElse(null);

        if (currentLang == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.LANGUAGE_NOT_IN_EVENT);
        }

        SessionSearchFilter sessionSearchFilter = new SessionSearchFilter();
        sessionSearchFilter.setLimit(filter.getLimit());
        sessionSearchFilter.setOffset(filter.getOffset());

        if (filter.getSessionStatus().contains(SessionStatus.PREVIEW)) {
            sessionSearchFilter.setStatus(Arrays.asList(SessionStatus.PREVIEW));
        } else if (filter.getSessionStatus().contains(SessionStatus.SCHEDULED) || filter.getSessionStatus().contains(SessionStatus.READY)) {
            sessionSearchFilter.setStatus(Arrays.asList(SessionStatus.READY, SessionStatus.SCHEDULED));
        } else {
            throw new OneboxRestException(ApiMgmtErrorCode.SESSION_INVALID_STATUS);
        }
        sessionSearchFilter.setSort(filter.getSort());
        sessionSearchFilter.setFields(
                List.of(
                        ID.name(),
                        NAME.name(),
                        STARTDATE.name(),
                        STATUS.name(),
                        RELEASEDATE.name()
                )
        );

        sessionSearchFilter.setGetQueueitInfo(true);
        Sessions sessions = eventsRepository.getSessions(SecurityUtils.getUserOperatorId(), eventId, sessionSearchFilter);

        ChannelConfig channelConfig = channelsRepository.getChannelConfig(channelId);
        Boolean v4Enabled = channelConfig != null && BooleanUtils.isTrue(channelConfig.getV4Enabled());
        String finalUrl = ChannelsUrlUtils.selectUrlByChannelConfig(v4Enabled, urlChannel, urlPortal);
        Boolean externalWhitelabel = validationService.getAndCheckExternalWhitelabel(channelConfig, channel);

        EventChannelContentPublishedLinks responseData =
                EventChannelContentsConverter.convertToEventChannelContentSessionLinksByLanguage(
                        sessions, finalUrl, channel, currentLang, event, filter, v4Enabled, externalWhitelabel);

        EventChannelContentSessionsLinksResponse response = EventChannelContentsConverter.convertToEventChannelContentSessionLinksResponse(responseData, sessions.getMetadata());

        try {
            CustomerIntegration customerIntegration = queueItService.getCustomerIntegrationConfiguration(event.getEntityId());
            response.getData().forEach(eventChannelContent -> {
                if (eventChannelContent.getQueueit() != null && eventChannelContent.getQueueit().getEnabled()) {
                    IntegrationConfigModel matchedConfig =
                            QueueITUtils.getRequestValidationByConfig(request, customerIntegration, eventChannelContent.getLink());
                    if (matchedConfig != null) {
                        eventChannelContent.getQueueit().setQueueEvent(matchedConfig.EventId);
                        eventChannelContent.getQueueit().setActionName(matchedConfig.Name);
                    }
                }
            });

        } catch (Exception ex) {
            LOGGER.error("Error retrieving queueit events information : {}", ex.getMessage());
        }
        return response;
    }

    private ChannelLanguagesDTO getLanguages(ChannelResponse channel) {
        Map<Long, String> languagesByIds = masterdataService.getLanguagesByIds();
        return ChannelConverter.convertToLanguageDTO(channel.getLanguages(), languagesByIds);
    }

    public TicketPreviewDTO getTicketPdfPreview(Long eventId, Long channelId, String language) {
        validationService.getAndCheckEvent(eventId);

        ChannelResponse channel = channelsRepository.getChannel(channelId);
        if (Objects.isNull(channel) || ChannelStatus.DELETED.equals(channel.getStatus())) {
            throw new OneboxRestException(ApiMgmtErrorCode.CHANNEL_NOT_FOUND);
        }

        Map<Long, String> languagesByIds = masterdataService.getLanguagesByIds();
        ChannelLanguagesDTO languages = ChannelConverter.convertToLanguageDTO(channel.getLanguages(), languagesByIds);

        Long languageId = Optional.ofNullable(language)
                .map(l -> getChannelLanguage(languagesByIds, languages, l))
                .orElseGet(() -> getChannelLanguage(languagesByIds, languages, languages.getDefaultLanguageCode()));

        MsSaleRequestsFilter filter = new MsSaleRequestsFilter();
        filter.setChannelId(Collections.singletonList(channelId));
        filter.setEventId(Collections.singletonList(eventId));
        filter.setStatus(Collections.singletonList(MsSaleRequestsStatus.ACCEPTED));
        filter.setLimit(1L);
        MsSaleRequestsResponseDTO searchSaleRequest = saleRequestsRepository.searchSaleRequests(filter);

        if (Objects.isNull(searchSaleRequest) || CollectionUtils.isEmpty(searchSaleRequest.getData())) {
            throw new OneboxRestException(ApiMgmtErrorCode.EVENT_CHANNEL_NOT_FOUND);
        }

        Optional<MsSaleRequestDTO> optSaleRequestId = searchSaleRequest.getData().stream().findFirst();
        if (optSaleRequestId.isPresent()) {
            TicketPreviewRequest request = new TicketPreviewRequest();
            request.setEntityId(channel.getEntityId());
            request.setEventId(eventId);
            request.setLanguageId(languageId);
            request.setItemId(optSaleRequestId.get().getId());
            request.setType(TicketPreviewType.EVENT_CHANNEL);
            return TicketPreviewConverter.toDTO(ticketPreviewRepository.getTicketPdfPreview(request));
        }
        throw new OneboxRestException(ApiMgmtErrorCode.EVENT_CHANNEL_NOT_FOUND);
    }

    private static Long getChannelLanguage(Map<Long, String> languagesByIds, ChannelLanguagesDTO languages, String language) {
        String languageCode = languages.getSelectedLanguageCode().stream()
                .filter(lang -> lang.equals(language))
                .findFirst()
                .map(ConverterUtils::toLocale)
                .orElseThrow(() -> ExceptionBuilder.build(ApiMgmtErrorCode.NOT_AVAILABLE_LANG));

        Optional<Map.Entry<Long, String>> optLangId = languagesByIds.entrySet().stream().filter(entry -> entry.getValue().contains(languageCode)).findFirst();
        if (optLangId.isPresent()) {
            return optLangId.get().getKey();
        }
        throw ExceptionBuilder.build(ApiMgmtErrorCode.NOT_AVAILABLE_LANG);
    }

    private ChannelResponse checkChannelAndEventAndGetChannel(Long eventId, Long channelId) {
        validationService.getAndCheckEvent(eventId);
        return checkChannelAndGetChannel(eventId, channelId);
    }

    private ChannelResponse checkChannelAndGetChannel(Long eventId, Long channelId) {
        ChannelResponse channel = channelsRepository.getChannel(channelId);
        if (Objects.isNull(channel) || ChannelStatus.DELETED.equals(channel.getStatus())) {
            throw new OneboxRestException(ApiMgmtErrorCode.CHANNEL_NOT_FOUND);
        }
        if (!ChannelType.OB_PORTAL.equals(channel.getType())) {
            throw new OneboxRestException((ApiMgmtChannelsErrorCode.CHANNEL_UNSUPPORTED_TYPE));
        }

        if (Objects.isNull(eventChannelsRepository.getEventChannel(eventId, channelId))) {
            throw new OneboxRestException(ApiMgmtErrorCode.EVENT_CHANNEL_NOT_FOUND);
        }

        return channel;
    }

    public List<BaseLinkDTO> getEventChannelEditAttendantsLinks(Long eventId, Long channelId) {
        ChannelResponse channel = checkChannelAndEventAndGetChannel(eventId, channelId);
        checkEventAttendantConfig(eventId, channelId);

        Map<Long, String> languagesByIds = masterdataService.getLanguagesByIds();
        ChannelLanguagesDTO languages = ChannelConverter.convertToLanguageDTO(channel.getLanguages(), languagesByIds);

        ChannelConfig channelConfig = channelsRepository.getChannelConfig(channelId);
        Boolean v4Enabled = channelConfig != null && BooleanUtils.isTrue(channelConfig.getV4Enabled());
        String finalUrl = ChannelsUrlUtils.selectUrlByChannelConfig(v4Enabled, urlChannel, urlPortal);

        return EventChannelContentsConverter.buildEditAttendantsLinks(finalUrl, channel.getUrl(), languages, v4Enabled);
    }

    public void updateFavoriteChannel(Long eventId, Long channelId, UpdateFavoriteChannelDTO updateFavoriteChannel) {
        Event event = eventsRepository.getEvent(eventId);

        if (event == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.EVENT_NOT_FOUND);
        }

        securityManager.checkEntityAccessible(event.getEntityId());

        eventChannelsRepository.getEventChannel(eventId, channelId);

        channelsRepository.updateEntityFavoriteChannel(event.getEntityId(), channelId,
                EventChannelContentsConverter.fromDTO(updateFavoriteChannel));
    }

    public SaleRequestChannelCandidatesResponseDTO getEventSaleRequestChannelsCandidates(Long eventId, EventSaleRequestChannelFilterDTO filter) {
        Event event = eventsRepository.getEvent(eventId);
        if (event == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.EVENT_NOT_FOUND);
        }
        Long entityId = event.getEntityId();
        securityManager.checkEntityAccessible(entityId);

        List<Long> visibleEntities = null;
        if (CommonUtils.isTrue(filter.getIncludeThirdPartyChannels())) {
            visibleEntities = securityManager.getVisibleEntities(entityId);
        }

        EventSaleRequestChannelFilter channelFilter = EventChannelConverter.toMs(filter, entityId, visibleEntities,
                SecurityUtils.getUserOperatorId());
        var channels = channelsRepository.getEventSaleRequestChannelsCandidates(channelFilter);

        return EventChannelConverter.fromMsChannelsResponse(channels);
    }

    private void checkEventAttendantConfig(Long eventId, Long channelId) {
        EventAttendantsConfigDTO attendantConfig = attendantTicketsRepository.getEventAttendantsConfig(eventId);
        if (!eventAllowsAttendantModification(attendantConfig) || !channelRequireAttendantData(attendantConfig, channelId)) {
            throw ExceptionBuilder.build(ApiMgmtErrorCode.EVENT_CHANNEL_NOT_REQUIRE_ATTENDANT_DATA);
        }

    }

    private static boolean eventAllowsAttendantModification(EventAttendantsConfigDTO eventAttendantsConfig) {
        return BooleanUtils.isTrue(eventAttendantsConfig.getAllowAttendantsModification());
    }

    private static boolean channelRequireAttendantData(EventAttendantsConfigDTO eventAttendantsConfig, Long channelId) {
        return BooleanUtils.isTrue(eventAttendantsConfig.getAllChannelsActive()) ||
                (CollectionUtils.isNotEmpty(eventAttendantsConfig.getActiveChannels())
                        && eventAttendantsConfig.getActiveChannels().contains(channelId));
    }
}
