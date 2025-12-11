package es.onebox.event.tickettemplates.dto;

import es.onebox.core.serializer.dto.response.BaseResponseCollection;
import es.onebox.core.serializer.dto.response.Metadata;

import java.util.List;

public class TicketTemplatesDTO extends BaseResponseCollection<TicketTemplateDTO, Metadata> {

    private static final long serialVersionUID = 1L;

    public TicketTemplatesDTO() {
    }

    public TicketTemplatesDTO(List<TicketTemplateDTO> data, Metadata metadata) {
        super(data, metadata);
    }
}
