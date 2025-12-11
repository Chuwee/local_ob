package es.onebox.mgmt.customers.services;

import es.onebox.mgmt.customers.converter.MemberCounterConverter;
import es.onebox.mgmt.customers.dto.MemberCounterDTO;
import es.onebox.mgmt.customers.dto.UpdateMemberCounterDTO;
import es.onebox.mgmt.datasources.ms.entity.dto.membercounter.MemberCounter;
import es.onebox.mgmt.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.mgmt.security.SecurityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MemberCounterService {

    private final EntitiesRepository entitiesRepository;
    private SecurityManager securityManager;

    @Autowired
    public MemberCounterService(EntitiesRepository entitiesRepository, SecurityManager securityManager) {
        this.entitiesRepository = entitiesRepository;
        this.securityManager = securityManager;
    }

    public MemberCounterDTO getMemberCounter(Long entityId) {
        securityManager.checkEntityAccessible(entityId);
        MemberCounter memberCounter = entitiesRepository.getMemberCounter(entityId);
        return MemberCounterConverter.toDTO(memberCounter);
    }

    public void updateMemberCounter(Long entityId, UpdateMemberCounterDTO request) {
        securityManager.checkEntityAccessible(entityId);
        entitiesRepository.updateMemberCounter(entityId, MemberCounterConverter.toMS(request));
    }

}
