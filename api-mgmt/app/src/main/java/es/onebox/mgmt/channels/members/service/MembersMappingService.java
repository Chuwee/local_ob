package es.onebox.mgmt.channels.members.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.channels.ChannelsHelper;
import es.onebox.mgmt.channels.dto.ChannelDetailDTO;
import es.onebox.mgmt.channels.enums.ChannelSubtype;
import es.onebox.mgmt.datasources.integration.avetconfig.repository.AvetConfigRepository;
import es.onebox.mgmt.datasources.ms.entity.dto.MemberCapacity;
import es.onebox.mgmt.datasources.ms.entity.dto.MemberConfigDTO;
import es.onebox.mgmt.exception.ApiMgmtChannelsErrorCode;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MembersMappingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MembersMappingService.class);

    private final ChannelsHelper channelsHelper;
    private final AvetConfigRepository avetConfigRepository;

    @Autowired
    public MembersMappingService(ChannelsHelper channelsHelper, AvetConfigRepository avetConfigRepository) {
        this.channelsHelper = channelsHelper;
        this.avetConfigRepository = avetConfigRepository;
    }

    public void mapCapacity(Long channelId) {
        channelsHelper.getAndCheckChannel(channelId);
        ChannelDetailDTO channelDetailDTO = channelsHelper.getChannel(channelId, null);

        if(!channelDetailDTO.getType().equals(ChannelSubtype.MEMBERS)) {
            throw new OneboxRestException(ApiMgmtErrorCode.CHANNEL_ID_INVALID);
        }

        if (avetConfigRepository.getClubConfigByEntity(channelDetailDTO.getEntity().getId()) == null) {
            throw new OneboxRestException(ApiMgmtChannelsErrorCode.CLUB_CONFIG_NOT_FOUND);
        }

        MemberConfigDTO memberConfigDTO = avetConfigRepository.getMemberConfigByChannel(channelId);
        if (memberConfigDTO == null) {
            throw new OneboxRestException(ApiMgmtChannelsErrorCode.MEMBER_CONFIG_NOT_FOUND);
        }

        Optional<MemberCapacity> mainCapacity = memberConfigDTO.getCapacities().stream().filter(MemberCapacity::isMain).findFirst();
        if(mainCapacity.isEmpty() || mainCapacity.get().getVenueTemplateId() == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.CHANNEL_CONFIG_NOT_COMPLETED);
        }
        Long venueTemplateId = mainCapacity.get().getVenueTemplateId();

        LOGGER.info("Create AVET mapping for template {}", venueTemplateId);
        avetConfigRepository.createTemplateMappings(venueTemplateId);
    }

}
