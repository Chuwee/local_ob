package es.onebox.mgmt.b2b.utils;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.security.EntityTypes;
import es.onebox.mgmt.common.BaseEntityRequestFilter;
import es.onebox.mgmt.datasources.ms.entity.dto.Entity;
import es.onebox.mgmt.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.security.SecurityManager;
import es.onebox.mgmt.security.SecurityUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;

@Service
public class B2BUtilsService {

    private final EntitiesRepository entitiesRepository;
    private final SecurityManager securityManager;

    public B2BUtilsService(EntitiesRepository entitiesRepository, SecurityManager securityManager) {
        this.entitiesRepository = entitiesRepository;
        this.securityManager = securityManager;
    }

    public void validateEntity(BaseEntityRequestFilter filter) {
        securityManager.checkEntityAccessible(filter);
        if (filter.getEntityId() == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, "entity_id required for operator user", null);
        }
        Entity entity = entitiesRepository.getEntity(filter.getEntityId());
        if (BooleanUtils.isNotTrue(entity.getModuleB2BEnabled())) {
            throw OneboxRestException.builder(ApiMgmtErrorCode.ENTITY_MODULE_B2B_DISABLED).build();
        }
    }

    public Long validateEntity(Long entityId) {
        if (entityId != null) {
            securityManager.checkEntityAccessible(entityId);
        } else if (SecurityUtils.hasEntityType(EntityTypes.OPERATOR)) {
            throw new OneboxRestException(ApiMgmtErrorCode.ENTITY_ID_MANDATORY, "entity_id required for operator user", null);
        }
        Long id = entityId != null ? entityId : SecurityUtils.getUserEntityId();
        Entity entity = entitiesRepository.getEntity(id);
        if (BooleanUtils.isNotTrue(entity.getModuleB2BEnabled())) {
            throw OneboxRestException.builder(ApiMgmtErrorCode.ENTITY_MODULE_B2B_DISABLED).build();
        }
        return id;
    }
}
