package es.onebox.mgmt.datasources.ms.notification.repository;

import es.onebox.mgmt.datasources.ms.notification.MsNotificationDatasource;
import es.onebox.mgmt.datasources.ms.notification.dto.CreateNotificationConfig;
import es.onebox.mgmt.datasources.ms.notification.dto.NotificationConfig;
import es.onebox.mgmt.datasources.ms.notification.dto.NotificationConfigs;
import es.onebox.mgmt.datasources.ms.notification.dto.SearchNotificationConfigFilter;
import es.onebox.mgmt.datasources.ms.notification.dto.UpdateNotificationConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class NotificationsConfigRepository {

    private final MsNotificationDatasource msNotificationDatasource;

    @Autowired
    public NotificationsConfigRepository(MsNotificationDatasource msNotificationDatasource) {
        this.msNotificationDatasource = msNotificationDatasource;
    }

    public NotificationConfig getNotificationConfig(String documentId){
        return msNotificationDatasource.getNotificationConfig(documentId);
    }

    public NotificationConfigs searchNotificationConfigs(SearchNotificationConfigFilter filter) {
        return msNotificationDatasource.searchNotificationConfigs(filter);
    }

    public NotificationConfig createNotificationConfig(CreateNotificationConfig createDTO){
        return msNotificationDatasource.createNotificationConfig(createDTO);
    }

    public void updateNotificationConfig(String documentId, UpdateNotificationConfig updateDTO){
        msNotificationDatasource.updateNotificationConfig(documentId, updateDTO);
    }

    public void deleteNotificationConfig(String documentId){
        msNotificationDatasource.deleteNotificationConfig(documentId);
    }

    public NotificationConfig regenerateApiKey(String documentId){
        return msNotificationDatasource.regenerateApiKey(documentId);
    }

}
