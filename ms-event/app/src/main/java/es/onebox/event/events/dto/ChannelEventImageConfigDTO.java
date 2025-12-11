package es.onebox.event.events.dto;

import es.onebox.event.events.enums.ImageOrigin;

import java.io.Serial;
import java.io.Serializable;

public class ChannelEventImageConfigDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 4538212811686717975L;

    private Long sessionId;
    private ImageOrigin imageOrigin;

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public ImageOrigin getImageOrigin() {
        return imageOrigin;
    }

    public void setImageOrigin(ImageOrigin imageOrigin) {
        this.imageOrigin = imageOrigin;
    }
}
