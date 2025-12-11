package es.onebox.event.seasontickets.dto;

import es.onebox.core.serializer.dto.response.BaseResponseCollection;
import es.onebox.core.serializer.dto.response.Metadata;

import java.util.List;

public class SeasonTicketsDTO  extends BaseResponseCollection<SearchSeasonTicketDTO, Metadata> {

    public SeasonTicketsDTO() {

    }

    public SeasonTicketsDTO(List<SearchSeasonTicketDTO> data, Metadata metadata) {
        super(data, metadata);
    }

    private static final long serialVersionUID = 1L;

}

