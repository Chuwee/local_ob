package es.onebox.mgmt.datasources.ms.entity.repository;

import es.onebox.mgmt.datasources.ms.entity.MsEntityDatasource;
import es.onebox.mgmt.datasources.ms.entity.dto.EntityVisibilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class EntityVisibilityRepository {

    private MsEntityDatasource msEntityDatasource;

    @Autowired
    public EntityVisibilityRepository(MsEntityDatasource msEntityDatasource) {
        this.msEntityDatasource = msEntityDatasource;
    }

    public EntityVisibilities getEntityVisibilities(Long entityId) {
        return msEntityDatasource.getEntityVisibilities(entityId);
    }

    public void updateEntityVisibilities(Long entityId, EntityVisibilities entityVisibilities) {
        msEntityDatasource.updateEntityVisibilities(entityId, entityVisibilities);
    }
}
