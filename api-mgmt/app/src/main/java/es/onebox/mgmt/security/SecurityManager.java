package es.onebox.mgmt.security;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.security.EntityTypes;
import es.onebox.core.security.Roles;
import es.onebox.mgmt.common.BaseEntityRequestFilter;
import es.onebox.mgmt.datasources.ms.entity.dto.Entity;
import es.onebox.mgmt.datasources.ms.entity.dto.Operator;
import es.onebox.mgmt.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class SecurityManager {

    public static final String MSG_ENTITY_MISSMATCH = "Can't access resources from other entities";
    
    private final EntitiesRepository entitiesRepository;

    @Autowired
    public SecurityManager(EntitiesRepository entitiesRepository) {
        this.entitiesRepository = entitiesRepository;
    }

    public void checkEntityAccessible(Collection<Long> entityIds) {
        entityIds.forEach(entityId -> checkEntityAccessible(entityId, false, true));
    }

    public void checkEntityAccessible(Long entityId) {
        checkEntityAccessible(entityId, false, true);
    }

    public void checkEntityAccessible(Long entityId, boolean checkEntityAdmin) {
        checkEntityAccessible(entityId, false, checkEntityAdmin);
    }

    public void checkEntityAccessibleIncludeEntityAdmin(Long entityId) {
        checkEntityAccessible(entityId, false, false);
    }

    public void checkEntityAccessibleWithVisibility(Long entityId) {
        checkEntityAccessible(entityId, true, true);
    }

    private void checkEntityAccessible(Long entityId, boolean checkVisibility, boolean checkEntityAdmin) {
        if (SecurityUtils.hasEntityType(EntityTypes.OPERATOR)) {
            Entity entity = entitiesRepository.getCachedEntity(entityId);
            checkEntityAdminUse(checkEntityAdmin, entityId);
            if (!SecurityUtils.accessibleResource(entity.getId(), entity.getOperator().getId())) {
                if (checkVisibleEntitiesAccessibility(entityId, checkVisibility)) {
                    return;
                }
                throw new AccessDeniedException("Can't access resources from other operators");
            }
        } else if (!SecurityUtils.hasEntityType(EntityTypes.SUPER_OPERATOR)) {
            if (SecurityUtils.hasAnyRole(Roles.ROLE_ENT_ADMIN)) {
                if (checkEntityAdminVisibility(entityId)) {
                    checkEntityAdminUse(checkEntityAdmin, entityId);
                    return;
                } else if(checkVisibleEntitiesAccessibility(entityId, checkVisibility)) {
                    return;
                }
                throw new AccessDeniedException(MSG_ENTITY_MISSMATCH);
            } else if (entityId.equals(SecurityUtils.getUserEntityId())) {
                return;
            } else if (checkVisibleEntitiesAccessibility(entityId, checkVisibility)) {
                return;
            }
            throw new AccessDeniedException(MSG_ENTITY_MISSMATCH);
        }
    }

    public void checkEntityAccessible(BaseEntityRequestFilter filter) {
        if (SecurityUtils.hasAnyRole(Roles.ROLE_ENT_ADMIN)) {
            checkEntityAdminAccessible(filter);
        } else if (!SecurityUtils.hasAnyEntityTypes(EntityTypes.OPERATOR, EntityTypes.SUPER_OPERATOR)) {
            if (filter.getEntityId() != null && !filter.getEntityId().equals(SecurityUtils.getUserEntityId())) {
                throw new AccessDeniedException(MSG_ENTITY_MISSMATCH);
            }
            if (filter.getEntityAdminId() != null) {
                throw new AccessDeniedException("Can't access resources from entity admin");
            }
            filter.setEntityId(SecurityUtils.getUserEntityId());
        } else if (!SecurityUtils.hasAnyRole(Roles.ROLE_SYS_MGR, Roles.ROLE_SYS_ANS)) {
            if (filter.getEntityId() != null) {
                checkEntityAccessible(filter.getEntityId());
            }
            if (filter.getEntityAdminId() != null) {
                checkEntityAccessible(filter.getEntityAdminId(), false);
            }
        }
    }

    public boolean isEntityAccessible(Long entityId, boolean checkVisibility) {
        if (SecurityUtils.hasEntityType(EntityTypes.OPERATOR)) {
            Entity entity = entitiesRepository.getCachedEntity(entityId);
            if (!SecurityUtils.accessibleResource(entity.getId(), entity.getOperator().getId())) {
                if (checkVisibleEntitiesAccessibility(entityId, checkVisibility)) {
                    return Boolean.TRUE;
                }
                return Boolean.FALSE;
            }
        } else if (SecurityUtils.hasAnyRole(Roles.ROLE_ENT_ADMIN)) {
            return checkEntityAdminVisibility(entityId);
        } else if (!entityId.equals(SecurityUtils.getUserEntityId())) {
            if (checkVisibleEntitiesAccessibility(entityId, checkVisibility)) {
                return Boolean.TRUE;
            }
            return Boolean.FALSE;
        }
        return  Boolean.TRUE;
    }

    public List<Long> getVisibleEntities(Long entityId) {
        return entitiesRepository.getVisibleEntities(entityId);
    }


    public void checkVisibleEntitiesfromManagedEntity(Long entityId, BaseEntityRequestFilter filter) {
        checkEntityAccessible(filter);
        if (filter != null && filter.getEntityAdminId() != null && filter.getEntityId() != null) {
            List<Long> allVisibleEntites = entitiesRepository.getVisibleEntities(filter.getEntityId());
            allVisibleEntites.addAll(entitiesRepository.getVisibleEntities(filter.getEntityAdminId()));
            if (!allVisibleEntites.contains(entityId)){
                throw new AccessDeniedException(MSG_ENTITY_MISSMATCH);
            }
        } else {
            throw new AccessDeniedException(MSG_ENTITY_MISSMATCH);
        }
    }
    public boolean isSameOperator(Long entityId) {
        Entity entity = entitiesRepository.getCachedEntity(entityId);
        return entity.getOperator().getId().equals(SecurityUtils.getUserOperatorId());
    }

    public void checkOperatorAccessible(Long operatorId) {
        Operator operator = entitiesRepository.getCachedOperator(operatorId);
        if (!SecurityUtils.hasEntityType(EntityTypes.SUPER_OPERATOR)) {
            if (operator != null && !operator.getId().equals(SecurityUtils.getUserOperatorId())) {
                throw new AccessDeniedException(MSG_ENTITY_MISSMATCH);
            }
        }
    }

    private boolean checkVisibleEntitiesAccessibility(Long entityId, boolean checkVisibility) {
        if (checkVisibility) {
            List<Long> visibleEntities = getVisibleEntities(SecurityUtils.getUserEntityId());
            return visibleEntities.contains(entityId);
        }
        return false;
    }

    public void checkEntityAdminAccessible(BaseEntityRequestFilter filter) {
        if (filter.getEntityAdminId() != null && !filter.getEntityAdminId().equals(SecurityUtils.getUserEntityId())) {
            throw new AccessDeniedException(MSG_ENTITY_MISSMATCH);
        }
        filter.setEntityAdminId(SecurityUtils.getUserEntityId());
        if (filter.getEntityId() != null && !checkEntityAdminVisibility(filter.getEntityId())) {
            throw new AccessDeniedException(MSG_ENTITY_MISSMATCH);
        }
    }

    public boolean checkEntityAdminVisibility(Long entityId) {
        if(entityId.equals(SecurityUtils.getUserEntityId())){
            return true;
        }
        return entitiesRepository.getCachedEntityAdminEntities(SecurityUtils.getUserEntityId()).contains(entityId);
    }

    private void checkEntityAdminUse(Boolean checkEntityAdmin, Long entityId) {
        if (Boolean.FALSE.equals(checkEntityAdmin)) {
            return;
        }
        Entity entity = entitiesRepository.getCachedEntity(entityId);
        if (CollectionUtils.isNotEmpty(entity.getTypes())
                && entity.getTypes().stream().anyMatch(type -> type.equals(EntityTypes.ENTITY_ADMIN))) {
            throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, "Can't use an entity admin type", null);
        }
    }

    public void checkIsEntityAdminEntity(Long entityId) {
        Entity entity = entitiesRepository.getCachedEntity(entityId);
        if (CollectionUtils.isNotEmpty(entity.getTypes()) && entity.getTypes().stream().noneMatch(type -> type.equals(EntityTypes.ENTITY_ADMIN))){
            throw new OneboxRestException(ApiMgmtErrorCode.ENTITY_IS_NOT_ENTITY_ADMIN);
        }
    }

}
