package es.onebox.event.sessions.dto;

import es.onebox.core.serializer.dto.response.ListWithMetadata;
import es.onebox.event.events.dto.EventRateDTO;
import es.onebox.event.events.dto.RateDTO;

import java.io.Serial;

public class SessionRatesDTO extends ListWithMetadata<EventRateDTO> {

    @Serial
    private static final long serialVersionUID = 2561049826599191279L;
}
