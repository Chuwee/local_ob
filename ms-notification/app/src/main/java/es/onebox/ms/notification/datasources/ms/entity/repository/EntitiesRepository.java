package es.onebox.ms.notification.datasources.ms.entity.repository;

import es.onebox.cache.annotation.Cached;
import es.onebox.cache.annotation.CachedArg;
import es.onebox.ms.notification.datasources.ms.entity.MsEntityDatasource;
import es.onebox.ms.notification.datasources.ms.entity.dto.Entities;
import es.onebox.ms.notification.datasources.ms.entity.dto.Entity;
import es.onebox.ms.notification.datasources.ms.entity.dto.ExternalMgmtConfig;
import es.onebox.ms.notification.datasources.ms.entity.dto.SearchEntitiesFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EntitiesRepository {

    private final MsEntityDatasource msEntityDatasource;

    @Autowired
    public EntitiesRepository(MsEntityDatasource msEntityDatasource) {
        this.msEntityDatasource = msEntityDatasource;
    }

    @Cached(key = "entity", expires = 5 * 60)
    public Entity getEntity(@CachedArg Integer entityId) {
        return msEntityDatasource.getEntity(entityId);
    }

    public Entities getEntities(List<Integer> entityIds) {
        SearchEntitiesFilter filter = new SearchEntitiesFilter();
        filter.setIds(entityIds);
        return msEntityDatasource.getEntities(filter);
    }

    public List<ExternalMgmtConfig> getExternalMgmtConfig(Long entityId) {
        return msEntityDatasource.getExternalMgmtConfig(entityId);
    }

}
