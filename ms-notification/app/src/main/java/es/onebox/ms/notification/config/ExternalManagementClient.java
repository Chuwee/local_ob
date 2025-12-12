package es.onebox.ms.notification.config;

import es.onebox.ms.notification.ie.utils.ObjectKey;
import es.onebox.ms.notification.ie.utils.Update;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import static es.onebox.core.utils.optional.OptionalString.of;

/**
 * User: grodrigues
 * Date: 22/05/2015
 */
@Configuration
public class ExternalManagementClient implements ObjectKey<String>, Update<ExternalManagementClient> {

    @Value("${onebox.externalManagement.clientId}")
    private String clientId;

    @Value("${onebox.externalManagement.entityId}")
    private String entityId;

    @Value("${onebox.externalManagement.user}")
    private String user;

    @Value("${onebox.externalManagement.password}")
    private String password;

    @Override
    public String getKey() {
        return this.entityId;
    }

    @Override
    public ExternalManagementClient update(ExternalManagementClient element) {
        of(element.getClientId()).ifHasContent(this::setClientId);
        of(element.getEntityId()).ifHasContent(this::setEntityId);
        of(element.getUser()).ifHasContent(this::setUser);
        of(element.getPassword()).ifHasContent(this::setPassword);

        return this;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
