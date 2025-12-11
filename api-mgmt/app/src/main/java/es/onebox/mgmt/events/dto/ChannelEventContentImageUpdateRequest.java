package es.onebox.mgmt.events.dto;

import es.onebox.mgmt.common.channelcontents.ChannelContentImageListDTO;
import es.onebox.mgmt.events.enums.ChannelEventContentImageType;
import es.onebox.mgmt.events.enums.EventChannelContentImageType;

import java.io.Serial;

public class ChannelEventContentImageUpdateRequest extends ChannelContentImageListDTO<ChannelEventContentImageType> {

    @Serial
    private static final long serialVersionUID = 1L;

}
