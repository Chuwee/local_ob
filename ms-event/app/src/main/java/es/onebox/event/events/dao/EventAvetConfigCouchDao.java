package es.onebox.event.events.dao;

import es.onebox.couchbase.annotation.CouchRepository;
import es.onebox.couchbase.dao.AbstractCouchDao;
import es.onebox.event.events.domain.eventconfig.EventAvetConfig;
import es.onebox.event.products.dao.couch.AvetSectorRestriction;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author jgomez
 */

@Repository
@CouchRepository(prefixKey = EventAvetConfigCouchDao.AVET_CONFIG, bucket = EventAvetConfigCouchDao.ONEBOX_OPERATIVE)
public class EventAvetConfigCouchDao extends AbstractCouchDao<EventAvetConfig> {

    public static final String ONEBOX_OPERATIVE = "onebox-operative";
    public static final String AVET_CONFIG = "avetConfig";

    public void removeRestrictionByEventIdAndRestrictionId(Long eventId, String restrictionId) {
        EventAvetConfig eventAvetConfig = get(eventId.toString());
        if (eventAvetConfig != null) {
            List<AvetSectorRestriction> restrictions = eventAvetConfig.getRestrictions();
            if (restrictions != null) {
                restrictions.removeIf(restriction -> restriction.getSid().equals(restrictionId));
                this.upsert(eventId.toString(), eventAvetConfig);
            }
        }
    }

}
