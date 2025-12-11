package es.onebox.mgmt.channels.faqs;

import es.onebox.mgmt.channels.ChannelsHelper;
import es.onebox.mgmt.channels.faqs.converter.ChannelFAQsConverter;
import es.onebox.mgmt.channels.faqs.dto.ChannelFAQDTO;
import es.onebox.mgmt.channels.faqs.dto.ChannelFAQUpsertRequestDTO;
import es.onebox.mgmt.channels.faqs.dto.ChannelFAQsDTO;
import es.onebox.mgmt.channels.utils.ChannelUtils;
import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelResponse;
import es.onebox.mgmt.datasources.ms.channel.dto.faqs.ChannelFAQs;
import es.onebox.mgmt.datasources.ms.channel.repositories.ChannelFAQsRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ChannelFAQsService {

    private final ChannelsHelper channelsHelper;
    private final ChannelFAQsRepository channelFAQsRepository;

    @Autowired
    public ChannelFAQsService(ChannelFAQsRepository channelFAQsRepository, ChannelsHelper channelsHelper) {
        this.channelsHelper = channelsHelper;
        this.channelFAQsRepository = channelFAQsRepository;
    }

    public ChannelFAQsDTO getChannelFAQs(final Long channelId, final String languageCode, List<String> tags, String q) {
        validateRequest(channelId, languageCode);
        ChannelFAQs faqs = channelFAQsRepository.getChannelFAQs(channelId, convertLanguage(languageCode), tags, q);
        return ChannelFAQsConverter.toDTO(faqs);
    }

    public ChannelFAQDTO getChannelFAQsItem(Long channelId, String key) {
        validatePortal(channelId);
        return ChannelFAQsConverter.toDTO(channelFAQsRepository.getChannelFAQsItem(channelId, key));
    }

    public void addChannelFAQ(Long channelId, ChannelFAQUpsertRequestDTO faq) {
        List<String> languagesRequest = faq.getValues().keySet().stream().toList();
        validateRequest(channelId, languagesRequest.toArray(String[]::new));
        channelFAQsRepository.addChannelFAQ(channelId, ChannelFAQsConverter.toMs(faq));
    }

    public void updateChannelFAQs(final Long channelId, final ChannelFAQUpsertRequestDTO faq, final String key) {
        List<String> languagesRequest = faq.getValues().keySet().stream().toList();
        validateRequest(channelId, languagesRequest.toArray(String[]::new));
        channelFAQsRepository.updateChannelFAQs(channelId, ChannelFAQsConverter.toMs(faq), key);
    }

    public void bulkUpdateChannelFAQs(Long channelId, final ChannelFAQsDTO faq) {
        if (CollectionUtils.isNotEmpty(faq)) {
            Set<String> languagesRequest = new HashSet<>();
            faq.forEach( faqEntry -> languagesRequest.addAll(faqEntry.getValues().keySet()));
            validateRequest(channelId, languagesRequest.toArray(String[]::new));
            channelFAQsRepository.bulkUpdateChannelFAQs(channelId, ChannelFAQsConverter.toMs(faq));
        }
    }

    private void validateRequest(final Long channelId, String... languageCodes) {
        ChannelResponse channelResponse = validatePortal(channelId);
        this.channelsHelper.validateLanguage(channelResponse, languageCodes);
    }

    private ChannelResponse validatePortal(Long channelId) {
        ChannelResponse channelResponse = channelsHelper.getAndCheckChannel(channelId);
        ChannelUtils.validateOBPortalOrMembers(channelResponse.getType());
        return channelResponse;
    }

    public void deleteChannelFAQ(Long channelId, String key) {
        channelFAQsRepository.deleteChannelFAQ(channelId, key);
    }

    private static String convertLanguage(final String languageCode) {
        if (languageCode == null) {
            return null;
        }
        return ConverterUtils.toLocale(languageCode);
    }

}
