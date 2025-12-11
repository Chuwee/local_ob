package es.onebox.mgmt.entities;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.common.FileUtils;
import es.onebox.mgmt.datasources.ms.entity.dto.Entity;
import es.onebox.mgmt.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.mgmt.entities.converter.EntityCustomContentsConverter;
import es.onebox.mgmt.entities.dto.EntityCustomContentsDTO;
import es.onebox.mgmt.entities.dto.UpdateEntityCustomContentsDTO;
import es.onebox.mgmt.entities.dto.UpdateEntityCustomContentsListDTO;
import es.onebox.mgmt.entities.enums.EntityCustomContentsExtension;
import es.onebox.mgmt.entities.enums.EntityCustomContentsType;
import es.onebox.mgmt.exception.ApiMgmtEntitiesErrorCode;
import es.onebox.mgmt.security.SecurityManager;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EntitiesCustomContentsService {

    private final EntitiesRepository entitiesRepository;

    private final SecurityManager securityManager;

    @Autowired
    public EntitiesCustomContentsService(EntitiesRepository entitiesRepository, SecurityManager securityManager) {
        this.entitiesRepository = entitiesRepository;
        this.securityManager = securityManager;
    }

    public List<EntityCustomContentsDTO> getCustomContents(Long entityId) {
        securityManager.checkEntityAccessibleIncludeEntityAdmin(entityId);
        return EntityCustomContentsConverter.toDTO(entitiesRepository.getCustomContents(entityId));
    }

    public void setCustomContents(Long entityId, UpdateEntityCustomContentsListDTO entityCustomContents) {
        securityManager.checkEntityAccessibleIncludeEntityAdmin(entityId);

        Entity entity = entitiesRepository.getEntity(entityId);
        if (entity.getCustomization() != null && entity.getCustomization().getEnabled()) {
            validateImages(entityCustomContents);
            entitiesRepository.setCustomContents(entityId, EntityCustomContentsConverter.toMs(entityCustomContents));
        } else {
            throw new OneboxRestException(ApiMgmtEntitiesErrorCode.ENTITY_CUSTOMIZATION_REQUIRED, "Entity customization required for entity_id: " + entityId, null);
        }
    }

    public void deleteCustomContents(Long entityId, EntityCustomContentsType tag) {
        securityManager.checkEntityAccessibleIncludeEntityAdmin(entityId);

        Entity entity = entitiesRepository.getEntity(entityId);
        if (entity.getCustomization() != null && entity.getCustomization().getEnabled()) {
            entitiesRepository.deleteCustomContents(entityId, tag.name());
        } else {
            throw new OneboxRestException(ApiMgmtEntitiesErrorCode.ENTITY_CUSTOMIZATION_REQUIRED, "Entity customization required for entity_id: " + entityId, null);
        }

    }

    private void validateImages(UpdateEntityCustomContentsListDTO entityCustomContents) {
        for (UpdateEntityCustomContentsDTO customContent: entityCustomContents){
            if (StringUtils.isNotBlank(customContent.getValue())){
                switch (customContent.getExtension()) {
                    case SVG -> FileUtils.validateSVG(customContent.getValue(), customContent.getTag().getSize());
                    case ICO ->
                            FileUtils.validateSize(customContent.getValue(), customContent.getTag().getSize(), EntityCustomContentsExtension.ICO.name());
                    default -> FileUtils.checkImage(customContent.getValue(), customContent.getTag(),
                            customContent.getTag().toString(), EntityCustomContentsExtension.getValues());
                }
            }
        }
    }
}
