package es.onebox.mgmt.channels.converter;

import es.onebox.mgmt.channels.dto.ChannelsFilter;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelFilter;
import es.onebox.mgmt.datasources.ms.channel.enums.ChannelStatus;
import es.onebox.mgmt.datasources.ms.channel.enums.ChannelSubtype;

import java.util.List;
import java.util.stream.Collectors;

public class ChannelFilterConverter {

    private ChannelFilterConverter() {
    }

    public static ChannelFilter convert(ChannelsFilter filter, List<Long> visibleEntities) {
        ChannelFilter msFilter = new ChannelFilter();
        msFilter.setEntityId(filter.getEntityId());
        msFilter.setEntityAdminId(filter.getEntityAdminId());
        msFilter.setName(filter.getName());
        msFilter.setOperatorId(filter.getOperatorId());
        if (filter.getStatus() != null) {
            msFilter.setStatus(
                filter.getStatus()
                        .stream()
                            .map(status->ChannelStatus.getById(status.getId()))
                                .collect(Collectors.toList())
            );
        }
        if (filter.getType() != null) {
            msFilter.setSubtype(
                filter.getType().stream().map(subtype -> ChannelSubtype.getById(subtype.getId())).toList()
            );
        }

        msFilter.setIncludeThirdPartyChannels(filter.getIncludeThirdPartyChannels());
        msFilter.setVisibleEntities(visibleEntities);
        msFilter.setOffset(filter.getOffset());
        msFilter.setLimit(filter.getLimit());
        msFilter.setSort(filter.getSort());
        return msFilter;
    }
}
