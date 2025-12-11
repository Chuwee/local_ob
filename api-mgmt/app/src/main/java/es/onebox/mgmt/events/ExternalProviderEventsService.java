package es.onebox.mgmt.events;

import es.onebox.mgmt.datasources.integration.dispatcher.repository.DispatcherRepository;
import es.onebox.mgmt.events.converter.ExternalEventConverter;
import es.onebox.mgmt.events.dto.ExternalEventBaseDTO;
import es.onebox.mgmt.events.dto.ExternalEventsProviderType;
import es.onebox.mgmt.security.SecurityManager;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExternalProviderEventsService {

    private final DispatcherRepository dispatcherRepository;
    private final SecurityManager securityManager;

    @Autowired
    public ExternalProviderEventsService(
            DispatcherRepository dispatcherRepository,
            SecurityManager securityManager
    ) {
        this.dispatcherRepository = dispatcherRepository;
        this.securityManager = securityManager;
    }

    public List<ExternalEventBaseDTO> getExternalEvents(Long entityId, Long venueTemplateId, ExternalEventsProviderType type, Boolean skipUsed) {
        securityManager.checkEntityAccessible(entityId);
        if (BooleanUtils.isTrue(skipUsed)) {
            return dispatcherRepository.getExternalEvents(entityId, venueTemplateId, type)
                    .stream().filter(event -> event.getInternalId() == null)
                    .map(ExternalEventConverter::fromIntDispatcher)
                    .toList();
        }
        return ExternalEventConverter
                .fromIntDispatcher(dispatcherRepository.getExternalEvents(entityId, venueTemplateId, type));
    }

}
