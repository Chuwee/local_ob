package es.onebox.mgmt.channels.ticketcontents;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.channels.ChannelsHelper;
import es.onebox.mgmt.channels.converter.ChannelConverter;
import es.onebox.mgmt.channels.ticketcontents.converter.ChannelTicketPrinterContentConverter;
import es.onebox.mgmt.channels.ticketcontents.dto.ChannelTicketPrinterImageContentsDTO;
import es.onebox.mgmt.channels.ticketcontents.enums.ChannelTicketPrinterImageContentType;
import es.onebox.mgmt.channels.utils.ChannelUtils;
import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.common.FileUtils;
import es.onebox.mgmt.common.MasterdataService;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelResponse;
import es.onebox.mgmt.datasources.ms.channel.dto.ticketcontent.ChannelTicketContent;
import es.onebox.mgmt.datasources.ms.channel.enums.ChannelType;
import es.onebox.mgmt.datasources.ms.channel.repositories.ChannelContentsRepository;
import es.onebox.mgmt.exception.ApiMgmtChannelsErrorCode;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class ChannelTicketPrinterContentService {

    private final ChannelsHelper channelsHelper;
    private final ChannelContentsRepository channelContentsRepository;
    private final MasterdataService masterdataService;

    @Autowired
    public ChannelTicketPrinterContentService(ChannelContentsRepository channelContentsRepository, ChannelsHelper channelsHelper,
                                              MasterdataService masterdataService) {
        this.channelsHelper = channelsHelper;
        this.channelContentsRepository = channelContentsRepository;
        this.masterdataService = masterdataService;
    }

    public ChannelTicketPrinterImageContentsDTO getTicketContent(final Long channelId, final String language, final ChannelTicketPrinterImageContentType typeDTO) {
        if (language != null) {
            validateRequest(channelId, language);
        } else {
            validateChannel(channelId);
        }
        String type = typeDTO != null ? typeDTO.name() : null;
        List<ChannelTicketContent> response = channelContentsRepository.getChannelTicketPrinterContent(channelId, ConverterUtils.toLocale(language), type);
        return ChannelTicketPrinterContentConverter.toDTO(response);
    }

    public void updateTicketContent(final Long channelId, final ChannelTicketPrinterImageContentsDTO body) {
        validateRequestImages(channelId, body);
        List<ChannelTicketContent> request = ChannelTicketPrinterContentConverter.fromDTO(body);
        channelContentsRepository.updateChannelTicketPrinterContent(channelId, request);
    }

    public void deleteTicketContent(final Long channelId, final String language, final ChannelTicketPrinterImageContentType typeDTO) {
        validateRequest(channelId, language);
        channelContentsRepository.deleteChannelTicketPrinterContent(channelId, ConverterUtils.toLocale(language), typeDTO.name());
    }

    private ChannelResponse validateChannel(final Long channelId) {
        ChannelResponse channelResponse = channelsHelper.getAndCheckChannel(channelId);
        if (!ChannelType.OB_BOX_OFFICE.equals(channelResponse.getType())) {
            throw new OneboxRestException(ApiMgmtChannelsErrorCode.CHANNEL_UNSUPPORTED_OPERATION);
        }
        return channelResponse;
    }

    private void validateRequestImages(final Long channelId, final ChannelTicketPrinterImageContentsDTO request) {
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
