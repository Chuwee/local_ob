package es.onebox.mgmt.datasources.ms.entity.repository;

import es.onebox.mgmt.datasources.ms.entity.MsEntityDatasource;
import es.onebox.mgmt.datasources.ms.entity.dto.InvoiceProviderInfo;
import es.onebox.mgmt.datasources.ms.entity.dto.Producer;
import es.onebox.mgmt.datasources.ms.entity.dto.ProducerFilter;
import es.onebox.mgmt.datasources.ms.entity.dto.Producers;
import es.onebox.mgmt.datasources.ms.entity.dto.RequestInvoiceProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ProducerRepository {

    private final MsEntityDatasource msEntityDatasource;

    @Autowired
    public ProducerRepository(MsEntityDatasource msEntityDatasource) {
        this.msEntityDatasource = msEntityDatasource;
    }

    public Producers getProducers(ProducerFilter filter) {
        return msEntityDatasource.getProducers(filter);
    }

    public Producers getProducersByEntityId(Long entityId) {
        return msEntityDatasource.getProducersByEntityId(entityId);
    }

    public Producer getProducer(Long producerId) {
        return msEntityDatasource.getProducer(producerId);
    }

    public InvoiceProviderInfo getProducerInvoiceProviderInfo(Long producerId) {
        return msEntityDatasource.getInvoiceProvider(producerId);
    }

    public InvoiceProviderInfo requestInvoiceProvider(Long producerId, RequestInvoiceProvider request) {
        return msEntityDatasource.requestInvoiceProvider(producerId, request);
    }
}
