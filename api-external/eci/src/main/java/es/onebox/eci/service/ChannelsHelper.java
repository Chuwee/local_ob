package es.onebox.eci.service;

import es.onebox.cache.annotation.Cached;
import es.onebox.cache.annotation.CachedArg;
import es.onebox.common.auth.service.AuthenticationService;
import es.onebox.common.datasources.ms.channel.dto.ChannelDTO;
import es.onebox.common.datasources.ms.channel.filter.ChannelsFilter;
import es.onebox.common.datasources.ms.channel.repository.ChannelRepository;
import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.core.exception.ExceptionBuilder;
import es.onebox.eci.ticketsales.dto.BrandType;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChannelsHelper {

    private final ChannelRepository channelRepository;

    @Autowired
    public ChannelsHelper(ChannelRepository channelRepository) {
        this.channelRepository = channelRepository;
    }

    public List<Long> getChannelIds(String channelIdentifier) {
        List<Long> channelIds = new ArrayList<>();
        if(BrandType.findByIdentifier(channelIdentifier) != null) {
            List<ChannelDTO> channels = getChannelDetailsByLoggedUser(BrandType.findByIdentifier(channelIdentifier));
            if (CollectionUtils.isNotEmpty(channels)) {
                channelIds = channels.stream().map(ChannelDTO::getId)
                        .collect(Collectors.toList());
            }
        }else{
            try{
                channelIds = Collections.singletonList(Long.valueOf(channelIdentifier));
            }catch(NumberFormatException e) {
                throw ExceptionBuilder.build(ApiExternalErrorCode.BAD_REQUEST_PARAMETER);
            }
        }
        return channelIds;
    }

    public List<ChannelDTO> getChannelDetails(String channelIdentifier) {
        List<ChannelDTO> channelDetails;
        if (BrandType.findByIdentifier(channelIdentifier) != null) {
            channelDetails = getChannelDetailsByLoggedUser(BrandType.findByIdentifier(channelIdentifier));
        }else{
            try {
                channelDetails = Collections.singletonList(channelRepository.getChannel(Long.valueOf(channelIdentifier)));
            }catch(NumberFormatException e){
                throw ExceptionBuilder.build(ApiExternalErrorCode.BAD_REQUEST_PARAMETER);
            }
        }
        return channelDetails;
    }

    public List<ChannelDTO> getChannelDetailsByLoggedUser(BrandType brandType) {
        if (brandType == null) {
            return new ArrayList<>();
        }
        List<ChannelDTO> channels;
        if (AuthenticationService.getAuthDataOperatorId().equals(AuthenticationService.getAuthDataEntityId())) {
            channels = getChannels(AuthenticationService.getAuthDataOperatorId(), null);
        } else {
            channels = getChannels(AuthenticationService.getAuthDataOperatorId(), AuthenticationService.getAuthDataEntityId());
        }
        List<ChannelDTO> channelDetails = new ArrayList<>();
        if(channels != null && CollectionUtils.isNotEmpty(channels)) {
            channelDetails = channels.stream()
                    .map(channel -> channelRepository.getChannel(channel.getId()))
                    .filter(channelDetail -> brandType.getValue().equals(channelDetail.getExternalReference()))
                    .collect(Collectors.toList());
        }
        return channelDetails;
    }

    @Cached(key = "api_external_eci_get_channels", expires = 10 * 60)
    public List<ChannelDTO> getChannels(@CachedArg Long operatorId, @CachedArg Long entityId) {
        if(operatorId == null && entityId == null) {
            return new ArrayList<>();
        }
        ChannelsFilter filter = new ChannelsFilter();
        if(operatorId != null) {
            filter.setOperatorId(operatorId.intValue());
        }
        if(entityId != null) {
            filter.setEntityId(entityId.intValue());
        }
        return channelRepository.getChannels(filter).getData();
    }
}
