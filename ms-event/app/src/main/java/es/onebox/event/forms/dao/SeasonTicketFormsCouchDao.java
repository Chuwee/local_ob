package es.onebox.event.forms.dao;

import es.onebox.couchbase.annotation.CouchRepository;
import es.onebox.couchbase.dao.AbstractCouchDao;
import es.onebox.event.config.CouchbaseKeys;
import es.onebox.event.forms.domain.Form;
import es.onebox.event.forms.enums.FormTypeDTO;
import org.springframework.stereotype.Repository;

@Repository
@CouchRepository(prefixKey = CouchbaseKeys.FORM,
        bucket = CouchbaseKeys.BUCKET_ONEBOX_OPERATIVE,
        scope = CouchbaseKeys.FORMS_SCOPE,
        collection = CouchbaseKeys.SEASON_TICKET_COLLECTION)
public class SeasonTicketFormsCouchDao extends AbstractCouchDao<Form> {

    public boolean exists(Long seasonTicketId, FormTypeDTO formType) {
        return super.exists(String.format("%s_%s", seasonTicketId, formType.name()));
    }

    public Form get(Long seasonTicketId, FormTypeDTO formType) {
        return super.get(String.format("%s_%s", seasonTicketId, formType.name()));
    }

    public void upsert(Long seasonTicketId, FormTypeDTO formType, Form form) {
        super.upsert(String.format("%s_%s", seasonTicketId, formType.name()), form);
    }
} 