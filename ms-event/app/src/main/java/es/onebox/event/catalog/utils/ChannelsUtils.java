package es.onebox.event.catalog.utils;

import es.onebox.event.datasources.ms.channel.dto.ChannelConfigDTO;
import es.onebox.event.events.enums.ChannelSubtype;

public class ChannelsUtils {

    public static boolean isB2BChannel(ChannelConfigDTO channel) {
        return channel != null && ChannelSubtype.PORTAL_B2B.equals(ChannelSubtype.getById(channel.getChannelType()));
    }
}
