package es.onebox.event.sessions.amqp.seatgeneration;

import java.io.Serializable;

/**
 * Created by mmolinero on 19/06/18.
 */
public class BlockingReason implements Serializable {

    private static final long serialVersionUID = 1L;

    protected int blockingReasonId;
    protected int action;

    public BlockingReason() {
    }

    public BlockingReason(int blockingReasonId, int action) {
        this.blockingReasonId = blockingReasonId;
        this.action = action;
    }

    public int getBlockingReasonId() {
        return blockingReasonId;
    }

    public void setBlockingReasonId(int blockingReasonId) {
        this.blockingReasonId = blockingReasonId;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

}
