package es.onebox.ms.notification.ie.orderrelease;

import es.onebox.message.broker.client.message.AbstractNotificationMessage;

import java.util.Set;

/**
 * Created by joandf on 28/04/2015.
 */
public class OrderReleaseMessage extends AbstractNotificationMessage {

    private String orderCode;
    private Set<Integer> sessionIds;

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public Set<Integer> getSessionIds() {
        return sessionIds;
    }

    public void setSessionIds(Set<Integer> sessionIds) {
        this.sessionIds = sessionIds;
    }
}
