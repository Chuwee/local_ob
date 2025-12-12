package es.onebox.common.datasources.avetconfig.repository;

import es.onebox.cache.annotation.Cached;
import es.onebox.cache.annotation.CachedArg;
import es.onebox.common.datasources.avetconfig.IntAvetConfigDatasource;
import es.onebox.common.datasources.avetconfig.dto.CapacityDTO;
import es.onebox.common.datasources.avetconfig.dto.ClubConfig;
import es.onebox.common.datasources.avetconfig.dto.SessionMatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class IntAvetConfigRepository {

    @Autowired
    private IntAvetConfigDatasource intAvetConfigDatasource;

    @Cached(key = "ClubConfig", expires = 30)
    public ClubConfig getClubByEntityId(@CachedArg Long entityId) {
        return intAvetConfigDatasource.getClubByEntityId(entityId);
    }

    @Cached(key = "getSessionMatch", expires = 30)
    public SessionMatch getSession(@CachedArg Long sessionId) {
        return intAvetConfigDatasource.getSession(sessionId);
    }

    @Cached(key = "getEntityCapacities", expires = 30)
    public List<CapacityDTO> getEntityCapacities(@CachedArg Long entityId) {
        return intAvetConfigDatasource.getEntityCapacities(entityId);
    }

}