package es.onebox.mgmt.events.dto.channel;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.events.enums.ImageOrigin;

import java.io.Serial;
import java.io.Serializable;

public class EventImageConfigDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 6614176738078553448L;

    @JsonProperty("session_id")
    private Long sessionId;
    @JsonProperty("image_origin")
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
