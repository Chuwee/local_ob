package es.onebox.mgmt.externalevents.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.security.EntityTypes;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.datasources.ms.event.dto.externalevent.ExternalEvent;
import es.onebox.mgmt.datasources.ms.event.repository.ExternalEventsRepository;
import es.onebox.mgmt.exception.ApiMgmtExternalEventErrorCode;
import es.onebox.mgmt.externalevents.converter.ExternalEventsConverter;
import es.onebox.mgmt.externalevents.dto.ExternalEventDTO;
import es.onebox.mgmt.externalevents.dto.ExternalEventTypeDTO;
import es.onebox.mgmt.externalevents.dto.ExternalEventsResponse;
import es.onebox.mgmt.security.SecurityManager;
import es.onebox.mgmt.security.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExternalEventsService {

    private final ExternalEventsRepository repository;
    private final SecurityManager securityManager;

    @Autowired
    public ExternalEventsService(ExternalEventsRepository repository, SecurityManager securityManager) {
        this.repository = repository;
        this.securityManager = securityManager;
    }

    public ExternalEventsResponse getExternalEvents(Long entityId, ExternalEventTypeDTO eventTypeDTO) {
        Long filterEntityId = verifyAndExtractEntityId(entityId);
        List<ExternalEvent> externalEvents = repository.getExternalEvents(filterEntityId,
                ExternalEventsConverter.convertExternalEventTypeDTO(eventTypeDTO));
        if(externalEvents != null && !externalEvents.isEmpty()) {
            List<ExternalEventDTO> externalEventDTOList = externalEvents.stream().map(ExternalEventsConverter::convertExternalEvent)
                    .sorted(Comparator.comparing(ExternalEventDTO::getEventName))
                    .collect(Collectors.toList());
            return new ExternalEventsResponse(externalEventDTOList);
        } else {
            return new ExternalEventsResponse(Collections.emptyList());
        }
    }

    private Long verifyAndExtractEntityId(Long entityId) {
        long filterEntityId;
        if(entityId != null) {
            securityManager.checkEntityAccessible(entityId);
            filterEntityId = entityId;
        } else {
            if(SecurityUtils.hasEntityType(EntityTypes.OPERATOR)) {
                throw new OneboxRestException(ApiMgmtExternalEventErrorCode.ENTITY_REQUIRED);
            } else {
                filterEntityId = SecurityUtils.getUserEntityId();
            }
        }
        return filterEntityId;
    }

    public List<IdNameDTO> getExternalEventRates(Long internalId) {
        ExternalEvent externalEvent = repository.getExternalEvent(internalId);
        securityManager.checkEntityAccessible(externalEvent.getEntityId().longValue());
        return repository.getExternalEventRates(internalId).stream()
                .sorted(Comparator.comparing(IdNameDTO::getName))
                .collect(Collectors.toList());
    }
}
