package es.onebox.mgmt.channels.forms;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.mgmt.channels.ChannelsHelper;
import es.onebox.mgmt.channels.converter.ChannelConverter;
import es.onebox.mgmt.channels.forms.converter.ChannelAgreementConverter;
import es.onebox.mgmt.channels.forms.dto.ChannelAgreementDTO;
import es.onebox.mgmt.channels.forms.dto.CreateChannelAgreementDTO;
import es.onebox.mgmt.channels.forms.dto.UpdateChannelAgreementDTO;
import es.onebox.mgmt.channels.utils.ChannelUtils;
import es.onebox.mgmt.common.MasterdataService;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelResponse;
import es.onebox.mgmt.datasources.ms.channel.dto.contents.ChannelAgreement;
import es.onebox.mgmt.datasources.ms.channel.repositories.ChannelContentsRepository;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class ChannelAgreementsService {

    private final ChannelsHelper channelsHelper;
    private final ChannelContentsRepository channelContentsRepository;
    private final MasterdataService masterdataService;

    @Autowired
    public ChannelAgreementsService(ChannelsHelper channelsHelper, ChannelContentsRepository channelContentsRepository,
                                    MasterdataService masterdataService) {
        this.channelsHelper = channelsHelper;
        this.channelContentsRepository = channelContentsRepository;
        this.masterdataService = masterdataService;
    }

    public List<ChannelAgreementDTO> getChannelAgreements(Long channelId) {
        ChannelResponse channel = this.channelsHelper.getAndCheckChannel(channelId);
        ChannelUtils.validateOBPortalOrMembers(channel.getType());
        List<ChannelAgreement> response = channelContentsRepository.getChannelAgreements(channelId);
        return ChannelAgreementConverter.toDTO(response);
    }

    public IdDTO createChannelAgreement(Long channelId, CreateChannelAgreementDTO body) {
        ChannelResponse channel = this.channelsHelper.getAndCheckChannel(channelId);
        ChannelUtils.validateOBPortalOrMembers(channel.getType());
        validateLanguages(body.getTexts(), channel);
        ChannelAgreement out = ChannelAgreementConverter.toDTO(body);
        return channelContentsRepository.createChannelAgreement(channelId, out);
    }

    public void updateChannelAgreement(Long channelId, Long agreementId, UpdateChannelAgreementDTO body) {
        ChannelResponse channel = this.channelsHelper.getAndCheckChannel(channelId);
        ChannelUtils.validateOBPortalOrMembers(channel.getType());
        if (MapUtils.isNotEmpty(body.getTexts())) {
            validateLanguages(body.getTexts(), channel);
        }
        ChannelAgreement out = ChannelAgreementConverter.toDTO(body);
        channelContentsRepository.updateChannelAgreement(channelId, agreementId, out);
    }

    public void deleteChannelAgreement(Long channelId, Long agreementId) {
        ChannelResponse channel = this.channelsHelper.getAndCheckChannel(channelId);
        ChannelUtils.validateOBPortalOrMembers(channel.getType());
        channelContentsRepository.deleteChannelAgreement(channelId, agreementId);
    }

    private void validateLanguages(Map<String, String> texts, ChannelResponse channel) {
        ChannelUtils.validateChannelLanguages(channel);
        Set<String> languageCodes = ChannelConverter.fromLanguageIdToLanguageCode(channel.getLanguages().getSelectedLanguages(),
                masterdataService.getLanguagesByIds());
        checkLanguages(texts.keySet(), languageCodes);
    }

    private static void checkLanguages(Set<String> languageKeys, Set<String> channelLanguages) {
        for (String languageKey : languageKeys) {
            if (channelLanguages.stream().noneMatch(l -> l.equals(languageKey))) {
                throw new OneboxRestException(ApiMgmtErrorCode.INVALID_LANG,
                        "Invalid language " + languageKey, null);
            }
        }
    }
}
