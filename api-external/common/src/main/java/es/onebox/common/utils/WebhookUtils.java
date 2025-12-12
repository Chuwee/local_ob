package es.onebox.common.utils;

import es.onebox.common.datasources.webhook.dto.fever.WebhookFeverDTO;

import java.util.Objects;

public final class WebhookUtils {

    public static final String HEADER_OB_EVENT = "ob-event";
    public static final String HEADER_OB_DELIVERY_ID = "ob-delivery-id";
    public static final String HEADER_OB_SIGNATURE = "ob-signature";
    public static final String HEADER_OB_ACTION = "ob-action";
    public static final String HEADER_OB_SUBTYPE = "ob-subtype";
    public static final String HEADER_OB_HOOK_ID = "ob-hook-id";

    private WebhookUtils() {
    }

    public static String buildProcessingMessage(WebhookFeverDTO webhookFever) {
        return buildMessage(webhookFever, "Processing");
    }

    public static String buildSendingMessage(WebhookFeverDTO webhookFever) {
        return buildMessage(webhookFever, "Sending");
    }

    public static String buildDiscardingMessage(WebhookFeverDTO webhookFever) {
        return buildMessage(webhookFever, "Discarding");
    }

    private static String buildMessage(WebhookFeverDTO webhookFever, String action) {
        String deliveryId = webhookFever.getHeaders().getHeader(HEADER_OB_DELIVERY_ID);
        String event = webhookFever.getHeaders().getHeader(HEADER_OB_EVENT);
        String webhookAction = webhookFever.getHeaders().getHeader(HEADER_OB_ACTION);
        String subtype = webhookFever.getHeaders().getHeader(HEADER_OB_SUBTYPE);

        String baseMessage = String.format("[FEVER WEBHOOK] %s notification %s Event: %s Action: %s",
                action, deliveryId, event, webhookAction);

        if (Objects.nonNull(subtype)) {
            baseMessage += String.format(" Subtype: %s", subtype);
        }

        return baseMessage;
    }
}
