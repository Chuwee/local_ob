package es.onebox.event.events.amqp.avetintegration;

import es.onebox.message.broker.client.message.AbstractNotificationMessage;
import es.onebox.message.broker.client.message.NotificationMessage;

import java.io.Serial;

public class IntegrationAvetMessage extends AbstractNotificationMessage implements NotificationMessage {

    @Serial
    private static final long serialVersionUID = 87293471942L;

    private Integer clubCode;
    private Integer seasonCode;
    private Integer capacityId;

    public IntegrationAvetMessage(Integer clubCode, Integer seasonCode, Integer capacityId) {
        this.clubCode = clubCode;
        this.seasonCode = seasonCode;
        this.capacityId = capacityId;
    }

    public IntegrationAvetMessage() {
    }

    public Integer getClubCode() {
        return clubCode;
    }

    public void setClubCode(Integer clubCode) {
        this.clubCode = clubCode;
    }

    public Integer getSeasonCode() {
        return seasonCode;
    }

    public void setSeasonCode(Integer seasonCode) {
        this.seasonCode = seasonCode;
    }

    public Integer getCapacityId() {
        return capacityId;
    }

    public void setCapacityId(Integer capacityId) {
        this.capacityId = capacityId;
    }
}
