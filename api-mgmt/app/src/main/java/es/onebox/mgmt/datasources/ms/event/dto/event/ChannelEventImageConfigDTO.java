package es.onebox.mgmt.datasources.ms.event.dto.event;


import java.io.Serial;
import java.io.Serializable;

public class ChannelEventImageConfigDTO  implements Serializable {

    @Serial
    private static final long serialVersionUID = -7620475076541596571L;

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
