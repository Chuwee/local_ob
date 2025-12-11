package es.onebox.mgmt.channels.ticketcontents;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.channels.ChannelsHelper;
import es.onebox.mgmt.channels.converter.ChannelConverter;
import es.onebox.mgmt.channels.ticketcontents.converter.ChannelTicketPDFContentConverter;
import es.onebox.mgmt.channels.ticketcontents.dto.ChannelTicketPDFImageContentsDTO;
import es.onebox.mgmt.channels.ticketcontents.enums.ChannelTicketPDFImageContentType;
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
public class ChannelTicketPDFContentService {

    private final ChannelsHelper channelsHelper;
    private final ChannelContentsRepository channelContentsRepository;
    private final MasterdataService masterdataService;

    @Autowired
    public ChannelTicketPDFContentService(ChannelContentsRepository channelContentsRepository, ChannelsHelper channelsHelper,
                                          MasterdataService masterdataService) {
        this.channelsHelper = channelsHelper;
        this.channelContentsRepository = channelContentsRepository;
        this.masterdataService = masterdataService;
    }

    public ChannelTicketPDFImageContentsDTO getTicketContent(final Long channelId, final String language, final ChannelTicketPDFImageContentType typeDTO) {
        if (language != null) {
            validateRequest(channelId, language);
        } else {
            validateChannel(channelId);
        }
        String type = typeDTO != null ? typeDTO.name() : null;
        List<ChannelTicketContent> response = channelContentsRepository.getChannelTicketPDFContent(channelId, ConverterUtils.toLocale(language), type);
        return ChannelTicketPDFContentConverter.toDTO(response);
    }

    public void updateTicketContent(final Long channelId, final ChannelTicketPDFImageContentsDTO body) {
        validateRequestImages(channelId, body);
        List<ChannelTicketContent> request = ChannelTicketPDFContentConverter.fromDTO(body);
        channelContentsRepository.updateChannelTicketPDFContent(channelId, request);
    }

    public void deleteTicketContent(final Long channelId, final String language, final ChannelTicketPDFImageContentType typeDTO) {
        validateRequest(channelId, language);
        channelContentsRepository.deleteChannelTicketPDFContent(channelId, ConverterUtils.toLocale(language), typeDTO.name());
    }

    private ChannelResponse validateChannel(final Long channelId) {
        return channelsHelper.getAndCheckChannel(channelId);
    }

    private void validateRequestImages(final Long channelId, final ChannelTicketPDFImageContentsDTO request) {
        ChannelResponse channelResponse = validateChannel(channelId);
        request.forEach(elem -> {
            validateLanguage(elem.getLanguage(), channelResponse);
            FileUtils.checkImage(elem.getImageBinary(), elem.getType(), elem.getType().name());
        });
    }

    private void validateRequest(final Long channelId, final String languageCode) {
        ChannelResponse channelResponse = validateChannel(channelId);
        validateLanguage(languageCode, channelResponse);
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
