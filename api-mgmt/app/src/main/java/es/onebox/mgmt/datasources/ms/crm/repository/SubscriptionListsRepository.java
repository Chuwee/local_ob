package es.onebox.mgmt.datasources.ms.crm.repository;

import es.onebox.mgmt.datasources.ms.crm.MsCrmDatasource;
import es.onebox.mgmt.datasources.ms.crm.dto.SubscriptionDTO;
import es.onebox.mgmt.entities.dto.SubscriptionRequestFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SubscriptionListsRepository {

    private final MsCrmDatasource msCrmDatasource;

    @Autowired
    public SubscriptionListsRepository(MsCrmDatasource msCrmDatasource) {
        this.msCrmDatasource = msCrmDatasource;
    }

    public List<SubscriptionDTO> getSubscriptionLists(SubscriptionRequestFilter filter, Long entityId) {
        return msCrmDatasource.getSubscriptionLists(filter, entityId);
    }

    public SubscriptionDTO getSubscriptionList(Long entityId, Long operatorId, Integer subscriptionListId, Long entityAdminId) {
        return msCrmDatasource.getSubscriptionList(entityId, operatorId, subscriptionListId, entityAdminId);
    }

    public SubscriptionDTO getSubscriptionList(Integer subscriptionListId) {
        return msCrmDatasource.getSubscriptionList(subscriptionListId);
    }

    public SubscriptionDTO addSubscriptionLists(Long entityId, SubscriptionDTO subscription) {
        return msCrmDatasource.addSubscriptionLists(entityId, subscription);
    }

    public void updateSubscriptionLists(Long entityId, Long subscriptionListId, SubscriptionDTO subscription) {
        msCrmDatasource.updateSubscriptionLists(entityId, subscriptionListId, subscription);
    }

    public void deleteSubscriptionLists(Long entityId, Long subscriptionListId) {
        msCrmDatasource.deleteSubscriptionLists(entityId, subscriptionListId);
    }

}
