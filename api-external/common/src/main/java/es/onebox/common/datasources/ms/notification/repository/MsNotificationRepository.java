package es.onebox.common.datasources.ms.notification.repository;

import es.onebox.common.datasources.ms.notification.MsNotificationDatasource;
import es.onebox.common.datasources.ms.notification.dto.NotificationConfigDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class MsNotificationRepository {

    private final MsNotificationDatasource msNotificationDatasource;

    @Autowired
    public MsNotificationRepository(MsNotificationDatasource msNotificationDatasource) {
        this.msNotificationDatasource = msNotificationDatasource;
    }

    public NotificationConfigDTO getNotificationConfig(String documentId) {
        return msNotificationDatasource.getNotificationConfig(documentId);
    }

}
