package es.onebox.mgmt.channels.converter;

import es.onebox.mgmt.channels.dto.adminchannels.AdminChannelDTO;
import es.onebox.mgmt.channels.dto.adminchannels.AdminChannelWhitelabelSettings;
import es.onebox.mgmt.channels.dto.adminchannels.AdminChannelsResponseDTO;
import es.onebox.mgmt.channels.enums.ChannelStatus;
import es.onebox.mgmt.channels.enums.ChannelSubtype;
import es.onebox.mgmt.channels.enums.WhitelabelType;
import es.onebox.mgmt.datasources.ms.channel.dto.AdminChannel;
import es.onebox.mgmt.datasources.ms.channel.dto.AdminChannelsResponse;
import es.onebox.mgmt.seasontickets.dto.channels.ChannelEntityDTO;

import java.util.Objects;
import java.util.stream.Collectors;

public class AdminChannelConverter {

    public static final String PREVIEWTOKEN_PARAM_KEY = "previewtoken";
    private static final Integer DEFAULT_V2_RECEIPT_TEMPLATE = 3;

    private AdminChannelConverter() {
    }

    public static AdminChannelsResponseDTO fromMsChannelsResponse(AdminChannelsResponse msResponse) {
        AdminChannelsResponseDTO dto = new AdminChannelsResponseDTO();
        dto.setMetadata(msResponse.getMetadata());
        dto.setData(msResponse.getData()
                .stream()
                .map(AdminChannelConverter::fromMsChannel)
                .collect(Collectors.toList()));
        return dto;
    }

    private static AdminChannelDTO fromMsChannel(AdminChannel channel) {
        AdminChannelDTO dto = new AdminChannelDTO();

        ChannelEntityDTO entity = new ChannelEntityDTO();
        entity.setId(channel.getEntityId());
        entity.setName(channel.getEntityName());
        entity.setLogo(channel.getEntityLogo());
        dto.setEntity(entity);

        dto.setId(channel.getId());
        dto.setName(channel.getName());
        dto.setStatus(ChannelStatus.getById(channel.getStatus().getId()));

        AdminChannelWhitelabelSettings whitelabelSettings = new AdminChannelWhitelabelSettings();
        whitelabelSettings.setV4ConfigEnabled(channel.getV4ConfigEnabled());
        whitelabelSettings.setV4Enabled(channel.getV4Enabled());
        whitelabelSettings.setV2ReceiptTemplateEnabled(DEFAULT_V2_RECEIPT_TEMPLATE <= channel.getIdReceiptTemplate());
        whitelabelSettings.setWhitelabelType(Objects.isNull(channel.getWhitelabelType()) ? WhitelabelType.INTERNAL : channel.getWhitelabelType());
        dto.setWhitelabelSettings(whitelabelSettings);

        if (channel.getSubtype() != null) {
            dto.setType(ChannelSubtype.getById(channel.getSubtype().getIdSubtipo()));
        }

        return dto;
    }
}
