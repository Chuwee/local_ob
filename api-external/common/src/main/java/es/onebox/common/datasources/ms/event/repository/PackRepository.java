package es.onebox.common.datasources.ms.event.repository;

import es.onebox.cache.annotation.Cached;
import es.onebox.cache.annotation.CachedArg;
import es.onebox.common.datasources.ms.event.dto.PackDTO;
import es.onebox.common.datasources.ms.event.MsEventDatasource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class PackRepository {

    private final MsEventDatasource msEventDatasource;

    @Autowired
    public PackRepository(MsEventDatasource msEventDatasource) {
        this.msEventDatasource = msEventDatasource;
    }


    @Cached(key = "PackRepository_getPack", expires = 5 * 60)
    public PackDTO getPack(@CachedArg Long packId) {
        return msEventDatasource.getPack(packId);
    }

}
