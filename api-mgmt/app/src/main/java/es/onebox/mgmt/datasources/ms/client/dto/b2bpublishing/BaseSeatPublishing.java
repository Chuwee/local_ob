package es.onebox.mgmt.datasources.ms.client.dto.b2bpublishing;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;

public class BaseSeatPublishing implements Serializable {
    @Serial private static final long serialVersionUID = 1L;

    private Long id;
    private Long seatId;
    private Long notNumberedAreaId;

    private Integer eventId;
    private Integer channelId;
    private Integer sessionId;

    private Integer clientId;
    private Integer userId;
    private PublishingUserType publishingUserType;
    private Integer clientEntityId;

    private TransactionType type;
    private ZonedDateTime date;
    private BaseAdditionalData additionalData;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Long getSeatId() {
        return seatId;
    }
    public void setSeatId(Long seatId) {
        this.seatId = seatId;
    }

    public Long getNotNumberedAreaId() {
        return notNumberedAreaId;
    }

    public void setNotNumberedAreaId(Long notNumberedAreaId) {
        this.notNumberedAreaId = notNumberedAreaId;
    }

    public Integer getEventId() {
        return eventId;
    }
    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public Integer getChannelId() {
        return channelId;
    }

    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }

    public Integer getSessionId() {
        return sessionId;
    }
    public void setSessionId(Integer sessionId) {
        this.sessionId = sessionId;
    }

    public Integer getClientId() {
        return clientId;
    }

    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public PublishingUserType getPublishingUserType() {
        return publishingUserType;
    }

    public void setPublishingUserType(PublishingUserType publishingUserType) {
        this.publishingUserType = publishingUserType;
    }

    public Integer getClientEntityId() {
        return clientEntityId;
    }

    public void setClientEntityId(Integer clientEntityId) {
        this.clientEntityId = clientEntityId;
    }

    public TransactionType getType() {
        return type;
    }
    public void setType(TransactionType type) {
        this.type = type;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public BaseAdditionalData getAdditionalData() {
        return additionalData;
    }
    public void setAdditionalData(BaseAdditionalData additionalData) {
        this.additionalData = additionalData;
    }
}
