package es.onebox.mgmt.entities;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.datasources.ms.entity.dto.EntityVisibilities;
import es.onebox.mgmt.datasources.ms.entity.repository.EntityVisibilityRepository;
import es.onebox.mgmt.entities.converter.EntityVisibilitiesConverter;
import es.onebox.mgmt.entities.dto.EntityVisibilitiesDTO;
import es.onebox.mgmt.entities.enums.EntityVisibilityType;
import es.onebox.mgmt.exception.ApiMgmtEntitiesErrorCode;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EntityVisibilityService {

    private EntityVisibilityRepository entityVisibilityRepository;

    @Autowired
    public EntityVisibilityService(EntityVisibilityRepository entityVisibilityRepository) {
        this.entityVisibilityRepository = entityVisibilityRepository;
    }

    public EntityVisibilitiesDTO getEntityVisibilities(Long entityId) {
        EntityVisibilities entityVisibilities = entityVisibilityRepository.getEntityVisibilities(entityId);
        return EntityVisibilitiesConverter.fromMs(entityVisibilities);
    }

    public void setEntityVisibilities(Long entityId, EntityVisibilitiesDTO visibilities) {
        if (!EntityVisibilityType.FILTERED.equals(visibilities.getVisibilityType())
                && (CollectionUtils.isNotEmpty(visibilities.getVisibleEntities())
                    || CollectionUtils.isNotEmpty(visibilities.getVisibleOperators()))) {
            throw new OneboxRestException(ApiMgmtEntitiesErrorCode.FILTERING_ONLY_AVAILABLE_IN_FILTERED_TYPE);
        }
        if (visibilities.getVisibleOperators() != null && visibilities.getVisibleOperators().stream().anyMatch(o -> o.getType() != null)) {
            throw new OneboxRestException(ApiMgmtEntitiesErrorCode.OPERATORS_VISIBILITIES_INCOMPATIBLE_WITH_TYPE);
        }
        EntityVisibilities entityVisibilities = EntityVisibilitiesConverter.toMs(visibilities);
        entityVisibilityRepository.updateEntityVisibilities(entityId, entityVisibilities);
    }
}
