package es.onebox.fusionauth.eip;

import es.onebox.fusionauth.dto.FusionAuthUserDTO;
import es.onebox.fusionauth.enums.WebhookType;
import es.onebox.message.broker.client.message.AbstractNotificationMessage;
import es.onebox.message.broker.client.message.NotificationMessage;

import java.io.Serial;

public class FusionAuthWebhookMessage extends AbstractNotificationMessage implements NotificationMessage {
    @Serial
    private static final long serialVersionUID = -3878323292241322944L;

    private Object user;
    private Object originalUser;
    private WebhookType type;

    public FusionAuthWebhookMessage() {}

    public Object getUser() {
        return user;
    }

    public void setUser(Object user) {
        this.user = user;
    }

    public Object getOriginalUser() {
        return originalUser;
    }

    public void setOriginalUser(Object originalUser) {
        this.originalUser = originalUser;
    }

    public WebhookType getType() {
        return type;
    }

    public void setType(WebhookType type) {
        this.type = type;
    }
}
