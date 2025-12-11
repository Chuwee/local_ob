package es.onebox.mgmt.events.eventchannel.b2b.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelResponse;
import es.onebox.mgmt.datasources.ms.channel.enums.ChannelSubtype;
import es.onebox.mgmt.datasources.ms.channel.repositories.ChannelsRepository;
import es.onebox.mgmt.datasources.ms.entity.dto.Entity;
import es.onebox.mgmt.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.mgmt.datasources.ms.event.dto.b2b.B2BSeatPublishingConfig;
import es.onebox.mgmt.datasources.ms.event.repository.B2BSeatPublishingConfigRepository;
import es.onebox.mgmt.datasources.ms.venue.dto.template.PriceType;
import es.onebox.mgmt.datasources.ms.venue.dto.template.Quota;
import es.onebox.mgmt.datasources.ms.venue.repository.VenuesRepository;
import es.onebox.mgmt.events.eventchannel.b2b.converter.B2BPublishingConfigConverter;
import es.onebox.mgmt.events.eventchannel.b2b.dto.B2BSeatPublishingConfigDTO;
import es.onebox.mgmt.events.eventchannel.b2b.dto.B2BSeatPublishingConfigRequestDTO;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.security.SecurityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class B2BPublishingConfigService {

    private final VenuesRepository venuesRepository;
    private final SecurityManager securityManager;
    private final EntitiesRepository entitiesRepository;
    private final ChannelsRepository channelsRepository;
    private final B2BSeatPublishingConfigRepository b2BSeatPublishingConfigRepository;

    @Autowired
    public B2BPublishingConfigService(VenuesRepository venuesRepository, SecurityManager securityManager, EntitiesRepository entitiesRepository, ChannelsRepository channelsRepository, B2BSeatPublishingConfigRepository b2BSeatPublishingConfigRepository) {
        this.venuesRepository = venuesRepository;
        this.securityManager = securityManager;
        this.entitiesRepository = entitiesRepository;
        this.channelsRepository = channelsRepository;
        this.b2BSeatPublishingConfigRepository = b2BSeatPublishingConfigRepository;
    }

    public B2BSeatPublishingConfigDTO getConfig(Long eventId, Long channelId, Long venueTemplateId) {

        ChannelResponse channelResponse = channelsRepository.getChannel(channelId);
        Entity entity = entitiesRepository.getEntity(channelResponse.getEntityId());
        channelsRepository.getChannelDeliveryMethods(channelId);
        validatePermissionToPublishingSeat(channelResponse, entity);
        B2BSeatPublishingConfig b2BSeatPublishingConfig = b2BSeatPublishingConfigRepository.getConfig(eventId, channelId, venueTemplateId);
        List<PriceType> priceTypes = venuesRepository.getPriceTypes(venueTemplateId);
        List<Quota> quotas = venuesRepository.getQuotas(venueTemplateId);
        return B2BPublishingConfigConverter.fromMsEvent(b2BSeatPublishingConfig, priceTypes, quotas);
    }

    private void validatePermissionToPublishingSeat(ChannelResponse channelResponse, Entity entity) {

        securityManager.checkEntityAccessible(channelResponse.getEntityId());
        if (!isB2BChannel(channelResponse)) {
            throwOneboxRestException(ApiMgmtErrorCode.CHANNEL_TYPE_IS_NOT_B2B);
        }

        if (!isB2BModuleEnabled(entity)) {
            throwOneboxRestException(ApiMgmtErrorCode.ENTITY_MODULE_B2B_DISABLED);
        }

        if (!isB2BPublishingAllowed(channelResponse, entity)) {
            throwOneboxRestException(ApiMgmtErrorCode.ALLOW_B2B_PUBLISHING_IS_NOT_ENABLED);
        }
    }

    private boolean isB2BChannel(ChannelResponse channelResponse) {
        return channelResponse != null && ChannelSubtype.PORTAL_B2B.equals(channelResponse.getSubtype());
    }

    private boolean isB2BModuleEnabled(Entity entity) {
        return entity != null && Boolean.TRUE.equals(entity.getModuleB2BEnabled());
    }

    private boolean isB2BPublishingAllowed(ChannelResponse channelResponse, Entity entity) {
        return channelResponse != null && entity != null
                && Boolean.TRUE.equals(channelResponse.getAllowB2BPublishing())
                && Boolean.TRUE.equals(entity.getAllowB2BPublishing());
    }


    private void throwOneboxRestException(ApiMgmtErrorCode errorCode) {
        throw OneboxRestException.builder(errorCode).build();
    }

    public void updateConfig(Long eventId, Long channelId, Long venueTemplateId, B2BSeatPublishingConfigRequestDTO newConfig) {

        B2BSeatPublishingConfig b2BSeatPublishingConfig = B2BPublishingConfigConverter.toMsEvent(newConfig);
        b2BSeatPublishingConfigRepository.updateConfig(eventId, channelId, venueTemplateId, b2BSeatPublishingConfig);
    }
}
