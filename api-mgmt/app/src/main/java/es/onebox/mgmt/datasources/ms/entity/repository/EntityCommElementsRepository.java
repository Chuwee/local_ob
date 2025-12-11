package es.onebox.mgmt.datasources.ms.entity.repository;

import es.onebox.mgmt.datasources.ms.entity.MsEntityDatasource;
import es.onebox.mgmt.datasources.ms.entity.dto.commElements.EntityCommElementsImages;
import es.onebox.mgmt.datasources.ms.entity.dto.commElements.EntityCommElementsTexts;
import es.onebox.mgmt.entities.enums.EntityImageContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EntityCommElementsRepository {

    private final MsEntityDatasource msEntityDatasource;

    @Autowired
    public EntityCommElementsRepository(MsEntityDatasource msEntityDatasource) {
        this.msEntityDatasource = msEntityDatasource;
    }

    public EntityCommElementsTexts updateEntityCommunicationElementsText(Long entityId, EntityCommElementsTexts entityCommElementsTexts) {
        return msEntityDatasource.updateEntityCommunicationElementsText(entityId, entityCommElementsTexts);
    }

    public EntityCommElementsTexts getEntityCommunicationElementsText(Long entityId) {
        return msEntityDatasource.getEntityCommunicationElementsText(entityId);
    }

    public void updateEntityCommunicationElementsImage(Long entityId, EntityCommElementsImages entityCommElementsImages) {
        msEntityDatasource.updateEntityCommunicationElementsImage(entityId, entityCommElementsImages);
    }

    public EntityCommElementsImages getEntityCommunicationElementsImages(Long entityId) {
        return msEntityDatasource.getEntityCommunicationElementsImages(entityId);
    }

    public void deleteEntityCommunicationElementImage(Long entityId, String language, EntityImageContentType type) {
        msEntityDatasource.deleteEntityCommunicationElementImage(entityId, language, type);
    }
}
