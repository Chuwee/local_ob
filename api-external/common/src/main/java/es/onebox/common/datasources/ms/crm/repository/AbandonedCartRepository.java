package es.onebox.common.datasources.ms.crm.repository;

import es.onebox.common.datasources.ms.crm.MsCrmDatasource;
import es.onebox.common.datasources.ms.crm.dto.CrmClientResponse;
import es.onebox.common.datasources.ms.crm.dto.CrmOrderParams;
import es.onebox.common.datasources.ms.crm.dto.CrmOrderResponse;
import es.onebox.common.datasources.ms.crm.dto.CrmParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class AbandonedCartRepository {

    private final MsCrmDatasource msCrmDatasource;

    @Autowired
    public AbandonedCartRepository(MsCrmDatasource msCrmDatasource) {
        this.msCrmDatasource = msCrmDatasource;
    }

    public CrmOrderResponse getAbandonedOrder(CrmOrderParams filter) {
        return msCrmDatasource.getAbandonedOrder(filter);
    }

    public CrmClientResponse getAbandonedClient(CrmParams filter) {
        return msCrmDatasource.getAbandonedClient(filter);
    }

}
