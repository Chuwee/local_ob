package es.onebox.mgmt.channels.reviews.dto;

import java.io.Serial;
import java.io.Serializable;

public class ChannelReviewConfigDetailsDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -3559614684755862408L;

    private ChannelReviewConfigDetailsEventDTO event;
    private ChannelReviewConfigDetailsSessionDTO session;

    public ChannelReviewConfigDetailsEventDTO getEvent() {
        return event;
    }

    public void setEvent(ChannelReviewConfigDetailsEventDTO event) {
        this.event = event;
    }

    public ChannelReviewConfigDetailsSessionDTO getSession() {
        return session;
    }

    public void setSession(ChannelReviewConfigDetailsSessionDTO session) {
        this.session = session;
    }
}
