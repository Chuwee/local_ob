package es.onebox.mgmt.seasontickets.service;

import es.onebox.core.exception.CoreErrorCode;
import es.onebox.core.exception.ExceptionBuilder;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.security.Roles;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.mgmt.channels.converter.ChannelConverter;
import es.onebox.mgmt.channels.dto.ChannelLanguagesDTO;
import es.onebox.mgmt.channels.utils.ChannelsUrlUtils;
import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.common.MasterdataService;
import es.onebox.mgmt.common.ticketpreview.TicketPreviewDTO;
import es.onebox.mgmt.common.ticketpreview.converter.TicketPreviewConverter;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelAcceptRequest;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelConfig;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelResponse;
import es.onebox.mgmt.datasources.ms.channel.enums.ChannelSubtype;
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
import es.onebox.mgmt.datasources.ms.event.dto.event.EventChannel;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicket;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicketInternalGenerationStatus;
import es.onebox.mgmt.datasources.ms.event.dto.session.Session;
import es.onebox.mgmt.datasources.ms.event.repository.EventChannelsRepository;
import es.onebox.mgmt.datasources.ms.event.repository.SeasonTicketRepository;
import es.onebox.mgmt.datasources.ms.event.repository.SessionsRepository;
import es.onebox.mgmt.datasources.ms.ticket.dto.TicketPreviewRequest;
import es.onebox.mgmt.datasources.ms.ticket.enums.TicketPreviewType;
import es.onebox.mgmt.datasources.ms.ticket.repository.TicketPreviewRepository;
import es.onebox.mgmt.events.dto.channel.EventChannelSearchFilter;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.seasontickets.converter.SeasonTicketChannelContentsConverter;
import es.onebox.mgmt.seasontickets.converter.SeasonTicketChannelConverter;
import es.onebox.mgmt.seasontickets.dto.channels.SeasonTicketChannelDTO;
import es.onebox.mgmt.seasontickets.dto.channels.SeasonTicketChannelLinks;
import es.onebox.mgmt.seasontickets.dto.channels.SeasonTicketChannelsDTO;
import es.onebox.mgmt.seasontickets.dto.channels.UpdateSeasonTicketChannelDTO;
import es.onebox.mgmt.security.SecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
public class SeasonTicketChannelService {

    @Autowired
    private SeasonTicketRepository seasonTicketRepository;
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
    private SessionsRepository sessionsRepository;
    @Autowired
    private SeasonTicketValidationService stValidationService;
    @Autowired
    private SaleRequestsRepository saleRequestsRepository;
    @Autowired
    private TicketPreviewRepository ticketPreviewRepository;

    @Value("${onebox.portal}")
    private String urlPortal;

    @Value("${onebox.webapps.channels.url}")
    private String urlChannel;

    public void createSeasonTicketChannel(Long seasonTicketId, Long channelId) {
        stValidationService.validateIds(seasonTicketId, channelId);
        stValidationService.getAndCheckSeasonTicket(seasonTicketId);
        ChannelResponse channel = channelsRepository.getChannel(channelId);
        if (channel == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.CHANNEL_NOT_FOUND);
        }
        SeasonTicketInternalGenerationStatus status = Optional.ofNullable(seasonTicketRepository.getSeasonTicketStatus(seasonTicketId))
                .orElseThrow(() -> new OneboxRestException(ApiMgmtErrorCode.SEASON_TICKET_NOT_READY))
                .getGenerationStatus();
        if (status != SeasonTicketInternalGenerationStatus.READY) {
            throw new OneboxRestException(ApiMgmtErrorCode.SEASON_TICKET_NOT_READY);
        }
        try {
            eventChannelsRepository.getEventChannel(seasonTicketId, channelId);
        } catch (OneboxRestException e) {
            if (e.getErrorCode() == null || !e.getErrorCode().equals(ApiMgmtErrorCode.EVENT_CHANNEL_NOT_FOUND.getErrorCode())) {
                throw new OneboxRestException(ApiMgmtErrorCode.GENERIC_ERROR);
            }
        }

