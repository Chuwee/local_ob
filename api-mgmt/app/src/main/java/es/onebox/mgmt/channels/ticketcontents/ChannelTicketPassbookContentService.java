package es.onebox.mgmt.channels.ticketcontents;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.channels.ChannelsHelper;
import es.onebox.mgmt.channels.converter.ChannelConverter;
import es.onebox.mgmt.channels.ticketcontents.converter.ChannelTicketPassbookContentConverter;
import es.onebox.mgmt.channels.ticketcontents.dto.ChannelTicketPassbookImageContentsDTO;
import es.onebox.mgmt.channels.ticketcontents.dto.ChannelTicketPassbookTextContentsDTO;
import es.onebox.mgmt.channels.ticketcontents.enums.ChannelTicketPassbookImageContentType;
import es.onebox.mgmt.channels.ticketcontents.enums.ChannelTicketPassbookTextContentType;
import es.onebox.mgmt.channels.utils.ChannelUtils;
import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.common.FileUtils;
import es.onebox.mgmt.common.MasterdataService;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelResponse;
import es.onebox.mgmt.datasources.ms.channel.dto.ticketcontent.ChannelTicketContent;
import es.onebox.mgmt.datasources.ms.channel.repositories.ChannelContentsRepository;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class ChannelTicketPassbookContentService {

    private final ChannelsHelper channelsHelper;
    private final ChannelContentsRepository channelContentsRepository;
    private final MasterdataService masterdataService;

    @Autowired
    public ChannelTicketPassbookContentService(ChannelContentsRepository channelContentsRepository, ChannelsHelper channelsHelper,
                                               MasterdataService masterdataService) {
        this.channelsHelper = channelsHelper;
        this.channelContentsRepository = channelContentsRepository;
        this.masterdataService = masterdataService;
    }

    public ChannelTicketPassbookImageContentsDTO getTicketImageContent(final Long channelId, final String language, final ChannelTicketPassbookImageContentType typeDTO) {
        validateRequest(channelId, language);
        String type = typeDTO != null ? typeDTO.name() : null;
        List<ChannelTicketContent> response = channelContentsRepository.getChannelTicketPassbookContent(channelId, ConverterUtils.toLocale(language), type);
        return ChannelTicketPassbookContentConverter.toDTOImages(response);
    }

    public void updateTicketImageContent(final Long channelId, final ChannelTicketPassbookImageContentsDTO body) {
        validateRequestImages(channelId, body);
        List<ChannelTicketContent> request = ChannelTicketPassbookContentConverter.fromDTOImages(body);
        channelContentsRepository.updateChannelTicketPassbookContent(channelId, request);
    }

    public void deleteTicketImageContent(final Long channelId, final String language, final ChannelTicketPassbookImageContentType typeDTO) {
        validateRequest(channelId, language);
        channelContentsRepository.deleteChannelTicketPassbookContent(channelId, ConverterUtils.toLocale(language), typeDTO.name());
    }

    public ChannelTicketPassbookTextContentsDTO getTicketTextContent(final Long channelId, final String language, final ChannelTicketPassbookTextContentType typeDTO) {
        validateRequest(channelId, language);
        String type = typeDTO != null ? typeDTO.name() : null;
        List<ChannelTicketContent> response = channelContentsRepository.getChannelTicketPassbookContent(channelId, ConverterUtils.toLocale(language), type);
        return ChannelTicketPassbookContentConverter.toDTOText(response);
    }

    public void updateTicketTextContent(final Long channelId, final ChannelTicketPassbookTextContentsDTO body) {
        validateChannel(channelId);
        List<ChannelTicketContent> request = ChannelTicketPassbookContentConverter.fromDTOText(body);
        channelContentsRepository.updateChannelTicketPassbookContent(channelId, request);
    }

    private ChannelResponse validateChannel(final Long channelId) {
        return channelsHelper.getAndCheckChannel(channelId);
    }

    private void validateRequestImages(final Long channelId, final ChannelTicketPassbookImageContentsDTO request) {
        ChannelResponse channelResponse = validateChannel(channelId);
        request.forEach(elem -> {
            validateLanguage(elem.getLanguage(), channelResponse);
            FileUtils.checkImage(elem.getImageBinary(), elem.getType(), elem.getType().name());
        });
    }

    private void validateRequest(final Long channelId, final String languageCode) {
        ChannelResponse channelResponse = validateChannel(channelId);
        if (languageCode != null) {
            validateLanguage(languageCode, channelResponse);
        }
    }

    private void validateLanguage(String languageCode, ChannelResponse channel) {
        ChannelUtils.validateChannelLanguages(channel);
        Set<String> languageCodes = ChannelConverter.fromLanguageIdToLanguageCode(channel.getLanguages().getSelectedLanguages(),
                masterdataService.getLanguagesByIds());
        if (languageCodes.stream().noneMatch(l -> l.equals(languageCode))) {
            throw new OneboxRestException(ApiMgmtErrorCode.INVALID_LANG, "Invalid language " + languageCode, null);
        }
    }
}
