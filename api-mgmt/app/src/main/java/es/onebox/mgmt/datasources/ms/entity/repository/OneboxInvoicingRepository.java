package es.onebox.mgmt.datasources.ms.entity.repository;

import es.onebox.mgmt.datasources.ms.entity.MsEntityDatasource;
import es.onebox.mgmt.datasources.ms.entity.dto.CreateOneboxInvoiceEntityRequest;
import es.onebox.mgmt.datasources.ms.entity.dto.EntityInvoiceConfigurationSearchFilter;
import es.onebox.mgmt.datasources.ms.entity.dto.GenerateOneboxInvoiceRequest;
import es.onebox.mgmt.datasources.ms.entity.dto.OneboxInvoiceEntities;
import es.onebox.mgmt.datasources.ms.entity.dto.OneboxInvoiceEntitiesFilter;
import es.onebox.mgmt.datasources.ms.entity.dto.OneboxInvoiceType;
import es.onebox.mgmt.datasources.ms.entity.dto.UpdateOneboxInvoiceEntityRequest;
import es.onebox.mgmt.datasources.ms.event.MsEventDatasource;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventSearchFilter;
import es.onebox.mgmt.datasources.ms.event.dto.event.Events;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class OneboxInvoicingRepository {

    private MsEntityDatasource msEntityDatasource;
    private MsEventDatasource msEventDatasource;

    @Autowired
    public OneboxInvoicingRepository(MsEntityDatasource msEntityDatasource,
                                     MsEventDatasource msEventDatasource) {
        this.msEntityDatasource = msEntityDatasource;
        this.msEventDatasource = msEventDatasource;
    }

    public void generateInvoice(GenerateOneboxInvoiceRequest request) {
        msEntityDatasource.generateInvoice(request);
    }

    public OneboxInvoiceEntitiesFilter getEntitiesFilter() {
        return msEntityDatasource.getEntitiesFilter();
    }

    public OneboxInvoiceEntities getEntitiesInvoiceConfiguration(EntityInvoiceConfigurationSearchFilter filter) {
        return msEntityDatasource.getEntitiesInvoiceConfiguration(filter);
    }

    public void createEntityInvoiceConfiguration(Long entityId, CreateOneboxInvoiceEntityRequest request) {
        msEntityDatasource.createEntityInvoiceConfiguration(entityId, request);
    }
    public void updateEntityInvoiceConfiguration(Long entityId, OneboxInvoiceType type, UpdateOneboxInvoiceEntityRequest request) {
        msEntityDatasource.updateEntityInvoiceConfiguration(entityId, type, request);
    }

    public Events getEvents(EventSearchFilter filter) {
        return msEventDatasource.getEvents(filter);
    }
}
