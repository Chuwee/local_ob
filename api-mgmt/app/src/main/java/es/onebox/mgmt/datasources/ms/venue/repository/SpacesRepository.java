package es.onebox.mgmt.datasources.ms.venue.repository;

import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.mgmt.datasources.ms.venue.MsVenueDatasource;
import es.onebox.mgmt.datasources.ms.venue.dto.space.VenueSpace;
import es.onebox.mgmt.datasources.ms.venue.dto.space.VenueSpaces;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class SpacesRepository {

    private final MsVenueDatasource msVenueDatasource;

    @Autowired
    public SpacesRepository(MsVenueDatasource msVenueDatasource) {
        this.msVenueDatasource = msVenueDatasource;
    }

    public VenueSpaces getVenueSpaces(Long venueId) {
        return msVenueDatasource.getVenueSpaces(venueId);
    }

    public VenueSpace getVenueSpace(Long venueId, Long spaceId) {
        return msVenueDatasource.getVenueSpace(venueId, spaceId);
    }

    public IdDTO createVenueSpace(VenueSpace newSpace){
        return msVenueDatasource.createVenueSpace(newSpace);
    }

    public void updateVenueSpace(VenueSpace patchedSpace){
        msVenueDatasource.updateVenueSpace(patchedSpace);
    }

    public void deleteVenueSpace(Long venueId, Long spaceId) {
        msVenueDatasource.deleteVenueSpace(venueId, spaceId);
    }
}
