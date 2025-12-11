package es.onebox.event.catalog.elasticsearch.indexer;

import es.onebox.event.catalog.dao.VenueDescriptorCouchDao;
import es.onebox.event.catalog.elasticsearch.context.EventIndexationContext;
import es.onebox.event.catalog.elasticsearch.context.EventIndexationType;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class VenueDescriptorIndexer {

    private final VenueDescriptorCouchDao venueDescriptorCouchDao;

    @Autowired
    public VenueDescriptorIndexer(VenueDescriptorCouchDao venueDescriptorCouchDao) {
        this.venueDescriptorCouchDao = venueDescriptorCouchDao;
    }

    public void indexVenueDescriptors(EventIndexationContext ctx) {
        if (EventIndexationType.PARTIAL_BASIC.equals(ctx.getType()) ||
                EventIndexationType.PARTIAL_COM_ELEMENTS.equals(ctx.getType()) ||
                EventIndexationType.SEASON_TICKET.equals(ctx.getType())) {
            return;
        }

        if (ctx.getVenueDescriptor() != null && CollectionUtils.isNotEmpty(ctx.getVenueDescriptor().values())) {
            venueDescriptorCouchDao.bulkUpsert(new ArrayList<>(ctx.getVenueDescriptor().values()));
        }
    }
}
