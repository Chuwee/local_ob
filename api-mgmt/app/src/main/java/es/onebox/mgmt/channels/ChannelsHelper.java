package es.onebox.mgmt.channels;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.security.Roles;
import es.onebox.core.serializer.dto.common.CodeNameDTO;
import es.onebox.mgmt.channels.converter.ChannelConverter;
import es.onebox.mgmt.channels.dto.ChannelDetailDTO;
import es.onebox.mgmt.channels.utils.ChannelUtils;
import es.onebox.mgmt.channels.utils.ChannelsUrlUtils;
import es.onebox.mgmt.common.MasterdataService;
import es.onebox.mgmt.datasources.ms.channel.dto.Channel;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelFilter;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelResponse;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelsResponse;
import es.onebox.mgmt.datasources.ms.channel.enums.ChannelStatus;
import es.onebox.mgmt.datasources.ms.channel.repositories.ChannelsRepository;
import es.onebox.mgmt.datasources.ms.entity.dto.Currency;
import es.onebox.mgmt.datasources.ms.entity.dto.Operator;
import es.onebox.mgmt.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.mgmt.exception.ApiMgmtChannelsErrorCode;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.security.SecurityManager;
import es.onebox.mgmt.security.SecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ChannelsHelper {


    private final SecurityManager securityManager;
    private final ChannelsRepository channelsRepository;
    private final MasterdataService masterdataService;
    private final EntitiesRepository entitiesRepository;

    @Value("${onebox.webapps.channels.url}")
    private String urlChannel;

    @Autowired
    public ChannelsHelper(SecurityManager securityManager, ChannelsRepository channelsRepository,
                          MasterdataService masterdataService, EntitiesRepository entitiesRepository) {
        this.securityManager = securityManager;
        this.channelsRepository = channelsRepository;
        this.masterdataService = masterdataService;
        this.entitiesRepository = entitiesRepository;
    }

    public ChannelResponse getAndCheckChannel(Long channelId) {
        ChannelResponse channelResponse = channelsRepository.getChannel(channelId);
        if (channelResponse == null || channelResponse.getStatus() == ChannelStatus.DELETED) {
            throw new OneboxRestException(ApiMgmtErrorCode.CHANNEL_NOT_FOUND);
        }
        securityManager.checkEntityAccessibleWithVisibility(channelResponse.getEntityId());

        return channelResponse;
    }

    public ChannelsResponse getAndCheckChannels(List<Long> channelIds) {
        ChannelFilter filter = new ChannelFilter();
        filter.setChannelIds(channelIds);
        filter.setLimit((long) channelIds.size());

        List<ChannelStatus> notDeletedStatuses = Arrays.stream(ChannelStatus.values()).collect(Collectors.toList());
        notDeletedStatuses.remove(ChannelStatus.DELETED);
        filter.setStatus(notDeletedStatuses);

        ChannelsResponse channelsResponse = channelsRepository.getChannels(SecurityUtils.getUserOperatorId(), filter);
        for (Channel channel : channelsResponse.getData()) {
            if (channel == null || channel.getStatus() == ChannelStatus.DELETED) {
                throw new OneboxRestException(ApiMgmtErrorCode.CHANNEL_NOT_FOUND);
            }
            securityManager.checkEntityAccessibleWithVisibility(channel.getEntityId());
        }
        return channelsResponse;
    }

    public ChannelDetailDTO getChannel(Long channelId, Boolean hasActivePromotion) {
        ChannelResponse channelResponse = getAndCheckChannel(channelId);
        List<CodeNameDTO> currencyIds = getCurrenciesFromResponse(channelResponse);

        return ChannelConverter.fromMsChannelsResponse(channelResponse, masterdataService.getLanguagesByIds(), urlChannel, currencyIds, hasActivePromotion);
    }

    @NotNull
    private List<CodeNameDTO> getCurrenciesFromResponse(ChannelResponse channelResponse) {
        List<CodeNameDTO> currencyIds = new ArrayList<>();
        Operator operator = entitiesRepository.getCachedOperator(SecurityUtils.getUserOperatorId());
        if (CollectionUtils.isNotEmpty(channelResponse.getCurrencies()) &&
                (BooleanUtils.isTrue(operator.getUseMultiCurrency()) || SecurityUtils.hasAnyRole(Roles.ROLE_SYS_ANS, Roles.ROLE_SYS_MGR))) {
            List<Currency> allCurrencies = masterdataService.getCurrencies();
            allCurrencies.forEach(c ->
                    channelResponse.getCurrencies().forEach(cc -> {
                        if (c.getId().equals(cc)) {
                            CodeNameDTO codeNameDTO = new CodeNameDTO();
                            codeNameDTO.setCode(c.getCode());
                            codeNameDTO.setName(c.getDescription());
                            currencyIds.add(codeNameDTO);
                        }
                    })
            );
        }
        return currencyIds;
    }

    public String buildChannelUrl(String channelUrl) {
        if (channelUrl == null) {
            return null;
        }
        return ChannelsUrlUtils.buildUrlByChannels(urlChannel, channelUrl);
    }

    public void validateLanguage(ChannelResponse channel, String... languageCodes) {
        List<String> languages = Arrays.stream(languageCodes).filter(Objects::nonNull).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(languages)) {
            return;
        }
        ChannelUtils.validateChannelLanguages(channel);
        Set<String> channelLanguages = ChannelConverter.fromLanguageIdToLanguageCode(channel.getLanguages().getSelectedLanguages(),
                masterdataService.getLanguagesByIds());
        if (!channelLanguages.containsAll(languages)) {
            throw new OneboxRestException(ApiMgmtChannelsErrorCode.CHANNEL_UNSUPPORTED_LANGUAGE);
        }
    }

    public List<String> getAndCheckLanguages(ChannelResponse channel) {
        Map<Long, String> masterLanguages = masterdataService.getLanguagesByIds();
        if (channel.getLanguages() == null || CollectionUtils.isEmpty(channel.getLanguages().getSelectedLanguages())) {
            throw new OneboxRestException(ApiMgmtChannelsErrorCode.CHANNEL_ACTIVE_LANGUAGE_MANDATORY);
        }
        return channel.getLanguages().getSelectedLanguages().stream()
                .filter(masterLanguages::containsKey)
                .map(masterLanguages::get)
                .toList();
    }

}
