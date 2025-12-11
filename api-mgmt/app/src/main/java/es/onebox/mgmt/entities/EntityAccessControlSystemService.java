package es.onebox.mgmt.entities;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.common.NameDTO;
import es.onebox.mgmt.accesscontrol.converter.AccessControlSystemsConverter;
import es.onebox.mgmt.accesscontrol.dto.AccessControlSystemsDTO;
import es.onebox.mgmt.accesscontrol.enums.AccessControlSystem;
import es.onebox.mgmt.accesscontrol.util.AccessControlValidationUtils;
import es.onebox.mgmt.datasources.ms.accesscontrol.repository.AccessControlSystemsRepository;
import es.onebox.mgmt.datasources.ms.entity.dto.Entity;
import es.onebox.mgmt.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.security.SecurityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EntityAccessControlSystemService {

    @Autowired
    private AccessControlSystemsRepository accessControlSystemsRepository;
    @Autowired
    private SecurityManager securityManager;
    @Autowired
    private EntitiesRepository entitiesRepository;


    public AccessControlSystemsDTO getAccessControlSystems(Long entityId) {
        if (entityId == null || entityId <= 0) {
            throw new OneboxRestException(ApiMgmtErrorCode.ENTITY_ID_MANDATORY);
        }
        securityManager.checkEntityAccessible(entityId);
        Entity e = entitiesRepository.getCachedEntity(entityId);
        if (e == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.ENTITY_NOT_FOUND);
        }
        return AccessControlSystemsConverter.convertFrom(accessControlSystemsRepository.findByEntityId(entityId));
    }

    public void createAccessControlSystems(Long entityId, NameDTO systemName) {
        if (entityId == null || entityId <= 0) {
            throw new OneboxRestException(ApiMgmtErrorCode.ENTITY_ID_MANDATORY);
        }
        AccessControlSystem system = AccessControlValidationUtils.validateAccessControlSystem(systemName);

        securityManager.checkEntityAccessible(entityId);
        Entity e = entitiesRepository.getCachedEntity(entityId);
        if (e == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.ENTITY_NOT_FOUND);
        }

        accessControlSystemsRepository.createAccessControlSystemEntity(entityId, system);
    }


    public void deleteAccessControlSystems(Long entityId, String systemName) {
        if (entityId == null || entityId <= 0) {
            throw new OneboxRestException(ApiMgmtErrorCode.ENTITY_ID_MANDATORY);
        }
        AccessControlSystem system = AccessControlValidationUtils.validateAccessControlSystem(systemName);
        securityManager.checkEntityAccessible(entityId);
        Entity e = entitiesRepository.getCachedEntity(entityId);
        if (e == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.ENTITY_NOT_FOUND);
        }

        accessControlSystemsRepository.deleteAccessControlSystemEntity(entityId, system);
    }

}
