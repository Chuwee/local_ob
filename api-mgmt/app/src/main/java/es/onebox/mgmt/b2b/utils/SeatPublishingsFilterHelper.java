package es.onebox.mgmt.b2b.utils;

import es.onebox.core.security.Roles;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.mgmt.b2b.publishing.dto.SeatPublishingFiltersRequest;
import es.onebox.mgmt.b2b.publishing.dto.SeatPublishingsSearchRequest;
import es.onebox.mgmt.datasources.ms.client.dto.b2bpublishing.SeatPublishingsFilter;
import es.onebox.mgmt.security.SecurityManager;
import es.onebox.mgmt.security.SecurityUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SeatPublishingsFilterHelper {

    private final SecurityManager securityManager;

    public SeatPublishingsFilterHelper(SecurityManager securityManager) {
        this.securityManager = securityManager;
    }

    public void addEntityConstraints(SeatPublishingsFilter msFilter) {
        if(SecurityUtils.hasAnyRole(Roles.ROLE_OPR_MGR, Roles.ROLE_OPR_ANS, Roles.ROLE_ENT_ADMIN)){
            msFilter.setOperatorId(SecurityUtils.getUserOperatorId());
            if(SecurityUtils.hasAnyRole(Roles.ROLE_ENT_ADMIN)){
                msFilter.setEntityAdminId(SecurityUtils.getUserEntityId());
            }
        }else if(CommonUtils.isEmpty(msFilter.getEntityIds())){
            msFilter.setEntityIds(securityManager.getVisibleEntities(SecurityUtils.getUserEntityId()));
        }
    }

    public void checkEntityFilterConstraints(SeatPublishingsSearchRequest request, SeatPublishingsFilter msFilter) {
        checkEntityFilterConstraints(request.getEntityIds(), msFilter);
    }

    public void checkEntityFilterConstraints(SeatPublishingFiltersRequest request, SeatPublishingsFilter msFilter) {
        checkEntityFilterConstraints(request.getEntityIds(), msFilter);
    }

    private void checkEntityFilterConstraints(List<Long> entityIds, SeatPublishingsFilter msFilter) {
        if (!CommonUtils.isEmpty(entityIds)) {
            boolean isEntAdmin = SecurityUtils.hasAnyRole(Roles.ROLE_ENT_ADMIN);
            List<Long> filteredEntityIds = entityIds.stream()
                    .filter(entityId ->
                            securityManager.isEntityAccessible(entityId, true) &&
                                    securityManager.isSameOperator(entityId) ||
                                    (isEntAdmin && securityManager.checkEntityAdminVisibility(entityId)))
                    .collect(Collectors.toList());
            msFilter.setEntityIds(filteredEntityIds);
        }
    }
}
