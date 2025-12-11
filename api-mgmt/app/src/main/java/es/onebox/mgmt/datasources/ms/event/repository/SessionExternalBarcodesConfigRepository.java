package es.onebox.mgmt.datasources.ms.event.repository;

import es.onebox.mgmt.datasources.ms.event.MsEventDatasource;
import es.onebox.mgmt.datasources.ms.event.dto.session.ExternalBarcodeSessionConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class SessionExternalBarcodesConfigRepository {

    private final MsEventDatasource msEventDatasource;

    @Autowired
    public SessionExternalBarcodesConfigRepository(MsEventDatasource msEventDatasource) {
        this.msEventDatasource = msEventDatasource;
    }

    public ExternalBarcodeSessionConfig getSessionExternalBarcodeConfig(Long sessionId) {
        return msEventDatasource.getExternalBarcodeSessionConfig(sessionId);
    }

    public void updateSessionExternalBarcodeConfig(Long sessionId, ExternalBarcodeSessionConfig body) {
        msEventDatasource.updateExternalBarcodeSessionConfig(sessionId, body);
    }
}
