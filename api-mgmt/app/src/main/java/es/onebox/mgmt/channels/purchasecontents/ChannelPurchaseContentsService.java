package es.onebox.mgmt.channels.purchasecontents;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.channels.ChannelsHelper;
import es.onebox.mgmt.channels.converter.ChannelConverter;
import es.onebox.mgmt.channels.purchasecontents.converter.ChannelPurchaseContentsConverter;
import es.onebox.mgmt.channels.purchasecontents.dto.ChannelPurchaseImageContentsDTO;
import es.onebox.mgmt.channels.purchasecontents.dto.ChannelPurchaseTextsContentsDTO;
import es.onebox.mgmt.channels.purchasecontents.enums.ChannelPurchaseImageContentType;
import es.onebox.mgmt.channels.purchasecontents.enums.ChannelPurchaseTextsContentType;
import es.onebox.mgmt.channels.utils.ChannelUtils;
import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.common.FileUtils;
import es.onebox.mgmt.common.MasterdataService;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelResponse;
import es.onebox.mgmt.datasources.ms.channel.dto.emailcontents.ChannelPurchaseContent;
import es.onebox.mgmt.datasources.ms.channel.repositories.ChannelContentsRepository;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
public class ChannelPurchaseContentsService {

    private MasterdataService masterdataService;
    private ChannelsHelper channelsHelper;
    private ChannelContentsRepository channelContentsRepository;

    @Autowired
    public ChannelPurchaseContentsService(MasterdataService masterdataService, ChannelsHelper channelsHelper,
                                          es.onebox.mgmt.datasources.ms.channel.repositories.ChannelContentsRepository channelContentsRepository) {
        this.masterdataService = masterdataService;
        this.channelsHelper = channelsHelper;
        this.channelContentsRepository = channelContentsRepository;
    }

    public ChannelPurchaseImageContentsDTO getEmailImage(Long channelId, String language, ChannelPurchaseImageContentType typeDTO) {
        if (language != null) {
            validateRequest(channelId, language);
        } else {
            validateChannel(channelId);
        }
        List<String> types = typeDTO != null ? Collections.singletonList(typeDTO.name()) : ChannelPurchaseImageContentType.getNamesList();
        List<ChannelPurchaseContent> response = channelContentsRepository.getChannelPurchaseContent(channelId, ConverterUtils.toLocale(language), types);
        return ChannelPurchaseContentsConverter.toDTOImages(response);
    }

    public void updateEmailImage(Long channelId, ChannelPurchaseImageContentsDTO body) {
        validateRequestImages(channelId, body);
        List<ChannelPurchaseContent> request = ChannelPurchaseContentsConverter.fromDTOImages(body);
        channelContentsRepository.updateChannelPurchaseContent(channelId, request);
    }


    public void deleteEmailImage(Long channelId, String language, ChannelPurchaseImageContentType type) {
        validateRequest(channelId, language);
        channelContentsRepository.deleteChannelPurchaseContent(channelId, ConverterUtils.toLocale(language), type.name());
    }

    public ChannelPurchaseTextsContentsDTO getEmailText(Long channelId, String language, ChannelPurchaseTextsContentType typeDTO) {
        if (language != null) {
            validateRequest(channelId, language);
        } else {
            validateChannel(channelId);
        }
        List<String> types = typeDTO != null ? Collections.singletonList(typeDTO.getKey()) : ChannelPurchaseTextsContentType.getNamesList();
        List<ChannelPurchaseContent> response = channelContentsRepository.getChannelPurchaseContent(channelId, ConverterUtils.toLocale(language), types);
        return ChannelPurchaseContentsConverter.toDTOText(response);
    }

    public void updateEmailTexts(Long channelId, ChannelPurchaseTextsContentsDTO body) {
        validateRequestText(channelId, body);
        List<ChannelPurchaseContent> request = ChannelPurchaseContentsConverter.fromDTOText(body);
        channelContentsRepository.updateChannelPurchaseContent(channelId, request);
    }

    private void validateRequestImages(final Long channelId, final ChannelPurchaseImageContentsDTO request) {
        ChannelResponse channelResponse = validateChannel(channelId);
        request.forEach(elem -> {
            validateLanguage(elem.getLanguage(), channelResponse);
            FileUtils.checkImage(elem.getImageBinary(), elem.getType(), elem.getType().name());
        });
    }

    private void validateRequestText(final Long channelId, final ChannelPurchaseTextsContentsDTO request) {
        ChannelResponse channelResponse = validateChannel(channelId);
        request.forEach(elem -> validateLanguage(elem.getLanguage(), channelResponse));
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

    private ChannelResponse validateChannel(final Long channelId) {
        return channelsHelper.getAndCheckChannel(channelId);
    }

}
