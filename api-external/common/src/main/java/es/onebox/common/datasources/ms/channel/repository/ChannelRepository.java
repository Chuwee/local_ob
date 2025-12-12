package es.onebox.common.datasources.ms.channel.repository;

import es.onebox.cache.annotation.Cached;
import es.onebox.cache.annotation.CachedArg;
import es.onebox.common.datasources.ms.channel.MsChannelDatasource;
import es.onebox.common.datasources.ms.channel.dto.ChannelDTO;
import es.onebox.common.datasources.ms.channel.dto.ChannelDeliveryMethodsDTO;
import es.onebox.common.datasources.ms.channel.dto.ChannelFormsResponse;
import es.onebox.common.datasources.ms.channel.dto.ChannelsResponse;
import es.onebox.common.datasources.ms.channel.dto.EmailServerDTO;
import es.onebox.common.datasources.ms.channel.dto.MsSaleRequestsFilter;
import es.onebox.common.datasources.ms.channel.dto.MsSaleRequestsResponseDTO;
import es.onebox.common.datasources.ms.channel.dto.config.ChannelConfigDTO;
import es.onebox.common.datasources.ms.channel.dto.config.ChannelEventSaleRestrictionResponse;
import es.onebox.common.datasources.ms.channel.filter.ChannelsFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class ChannelRepository {

    private final MsChannelDatasource msChannelDatasource;

    @Autowired
    public ChannelRepository(MsChannelDatasource msChannelDatasource) {
        this.msChannelDatasource = msChannelDatasource;
    }

    public ChannelsResponse getChannels (ChannelsFilter filter) {
        return msChannelDatasource.getChannels(filter);
    }

    public ChannelDTO getChannelNoCache (Long channelId) {
        return msChannelDatasource.getChannel(channelId);
    }

    @Cached(key = "ms_channel_get_channel", expires = 3 * 60)
    public ChannelDTO getChannel (@CachedArg Long channelId) {
        return msChannelDatasource.getChannel(channelId);
    }

    @Cached(key = "ms_channel_get_channel_config", expires = 3 * 60)
    public ChannelConfigDTO getChannelConfig(@CachedArg Long channelId) {
        return msChannelDatasource.getChannelConfig(channelId);
    }

    @Cached(key = "ms_channel_get_channel_delivery_methods", expires = 3 * 60)
    public ChannelDeliveryMethodsDTO getChannelDeliveryMethods(@CachedArg Long channelId) {
        return msChannelDatasource.getChannelDeliveryMethods(channelId);
    }

    @Cached(key = "ms_channel_get_channel_email_server_methods", expires = 3 * 60)
    public EmailServerDTO getChannelEmailServerConfiguration(@CachedArg Long channelId) {
        return msChannelDatasource.getChannelEmailServerConfiguration(channelId);
    }

    @Cached(key = "ms_channel_get_sale_request", expires = 10 * 60)
    public MsSaleRequestsResponseDTO getSaleRequests(@CachedArg Long channelId, @CachedArg Long eventId) {
        return msChannelDatasource.searchSaleRequests(channelId, eventId);
    }
    public MsSaleRequestsResponseDTO getSaleRequests(MsSaleRequestsFilter filter) {
        return msChannelDatasource.searchSaleRequests(filter);
    }

    public ChannelFormsResponse getChannelFormByType(Long channelId, String formType){
        return msChannelDatasource.getChannelFormByType(channelId, formType);
    }

    public Map<Integer, List<Integer>> getEventSaleRestrictions(Long channelId) {
        ChannelEventSaleRestrictionResponse saleRestrictions = msChannelDatasource.getEventSaleRestrictions(channelId);
        return saleRestrictions.getEventSaleRestrictions();
    }
}
