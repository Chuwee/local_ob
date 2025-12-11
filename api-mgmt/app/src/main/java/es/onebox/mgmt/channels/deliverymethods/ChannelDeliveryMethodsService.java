package es.onebox.mgmt.channels.deliverymethods;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.security.Roles;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.mgmt.channels.deliverymethods.converter.ChannelDeliveryMethodsConverter;
import es.onebox.mgmt.channels.deliverymethods.dto.B2bExternalDownloadURLUpdateDTO;
import es.onebox.mgmt.channels.deliverymethods.dto.ChannelDeliveryMethodsDTO;
import es.onebox.mgmt.channels.deliverymethods.dto.ChannelDeliveryMethodsUpdateDTO;
import es.onebox.mgmt.channels.utils.ChannelUtils;
import es.onebox.mgmt.common.MasterdataService;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelResponse;
import es.onebox.mgmt.datasources.ms.channel.dto.deliverymethod.ChannelDeliveryMethods;
import es.onebox.mgmt.datasources.ms.channel.enums.ChannelStatus;
import es.onebox.mgmt.datasources.ms.channel.enums.ChannelSubtype;
import es.onebox.mgmt.datasources.ms.channel.repositories.ChannelsRepository;
import es.onebox.mgmt.datasources.ms.entity.dto.Currency;
import es.onebox.mgmt.datasources.ms.entity.dto.Operator;
import es.onebox.mgmt.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.mgmt.exception.ApiMgmtChannelsErrorCode;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.security.SecurityManager;
import es.onebox.mgmt.security.SecurityUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ChannelDeliveryMethodsService {

    private final ChannelsRepository channelsRepository;
    private final SecurityManager securityManager;
    private final EntitiesRepository entitiesRepository;
    private final MasterdataService masterdataService;

    @Autowired
    public ChannelDeliveryMethodsService(ChannelsRepository channelsRepository, SecurityManager securityManager, EntitiesRepository entitiesRepository, MasterdataService masterdataService) {
        this.channelsRepository = channelsRepository;
        this.securityManager = securityManager;
        this.entitiesRepository = entitiesRepository;
        this.masterdataService = masterdataService;
    }


    public ChannelDeliveryMethodsDTO getById(Long channelId) {
        ChannelResponse channel = validateChannel(channelId);
        ChannelDeliveryMethods response = channelsRepository.getChannelDeliveryMethods(channelId);
        if (response.getB2bExternalDownloadURL()!= null && response.getB2bExternalDownloadURL().getTargetChannelId() != null){
            channel = channelsRepository.getChannel(response.getB2bExternalDownloadURL().getTargetChannelId(), true);
        }

        //TODO: remove defaultCurrency Migration multicurrency
        Operator operator = entitiesRepository.getCachedOperator(channel.getEntityId());
        String currencyDefault = null;
        List<Currency> currencies = masterdataService.getCurrencies();
        if (BooleanUtils.isTrue(operator.getUseMultiCurrency())) {
            currencyDefault =  operator.getCurrencies().getDefaultCurrency();
        } else if (operator.getCurrency() != null) {
            currencyDefault = currencies.stream()
                    .filter(curr -> curr.getId().equals(operator.getCurrency().getId().longValue()))
                    .map(Currency::getCode)
                    .findFirst()
                    .orElse(null);
        }
        return ChannelDeliveryMethodsConverter.fromMs(response, channel, currencies, currencyDefault);

    }

    public void updateByChannelId(Long channelId, ChannelDeliveryMethodsUpdateDTO request) {
        ChannelResponse channel = validateChannel(channelId);
        validateB2bExternalDownload(channel, request.getB2bExternalDownloadUrlUpdate());

        ChannelDeliveryMethods msRequest = ChannelDeliveryMethodsConverter.toMs(request, masterdataService.getCurrencies());
        channelsRepository.updateChannelDeliveryMethods(channelId, msRequest);
    }


    private ChannelResponse validateChannel(Long channelId) {
        ChannelResponse channelResponse = Optional.ofNullable(channelsRepository.getChannel(channelId))
                .orElseThrow(() -> new OneboxRestException(ApiMgmtErrorCode.CHANNEL_NOT_FOUND));
        securityManager.checkEntityAccessible(channelResponse.getEntityId());
        return channelResponse;
    }

    private void validateB2bExternalDownload(ChannelResponse channel, B2bExternalDownloadURLUpdateDTO requestPart) {
        if (requestPart != null) {
            if (!SecurityUtils.hasAnyRole(Roles.ROLE_OPR_MGR)){
                throw new OneboxRestException(ApiMgmtErrorCode.FORBIDDEN_RESOURCE);
            }
            if(CommonUtils.isTrue(requestPart.getEnabled()) && requestPart.getTargetChannelId() == null) {
                throw new OneboxRestException(ApiMgmtChannelsErrorCode.ENABLED_B2B_EXTERNAL_DOWNLOAD_WITHOUT_TARGET_CHANNEL);
            }
            if (!ChannelUtils.isB2bPortal(channel)) {
                throw new OneboxRestException(ApiMgmtChannelsErrorCode.B2B_EXTERNAL_DOWNLOAD_URL_ONLY_ALLOWED_FOR_B2B_CHANNELS);
            }
            if (requestPart.getTargetChannelId() != null) {
                ChannelResponse targetChannel = validateChannel(requestPart.getTargetChannelId());
                if (!Objects.equals(channel.getEntityId(), targetChannel.getEntityId())) {
                    throw new OneboxRestException(ApiMgmtChannelsErrorCode.TARGET_CHANNEL_INVALID);
                }
                if (!ChannelSubtype.PORTAL_WEB.equals(targetChannel.getSubtype())) {
                    throw new OneboxRestException(ApiMgmtChannelsErrorCode.TARGET_CHANNEL_TYPE_NOT_SUITABLE_FOR_EXTERNAL_DOWNLOAD_URL);
                }
                if (!ChannelStatus.ACTIVE.equals(targetChannel.getStatus())) {
                    throw new OneboxRestException((ApiMgmtChannelsErrorCode.CHANNEL_STATUS_INVALID));
                }
            }
        }
    }

}
