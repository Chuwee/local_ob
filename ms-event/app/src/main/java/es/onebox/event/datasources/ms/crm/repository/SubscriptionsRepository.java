package es.onebox.event.datasources.ms.crm.repository;

import es.onebox.event.datasources.ms.crm.MsCRMDatasource;
import es.onebox.event.datasources.ms.crm.dto.SubscriptionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class SubscriptionsRepository {

    private final MsCRMDatasource msCRMDatasource;

    @Autowired
    public SubscriptionsRepository(MsCRMDatasource msCRMDatasource) {
        this.msCRMDatasource = msCRMDatasource;
    }

    public SubscriptionDTO getSubscriptionList(Integer entityId, Integer subscriptionListId) {
        return msCRMDatasource.getSubscriptionList(entityId, subscriptionListId);
    }
}
