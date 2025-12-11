package es.onebox.event.events.dao;

import es.onebox.couchbase.annotation.CouchRepository;
import es.onebox.couchbase.dao.AbstractCouchDao;
import es.onebox.event.events.dto.ExternalBarcodeEventConfigDTO;
import org.springframework.stereotype.Repository;

@Repository
@CouchRepository(prefixKey = EventExternalBarcodeConfigCouchDao.EVENT_EXTERNAL_BARCODE_CONFIG, bucket = EventExternalBarcodeConfigCouchDao.ONEBOX_OPERATIVE)
public class EventExternalBarcodeConfigCouchDao extends AbstractCouchDao<ExternalBarcodeEventConfigDTO> {

    public static final String ONEBOX_OPERATIVE = "onebox-operative";
    public static final String EVENT_EXTERNAL_BARCODE_CONFIG = "externalBarcodeEventConfig";
}
