package es.onebox.event.forms.dao;

import es.onebox.couchbase.annotation.CouchRepository;
import es.onebox.couchbase.dao.AbstractCouchDao;
import es.onebox.event.config.CouchbaseKeys;
import es.onebox.event.forms.domain.MasterFormFields;
import org.springframework.stereotype.Repository;

@Repository
@CouchRepository(prefixKey = CouchbaseKeys.MASTER_FORMS,
        bucket = CouchbaseKeys.BUCKET_ONEBOX_OPERATIVE,
        scope = CouchbaseKeys.FORMS_SCOPE,
        collection = CouchbaseKeys.MASTER_COLLECTION)
public class MasterFormsFieldsCouchDao extends AbstractCouchDao<MasterFormFields> {

    public MasterFormFields getByEntityId(Long entityId) {
        return this.get(String.valueOf(entityId));
    }

} 