package es.onebox.mgmt.channels.catalog.converter;

import es.onebox.mgmt.channels.catalog.dto.ChannelSessionDTO;
import es.onebox.mgmt.channels.catalog.dto.ChannelSessionsDTO;
import es.onebox.mgmt.channels.catalog.dto.ChannelSessionsFilter;
import es.onebox.mgmt.datasources.ms.channel.dto.catalog.ChannelEventMsFilter;
import es.onebox.mgmt.datasources.ms.channel.dto.catalog.ChannelSession;
import es.onebox.mgmt.datasources.ms.channel.dto.catalog.ChannelSessions;
import es.onebox.mgmt.datasources.ms.channel.dto.catalog.ChannelSessionsMsFilter;
import org.apache.commons.collections.CollectionUtils;


public class ChannelSessionConverter {

    public static ChannelSessionsDTO toDTO(ChannelSessions in) {
        ChannelSessionsDTO out = new ChannelSessionsDTO();
        out.setMetadata(in.getMetadata());
        if (CollectionUtils.isEmpty(in.getData())) {
            return out;
        }
        out.setData(in.getData().stream().map(ChannelSessionConverter::toDTO).toList());
        return out;
    }

    public static ChannelSessionDTO toDTO(ChannelSession in) {
        ChannelSessionDTO out = new ChannelSessionDTO();
        out.setId(in.getId());
        out.setName(in.getName());
        out.setStartDate(in.getStartDate());
        return out;
    }

    public static ChannelSessionsMsFilter toFilter(ChannelSessionsFilter filter) {
        ChannelSessionsMsFilter out = new ChannelSessionsMsFilter();
        out.setLimit(filter.getLimit());
        out.setOffset(filter.getOffset());
        out.setQ(filter.getQ());
        out.setOlsonId(filter.getOlsonId());
        out.setStartDate(filter.getStartDate());
        out.setDaysOfWeek(filter.getDaysOfWeek());
        return out;
    }
}
