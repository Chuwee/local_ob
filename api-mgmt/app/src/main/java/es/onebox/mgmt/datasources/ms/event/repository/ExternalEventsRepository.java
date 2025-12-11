package es.onebox.mgmt.datasources.ms.event.repository;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.datasources.ms.event.MsEventDatasource;
import es.onebox.mgmt.datasources.ms.event.dto.externalevent.ExternalEvent;
import es.onebox.mgmt.datasources.ms.event.dto.externalevent.ExternalEventType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ExternalEventsRepository {

    private final MsEventDatasource datasource;

    @Autowired
    public ExternalEventsRepository(MsEventDatasource datasource) {
        this.datasource = datasource;
    }

    public List<ExternalEvent> getExternalEvents(Long entityId, ExternalEventType eventType) {
        return datasource.getExternalEvents(entityId, eventType);
    }

    public List<IdNameDTO> getExternalEventRates(Long internalId) {
        return datasource.getExternalEventRates(internalId);
    }

    public ExternalEvent getExternalEvent(Long id) {
        return datasource.getExternalEvent(id);
    }
}
