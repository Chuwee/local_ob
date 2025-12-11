package es.onebox.event.sessions.dao;

import es.onebox.couchbase.annotation.CouchRepository;
import es.onebox.couchbase.dao.AbstractCouchDao;
import es.onebox.event.sessions.dto.ExternalBarcodeSessionConfigDTO;
import org.springframework.stereotype.Repository;

@Repository
@CouchRepository(prefixKey = SessionExternalBarcodeConfigDao.SESSION_EXTERNAL_BARCODE_CONFIG, bucket = SessionExternalBarcodeConfigDao.ONEBOX_OPERATIVE)
public class SessionExternalBarcodeConfigDao extends AbstractCouchDao<ExternalBarcodeSessionConfigDTO> {

    public static final String ONEBOX_OPERATIVE = "onebox-operative";
    public static final String SESSION_EXTERNAL_BARCODE_CONFIG = "externalBarcodeSessionConfig";
}