        eventChannelsRepository.createEventChannel(seasonTicketId, channelId);
    }

    public void deleteSeasonTicketChannel(Long seasonTicketId, Long channelId) {
        stValidationService.getAndCheckSeasonTicketChannel(seasonTicketId, channelId);
        eventChannelsRepository.deleteEventChannel(seasonTicketId, channelId);
    }

    public void requestChannelApproval(Long seasonTicketId, Long channelId) {
        stValidationService.validateIds(seasonTicketId, channelId);
        SeasonTicket seasonTicket = stValidationService.getAndCheckSeasonTicket(seasonTicketId);
        EventChannel eventChannel = stValidationService.getAndCheckSeasonTicketChannelRelationExists(seasonTicketId, channelId);
        User u = usersRepository.getUser(SecurityUtils.getUsername(), SecurityUtils.getUserOperatorId(),
                SecurityUtils.getApiKey());

        eventChannelsRepository.requestChannelApproval(seasonTicketId, channelId, u.getId());

        channelsRepository.requestChannelEventApproval(seasonTicketId, channelId);

        if (SecurityUtils.hasAnyRole(Roles.ROLE_CNL_MGR, Roles.ROLE_OPR_MGR) &&
                eventChannel.getChannel().getEntityId().equals(seasonTicket.getEntityId())) {
            acceptRequest(seasonTicketId, channelId, u.getId(), eventChannel.getChannel().getEntityId());
        }
    }

    private void acceptRequest(Long seasonTicketId, Long channelId, Long userId, Long channelEntityId) {
        ChannelAcceptRequest channelAcceptRequestDTO = new ChannelAcceptRequest();
        channelAcceptRequestDTO.setUserId(userId);
        List<SubscriptionDTO> subscriptionDTOLists = msCrmDatasource.getSubscriptionLists(null, channelEntityId);
        Long mailingListId = null;
        if (subscriptionDTOLists != null) {
            mailingListId = subscriptionDTOLists.stream()
                    .filter(SubscriptionDTO::getDefault)
                    .map(SubscriptionDTO::getId)
                    .map(Integer::longValue)
                    .findAny()
                    .orElse(null);
        }
        channelAcceptRequestDTO.setChannelMailingListId(mailingListId);
        channelsRepository.acceptEventRequest(seasonTicketId, channelId, channelAcceptRequestDTO);
    }


    public SeasonTicketChannelsDTO getSeasonTicketChannels(Long seasonTicketId) {
        if (seasonTicketId == null || seasonTicketId <= 0) {
            throw new OneboxRestException(ApiMgmtErrorCode.INVALID_SEASON_TICKET_ID);
        }
        stValidationService.getAndCheckSeasonTicket(seasonTicketId);
        return SeasonTicketChannelConverter.fromEntity(eventChannelsRepository.getEventChannels(seasonTicketId,
                EventChannelSearchFilter.builder().limit(999L).offset(0L).build()));
    }

    public SeasonTicketChannelDTO getSeasonTicketChannel(Long seasonTicketId, Long channelId) {
        EventChannel eventChannel = stValidationService.getAndCheckSeasonTicketChannel(seasonTicketId, channelId);

        ChannelResponse channel = channelsRepository.getChannel(channelId);
        Map<Long, String> languagesByIds = masterdataService.getLanguagesByIds();
        ChannelLanguagesDTO languages = ChannelConverter.convertToLanguageDTO(channel.getLanguages(), languagesByIds);

        return SeasonTicketChannelConverter.fromEntity(eventChannel, languages);
    }

    public void updateSeasonTicketChannel(Long seasonTicketId, Long channelId, UpdateSeasonTicketChannelDTO updateData) {
        if (CommonUtils.isNull(updateData)) {
            throw new OneboxRestException(CoreErrorCode.BAD_PARAMETER, "Update data is mandatory", null);
        }
        EventChannel eventChannel = stValidationService.getAndCheckSeasonTicketChannel(seasonTicketId, channelId);

        try {
            if (updateData.getSettings() != null && updateData.getSettings().getSecondaryMarket() != null) {
                boolean enablingSecMkt = CommonUtils.isTrue(updateData.getSettings().getSecondaryMarket().getEnabled());
                boolean enablingSecMktDates = updateData.getSettings().getSecondaryMarket().getStartDate() != null
                                            && updateData.getSettings().getSecondaryMarket().getEndDate() != null;

                if (enablingSecMkt && CommonUtils.isFalse(updateData.getSettings().getUseEventDates()) && !enablingSecMktDates) {
                    throw new OneboxRestException(ApiMgmtErrorCode.EVENT_CHANNEL_SETTINGS_DATES_MANDATORY);
                }
                if (enablingSecMktDates && !updateData.getSettings().getSecondaryMarket().getStartDate()
                        .isBefore(updateData.getSettings().getSecondaryMarket().getEndDate())) {
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
            if (e.getErrorCode().equals(ApiMgmtErrorCode.SEASON_TICKET_CHANNEL_NOT_FOUND.getErrorCode())) {
                throw new OneboxRestException(ApiMgmtErrorCode.SEASON_TICKET_CHANNEL_NOT_FOUND);
            }
            throw e;
        }

        eventChannelsRepository.updateEventChannel(seasonTicketId, channelId, SeasonTicketChannelConverter.fromDTO(updateData));
    }

    public SeasonTicketChannelLinks getSeasonTicketChannelContentLinks(Long seasonTicketId, Long channelId) {
        stValidationService.validateIds(seasonTicketId, channelId);
        SeasonTicket seasonTicket = stValidationService.getAndCheckSeasonTicket(seasonTicketId);
        stValidationService.getAndCheckSeasonTicketChannelRelationExists(seasonTicketId, channelId);
        ChannelResponse channel = channelsRepository.getChannel(channelId);
        if (channel == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.SEASON_TICKET_NOT_FOUND);
        }
        Session session = sessionsRepository.getSession(seasonTicket.getSessionId());
        Map<Long, String> languagesByIds = masterdataService.getLanguagesByIds();
        ChannelLanguagesDTO languages = ChannelConverter.convertToLanguageDTO(channel.getLanguages(), languagesByIds);

        ChannelConfig channelConfig = channelsRepository.getChannelConfig(channelId);
        String finalUrl = ChannelsUrlUtils.selectUrlByChannelConfig(channelConfig.getV4Enabled(), urlChannel, urlPortal);

        return SeasonTicketChannelContentsConverter.convert(languages,session, finalUrl, channel.getUrl(), seasonTicketId, channelConfig.getV4Enabled());
    }

    public TicketPreviewDTO getTicketPdfPreview(Long seasonTicketId, Long channelId, String language) {
        stValidationService.validateIds(seasonTicketId, channelId);
        SeasonTicket seasonTicket = stValidationService.getAndCheckSeasonTicket(seasonTicketId);
        stValidationService.getAndCheckSeasonTicketChannelRelationExists(seasonTicketId, channelId);
        ChannelResponse channel = channelsRepository.getChannel(channelId);
        if (channel == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.SEASON_TICKET_NOT_FOUND);
        }

        Map<Long, String> languagesByIds = masterdataService.getLanguagesByIds();
        ChannelLanguagesDTO languages = ChannelConverter.convertToLanguageDTO(channel.getLanguages(), languagesByIds);

        Long languageId = Optional.ofNullable(language)
                .map(l -> getChannelLanguage(languagesByIds, languages, l))
                .orElseGet(() -> getChannelLanguage(languagesByIds, languages, languages.getDefaultLanguageCode()));

        MsSaleRequestsFilter filter = new MsSaleRequestsFilter();
        filter.setChannelId(Collections.singletonList(channelId));
        filter.setEventId(Collections.singletonList(seasonTicketId));
        filter.setStatus(Collections.singletonList(MsSaleRequestsStatus.ACCEPTED));
        filter.setLimit(1L);
        MsSaleRequestsResponseDTO searchSaleRequest = saleRequestsRepository.searchSaleRequests(filter);

        if(Objects.isNull(searchSaleRequest) || CollectionUtils.isEmpty(searchSaleRequest.getData())) {
            throw new OneboxRestException(ApiMgmtErrorCode.EVENT_CHANNEL_NOT_FOUND);
        }

        Optional<MsSaleRequestDTO> optSaleRequestId = searchSaleRequest.getData().stream().findFirst();
        if(optSaleRequestId.isPresent()) {
            TicketPreviewRequest request = new TicketPreviewRequest();
            request.setEntityId(seasonTicket.getEntityId());
            request.setEventId(seasonTicketId);
            request.setLanguageId(languageId);
            request.setItemId(optSaleRequestId.get().getId());
            request.setType(TicketPreviewType.EVENT_CHANNEL);
            return TicketPreviewConverter.toDTO(ticketPreviewRepository.getTicketPdfPreview(request));
        }
        throw new OneboxRestException(ApiMgmtErrorCode.EVENT_CHANNEL_NOT_FOUND);
    }

    private Long getChannelLanguage(Map<Long, String> languagesByIds, ChannelLanguagesDTO languages, String language) {
        String languageCode = languages.getSelectedLanguageCode().stream()
                .filter(lang -> lang.equals(language))
                .findFirst()
                .map(ConverterUtils::toLocale)
                .orElseThrow(() -> ExceptionBuilder.build(ApiMgmtErrorCode.NOT_AVAILABLE_LANG));

        return languagesByIds.entrySet().stream()
                .filter(entry -> entry.getValue().contains(languageCode))
                .findFirst().get().getKey();
    }

}
