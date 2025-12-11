package es.onebox.mgmt.datasources.ms.client.dto.b2bpublishing;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

public class SeatPublishing extends BaseSeatPublishing implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Integer channelId;
    private Integer entityId;
    private Integer clientId;
    private Integer clientEntityId;
    private Integer userId;
    private PublishingUserType userType;

    private Integer sourceQuotaId;
    private Integer targetQuotaId;
    private Integer sourcePriceTypeId;
    private Integer targetPriceTypeId;

    private ZonedDateTime date;
    private String traceId;

    private List<Historic> historic;
    private AdditionalData additionalData;

    public Integer getChannelId() {
        return channelId;
    }
    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }

    public Integer getEntityId() {
        return entityId;
    }
    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }

    public Integer getClientId() {
        return clientId;
    }
    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }

    public Integer getClientEntityId() {
        return clientEntityId;
    }
    public void setClientEntityId(Integer clientEntityId) {
        this.clientEntityId = clientEntityId;
    }

    public Integer getUserId() {
        return userId;
    }
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public PublishingUserType getPublishingUserType() {
        return userType;
    }
    public void setPublishingUserType(PublishingUserType publishingUserType) {
        this.userType = publishingUserType;
    }

    public Integer getSourceQuotaId() {
        return sourceQuotaId;
    }
    public void setSourceQuotaId(Integer sourceQuotaId) {
        this.sourceQuotaId = sourceQuotaId;
    }

    public Integer getTargetQuotaId() {
        return targetQuotaId;
    }
    public void setTargetQuotaId(Integer targetQuotaId) {
        this.targetQuotaId = targetQuotaId;
    }

    public Integer getSourcePriceTypeId() {
        return sourcePriceTypeId;
    }
    public void setSourcePriceTypeId(Integer sourcePriceTypeId) {
        this.sourcePriceTypeId = sourcePriceTypeId;
    }

    public Integer getTargetPriceTypeId() {
        return targetPriceTypeId;
    }
    public void setTargetPriceTypeId(Integer targetPriceTypeId) {
        this.targetPriceTypeId = targetPriceTypeId;
    }

    public ZonedDateTime getDate() {
        return date;
    }
    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public String getTraceId() {
        return traceId;
    }
    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public List<Historic> getHistoric() {
        return historic;
    }
    public void setHistoric(List<Historic> historic) {
        this.historic = historic;
    }

    @Override
    public AdditionalData getAdditionalData() {
        return additionalData;
    }
    public void setAdditionalData(AdditionalData additionalData) {
        this.additionalData = additionalData;
    }
}
