package es.onebox.event.catalog.dao;

import es.onebox.couchbase.annotation.CouchRepository;
import es.onebox.couchbase.dao.AbstractCouchDao;
import es.onebox.event.catalog.dto.venue.container.VenueDescriptor;
import org.springframework.stereotype.Repository;

@Repository
@CouchRepository(prefixKey = VenueDescriptorCouchDao.KEY, bucket = VenueDescriptorCouchDao.BUCKET, scope = VenueDescriptorCouchDao.SCOPE, collection = VenueDescriptorCouchDao.COLLECTION)
public class VenueDescriptorCouchDao extends AbstractCouchDao<VenueDescriptor> {

    public static final String BUCKET = "onebox-operative";
    public static final String KEY = "venueDescriptor";
    public static final String SCOPE = "catalog";
    public static final String COLLECTION = "venue-descriptor";
}
