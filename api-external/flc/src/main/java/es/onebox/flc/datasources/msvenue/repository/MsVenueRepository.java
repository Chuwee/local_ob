package es.onebox.flc.datasources.msvenue.repository;

import es.onebox.cache.annotation.Cached;
import es.onebox.cache.annotation.CachedArg;
import es.onebox.flc.datasources.msvenue.MsVenueDatasource;
import es.onebox.flc.datasources.msvenue.dto.VenueDTO;
import es.onebox.flc.datasources.msvenue.dto.VenuesDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class MsVenueRepository {
    @Autowired
    private MsVenueDatasource msVenueDatasource;

    public VenuesDTO getVenues(Integer entityId, Integer operatorId, Boolean inUse, Long limit, Long offset) {
        return msVenueDatasource.getVenues(entityId, operatorId, inUse, limit, offset);
    }

    @Cached(key = "venueById", expires = 60 * 60)
    public VenueDTO getVenue(@CachedArg Long venueId) {
        return msVenueDatasource.getVenue(venueId);
    }
}
