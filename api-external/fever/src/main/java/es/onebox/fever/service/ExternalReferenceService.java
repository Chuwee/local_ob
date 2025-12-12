package es.onebox.fever.service;

import es.onebox.common.datasources.ms.entity.dto.EntityDTO;
import es.onebox.common.datasources.ms.entity.dto.RequestEntityDTO;
import es.onebox.common.datasources.ms.entity.dto.User;
import es.onebox.common.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.common.datasources.ms.entity.repository.UsersRepository;
import es.onebox.common.datasources.ms.event.dto.EventDTO;
import es.onebox.common.datasources.ms.event.dto.SessionDTO;
import es.onebox.common.datasources.ms.event.repository.MsEventRepository;
import es.onebox.common.datasources.ms.event.request.UpdateSessionRequest;
import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.fever.dto.UpdateExtReferenceRequest;

import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExternalReferenceService {

    @Autowired
    private MsEventRepository msEventRepository;

    @Autowired
    private EntitiesRepository entitiesRepository;

    @Autowired
    private UsersRepository usersRepository;

    public void updateExternalReference(UpdateExtReferenceRequest request) {
        String externalReference = Optional.ofNullable(request.getExternalReference()).orElse(StringUtils.EMPTY);
        UpdateMode mode = UpdateMode.resolve(request);

        switch (mode) {
            case EVENT -> {
                EventDTO event = getEventAndValidateRequest(request);
                event.setExternalReference(externalReference);
                msEventRepository.updateEvent(request.getEventId(), event);
            }
            case SESSION -> {
                getEventAndValidateRequest(request);
                validateSession(request);
                UpdateSessionRequest updateSessionRequest = new UpdateSessionRequest();
                updateSessionRequest.setExternalReference(externalReference);
                msEventRepository.updateSession(request.getEventId(), request.getSessionId(), updateSessionRequest);
            }
            case ENTITY -> {
                validateEntity(request);
                RequestEntityDTO entity = new RequestEntityDTO();
                entity.setExternalReference(externalReference);
                entitiesRepository.updateEntity(request.getEntityId(), entity);
            }
            case USER -> {
                validateUser(request);
                User userUpdate = new User();
                userUpdate.setExternalReference(externalReference);
                usersRepository.updateUser(request.getUserId(), userUpdate);
            }
        }
    }

    private void validateSession(UpdateExtReferenceRequest request) {
        SessionDTO session = msEventRepository.getSession(request.getEventId(), request.getSessionId());
        if (Objects.isNull(session)) {
            throw new OneboxRestException(ApiExternalErrorCode.SESSION_NOT_FOUND);
        }
    }

    private EventDTO getEventAndValidateRequest(UpdateExtReferenceRequest request) {
        EventDTO event = msEventRepository.getEvent(request.getEventId());
        if (Objects.isNull(event)) {
            throw new OneboxRestException(ApiExternalErrorCode.EVENT_NOT_FOUND);
        }
        return event;
    }

    private void validateEntity(UpdateExtReferenceRequest request) {
        EntityDTO entity = entitiesRepository.getByIdCached(request.getEntityId());
        if (Objects.isNull(entity)) {
            throw new OneboxRestException(ApiExternalErrorCode.ENTITY_NOT_FOUND);
        }
    }

    private void validateUser(UpdateExtReferenceRequest request) {
        User user = usersRepository.getById(request.getUserId());
        if (Objects.isNull(user)) {
            throw new OneboxRestException(ApiExternalErrorCode.USER_NOT_FOUND);
        }
    }
}
