package es.onebox.mgmt.loyaltypoints.channels.service;


import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.channels.ChannelsHelper;
import es.onebox.mgmt.channels.converter.ChannelConverter;
import es.onebox.mgmt.channels.dto.UpdateChannelVouchersRequestDTO;
import es.onebox.mgmt.common.MasterdataService;
import es.onebox.mgmt.loyaltypoints.channels.converter.LoyaltyPointsConverter;
import es.onebox.mgmt.loyaltypoints.channels.dto.ChannelLoyaltyPointsDTO;
import es.onebox.mgmt.loyaltypoints.channels.dto.UpdateChannelLoyaltyPointsDTO;
import es.onebox.mgmt.datasources.ms.channel.dto.*;
import es.onebox.mgmt.datasources.ms.channel.repositories.ChannelsRepository;
import es.onebox.mgmt.datasources.ms.entity.dto.Entity;
import es.onebox.mgmt.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.mgmt.exception.ApiMgmtChannelsErrorCode;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoyaltyPointsService {
    private final ChannelsRepository channelsRepository;
    private final ChannelsHelper channelsHelper;
    private final EntitiesRepository entitiesRepository;
    private final MasterdataService masterdataService;

    @Autowired
    public LoyaltyPointsService(ChannelsRepository channelsRepository, ChannelsHelper channelsHelper, EntitiesRepository entitiesRepository, MasterdataService masterdataService) {
        this.channelsRepository = channelsRepository;
        this.channelsHelper = channelsHelper;
        this.entitiesRepository = entitiesRepository;
        this.masterdataService = masterdataService;
    }

    public void updateLoyaltyPoints(Long channelId, UpdateChannelLoyaltyPointsDTO updateLoyaltyPointsDTO) {
        ChannelResponse channel = channelsHelper.getAndCheckChannel(channelId);
        Entity entity = entitiesRepository.getEntity(channel.getEntityId());

        if (BooleanUtils.isTrue(updateLoyaltyPointsDTO.getAllowLoyaltyPoints()) && BooleanUtils.isNotTrue(entity.getAllowLoyaltyPoints())) {
            throw new OneboxRestException(ApiMgmtChannelsErrorCode.LOYALTY_POINTS_MUST_BE_ENABLED);
        }

        if (BooleanUtils.isTrue(updateLoyaltyPointsDTO.getAllowLoyaltyPoints())) {
            UpdateChannelVouchersRequestDTO vouchersRequestDTO = new UpdateChannelVouchersRequestDTO();
            vouchersRequestDTO.setAllowRedeemVouchers(false);
            channelsRepository.updateChannelVouchersConfig(channelId, ChannelConverter.fromDTO(vouchersRequestDTO, masterdataService.getCurrencies()));
        }

        channelsRepository.updateChannelLoyaltyPoints(channelId, LoyaltyPointsConverter.fromDTO(updateLoyaltyPointsDTO));
    }

    public ChannelLoyaltyPointsDTO getLoyaltyPoints(Long channelId) {
        channelsHelper.getAndCheckChannel(channelId);
        return LoyaltyPointsConverter.toDTO(channelsRepository.getLoyaltyPoints(channelId));

    }
}