package es.onebox.fusionauth.dto;

import es.onebox.fusionauth.enums.WebhookType;

import java.io.Serial;
import java.io.Serializable;

public class FusionAuthEventDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -5866276803607291909L;

    private String createInstant;
    private String id;
    private WebhookType type;
    private Object user;
    private String tenantId;
    private Object original;

    public String getCreateInstant() {
        return createInstant;
    }

    public void setCreateInstant(String createInstant) {
        this.createInstant = createInstant;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public WebhookType getType() {
        return type;
    }

    public void setType(String type) {
        this.type = WebhookType.fromType(type);
    }

    public Object getUser() {
        return user;
    }

    public void setUser(Object user) {
        this.user = user;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public Object getOriginal() {
        return original;
    }

    public void setOriginal(Object original) {
        this.original = original;
    }
}
