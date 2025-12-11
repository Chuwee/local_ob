package es.onebox.event.events.dto;

import es.onebox.core.serializer.dto.response.BaseResponseCollection;
import es.onebox.core.serializer.dto.response.Metadata;

import java.util.List;

public class EventChannelsDTO extends BaseResponseCollection<BaseEventChannelDTO, Metadata> {

    private static final long serialVersionUID = 1L;

    public EventChannelsDTO() {
    }

    public EventChannelsDTO(List<BaseEventChannelDTO> data, Metadata metadata) {
        super(data, metadata);
    }

}
