package es.onebox.mgmt.packs.service;

import es.onebox.mgmt.channels.ChannelsHelper;
import es.onebox.mgmt.channels.utils.ChannelsUrlUtils;
import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelResponse;
import es.onebox.mgmt.datasources.ms.channel.enums.ChannelSubtype;
import es.onebox.mgmt.datasources.ms.channel.enums.PackSubtype;
import es.onebox.mgmt.packs.dto.ticketcontents.PackUrlDTO;
import es.onebox.mgmt.packs.helper.PacksHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PackUrlsService {

    private final PacksValidationService packsValidationService;
    private final ChannelsHelper channelsHelper;
    private final PacksHelper packsHelper;

    @Value("${onebox.webapps.channels.url}")
    private String urlChannel;

    public PackUrlsService(PacksValidationService packsValidationService,
                           ChannelsHelper channelsHelper,
                           PacksHelper packsHelper) {
        this.packsValidationService = packsValidationService;
        this.channelsHelper = channelsHelper;
        this.packsHelper = packsHelper;
    }

    public List<PackUrlDTO> getChannelPackUrls(Long channelId, Long packId, PackSubtype packType) {
        packsValidationService.validateGetPackUrlsByChannel(channelId);

        if (PackSubtype.PROMOTER.equals(packType)) {
            packsHelper.getAndCheckPack(packId);
        } else {
            packsHelper.getAndCheckPack(channelId, packId);
        }

        ChannelResponse channel = channelsHelper.getAndCheckChannel(channelId);
        List<String> languages = channelsHelper.getAndCheckLanguages(channel);

        return languages.stream()
                .map(language -> getPackUrlDTO(packId, language, channel.getUrl(), channel.getSubtype()))
                .toList();
    }

    private PackUrlDTO getPackUrlDTO(Long packId, String language, String urlIntegration, ChannelSubtype subtype) {
        PackUrlDTO packUrlDTO = new PackUrlDTO();
        language = ConverterUtils.toLanguageTag(language);
        packUrlDTO.setDetailLink(ChannelsUrlUtils.buildUrlByChannelsPackDetail(urlChannel, urlIntegration, packId, language, subtype));
        packUrlDTO.setSelectLink(ChannelsUrlUtils.buildUrlByChannelsPack(urlChannel, urlIntegration, packId, language, subtype));
        packUrlDTO.setLanguage(language);
        return packUrlDTO;
    }


}
