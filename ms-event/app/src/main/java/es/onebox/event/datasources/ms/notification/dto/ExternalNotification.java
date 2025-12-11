package es.onebox.event.datasources.ms.notification.dto;

import java.io.Serializable;

public class ExternalNotification implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer channelId;
    private String service;
    private String notificationUrl;
    private String password;

    public Integer getChannelId() {
        return channelId;
    }

    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }

    public String getNotificationUrl() {
        return notificationUrl;
    }

    public void setNotificationUrl(String notificationUrl) {
        this.notificationUrl = notificationUrl;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
