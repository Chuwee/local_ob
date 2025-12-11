package es.onebox.event.sessions.amqp.seatgeneration;

import es.onebox.message.broker.client.message.AbstractNotificationMessage;
import es.onebox.message.broker.client.message.NotificationMessage;

import java.io.Serial;
import java.util.List;

public class PreGenerateSeatMessage extends AbstractNotificationMessage implements NotificationMessage {


    @Serial
    private static final long serialVersionUID = -5237882088409052372L;

    protected Long sessionId;
    protected boolean seasonPass;
    protected boolean creation;
    private Integer eventType;
    protected List<BlockingReason> blockingReasons;
    private int delayedCreationSeconds;

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public boolean isSeasonPass() {
        return seasonPass;
    }

    public void setSeasonPass(boolean seasonPass) {
        this.seasonPass = seasonPass;
    }

    public boolean isCreation() {
        return creation;
    }

    public void setCreation(boolean creation) {
        this.creation = creation;
    }

    public Integer getEventType() {
        return eventType;
    }

    public void setEventType(Integer eventType) {
        this.eventType = eventType;
    }

    public List<BlockingReason> getBlockingReasons() {
        return blockingReasons;
    }

    public void setBlockingReasons(List<BlockingReason> blockingReasons) {
        this.blockingReasons = blockingReasons;
    }

    public int getDelayedCreationSeconds() {
        return delayedCreationSeconds;
    }

    public void setDelayedCreationSeconds(int delayedCreationSeconds) {
        this.delayedCreationSeconds = delayedCreationSeconds;
    }
}
