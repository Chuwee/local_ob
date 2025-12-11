package es.onebox.mgmt.channels;

import static es.onebox.mgmt.exception.ApiMgmtErrorCode.BAD_REQUEST_PARAMETER;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.channels.converter.AdminChannelConverter;
import es.onebox.mgmt.channels.converter.AdminChannelFilterConverter;
import es.onebox.mgmt.channels.dto.adminchannels.AdminChannelsFilter;
import es.onebox.mgmt.channels.dto.adminchannels.AdminChannelsResponseDTO;
import es.onebox.mgmt.channels.enums.WhitelabelType;
import es.onebox.mgmt.datasources.ms.channel.dto.AdminChannelsResponse;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelFilter;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelUpdateRequest;
import es.onebox.mgmt.datasources.ms.channel.repositories.AdminChannelsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlatformAdminChannelsService {
    private static final Integer V1_RECEIPT_TEMPLATE = 1;
    private static final Integer V2_RECEIPT_TEMPLATE = 3;


    private AdminChannelsRepository adminChannelsRepository;

    @Autowired
    public PlatformAdminChannelsService(AdminChannelsRepository adminChannelsRepository) {
        this.adminChannelsRepository = adminChannelsRepository;
    }

    public AdminChannelsResponseDTO getChannels(AdminChannelsFilter filter) {
        ChannelFilter channelFilter = AdminChannelFilterConverter.convert(filter, null);
        AdminChannelsResponse channels = adminChannelsRepository.getAdminChannels(channelFilter);
        return AdminChannelConverter.fromMsChannelsResponse(channels);
    }

    public void migrateChannel(Long channelId, Boolean migrateToChannels, Boolean stripeHookChecked) {
        adminChannelsRepository.migrateChannel(channelId, migrateToChannels, stripeHookChecked);
    }

    public void updateReceiptTemplate(Long channelId, Boolean updateReceiptTemplate) {
        ChannelUpdateRequest channelUpdateRequest = new ChannelUpdateRequest();
        if (Boolean.TRUE.equals(updateReceiptTemplate)) {
            channelUpdateRequest.setIdReceiptTemplate(V2_RECEIPT_TEMPLATE);
        } else {
            channelUpdateRequest.setIdReceiptTemplate(V1_RECEIPT_TEMPLATE);
        }
        adminChannelsRepository.updateChannel(channelId, channelUpdateRequest);
    }

    public void updateWhitelabelType(Long channelId, WhitelabelType whitelabelType) {

        if (whitelabelType == null){
            throw new OneboxRestException(BAD_REQUEST_PARAMETER, "Whitelabel type cannot be null ", null);
        }

        ChannelUpdateRequest channelUpdateRequest = new ChannelUpdateRequest();
        channelUpdateRequest.setWhitelabelType(whitelabelType);
        if (WhitelabelType.EXTERNAL.equals(whitelabelType)) {
            channelUpdateRequest.setForceSquarePictures(true);
        }
        adminChannelsRepository.updateChannel(channelId, channelUpdateRequest);
    }
}
