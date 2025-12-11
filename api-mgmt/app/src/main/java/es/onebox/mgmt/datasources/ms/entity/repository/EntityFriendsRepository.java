package es.onebox.mgmt.datasources.ms.entity.repository;

import es.onebox.mgmt.datasources.ms.entity.MsEntityDatasource;
import es.onebox.mgmt.datasources.ms.entity.dto.EntityFriendsConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class EntityFriendsRepository {

    private final MsEntityDatasource msEntityDatasource;

    @Autowired
    public EntityFriendsRepository(MsEntityDatasource msEntityDatasource) {
        this.msEntityDatasource = msEntityDatasource;
    }

    public EntityFriendsConfig getConfig(Long entityId) {
        return msEntityDatasource.getEntityFriendsConfig(entityId);
    }

    public void updateConfig(Long entityId, EntityFriendsConfig config) {
        msEntityDatasource.updateEntityFriendsConfig(entityId, config);
    }
}
