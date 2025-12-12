package es.onebox.ms.notification.webhooks.queue;

import es.onebox.ms.notification.webhooks.enums.NotificationAction;
import es.onebox.ms.notification.webhooks.enums.NotificationType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultNotificationConfigs {

    private static final Map<String, Map<NotificationAction, String>> defaultConfig = new HashMap<>();

    static {
        setDefaultConfig(NotificationType.ENTITY_FVZONE, List.of(NotificationAction.CREATE, NotificationAction.UPDATE));
        setDefaultConfig(NotificationType.USER_FVZONE, List.of(NotificationAction.CREATE, NotificationAction.REACTIVATE, NotificationAction.DEACTIVATE));
    }

    public static String getDocumentId(String notificationType, NotificationAction action) {
        Map<NotificationAction, String> defaultConfigActions = defaultConfig.get(notificationType);
        if (defaultConfigActions != null && defaultConfigActions.containsKey(action)) {
            return defaultConfigActions.get(action);
        }
        return null;
    }

    private static void setDefaultConfig(NotificationType type, List<NotificationAction> actions) {
        Map<NotificationAction, String> defaultDocuments = new HashMap<>();
        actions.forEach(action -> defaultDocuments.put(action, type.name() + "_" + action.name()));
        defaultConfig.put(type.name(), defaultDocuments);
    }
}
