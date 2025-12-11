package es.onebox.mgmt.datasources.ms.delivery.repositories;

import es.onebox.mgmt.datasources.ms.delivery.MsDeliveryDatasource;
import es.onebox.mgmt.datasources.ms.delivery.dto.ChannelEmailTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class DeliveryEmailRepository {

    private final MsDeliveryDatasource msDeliveryDatasource;

    @Autowired
    public DeliveryEmailRepository(MsDeliveryDatasource msDeliveryDatasource) {
        this.msDeliveryDatasource = msDeliveryDatasource;
    }

    public void sendTestEmail(ChannelEmailTest body) {
        msDeliveryDatasource.sendTestEmail(body);
    }
}
