package es.onebox.mgmt.datasources.ms.client.dto.b2bpublishing;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

public class Historic implements Serializable {

    private Integer clientId;
    private String clientName;
    private Integer clientEntityId;
    private Integer userId;
    private String userName;
    private PublishingUserType userType;

    private Integer sourceQuotaId;
    private Integer targetQuotaId;
    private Integer sourcePriceTypeId;
    private Integer targetPriceTypeId;

    private ZonedDateTime date;
    private TransactionType type;
    private BigDecimal price;
    private TicketStatus status;
    private String traceId;

    public Integer getClientId() {
        return clientId;
    }
    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public PublishingUserType getUserType() {
        return userType;
    }
    public void setUserType(PublishingUserType userType) {
        this.userType = userType;
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

    public TransactionType getType() {
        return type;
    }
    public void setType(TransactionType type) {
        this.type = type;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }

    public String getTraceId() {
        return traceId;
    }
    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }
}
