package es.onebox.common.service;

import es.onebox.common.datasources.ms.event.dto.EventDTO;
import es.onebox.common.datasources.ms.event.repository.MsEventRepository;
import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.core.exception.ExceptionBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventValidationService {

    private MsEventRepository msEventRepository;

    @Autowired
    public EventValidationService(final MsEventRepository msEventRepository) {
        this.msEventRepository = msEventRepository;
    }

    public void validate(final Long eventId, final Long entityId){
        EventDTO event = msEventRepository.getEvent(eventId);
       if(!entityId.equals(event.getEntityId())){
           throw ExceptionBuilder.build(ApiExternalErrorCode.EVENT_NOT_FOUND);
       }
    }
}
