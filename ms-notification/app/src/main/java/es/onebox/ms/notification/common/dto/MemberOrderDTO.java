package es.onebox.ms.notification.common.dto;

import es.onebox.ms.notification.common.enums.MemberOrderType;

import java.io.Serial;
import java.io.Serializable;

public class MemberOrderDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 6012039159500007659L;

    private String code;
    private Long entityId;
    private Long channelId;
    private MemberOrderType type;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public MemberOrderType getType() {
        return type;
    }

    public void setType(MemberOrderType type) {
        this.type = type;
    }
}
