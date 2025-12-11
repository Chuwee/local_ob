package es.onebox.event.forms.dao;

import es.onebox.couchbase.annotation.CouchRepository;
import es.onebox.couchbase.dao.AbstractCouchDao;
import es.onebox.event.config.CouchbaseKeys;
import es.onebox.event.forms.domain.Form;
import es.onebox.event.forms.enums.FormTypeDTO;
import org.springframework.stereotype.Repository;

@Repository
@CouchRepository(prefixKey = CouchbaseKeys.DEFAULT_FORM,
        bucket = CouchbaseKeys.BUCKET_ONEBOX_OPERATIVE,
        scope = CouchbaseKeys.FORMS_SCOPE,
        collection = CouchbaseKeys.MASTER_COLLECTION)
public class DefaultFormCouchDao extends AbstractCouchDao<Form> {

    public Form getByFormType(FormTypeDTO formType) {
        if (formType == null) {
            return this.get();
        }
        
        return this.get(formType.name());
    }
} 