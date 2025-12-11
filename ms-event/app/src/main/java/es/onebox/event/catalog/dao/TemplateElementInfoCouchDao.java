package es.onebox.event.catalog.dao;

import es.onebox.couchbase.annotation.CouchRepository;
import es.onebox.couchbase.core.Key;
import es.onebox.couchbase.dao.AbstractCouchDao;
import es.onebox.event.catalog.dao.couch.TemplateElementInfo;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@CouchRepository(prefixKey = TemplateElementInfoCouchDao.PREFIX_TEMPLATE_INFO, bucket = TemplateElementInfoCouchDao.BUCKET_ONEBOX_OPERATIVE
        ,scope = TemplateElementInfoCouchDao.VENUE_TEMPLATES_SCOPE, collection = TemplateElementInfoCouchDao.TEMPLATES_INFO_COLLECTION
)
public class TemplateElementInfoCouchDao extends AbstractCouchDao<TemplateElementInfo> {

    public static final String BUCKET_ONEBOX_OPERATIVE = "onebox-operative";
    public static final String PREFIX_TEMPLATE_INFO = "templateInfo";
    public static final String VENUE_TEMPLATES_SCOPE = "venue-templates";
    public static final String TEMPLATES_INFO_COLLECTION = "templates-info";

    public List<TemplateElementInfo> bulkGet(Integer templateId, List<Long> ids, String templateElementInfoType) {
        List<Key> keys = ids.stream().map(id -> {
            Key key = new Key();
            key.setKey(new String[]{templateId.toString(), templateElementInfoType, id.toString()});
            return key;
        }).toList();
        return super.bulkGet(keys);
    }

}

