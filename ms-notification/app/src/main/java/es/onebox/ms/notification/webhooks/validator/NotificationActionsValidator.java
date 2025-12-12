package es.onebox.ms.notification.webhooks.validator;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.ms.notification.exception.MsNotificationErrorCode;
import es.onebox.ms.notification.webhooks.enums.ChannelAction;
import es.onebox.ms.notification.webhooks.enums.EntityFvZoneAction;
import es.onebox.ms.notification.webhooks.enums.EventAction;
import es.onebox.ms.notification.webhooks.enums.ItemAction;
import es.onebox.ms.notification.webhooks.enums.MemberOrderAction;
import es.onebox.ms.notification.webhooks.enums.NotificationType;
import es.onebox.ms.notification.webhooks.enums.OrderAction;
import es.onebox.ms.notification.webhooks.enums.PreorderAction;
import es.onebox.ms.notification.webhooks.enums.ProductAction;
import es.onebox.ms.notification.webhooks.enums.PromotionAction;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class NotificationActionsValidator {

    public static void validateActions(Map<NotificationType, List<String>> events) {
        if (events != null && !events.isEmpty()) {
            events.keySet().forEach(key -> {
                switch (key.name()) {
                    case "ORDER" -> events.get(key).forEach(value -> {
                        if (Arrays.stream(OrderAction.values()).noneMatch(action -> action.name().equals(value))) {
                            throw OneboxRestException.builder(MsNotificationErrorCode.INCORRECT_EVENT_ACTION_REL).build();
                        }
                    });
                    case "ITEM" -> events.get(key).forEach(value -> {
                        if (Arrays.stream(ItemAction.values()).noneMatch(action -> action.name().equals(value))) {
                            throw OneboxRestException.builder(MsNotificationErrorCode.INCORRECT_EVENT_ACTION_REL).build();
                        }
                    });
                    case "MEMBERORDER" -> events.get(key).forEach(value -> {
                        if (Arrays.stream(MemberOrderAction.values()).noneMatch(action -> action.name().equals(value))) {
                            throw OneboxRestException.builder(MsNotificationErrorCode.INCORRECT_EVENT_ACTION_REL).build();
                        }
                    });
                    case "PREORDER" -> events.get(key).forEach(value -> {
                        if (Arrays.stream(PreorderAction.values()).noneMatch(action -> action.name().equals(value))) {
                            throw OneboxRestException.builder(MsNotificationErrorCode.INCORRECT_EVENT_ACTION_REL).build();
                        }
                    });
                    case "EVENT", "SESSION" -> events.get(key).forEach(value -> {
                        if (Arrays.stream(EventAction.values()).noneMatch(action -> action.name().equals(value))) {
                            throw OneboxRestException.builder(MsNotificationErrorCode.INCORRECT_EVENT_ACTION_REL).build();
                        }
                    });
                    case "PROMOTION" -> events.get(key).forEach(value -> {
                        if (Arrays.stream(PromotionAction.values()).noneMatch(action -> action.name().equals(value))) {
                            throw OneboxRestException.builder(MsNotificationErrorCode.INCORRECT_EVENT_ACTION_REL).build();
                        }
                    });
                    case "CHANNEL" -> events.get(key).forEach(value -> {
                        if (Arrays.stream(ChannelAction.values()).noneMatch(action -> action.name().equals(value))) {
                            throw OneboxRestException.builder(MsNotificationErrorCode.INCORRECT_EVENT_ACTION_REL).build();
                        }
                    });
                    case "ENTITY_FVZONE" -> events.get(key).forEach(value -> {
                        if (Arrays.stream(EntityFvZoneAction.values()).noneMatch(action -> action.name().equals(value))) {
                            throw OneboxRestException.builder(MsNotificationErrorCode.INCORRECT_EVENT_ACTION_REL).build();
                        }
                    });
                    case "PRODUCT" -> events.get(key).forEach(value -> {
                        if (Arrays.stream(ProductAction.values()).noneMatch(action -> action.name().equals(value))) {
                            throw OneboxRestException.builder(MsNotificationErrorCode.INCORRECT_EVENT_ACTION_REL).build();
                        }
                    });
                }
            });
        }
    }
}
