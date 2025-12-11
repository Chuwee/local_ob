package es.onebox.event.datasources.ms.accesscontrol.repository;

import es.onebox.cache.annotation.Cached;
import es.onebox.cache.annotation.CachedArg;
import es.onebox.event.datasources.ms.accesscontrol.MsAccessControlDatasource;
import es.onebox.event.datasources.ms.accesscontrol.dto.enums.AccessControlSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AccessControlSystemsRepository {

    private static final String VENUE_ACCESS_CONTROL_SYSTEMS = "venueAccessControlSystems";
    private final MsAccessControlDatasource msAccessControlDatasource;

    @Autowired
    public AccessControlSystemsRepository(final MsAccessControlDatasource msAccessControlDatasource) {
        this.msAccessControlDatasource = msAccessControlDatasource;
    }

    @Cached(key = VENUE_ACCESS_CONTROL_SYSTEMS)
    public List<AccessControlSystem> findByVenueId(@CachedArg Long venueId) {
        return msAccessControlDatasource.getSystems(null, venueId);
    }


}
