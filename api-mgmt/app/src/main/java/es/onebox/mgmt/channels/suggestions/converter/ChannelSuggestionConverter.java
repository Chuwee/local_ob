package es.onebox.mgmt.channels.suggestions.converter;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.channels.suggestions.controller.ChannelSuggestionFilter;
import es.onebox.mgmt.channels.suggestions.dto.ChannelSuggestionDTO;
import es.onebox.mgmt.channels.suggestions.dto.SuggestionDTO;
import es.onebox.mgmt.channels.suggestions.enums.SuggestionType;
import es.onebox.mgmt.currencies.CurrenciesUtils;
import es.onebox.mgmt.datasources.ms.channel.dto.suggestions.ChannelSuggestion;
import es.onebox.mgmt.datasources.ms.channel.dto.suggestions.ChannelSuggestionMsFilter;
import es.onebox.mgmt.datasources.ms.channel.dto.suggestions.Suggestion;
import es.onebox.mgmt.datasources.ms.entity.dto.Currency;
import es.onebox.mgmt.exception.ApiMgmtChannelsErrorCode;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class ChannelSuggestionConverter {

    private ChannelSuggestionConverter() {

    }

    public static es.onebox.mgmt.datasources.ms.channel.enums.SuggestionType toMs(SuggestionType suggestionType) {
        if (suggestionType == null) {
            return null;
        }
        switch (suggestionType) {
            case EVENT:
                return es.onebox.mgmt.datasources.ms.channel.enums.SuggestionType.EVENT;
            case SESSION:
                return es.onebox.mgmt.datasources.ms.channel.enums.SuggestionType.SESSION;
            default:
                throw new OneboxRestException(ApiMgmtChannelsErrorCode.CHANNEL_SUGGESTION_TYPE_NOT_FOUND,
                        "Unknown suggestionType provided: " + suggestionType, null);
        }
    }

    public static ChannelSuggestionMsFilter fillChannelSuggestionFilter(ChannelSuggestionFilter channelSuggestionFilter) {
        ChannelSuggestionMsFilter filter = new ChannelSuggestionMsFilter();

        if (StringUtils.isNotBlank(channelSuggestionFilter.getQ())) {
            filter.setQ(channelSuggestionFilter.getQ());
        }
        if (CollectionUtils.isNotEmpty(channelSuggestionFilter.getSessionIds())) {
            filter.setSessionIds(channelSuggestionFilter.getSessionIds());
        }
        if (CollectionUtils.isNotEmpty(channelSuggestionFilter.getEventIds())) {
            filter.setEventIds(channelSuggestionFilter.getEventIds());
        }
        if (channelSuggestionFilter.getPublished() != null) {
            filter.setPublished(channelSuggestionFilter.getPublished());
        }
        if (channelSuggestionFilter.getSourceType() != null) {
            filter.setSourceType(channelSuggestionFilter.getSourceType());
        }

        filter.setLimit(channelSuggestionFilter.getLimit());
        filter.setOffset(channelSuggestionFilter.getOffset());
        return filter;
    }

    public static ChannelSuggestionDTO fromMsChannel(ChannelSuggestion channelSuggestion, List<Currency> currencies) {
        ChannelSuggestionDTO channelSuggestionDTO = new ChannelSuggestionDTO();
        List<SuggestionDTO> suggestionDTOList = new ArrayList<>();
        channelSuggestionDTO.setSource(new SuggestionDTO());
        channelSuggestionDTO.setTargets(suggestionDTOList);
        toSuggestionSourceDTO(channelSuggestionDTO.getSource(), channelSuggestion.getSource(), currencies);
        toSuggestionsTargetsDTO(channelSuggestionDTO.getTargets(), channelSuggestion.getTargets(), currencies);
        return channelSuggestionDTO;
    }

    private static void toSuggestionSourceDTO(SuggestionDTO sourceDTO, Suggestion sourceFromMs, List<Currency> currencies) {
        sourceDTO.setId(sourceFromMs.getId());
        sourceDTO.setSuggestionType(SuggestionType.valueOf(sourceFromMs.getSuggestionType().name()));
        sourceDTO.setName(sourceFromMs.getName());
        sourceDTO.setParentName(sourceFromMs.getParentName());
        sourceDTO.setStartDate(sourceFromMs.getStartDate());
        if(sourceFromMs.getCurrencyId() != null){
            sourceDTO.setCurrency(CurrenciesUtils.getCurrencyCode(currencies, sourceFromMs.getCurrencyId().longValue()));
        }
    }

    private static void toSuggestionsTargetsDTO(List<SuggestionDTO> suggestionsTargetsDTO, List<Suggestion> targetsFromMs, List<Currency> currencies) {
        for (Suggestion suggestionFromMs : targetsFromMs) {
            SuggestionDTO suggestionDTO = new SuggestionDTO();
            suggestionDTO.setId(suggestionFromMs.getId());
            suggestionDTO.setSuggestionType(SuggestionType.valueOf(suggestionFromMs.getSuggestionType().name()));
            suggestionDTO.setName(suggestionFromMs.getName());
            suggestionDTO.setParentName(suggestionFromMs.getParentName());
            suggestionDTO.setStartDate(suggestionFromMs.getStartDate());
            if(suggestionFromMs.getCurrencyId() != null){
                suggestionDTO.setCurrency(CurrenciesUtils.getCurrencyCode(currencies, suggestionFromMs.getCurrencyId().longValue()));
            }
            suggestionsTargetsDTO.add(suggestionDTO);
        }
    }

}
