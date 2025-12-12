package es.onebox.common.datasources.ms.collective.repository;

import es.onebox.cache.annotation.Cached;
import es.onebox.cache.annotation.CachedArg;
import es.onebox.common.datasources.ms.collective.MsCollectiveDatasource;
import es.onebox.common.datasources.ms.collective.dto.ResponseCollectiveDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class CollectivesRepository {

    private final MsCollectiveDatasource msCollectiveDatasource;

    @Autowired
    public CollectivesRepository(MsCollectiveDatasource msCollectiveDatasource) {
        this.msCollectiveDatasource = msCollectiveDatasource;
    }

    @Cached(key = "CollectivesRepository_getCollective", expires = 10 * 60)
    public ResponseCollectiveDTO getCollective(@CachedArg Long id) {
        return msCollectiveDatasource.getCollective(id);
    }
}
