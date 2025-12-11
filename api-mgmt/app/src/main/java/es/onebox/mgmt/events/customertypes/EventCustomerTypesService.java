package es.onebox.mgmt.events.customertypes;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.datasources.ms.event.dto.customertypes.UpdateEventCustomerTypes;
import es.onebox.mgmt.datasources.ms.event.repository.EventsRepository;
import es.onebox.mgmt.events.converter.EventCustomerTypesConverter;
import es.onebox.mgmt.events.dto.EventCustomerTypeDTO;
import es.onebox.mgmt.events.dto.UpdateEventCustomerTypesDTO;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventCustomerTypesService {

    private final EventsRepository eventsRepository;

    public EventCustomerTypesService(EventsRepository eventsRepository) {
        this.eventsRepository = eventsRepository;
    }

   public List<EventCustomerTypeDTO> getEventCustomerTypes(Integer eventId) {
        validateEvent(eventId);
        return EventCustomerTypesConverter.toDTO(eventsRepository.getEventCustomerTypes(Long.valueOf(eventId)));
    }

    public void updateEventCustomerTypes(Integer eventId, UpdateEventCustomerTypesDTO eventCustomerTypes) {
        validateEvent(eventId);
        UpdateEventCustomerTypes customerTypes = EventCustomerTypesConverter.toMs(eventCustomerTypes);
        eventsRepository.putEventCustomerTypes(Long.valueOf(eventId), customerTypes);
    }

    private void validateEvent(Integer eventId) {
        if (eventsRepository.getEvent(Long.valueOf(eventId)) == null) {
            throw OneboxRestException.builder(ApiMgmtErrorCode.EVENT_NOT_FOUND)
                    .setMessage("no event found with id: " + eventId)
                    .build();
        }
    }
}
