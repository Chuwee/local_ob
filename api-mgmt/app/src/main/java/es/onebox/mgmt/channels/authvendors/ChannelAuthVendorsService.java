package es.onebox.mgmt.channels.authvendors;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.channels.ChannelsHelper;
import es.onebox.mgmt.channels.authvendors.converter.ChannelAuthVendorsConverter;
import es.onebox.mgmt.channels.authvendors.dto.ChannelAuthVendorDTO;
import es.onebox.mgmt.channels.authvendors.enums.ChannelAuthVendorsType;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelResponse;
import es.onebox.mgmt.datasources.ms.channel.dto.authvendor.ChannelAuthVendor;
import es.onebox.mgmt.datasources.ms.channel.repositories.ChannelsRepository;
import es.onebox.mgmt.datasources.ms.client.dto.AuthVendorEntityConfig;
import es.onebox.mgmt.datasources.ms.client.repositories.AuthVendorEntityRepository;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChannelAuthVendorsService {

    private final ChannelsHelper channelsHelper;
    private final ChannelsRepository channelsRepository;
    private final AuthVendorEntityRepository authVendorEntityRepository;

    @Autowired
    public ChannelAuthVendorsService(ChannelsHelper channelsHelper, ChannelsRepository channelsRepository,
                                     AuthVendorEntityRepository authVendorEntityRepository) {
        this.channelsHelper = channelsHelper;
        this.channelsRepository = channelsRepository;
        this.authVendorEntityRepository = authVendorEntityRepository;
    }

    public ChannelAuthVendorDTO getAuthVendor(Long channelId, ChannelAuthVendorsType type) {
        validateRequest(channelId);
        ChannelAuthVendor authVendor = channelsRepository.getChannelAuthVendor(channelId);
        return ChannelAuthVendorsConverter.toDTO(authVendor, type);
    }

    public void updateAuthVendor(Long channelId, ChannelAuthVendorsType type, ChannelAuthVendorDTO body) {
        validateUpdate(channelId, body);
        ChannelAuthVendor authVendor = ChannelAuthVendorsConverter.toMS(body, type);
        channelsRepository.updateChannelAuthVendor(channelId, authVendor);
    }

    private AuthVendorEntityConfig validateRequest(Long channelId) {
        ChannelResponse channelResponse = channelsHelper.getAndCheckChannel(channelId);
        AuthVendorEntityConfig authVendorEntityConfig = authVendorEntityRepository.getAuthVendorEntityConfiguration(channelResponse.getEntityId());
        if (authVendorEntityConfig == null || BooleanUtils.isFalse(authVendorEntityConfig.getAllowed())) {
            throw new OneboxRestException(ApiMgmtErrorCode.ENTITY_AUTH_VENDOR_NOT_ALLOWED);
        }
        return authVendorEntityConfig;
    }

    private void validateUpdate(Long channelId, ChannelAuthVendorDTO body) {
        List<String> validVendors = validateRequest(channelId).getvendors();
        if (BooleanUtils.isTrue(body.getAllowed()) && body.getVendors().isEmpty()) {
            throw new OneboxRestException(ApiMgmtErrorCode.AUTH_VENDOR_ENABLED_REQUIRE_VENDOR);
        }
        if (!validVendors.containsAll(body.getVendors())) {
            throw new OneboxRestException(ApiMgmtErrorCode.AUTH_VENDOR_NOT_FOUND_IN_ENTITY_VENDORS);
        }
    }
}
