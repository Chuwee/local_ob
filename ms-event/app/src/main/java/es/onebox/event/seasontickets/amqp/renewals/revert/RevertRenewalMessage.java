package es.onebox.event.seasontickets.amqp.renewals.revert;

import es.onebox.message.broker.client.message.AbstractNotificationMessage;

import java.util.List;

public class RevertRenewalMessage extends AbstractNotificationMessage {
    private static final long serialVersionUID = 1L;

    private List<RevertRenewalMessageItem> items;

    public List<RevertRenewalMessageItem> getItems() {
        return items;
    }

    public void setItems(List<RevertRenewalMessageItem> items) {
        this.items = items;
    }
}
