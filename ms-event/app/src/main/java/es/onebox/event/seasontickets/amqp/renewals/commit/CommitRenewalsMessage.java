package es.onebox.event.seasontickets.amqp.renewals.commit;

import es.onebox.message.broker.client.message.AbstractNotificationMessage;

import java.util.List;

public class CommitRenewalsMessage extends AbstractNotificationMessage {
    private static final long serialVersionUID = 1L;

    private List<CommitRenewalsItem> items;

    public List<CommitRenewalsItem> getItems() {
        return items;
    }

    public void setItems(List<CommitRenewalsItem> items) {
        this.items = items;
    }
}
