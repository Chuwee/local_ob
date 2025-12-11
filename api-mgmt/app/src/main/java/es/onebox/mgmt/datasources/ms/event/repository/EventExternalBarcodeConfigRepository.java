package es.onebox.mgmt.datasources.ms.event.repository;

import es.onebox.mgmt.datasources.ms.event.MsEventDatasource;
import es.onebox.mgmt.datasources.ms.event.dto.event.ExternalBarcodeEventConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class EventExternalBarcodeConfigRepository {

    private final MsEventDatasource msEventDatasource;

    @Autowired
    public EventExternalBarcodeConfigRepository(MsEventDatasource msEventDatasource) {
        this.msEventDatasource = msEventDatasource;
    }

    public ExternalBarcodeEventConfig getExternalBarcodeEntityConfig(Long eventId) {
        return msEventDatasource.getExternalBarcodeEntityConfig(eventId);
    }

    public void updateExternalBarcodeEntityConfig(Long eventId, ExternalBarcodeEventConfig body) {
        msEventDatasource.updateExternalBarcodeEntityConfig(eventId, body);
    }
}
