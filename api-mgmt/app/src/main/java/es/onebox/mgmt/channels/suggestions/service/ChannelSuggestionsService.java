package es.onebox.mgmt.channels.suggestions.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.channels.ChannelsHelper;
import es.onebox.mgmt.channels.suggestions.controller.ChannelSuggestionFilter;
import es.onebox.mgmt.channels.suggestions.converter.ChannelSuggestionConverter;
import es.onebox.mgmt.channels.suggestions.dto.ChannelSuggestionDTO;
import es.onebox.mgmt.channels.suggestions.dto.ChannelSuggestionsResponseDTO;
import es.onebox.mgmt.channels.suggestions.dto.CreateSuggestionTargetRequestDTO;
import es.onebox.mgmt.channels.suggestions.enums.SuggestionType;
import es.onebox.mgmt.common.MasterdataService;
import es.onebox.mgmt.datasources.ms.channel.dto.suggestions.ChannelSuggestion;
import es.onebox.mgmt.datasources.ms.channel.dto.suggestions.ChannelSuggestions;
import es.onebox.mgmt.datasources.ms.channel.repositories.ChannelsRepository;
import es.onebox.mgmt.datasources.ms.event.dto.event.Event;
import es.onebox.mgmt.datasources.ms.event.dto.session.Session;
import es.onebox.mgmt.datasources.ms.event.repository.EventsRepository;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChannelSuggestionsService {

    private final ChannelsRepository channelsRepository;
    private final EventsRepository eventsRepository;
    private final ChannelsHelper channelsHelper;
    private final MasterdataService masterdataService;

    @Autowired
    public ChannelSuggestionsService(ChannelsRepository channelsRepository, EventsRepository eventsRepository,
                                     ChannelsHelper channelsHelper, MasterdataService masterdataService) {
        this.channelsRepository = channelsRepository;
        this.eventsRepository = eventsRepository;
        this.channelsHelper = channelsHelper;
        this.masterdataService = masterdataService;
    }

    public ChannelSuggestionsResponseDTO getChannelSuggestions(Long channelId, ChannelSuggestionFilter filter) {
        channelsHelper.getAndCheckChannel(channelId);
        ChannelSuggestions channelSuggestions = channelsRepository.getSuggestions(channelId, ChannelSuggestionConverter.fillChannelSuggestionFilter(filter));

        ChannelSuggestionsResponseDTO channelSuggestionsResponseDTO = new ChannelSuggestionsResponseDTO();
        List<ChannelSuggestionDTO> channelSuggestionDTOList = new ArrayList<>();

        for (ChannelSuggestion channelSuggestion : channelSuggestions.getData()) {
            ChannelSuggestionDTO channelSuggestionDTO = ChannelSuggestionConverter.fromMsChannel(channelSuggestion, masterdataService.getCurrencies());
            channelSuggestionDTOList.add(channelSuggestionDTO);
        }

        channelSuggestionsResponseDTO.setData(channelSuggestionDTOList);
        channelSuggestionsResponseDTO.setMetadata(channelSuggestions.getMetadata());

        return channelSuggestionsResponseDTO;
    }

    public void addChannelSuggestion(Long channelId, SuggestionType sourceType, Long sourceId, CreateSuggestionTargetRequestDTO createSuggestionTargetRequestDTO){
        channelsHelper.getAndCheckChannel(channelId);
        Long sourceCurrency = getCurrency(sourceId, sourceType);
        createSuggestionTargetRequestDTO.forEach(target -> {
            Long targetCurrency = getCurrency(target.getId(), target.getType());
            if (!sourceCurrency.equals(targetCurrency)) {
                throw new OneboxRestException(ApiMgmtErrorCode.SUGGESTION_WITH_DIFFERENT_CURRENCY);
            }
        });
        channelsRepository.addChannelSuggestion(channelId, ChannelSuggestionConverter.toMs(sourceType), sourceId, createSuggestionTargetRequestDTO);
    }

    public void deleteChannelSuggestion(Long channelId, SuggestionType sourceType, Long sourceId, SuggestionType targetType, Long targetId) {
        channelsHelper.getAndCheckChannel(channelId);
        channelsRepository.deleteSuggestion(channelId, ChannelSuggestionConverter.toMs(sourceType), sourceId,
                ChannelSuggestionConverter.toMs(targetType), targetId);
    }

    public void deleteChannelSuggestions(Long channelId, SuggestionType sourceType, Long sourceId) {
        channelsHelper.getAndCheckChannel(channelId);
        channelsRepository.deleteSuggestions(channelId, ChannelSuggestionConverter.toMs(sourceType), sourceId);
    }

    private Long getCurrency(Long id, SuggestionType type) {
        Long currencyEvent = null;

        if (type == SuggestionType.EVENT) {
            Event event = eventsRepository.getEvent(id);
            if (event != null && event.getCurrencyId() != null) {
                return event.getCurrencyId();
            }
            throw new OneboxRestException(ApiMgmtErrorCode.EVENT_NOT_FOUND);
        }  else if (type == SuggestionType.SESSION) {
            Session session = eventsRepository.getSessionWithoutEventId(id);
            if (session != null) {
                Long eventId = session.getEventId();
                Event event = eventsRepository.getEvent(eventId);
                if (event != null && event.getCurrencyId() != null) {
                    return event.getCurrencyId();
                }
            }
            throw new OneboxRestException(ApiMgmtErrorCode.SESSION_NOT_FOUND);
        }
        return null;
    }
}
