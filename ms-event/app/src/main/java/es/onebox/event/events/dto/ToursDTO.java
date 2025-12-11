package es.onebox.event.events.dto;

import es.onebox.core.serializer.dto.response.BaseResponseCollection;
import es.onebox.core.serializer.dto.response.Metadata;

import java.util.List;

public class ToursDTO extends BaseResponseCollection<BaseTourDTO, Metadata> {

    private static final long serialVersionUID = 1L;

    public ToursDTO() {
    }

    public ToursDTO(List<BaseTourDTO> data, Metadata metadata) {
        super(data, metadata);
    }
}
