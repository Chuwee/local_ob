package es.onebox.ms.notification.datasources.ms.crm.repository;

import es.onebox.ms.notification.datasources.ms.crm.MsCrmDatasource;
import es.onebox.ms.notification.datasources.ms.crm.dto.CrmClientResponse;
import es.onebox.ms.notification.datasources.ms.crm.dto.CrmOrderResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class AuditCrmRepository {

    private final MsCrmDatasource msCrmDatasource;

    @Autowired
    public AuditCrmRepository(MsCrmDatasource msCrmDatasource) {
        this.msCrmDatasource = msCrmDatasource;
    }

    public CrmOrderResponse getAuditCrmOrders(String orderId, Long entityId) {
        return msCrmDatasource.getAuditCrmOrders(orderId, entityId);
    }

    public CrmClientResponse getAuditCrmBuyers(String email, Long entityId) {
        return msCrmDatasource.getAuditCrmBuyers(email, entityId);
    }

}
