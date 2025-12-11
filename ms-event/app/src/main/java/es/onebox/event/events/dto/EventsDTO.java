package es.onebox.event.events.dto;

import es.onebox.core.serializer.dto.response.BaseResponseCollection;
import es.onebox.core.serializer.dto.response.Metadata;

import java.io.Serial;
import java.util.List;

public class EventsDTO extends BaseResponseCollection<EventDTO, Metadata> {

    @Serial
    private static final long serialVersionUID = -1873753565236825535L;

    public EventsDTO() {
    }

    public EventsDTO(List<EventDTO> data, Metadata metadata) {
        super(data, metadata);
    }
}
