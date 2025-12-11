package es.onebox.mgmt.sessions;

import es.onebox.mgmt.datasources.integration.dispatcher.dto.ExternalSessionBase;
import es.onebox.mgmt.datasources.integration.dispatcher.repository.DispatcherRepository;
import es.onebox.mgmt.events.converter.ExternalSessionConverter;
import es.onebox.mgmt.events.dto.ExternalSessionBaseDTO;
import es.onebox.mgmt.events.dto.ExternalSessionStatus;
import es.onebox.mgmt.security.SecurityManager;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExternalProviderSessionsService {

    private final DispatcherRepository dispatcherRepository;
    private final SecurityManager securityManager;

    @Autowired
    public ExternalProviderSessionsService(
            DispatcherRepository dispatcherRepository,
            SecurityManager securityManager
    ) {
        this.dispatcherRepository = dispatcherRepository;
        this.securityManager = securityManager;
    }

    public List<ExternalSessionBaseDTO> getExternalSessions(Long entityId, Long eventId, ExternalSessionStatus status, Boolean skipUsed) {
        securityManager.checkEntityAccessible(entityId);
        List<ExternalSessionBase> externalSessions = dispatcherRepository.getExternalSessions(entityId, eventId, status);
        if (BooleanUtils.isTrue(skipUsed)) {
            externalSessions = externalSessions.stream()
                    .filter(externalSession -> externalSession.getInternalId() == null)
                    .toList();
        }
        return ExternalSessionConverter.fromIntDispatcher(externalSessions);
    }

}
