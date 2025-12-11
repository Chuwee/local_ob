package es.onebox.mgmt.channels.blacklists.converter;

import es.onebox.mgmt.channels.blacklists.dto.ChannelBlacklistDTO;
import es.onebox.mgmt.channels.blacklists.dto.ChannelBlacklistStatusDTO;
import es.onebox.mgmt.channels.blacklists.dto.ChannelBlacklistsDTO;
import es.onebox.mgmt.channels.blacklists.dto.ChannelBlacklistsResponseDTO;
import es.onebox.mgmt.channels.blacklists.enums.ChannelBlacklistType;
import es.onebox.mgmt.channels.blacklists.filter.ChannelBlacklistFilterDTO;
import es.onebox.mgmt.datasources.ms.channel.dto.blacklists.BlacklistStatus;
import es.onebox.mgmt.datasources.ms.channel.dto.blacklists.ChannelBlacklist;
import es.onebox.mgmt.datasources.ms.channel.dto.blacklists.ChannelBlacklistFilter;
import es.onebox.mgmt.datasources.ms.channel.dto.blacklists.ChannelBlacklistStatus;
import es.onebox.mgmt.datasources.ms.channel.dto.blacklists.ChannelBlacklistsResponse;

import java.util.List;
import java.util.stream.Collectors;

public class ChannelBlacklistsConverter {

    private ChannelBlacklistsConverter() {}

    public static ChannelBlacklistType toDTO(es.onebox.mgmt.datasources.ms.channel.dto.blacklists.ChannelBlacklistType source) {
        return ChannelBlacklistType.valueOf(source.name());
    }

    public static es.onebox.mgmt.datasources.ms.channel.dto.blacklists.ChannelBlacklistType toMs(ChannelBlacklistType source) {
        return es.onebox.mgmt.datasources.ms.channel.dto.blacklists.ChannelBlacklistType.valueOf(source.name());
    }

    public static es.onebox.mgmt.channels.blacklists.enums.ChannelBlacklistStatus toDTO(BlacklistStatus source) {
        return es.onebox.mgmt.channels.blacklists.enums.ChannelBlacklistStatus.valueOf(source.name());
    }

    public static BlacklistStatus toMs(es.onebox.mgmt.channels.blacklists.enums.ChannelBlacklistStatus source) {
        return BlacklistStatus.valueOf(source.name());
    }

    public static ChannelBlacklistStatusDTO toDTO(ChannelBlacklistStatus source) {
        ChannelBlacklistStatusDTO target = new ChannelBlacklistStatusDTO();
        target.setStatus(toDTO(source.getStatus()));
        return target;
    }

    public static ChannelBlacklistStatus toMs(ChannelBlacklistStatusDTO source) {
        ChannelBlacklistStatus target = new ChannelBlacklistStatus();
        target.setStatus(toMs(source.getStatus()));
        return target;
    }

    public static ChannelBlacklistsResponseDTO toDTO(ChannelBlacklistsResponse source) {
        ChannelBlacklistsResponseDTO target = new ChannelBlacklistsResponseDTO();
        target.setMetadata(source.getMetadata());
        target.setData(source.getData().stream().map(ChannelBlacklistsConverter::toDTO).collect(Collectors.toList()));
        return target;
    }

    public static ChannelBlacklistDTO toDTO(ChannelBlacklist source) {
        ChannelBlacklistDTO target = new ChannelBlacklistDTO();
        target.setCreationDate(source.getCreationDate());
        target.setValue(source.getValue());
        return target;
    }

    public static List<ChannelBlacklist> toMs(Integer channelId, ChannelBlacklistType type, ChannelBlacklistsDTO source) {
        return source.stream().map(elem -> ChannelBlacklistsConverter.toMs(channelId, type, elem)).collect(Collectors.toList());
    }

    public static ChannelBlacklist toMs(Integer channelId, ChannelBlacklistType type, ChannelBlacklistDTO source) {
        ChannelBlacklist target = new ChannelBlacklist();
        target.setChannelId(channelId);
        target.setValue(source.getValue());
        target.setType(toMs(type));
        return target;
    }

    public static ChannelBlacklistFilter toMs(ChannelBlacklistFilterDTO source) {
        ChannelBlacklistFilter target =
                new ChannelBlacklistFilter();
        target.setKeyword(source.getQ());
        target.setCreationDate(source.getDate());
        target.setSort(source.getSort());
        target.setLimit(source.getLimit());
        target.setOffset(source.getOffset());
        return target;
    }
}
